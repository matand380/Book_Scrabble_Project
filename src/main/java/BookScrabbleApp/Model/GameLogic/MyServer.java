package BookScrabbleApp.Model.GameLogic;


import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.ViewModel.BS_Host_ViewModel;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {

    public List<Socket> clients;
    System.Logger logger;
    ExecutorService executorService;
    Map<String, Socket> clientsMap = new HashMap<>();
    private int port;
    private HostCommunicationHandler ch;
    private volatile boolean stop;


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
        this.ch = BS_Host_Model.getModel().getCommunicationHandler();
        this.stop = false;
        this.clients = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(3);
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
        ServerSocket server = new ServerSocket(port, 3);
        server.setSoTimeout(1000);
        logger = System.getLogger("MyServer");
        logger.log(System.Logger.Level.INFO, "Server is alive and waiting for clients");
        logger.log(System.Logger.Level.INFO, ip() + "  " + port);
        while (!stop) {
            try {
                Socket aClient = server.accept(); // blocking call
                String clientID = UUID.randomUUID().toString().substring(0, 6);
                clients.add(aClient);
                clientsMap.put(clientID, aClient);

                logger.log(System.Logger.Level.INFO, "New client connected");

                executorService.submit(() -> {
                    try {
                        ping(clientID);
                        ch.handleClient(aClient.getInputStream(), aClient.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (SocketTimeoutException e) {
                //wait for another client
            }

        }
        if (clientsMap.isEmpty()) {
            //close all threads
            ch.close();
            server.close();
        } else {
            //force close all threads
            logger.log(System.Logger.Level.INFO, "Server is shutting down");
        }
    }

    private void ping(String clientID) {
        updateSpecificPlayer(clientID, "ping:" + clientID);
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
            URL url = new URL("https://ifconfig.me/ip");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = in.readLine();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

    public void updateAll(String s) {
        //foreach socket in map send s
        for (String id : clientsMap.keySet()) {
            updateSpecificPlayer(id, s);
        }
//        clientsMap.forEach((id, socket) -> {
//
//            PrintWriter out;
//            try {
//                out = new PrintWriter(socket.getOutputStream());
//            } catch (IOException e) {
//                logger.log(System.Logger.Level.ERROR, "Error in update all: getting output stream");
//                throw new RuntimeException(e);
//            }
//            out.println(s);
//            out.flush();
//        });
    }

    public void updateSpecificPlayer(String id, Object obj) {

        Socket s = clientsMap.get(id);
        PrintWriter out;
        try {
            if (s != null) {
                out = new PrintWriter(s.getOutputStream());
                out.println(obj);
                out.flush();
            }
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error in update specific player: getting output stream");
            throw new RuntimeException(e);
        }


    }

    public boolean isRunning() {
        return !stop;
    }
}


