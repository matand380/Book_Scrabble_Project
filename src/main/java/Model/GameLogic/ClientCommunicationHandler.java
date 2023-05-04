package Model.GameLogic;

import Model.BS_Guest_Model;
import Model.GameData.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ClientCommunicationHandler  {
    ObjectOutputStream out;
    ObjectInputStream in;

    Map<String, ObjectFactory> creatorMap = new HashMap<>();
    Map<String, Runnable> inMessagesMap = new HashMap<>();
    Map<String, String> outMessagesMap = new HashMap<>();

    public ClientCommunicationHandler() {
        try{
            out = new ObjectOutputStream(BS_Guest_Model.getModel().getSocket().getOutputStream());
            in = new ObjectInputStream(BS_Guest_Model.getModel().getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO: 04/05/2023 implement client socket connection

        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());

//        outMessagesMap.put("passTurn",  "passTurn:"+BS_Guest_Model.getModel().getPlayer()._id);

        // TODO: 04/05/2023 implement client messages to host - is it only String messages?
        inMessagesMap.put("try successful", () -> BS_Guest_Model.getModel().notifyObservers("try successful")); //think about this


    }


//    public void handleClient(InputStream inputStream, OutputStream outputStream) {
//
//        String key;
//        Board board;
//        Tile[][] boardTiles;
//        Tile.Bag bag;
//        Player player;
//        Tile tile;
//        Word word;
//        try {
//
//            Object object = in.readObject();
//            if (object instanceof String) {
//                key = (String) object;
//                inMessages(key);
//            } else if (object instanceof Tile[][]) {
//                boardTiles = (Tile[][]) object;
//                BS_Guest_Model.getModel().setBoard(boardTiles);
//
//                //rethink if this is needed
//            } else if (object instanceof Board) {
//                board = (Board) object;
//
//            } else if (object instanceof Tile.Bag) {
//                bag = (Tile.Bag) object;
//            } else if (object instanceof Player) {
//                player = (Player) object;
//            } else if (object instanceof Tile) {
//                tile = (Tile) object;
//            } else if (object instanceof Word) {
//                word = (Word) object;
//            }
//
//
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    public void inMessages() {
        String id;
        try {
             id = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(id);
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
