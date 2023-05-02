package Model.GameLogic;

import Model.GameData.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class CommunicationHandler implements ClientHandler {



    Map<String,ObjectFactory > creatorMap = new HashMap<>();
    public CommunicationHandler() {
        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player("Player"));

    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        try {
            ObjectInputStream in = new ObjectInputStream(inFromclient);
            ObjectOutputStream out = new ObjectOutputStream(outToClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Object getInstance(String key){
        if (creatorMap.containsKey(key)) {
            return creatorMap.get(key).create();
        }
        return null;
    }


    @Override
    public void close() {

    }


    private interface Creator<Serializable> {
        Serializable create();
    }
}
