package Model.GameLogic;
import Model.BS_Guest_Model;
import Model.GameData.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ClientCommunicationHandler implements ClientHandler {
    ObjectOutputStream out;
    ObjectInputStream in;

    Map<String, ObjectFactory> creatorMap = new HashMap<>();
    Map<String, Runnable> inMessagesMap = new HashMap<>();
    Map<String, String> outMessagesMap = new HashMap<>();

    public ClientCommunicationHandler(){
        // TODO: 04/05/2023 implement client socket connection


        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());


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
    public void outMessages(String key) {
        if (key != null) {
            try {
                out.writeObject(outMessagesMap.get(key));
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

    @Override
    public void close() {

    }
}
