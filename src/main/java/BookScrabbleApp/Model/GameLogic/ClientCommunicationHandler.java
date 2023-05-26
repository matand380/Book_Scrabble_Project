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
        handlers.put("sortAndSetIndex", (message) -> {
            String[] key = message.split(":");
            int size = Integer.parseInt(key[1]);
            BS_Guest_Model.getModel().playersScores = new String[size];
            for (int i = 0; i < size; i++) {
                String[] player = key[i + 2].split(",");
                if (player[1].equals(BS_Guest_Model.getModel().getPlayer().get_socketID())) {
                    BS_Guest_Model.getModel().getPlayer().set_index(Integer.parseInt(player[0]));
                    BS_Guest_Model.getModel().hasChanged();
                    BS_Guest_Model.getModel().notifyObservers("sortAndSetIndex:" + BS_Guest_Model.getModel().getPlayer().get_index());
                }
            }
        });

        handlers.put("wordsForChallenge", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
        });

        handlers.put("passTurn", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
        });

        handlers.put("winner", (message) -> {
            String[] key = message.split(":");
            int index = Integer.parseInt(key[1]);
            String winnerName = key[2];
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("winner:" + BS_Guest_Model.getModel().playersScores[index] + winnerName);
        });

        handlers.put("ping", (message) -> {
            String[] key = message.split(":");
            String socketID = key[1];
            BS_Guest_Model.getModel().getPlayer().set_socketID(socketID);
            outMessages("addPlayer:" + socketID + ":" + BS_Guest_Model.getModel().getPlayer().get_name());
        });

        handlers.put("hand", (message) -> {
            String hand = message.substring(5);
            Gson gson = new Gson();
            Tile[] newTiles = gson.fromJson(hand, Tile[].class);
            List<Tile> newHand = Arrays.asList(newTiles);
            BS_Guest_Model.getModel().getPlayer().updateHand(newHand);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("hand updated");
        });

        handlers.put("tileBoard", (message) -> {
            String tiles = message.substring(10);
            Gson gson = new Gson();
            Tile[][] newTiles = gson.fromJson(tiles, Tile[][].class);
            //for testing
            System.out.println("client tileBoard:\n");
            System.out.println(TestHelper.formatTiles(newTiles));
            BS_Guest_Model.getModel().setBoard(newTiles);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("tileBoard updated");
        });

        handlers.put("playersScores", (message) -> {
            String scores = message.substring(14);
            Gson gson = new Gson();
            String[] newScores = gson.fromJson(scores, String[].class);
            BS_Guest_Model.getModel().setPlayersScores(newScores);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("playersScores updated");
        });

        handlers.put("challengeSuccess", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
        });

        handlers.put("invalidWord", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
        });

        handlers.put("challengeAlreadyActivated", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
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
