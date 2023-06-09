package BookScrabbleApp.Model.GameLogic;

import BookScrabbleApp.Model.BS_Guest_Model;
import BookScrabbleApp.Model.GameData.TestHelper;
import BookScrabbleApp.Model.GameData.Tile;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import java.util.concurrent.*;
import java.util.function.Consumer;


public class ClientCommunicationHandler {
    PrintWriter out;
    Scanner in;
    Map<String, Consumer<String>> handlers = new HashMap<>();
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    ExecutorService executor = Executors.newFixedThreadPool(2);


    volatile boolean stop = false;


    /**
     * The ClientCommunicationHandler constructor that initializes the handler map.
     * The handler map contains all the methods that are used to handle messages from the host.
     */
    public ClientCommunicationHandler() {
        //put all the methods in the map for being able to invoke them
        handlers.put("sortAndSetIndex", message -> {
           BS_Guest_Model.getModel().sortAndSetIndex(message);
        });

        handlers.put("wordsForChallenge", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });

        handlers.put("turnPassed", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });

        handlers.put("winner", message -> {
            BS_Guest_Model.getModel().setWinner(message);
        });

        handlers.put("ping", message -> {
            String[] key = message.split(":");
            String socketID = key[1];
            BS_Guest_Model.getModel().getPlayer().set_socketID(socketID);
            outMessages("addPlayer:" + socketID + ":" + BS_Guest_Model.getModel().getPlayer().get_name());
        });

        handlers.put("hand", message -> {
            BS_Guest_Model.getModel().setHand(message);
        });

        handlers.put("tileBoard", message -> {
               BS_Guest_Model.getModel().setTileBoard(message);
        });

        handlers.put("playersScores", message -> {
            BS_Guest_Model.getModel().setPlayersScore(message);
        });

        handlers.put("challengeSuccess", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });

        handlers.put("invalidWord", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });

        handlers.put("challengeAlreadyActivated", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });

        handlers.put("playersName", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });

        handlers.put("gameStart", message -> {
            BS_Guest_Model.getModel().toFacade(message);
        });
    }

    /**
     * The handleInput function is responsible for taking input from the inputQueue and passing it to the appropriate handler.
     * The handleInput function will take a string from the queue, split it into an array of strings based on colons, and then use
     * that first element as a key to find which handler should be used.
     * If no such handler exists, then nothing happens.
     * Otherwise, if there is a valid method name in a message[0], then we call that method with the message as its argument.
     */
    public void handleInput() {
        while (!stop) {

            try {
                String key = inputQueue.take(); //blocking call
                String[] message = key.split(":");
                String methodName = message[0];
                System.out.println("GuestCom ---- updateType: " + message);
                if (handlers.get(methodName) != null) {
                  handlers.get(methodName).accept(key);
                } else {
                    System.out.println("No handler for method(Client) :" + methodName);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * The setCom function is used to set up the communication between the client and server.
     * It does this by creating a PrintWriter object called out, which is used to send messages from the client to the host.
     * The function also creates a Scanner object called in, which is used to receive messages from the host.
     * The function also creates a thread that runs the inMessages function, which is responsible for constantly checking for new messages from the host.
     * The function also creates a thread that runs the handleInput function, which is responsible for taking input from the inputQueue and passing it to the appropriate handler.
     */
    public void setCom() {
        try {
            out = new PrintWriter(BS_Guest_Model.getModel().getSocket().getOutputStream());
            in = new Scanner(BS_Guest_Model.getModel().getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executor.submit(this::inMessages);
        executor.submit(this::handleInput);
    }

    /**
     * The inMessages function is a thread that runs in the background and constantly checks for new messages from the host.
     * If there are any, it puts them into an inputQueue to be processed by another function.
     */
    public void inMessages() {
        String key = null;
        try {
            while (!stop) {
                if (in.hasNext()) {
                    key = in.next();
                }
                if (key != null)// Read an object from the server
                {
                    inputQueue.put(key); // Put the received object in the queue
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * The outMessages function is used to send a message out the hostCommunicationHandler from the client.
     * It does this by taking a string as an argument and sending it to the host.
     */
    public void outMessages(String key) {
        if (key != null) {
           out.println(key);
            out.flush();
        }
    }


    /**
     * The close function closes the connection to the server.
     * It stops all threads and shuts down the executor.
     * It also closes the input and output streams.
     */
    public void close() {
        stop = true;
        executor.shutdown();
        in.close();
        out.close();
    }
}
