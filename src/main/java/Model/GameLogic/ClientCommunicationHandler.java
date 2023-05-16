package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;


public class ClientCommunicationHandler {
    ObjectOutputStream out;
    ObjectInputStream in;
    Map<String, Function<String[], String>> handlers = new HashMap<>();

    volatile boolean stop = false;


    public ClientCommunicationHandler() {

        //put all the methods in the map for being able to invoke them in handleRequests
        handlers.put("tryPlaceWord", (message) -> {
            int index = Integer.parseInt(message[1]);
            String score = message[2];
            if (score.equals("0")) {
                if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                    BS_Guest_Model.getModel().hasChanged();
                    BS_Guest_Model.getModel().notifyObservers("tryPlaceWord:" + index + ":" + "0"); // FIXME: 16/05/2023 move to playerScore
                }
            } else {
                if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                    BS_Guest_Model.getModel().getPlayer().set_score(BS_Guest_Model.getModel().getPlayer().get_score() + Integer.parseInt(score));// FIXME: 16/05/2023 move to playerScore
                }
                BS_Guest_Model.getModel().setPlayerScore(index, score); //FIXME: 16/05/2023 move to playerScore
            }
            return "";
        });
        handlers.put("challengeWord", (message) -> {
            int index = Integer.parseInt(message[1]);
            String score = message[2];
            BS_Guest_Model.getModel().playersScores[index] += score; //FIXME: 16/05/2023 move to playerScore
            if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                BS_Guest_Model.getModel().getPlayer().set_score(BS_Guest_Model.getModel().getPlayer().get_score() + Integer.parseInt(score)); //FIXME: 16/05/2023 move to playerScore
            }
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("challengeWord:" + index + ":" + message[2]); //FIXME: 16/05/2023 move to playerScore
            return "";
        });
        handlers.put("sortAndSetIndex", (message) -> {
            int index = Integer.parseInt(message[1]);
            int sizeSort = Integer.parseInt(message[2]);
            BS_Guest_Model.getModel().playersScores = new String[sizeSort];
            for (int i = 0; i < sizeSort; i++) {
                String[] player = message[i + 2].split(",");
                if (player[i].equals(BS_Guest_Model.getModel().getPlayer().get_socketID())) {
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
        handlers.put("gameOver", (message) -> {
            int index = Integer.parseInt(message[1]);
            String winnerName = message[2];
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("gameOver:" + BS_Guest_Model.getModel().playersScores[index] + winnerName);
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
            return "";
            // TODO: 16/05/2023 hasChanged() and notifyObservers() in what convention?
        });

        handlers.put("tileBoard", (message) -> {
            String tiles = message[1];
            Gson gson = new Gson();
            Tile[][] newTiles = gson.fromJson(tiles, Tile[][].class);
            BS_Guest_Model.getModel().setBoard(newTiles);
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("tileBoard updated");
            // TODO: 16/05/2023 hasChanged() and notifyObservers() in what convention?
            // FIXME: 15/05/2023 need to implement this on host side
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
            // TODO: 16/05/2023 hasChanged() and notifyObservers() in what convention?
            // FIXME: 15/05/2023 need to implement this on host side
        });



        try {
            out = new ObjectOutputStream(BS_Guest_Model.getModel().getSocket().getOutputStream());
            in = new ObjectInputStream(BS_Guest_Model.getModel().getSocket().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCom() {
//        new Thread(() -> {
//            try {
//                out = new ObjectOutputStream(BS_Guest_Model.getModel().getSocket().getOutputStream());
//                in = new ObjectInputStream(BS_Guest_Model.getModel().getSocket().getInputStream());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            while (!stop) {
//                inMessages(in);
//            }
//    }).start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (!stop) {
                inMessages();
            }
            // FIXME: 15/05/2023 check this again
        });

    }


    public void inMessages() {

        String key = null;
        try {
            Object inObject = in.readObject();
            if (inObject instanceof String) {
                key = (String) inObject;
            }
            if (inObject instanceof Tile[][]) {
                BS_Guest_Model.getModel().setBoard((Tile[][]) inObject);

            }

        } catch (IOException | ClassNotFoundException e) {

        }

        System.out.println("Received message: " + key);
        String[] message = key.split(":");
        String methodName = message[0];
        if (handlers.get(methodName) != null) {
            handlers.get(methodName).apply(message);
        } else {
            System.out.println("No handler for method " + methodName);
        }

    }

    public void outMessages(String key) {
        if (key != null) {
            try {
                out.writeObject(key); // implement client validation tests
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void outObjects(Object key) {
        if (key != null) {
            try {
                if (key instanceof Tile[][]) {
                    Tile[][] tiles = (Tile[][]) key;
                    out.writeObject(tiles); // implement client validation tests
                } else if (key instanceof Player) {
                    Player player = (Player) key;
                    out.writeObject(player); // implement client validation tests
                } else if (key instanceof Word) {
                    Word word = (Word) key;
                    out.writeObject(word); // implement client validation tests
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
