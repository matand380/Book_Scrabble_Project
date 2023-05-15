package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                    BS_Guest_Model.getModel().notifyObservers("tryPlaceWord:" + index + ":" + "0");
                }
            } else {
                if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                    BS_Guest_Model.getModel().getPlayer().set_score(BS_Guest_Model.getModel().getPlayer().get_score() + Integer.parseInt(score));
                }
                BS_Guest_Model.getModel().setPlayersScores(index, score);
            }
            return "";
        });
        handlers.put("challengeWord", (message) -> {
            int index = Integer.parseInt(message[1]);
            String score = message[2];
            BS_Guest_Model.getModel().playersScores[index] += score;
            if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                BS_Guest_Model.getModel().getPlayer().set_score(BS_Guest_Model.getModel().getPlayer().get_score() + Integer.parseInt(score));
            }
            BS_Guest_Model.getModel().hasChanged();
            BS_Guest_Model.getModel().notifyObservers("challengeWord:" + index + ":" + message[2]);
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
        Thread inMessageThread = new Thread(this::inMessages);
        inMessageThread.start();


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
            if (inObject instanceof Tile[]) {
                List<Tile> newHand = Arrays.asList((Tile[]) inObject);
                BS_Guest_Model.getModel().getPlayer().updateHand(newHand);
                // FIXME: 14/05/2023 need to check again casting to List<Tile> and its communication flow

                // TODO: 13/05/2023 hasChanged() and notifyObservers()
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
