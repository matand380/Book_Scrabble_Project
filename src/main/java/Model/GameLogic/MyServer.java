package Model.GameLogic;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MyServer {

    private int port;
    private ClientHandler ch;
    private volatile boolean stop;

    /**
     * The MyServer function is a constructor for the MyServer class.
     * It initializes the port and client handler of this server.
     *<p>
     * @param  port Define the port number that the server will be listening on
     * @param  ch Pass the client handler object to the server class
     *
     *
     */
    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        this.stop = false;
    }

    /**
     * The runServer function is the main function of the server.
     * It creates a new ServerSocket and sets its timeout to 1000 milliseconds.
     * Then, it enters an infinite loop that waits for clients to connect,
     * and when they do, it handles them using handleClient().
     * <p>
     * @throws Exception If an exception occurred
     */
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

    /**
     * The start function creates a new thread and runs the runServer function in that thread.
     * This is done so that the server can continue to accept connections while it is processing requests.
     *
     */
    public void start() {
        new Thread(() -> {
            try {
                runServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * The stop function sets the stop variable to true, which will cause the run function to exit.
     *
     */
    public void stop() {
        stop = true;
    }

    /**
     * The close function stops the thread and closes the socket.
     *
     */
    public void close() {
        stop();
    }
}


