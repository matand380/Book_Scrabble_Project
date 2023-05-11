package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientCommunicationHandler {
    ObjectOutputStream out;
    ObjectInputStream in;
    Map<String, ObjectFactory> creatorMap = new HashMap<>();
    Map<String, Function<String[], String>> handlers = new HashMap<>();

    public ClientCommunicationHandler() {
        //put all the classes in the map for being able to create them in handleRequests
        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());

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
                BS_Guest_Model.getModel().setPlayersScores(index, score);
            }
            return "";
        });
        handlers.put("challengeWord", (message) -> {
            int index = Integer.parseInt(message[1]);
            BS_Guest_Model.getModel().playersScores[index] = message[2];
            if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                BS_Guest_Model.getModel().getPlayer().set_score(Integer.parseInt(message[2]));
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
                if (player[1].equals(BS_Guest_Model.getModel().getPlayer().get_name())) {
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
            outMessages("ping:" + socketID + ":" + BS_Guest_Model.getModel().getPlayer().get_name());
            return "";
        });
    }

    public void setCom() {
        try {
            out = new ObjectOutputStream(BS_Guest_Model.getModel().getSocket().getOutputStream());
            in = new ObjectInputStream(BS_Guest_Model.getModel().getSocket().getInputStream());
            inMessages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                return;
            }
        } catch (IOException | ClassNotFoundException e) {
            return;
        }
        String[] message = key.split(":");
        String methodName = message[0];
        Function<String[], String> handler = handlers.get(methodName);
        if (handler != null) {
            handler.apply(message);
        } else {
            System.out.println("No handler for method " + methodName);
        }
        /*
        String[] keyArray = key.split(":");
        int index = Integer.parseInt(keyArray[1]);
        switch (keyArray[0]) {
            case "tryPlaceWord"://v
                String score = keyArray[2];
                if (score.equals("0")) {
                    if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                        BS_Guest_Model.getModel().hasChanged();
                        BS_Guest_Model.getModel().notifyObservers("tryPlaceWord:" + index + ":" + "0");
                    }
                } else {
                    BS_Guest_Model.getModel().setPlayersScores(index, score);
                }
            case "sortAndSetIndex"://v
                String[] players = key.split(":");
                int sizeSort = Integer.parseInt(players[1]);
                BS_Guest_Model.getModel().playersScores = new String[sizeSort];
                for (int i = 0; i < sizeSort; i++) {
                    String[] player = players[i + 2].split(",");
                    if (player[1].equals(BS_Guest_Model.getModel().getPlayer().get_name())) {
                        BS_Guest_Model.getModel().getPlayer().set_index(Integer.parseInt(player[0]));
                        BS_Guest_Model.getModel().hasChanged();
                        BS_Guest_Model.getModel().notifyObservers("sortAndSetIndex:" + BS_Guest_Model.getModel().getPlayer().get_index());
                    }
                }
            case "challengeWord"://v
                BS_Guest_Model.getModel().playersScores[index] = keyArray[2];
                if (index == BS_Guest_Model.getModel().getPlayer().get_index()) {
                    BS_Guest_Model.getModel().getPlayer().set_score(Integer.parseInt(keyArray[2]));
                }
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers("challengeWord:" + index + ":" + keyArray[2]);
            case "gameOver"://v
                String winnerName = keyArray[2];
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers("gameOver:"+ BS_Guest_Model.getModel().playersScores[index] + winnerName);
            case "wordsForChallenge":
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers(key);
                break;
            case "passTurn":
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers(key);
                break;
            case "ping":
                outMessages("ping:"+index+":"+BS_Guest_Model.getModel().getPlayer().get_name());
        }*/
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

    public Object getInstance(String key) {
        if (creatorMap.containsKey(key)) {
            return creatorMap.get(key).create();
        }
        return null;
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
