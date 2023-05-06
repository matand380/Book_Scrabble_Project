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
        String id;
        try {
            id = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(id); //hear me?
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
