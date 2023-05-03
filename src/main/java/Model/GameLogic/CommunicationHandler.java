package Model.GameLogic;

import Model.BS_Host_Model;
import Model.GameData.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class CommunicationHandler implements ClientHandler {


    Map<String, Runnable> inMessagesMap = new HashMap<>();
    Map<String, String> outMessagesMap = new HashMap<>();

    Map<String, ObjectFactory> creatorMap = new HashMap<>();

    ObjectOutputStream out;
    ObjectInputStream in;

    public CommunicationHandler() {
        //guest messages
        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());
        inMessagesMap.put("passTurn", () -> BS_Host_Model.getModel().passTurn());
        inMessagesMap.put("tryPlaceWord", () -> BS_Host_Model.getModel().tryPlaceWord());
        inMessagesMap.put("challengeWord", () -> BS_Host_Model.getModel().challengeWord());

        outMessagesMap.put("isFound", "challengeFailed");
        outMessagesMap.put("isNotFound", "challengeSucceeded");
    }

    @Override
    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        String key;
        Board board;
        Tile[][] boardTiles;
        Tile.Bag bag;
        Player player;
        Tile tile;
        Word word;
        try {
            in = new ObjectInputStream(inputStream);
            Object object = in.readObject();
            if (object instanceof String) {
                key = (String) object;
                inMessages(key);
            } else if (object instanceof Tile[][]) {
                boardTiles = (Tile[][]) object;
            } else if (object instanceof Board) {
                board = (Board) object;
            } else if (object instanceof Tile.Bag) {
                bag = (Tile.Bag) object;
            } else if (object instanceof Player) {
                player = (Player) object;
            } else if (object instanceof Tile) {
                tile = (Tile) object;
            } else if (object instanceof Word) {
                word = (Word) object;
            }

            out = new ObjectOutputStream(outputStream);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void inMessages(String key) {
        // TODO: 03/05/2023 read the key. convention is player id, method name, parameters(if any). delimiter is ","
        // TODO: 03/05/2023 example: 1,tryPlaceWord, word, x, y, direction

        if (inMessagesMap.containsKey(key)) {
            inMessagesMap.get(key).run();
        }
    }

    public Object getInstance(String key) {
        if (creatorMap.containsKey(key)) {
            return creatorMap.get(key).create();
        }
        return null;
    }

    @Override
    public void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void outMessages(String key) {
        if (key != null) {
            try {
                out.writeObject(outMessagesMap.get(key));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



}


