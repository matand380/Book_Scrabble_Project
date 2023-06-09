package BookScrabbleApp.Model.GameLogic;

import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.Model.GameData.Player;
import BookScrabbleApp.Model.GameData.Tile;
import BookScrabbleApp.Model.GameData.Word;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


public class HostCommunicationHandler implements ClientHandler {
    Map<String, Consumer<String[]>> handlers = new HashMap<>();
    PrintWriter toGameServer;
    Scanner fromGameServer;
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    public ExecutorService executor = Executors.newFixedThreadPool(3);
    volatile boolean stop = false;

    /**
     * The HostCommunicationHandler function is responsible for handling all the requests from the clients.
     * It uses a map of functions to invoke them when needed.
     * <p>
     */
    public HostCommunicationHandler() {
        executor.submit(this::handleRequests);

        //put all the methods in the map for being able to invoke them in handleRequests
        handlers.put("passTurn", message ->
                BS_Host_Model.getModel().passTurn(Integer.parseInt(message[1])));

        handlers.put("addPlayer", message -> {
            Player p = new Player();
            p.set_socketID(message[1]);
            p.set_name(message[2]);
            BS_Host_Model.getModel().getPlayerToSocketID().put(p.get_name(), p.get_socketID());
            p.setTileLottery();
            BS_Host_Model.getModel().addPlayer(p);

        });

        handlers.put("tryPlaceWord", message -> {
            String PlayerIndex = message[1];
            String word = message[2];
            int row = Integer.parseInt(message[3]);
            int col = Integer.parseInt(message[4]);
            boolean direction = message[5].equals("true");

            BS_Host_Model.getModel().tryPlaceWord(word, row, col, direction);
        });

        handlers.put("challengeWord", message -> {
            String PlayerIndex = message[1];
            String word = message[2];
            boolean exist = BS_Host_Model.getModel().currentPlayerWords.stream().anyMatch(w1 -> w1.toString().equals(word));
            if (exist) {
                String challengeInfo = PlayerIndex + ":" + word;
                BS_Host_Model.getModel().requestChallengeActivation(challengeInfo);
            } else {
                BS_Host_Model.getModel().hostLogger.log(System.Logger.Level.ERROR, "Player " + PlayerIndex + " tried to challenge a word that doesn't exist");
            }
        });

        handlers.put("endGame", message -> {
            String id = message[1];
            try {
                BS_Host_Model.getModel().getCommunicationServer().clientsMap.get(id).close();
                BS_Host_Model.getModel().getCommunicationServer().clientsMap.remove(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (BS_Host_Model.getModel().getCommunicationServer().clientsMap.size() == 0) {
                BS_Host_Model.getModel().endGame();
            }
        });

        handlers.put("unPark", message -> {
            BS_Host_Model.getModel().unPark();
        });
    }


    /**
     * The handleRequests function is the main function of the HostCommunicationHandler class.
     * It takes in a request from the inputQueue, and then uses that request to call
     * one of its handlers. The handler will then process that request and send it back to
     * whoever requested it. This function runs as long as there are requests in the queue,
     * or until communicationServer stops running (which happens when all threads have been closed).
     * <p>
     */
    public void handleRequests() {
        while (!stop) {
            try {
                String key = inputQueue.take(); //blocking call
                String[] message = key.split(":");
                String methodName = message[0];
                if (handlers.get(methodName) != null) {
                    handlers.get(methodName).accept(message);
                } else {
                    System.out.println("No handler for method(Host) : " + methodName);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * The handleClient function is the function that handles all the communication between
     * the client and server. It takes in an input stream and output stream, which are used to
     * communicate with each other. The handleClient function will continuously run while it is
     * connected to a client, as long as there are no errors or exceptions thrown. The handleClient
     * function will take in a key from the inputStream (which comes from the client), put it into
     * an ArrayBlockingQueue called &quot;inputQueue&quot;, then wait for another key to be sent by repeating this process.
     *
     * @param inputStream  Read data from the client
     * @param outputStream Write to the client
     */
    @Override
    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        try (
                Scanner newClientInput = new Scanner(inputStream);
        ) {
            while (!stop) {
                String key = null;
                if (newClientInput.hasNext()) {
                    key = newClientInput.next();
                }
                if (key != null)// Read an object from the server
                {
                    try {
                        inputQueue.put(key);
                    } catch (InterruptedException e) {
                        System.out.println("Error(HOST) in handleClient");
                    }
                }
            }
        }
    }


    /**
     * The close function closes the connection to the server.
     * <p>
     */
    @Override
    public void close() {
        stop = true;
        //in.close();
        //out.close();
        toGameServer.close();
        fromGameServer.close();
    }

    /**
     * The messagesToGameServer function is used to send messages from the client to the game server.
     * <p>
     *
     * @param key Send a message to the game server
     */
    public void messagesToGameServer(String key) {
        if (key != null) {
            try {
                toGameServer = new PrintWriter(BS_Host_Model.getModel().getGameSocket().getOutputStream(), true);
                toGameServer.println(key);
                toGameServer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * The messagesFromGameServer function is used to receive messages from the game server.
     * <p>
     *
     * @return A string containing the message from the game server
     */
    public String messagesFromGameServer() {
        try {
            String key = null;
            try {
                fromGameServer = new Scanner(BS_Host_Model.getModel().getGameSocket().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            key = fromGameServer.next();
            return key;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}





