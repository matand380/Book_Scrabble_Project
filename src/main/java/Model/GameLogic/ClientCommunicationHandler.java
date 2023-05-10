package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ClientCommunicationHandler {
    ObjectOutputStream out;
    ObjectInputStream in;

    Map<String, ObjectFactory> creatorMap = new HashMap<>();
    Map<String, Runnable> inMessagesMap = new HashMap<>();
    Map<String, String> outMessagesMap = new HashMap<>();

    public ClientCommunicationHandler() {

        // TODO: 04/05/2023 implement client socket connection

        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());

        // TODO: 04/05/2023 implement client messages to host - is it only String messages?
        inMessagesMap.put("try successful", () -> BS_Guest_Model.getModel().notifyObservers("try successful")); //think about this


    }

    public void setCom() {
        try {
            out = new ObjectOutputStream(BS_Guest_Model.getModel().getSocket().getOutputStream());
            in = new ObjectInputStream(BS_Guest_Model.getModel().getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void inMessages() {
        String key;
        try {
            key = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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
            case "challenge":

            case "passTurn":
                BS_Guest_Model.getModel().hasChanged();
                BS_Guest_Model.getModel().notifyObservers("passTurn:" + id);
            case "sortAndSetID":
                String[] players = key.split(":");
                int size = Integer.parseInt(players[1]);
                for (int i = 0; i < size; i++) {
                    String[] player = players[i + 2].split(",");
                    if(player[1].equals(BS_Guest_Model.getModel().getPlayer().get_name())){
                        BS_Guest_Model.getModel().getPlayer().set_id(Integer.parseInt(player[0]));
                    }
                }


        }
        // TODO: 03/05/2023 read the key. convention is player id, method name, parameters(if any). delimiter is ","
        // TODO: 03/05/2023 example: 1,tryPlaceWord, word, x, y, direction

//        if (inMessagesMap.containsKey(key)) {
//            inMessagesMap.get(key).run();
//        }
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
