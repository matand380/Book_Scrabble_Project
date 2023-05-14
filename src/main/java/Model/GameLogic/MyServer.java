package Model.GameLogic;


import Model.BS_Host_Model;

import java.io.*;
import java.net.*;
import java.util.*;

public class MyServer {

    public List<Socket> clients;
    private int port;
    private ClientHandler ch;
    private volatile boolean stop;
    System.Logger logger;

    Map<String,Socket> clientsMap = new HashMap<>();


    /**
     * The MyServer function is a constructor for the MyServer class.
     * It initializes the port and client handler of this server.
     * <p>
     *
     * @param port Define the port number that the server will be listening on
     * @param ch   Pass the client handler object to the server class
     */
    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        this.stop = false;
        this.clients = new ArrayList<>();
    }

    /**
     * The runServer function is the main function of the server.
     * It creates a new ServerSocket and sets its timeout to 1000 milliseconds.
     * Then, it enters an infinite loop that waits for clients to connect,
     * and when they do, it handles them using handleClient().
     * <p>
     *
     * @throws Exception If an exception occurred
     */
    private void runServer() throws Exception {
        ServerSocket server = new ServerSocket(port);
        server.setSoTimeout(1000);
        logger = System.getLogger("MyServer");
        logger.log(System.Logger.Level.INFO, "Server is alive and waiting for clients");
        while (!stop) {
            try {
                Socket aClient = server.accept(); // blocking call
                String clientID = UUID.randomUUID().toString().substring(0,6);
                clientsMap.put(clientID,aClient);
                ping(clientID);

                logger.log(System.Logger.Level.INFO, "New client connected");

                try {
                    try {
                        ch.handleClient((aClient.getInputStream()), (aClient.getOutputStream()));
                        // TODO: 05/05/2023 implement it in thread
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (SocketTimeoutException e) {
                //wait for another client
            }

        }
        for (Socket client : clients) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        server.close();
    }

    private void ping(String clientID) {
        Socket s = clientsMap.get(clientID);
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject("ping:" + clientID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * The start function creates a new thread and runs the runServer function in that thread.
     * This is done so that the server can continue to accept connections while it is processing requests.
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
     */
    public void stop() {
        stop = true;
    }

    /**
     * The close function stops the thread and closes the socket.
     */
    public void close() {
        stop();
    }

    public String ip() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.getHostAddress().contains(":")) {
                        continue; // skip IPv6 addresses
                    }
                    return addr.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublicIp() {
        String ip = null;
        try {
            URL url = new URL("https://api.ipify.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = in.readLine();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

    public void updateAll(Object s) {

        for (Socket client : clients) {
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException e) {
                logger.log(System.Logger.Level.ERROR, "Error in update all: getting output stream");
                throw new RuntimeException(e);
            }
            try {
                out.writeObject(s);
            } catch (IOException e) {
                logger.log(System.Logger.Level.ERROR, "Error in update all: writing to output stream");
                throw new RuntimeException(e);
            }

        }
    }

    public void updateSpecificPlayer(String id, Object obj)
    {
        Socket s = clientsMap.get(id);
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error in update specific player: getting output stream");
            throw new RuntimeException(e);
        }
        try {
            out.writeObject(obj);
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error in update specific player: writing to output stream");
            throw new RuntimeException(e);
        }

    }
}


