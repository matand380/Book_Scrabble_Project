package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientCommunicationHandler {
    ObjectOutputStream out;
    ObjectInputStream in;
    Map<String, ObjectFactory> creatorMap = new HashMap<>();
    public ClientCommunicationHandler() {
        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());
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
            Object o = in.readObject();
            if (o instanceof String) {
                key = (String) o;
            }
            if (o instanceof Tile[][]) {
                BS_Guest_Model.getModel().setBoard((Tile[][]) o);
                return;
            }
        } catch (IOException | ClassNotFoundException e) {
            return;
        }
        String[] keyArray = key.split(":");
        String id = keyArray[1];
        switch (keyArray[0]) {
            case "tryPlaceWord":
                String score = keyArray[2];
                if (score.equals("0")) {
                    if (Integer.parseInt(id) == BS_Guest_Model.getModel().getPlayer()._id) {
                        BS_Guest_Model.getModel().hasChanged();
                        BS_Guest_Model.getModel().notifyObservers("tryPlaceWord:" + id + ":" + "0");
                    }
                } else {
                    BS_Guest_Model.getModel().setPlayersScores(id, score);
                }
            case "sortAndSetID":
                String[] players = key.split(":");
                int sizeSort = Integer.parseInt(players[1]);
                BS_Guest_Model.getModel().playersScores = new String[sizeSort];
                for (int i = 0; i < sizeSort; i++) {
                    String[] player = players[i + 2].split(",");
                    if (player[1].equals(BS_Guest_Model.getModel().getPlayer().get_name())) {
                        BS_Guest_Model.getModel().getPlayer().set_id(Integer.parseInt(player[0]));
                        BS_Guest_Model.getModel().hasChanged();
                        BS_Guest_Model.getModel().notifyObservers("sortAndSetID:" + BS_Guest_Model.getModel().getPlayer().get_id());
                    }
                }
            case "challengeWord":
                BS_Guest_Model.getModel().playersScores[Integer.parseInt(id)] = keyArray[2];
                if (Integer.parseInt(id) == BS_Guest_Model.getModel().getPlayer().get_id()) {
                    BS_Guest_Model.getModel().getPlayer().set_score(Integer.parseInt(keyArray[2]));
                }
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers("challengeWord:" + id + ":" + keyArray[2]);

            case "wordsForChallenge":
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers(key);
                break;
            case "passTurn":
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers(key);
                break;
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
