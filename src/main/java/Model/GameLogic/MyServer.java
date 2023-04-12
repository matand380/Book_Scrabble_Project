package Model.GameLogic;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MyServer {

    private int port;
    private ClientHandler ch;
    private volatile boolean stop;

    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        this.stop = false;
    }

    private void runServer() throws Exception {
        ServerSocket server = new ServerSocket(port);
        server.setSoTimeout(1000);
        while (!stop) {
            try {
                Socket aClient = server.accept(); // blocking call
                try {
                    ch.handleClient(aClient.getInputStream(), aClient.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ch.close();
                        aClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SocketTimeoutException e) {
                //wait for another client
            }
        }
        server.close();
    }

    public void start() {
        new Thread(() -> {
            try {
                runServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stop() {
        stop = true;
    }

    //if a client is connected, let it finish
    public void close() {
        stop();
    }
}


