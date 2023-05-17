package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.*;

import com.google.gson.Gson;

import java.util.concurrent.*;
import java.util.function.Function;


public class ClientCommunicationHandler {
    PrintWriter out;
    Scanner in;
    Map<String, Function<String[], String>> handlers = new HashMap<>();
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

    volatile boolean stop = false;


    public ClientCommunicationHandler() {

        //put all the methods in the map for being able to invoke them
        handlers.put("sortAndSetIndex", (message) -> {
            int size = Integer.parseInt(message[1]);
            BS_Guest_Model.getModel().playersScores = new String[size];
            for (int i = 0; i < size; i++) {
                String[] player = message[i + 2].split(",");
                if (player[1].equals(BS_Guest_Model.getModel().getPlayer().get_socketID())) {
                    BS_Guest_Model.getModel().getPlayer().set_index(Integer.parseInt(player[0]));
                    BS_Guest_Model.getModel().hasChanged();
                    BS_Guest_Model.getModel().notifyObservers("sortAndSetIndex:" + BS_Guest_Model.getModel().getPlayer().get_index());
                }
            }
            return "";
        });
        handlers.put("wordsForChallenge", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
            return "";
        });
        handlers.put("passTurn", (message) -> {
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers(message);
            return "";
        });
        handlers.put("winner", (message) -> {
            int index = Integer.parseInt(message[1]);
            String winnerName = message[2];
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("winner:" + BS_Guest_Model.getModel().playersScores[index] + winnerName);
            return "";
        });
        handlers.put("ping", (message) -> {
            String socketID = message[1];
            BS_Guest_Model.getModel().getPlayer().set_socketID(socketID);
            outMessages("addPlayer:" + socketID + ":" + BS_Guest_Model.getModel().getPlayer().get_name());
            return "";
        });

        handlers.put("hand", (message) -> {
            String hand = message[1];
            Gson gson = new Gson();
            Tile[] newTiles = gson.fromJson(hand, Tile[].class);
            List<Tile> newHand = Arrays.asList(newTiles);
            BS_Guest_Model.getModel().getPlayer().updateHand(newHand);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("hand updated");
            return "";
        });

        handlers.put("tileBoard", (message) -> {
            String tiles = message[1];
            Gson gson = new Gson();
            Tile[][] newTiles = gson.fromJson(tiles, Tile[][].class);
            BS_Guest_Model.getModel().setBoard(newTiles);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("tileBoard updated");
            return "";
        });

        handlers.put("playersScores", (message) -> {
            String scores = message[1];
            Gson gson = new Gson();
            String[] newScores = gson.fromJson(scores, String[].class);
            BS_Guest_Model.getModel().setPlayersScores(newScores);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("playersScores updated");
            return "";
        });

    }

    public void handleInput() {
        while (!stop) {

            try {
                String key = inputQueue.take();
                String[] message = key.split(":");
                String methodName = message[0];
                if (handlers.get(methodName) != null) {
                    handlers.get(methodName).apply(message);
                } else {
                    System.out.println("No handler for method " + methodName);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    public void setCom() {
        try {
            out = new PrintWriter(BS_Guest_Model.getModel().getSocket().getOutputStream());
            in = new Scanner(BS_Guest_Model.getModel().getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // FIXME: 15/05/2023 check this again
        executor.submit(this::inMessages);
        executor.submit(this::handleInput);


    }

    public void inMessages() {
            String key = null;
            try {
                while (!stop) {
                    if (in.hasNext()) {
                        key = in.next();
                    }
                    if (key!=null)// Read an object from the server
                    {
                    inputQueue.put(key); // Put the received object in the queue
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//        String key = null;
//            try {
//                key = (String) in.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (key == null) {
//                return;
//            }
//            handleInput(key);
//        }
    }

    public void outMessages(String key) {
        if (key != null) {
            out.println(key);
            out.flush();
        }
        }


    public void close() {
        in.close();
        out.close();
    }
}
