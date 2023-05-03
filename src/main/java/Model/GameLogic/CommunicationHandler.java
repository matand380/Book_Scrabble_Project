package Model.GameLogic;

import Model.BS_Host_Model;
import Model.GameData.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class CommunicationHandler implements ClientHandler {


    Map<String, Runnable> inMessages = new HashMap<>();
    Map<String, String> outMessages = new HashMap<>();

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
        inMessages.put("passTurn", () -> BS_Host_Model.getModel().passTurn());
        inMessages.put("tryPlaceWord", () -> BS_Host_Model.getModel().tryPlaceWord());
        inMessages.put("challengeWord", () -> BS_Host_Model.getModel().challengeWord());

        outMessages.put("isFound", "challengeFailed");
        outMessages.put("isNotFound", "challengeSucceeded");
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
                inMessages.get(key).run();
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

    public Object getInstance(String key) {
        if (creatorMap.containsKey(key)) {
            return creatorMap.get(key).create();
        }
        return null;
    }

    @Override
    public void close() {

    }

    public void outMessages(String key) {
        if (key != null) {
            try {
                out.writeObject(outMessages.get(key));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void inMassages(String key) {
        inMessages.get(key).run();

    }


}


