package Model.GameLogic;

import Model.BS_Host_Model;
import Model.BS_Model;
import Model.GameData.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class CommunicationHandler implements ClientHandler {

    Map<String,ObjectFactory > creatorMap = new HashMap<>();
    Map<String,String> inMessages = new HashMap<>();
    Map<String,String> outMessages = new HashMap<>();

    ObjectOutputStream outToServer;
    ObjectInputStream inFromClient;

    public CommunicationHandler() {
        creatorMap.put("Board", Board.getBoard());
        creatorMap.put("Bag", Tile.Bag.getBag());
        creatorMap.put("Player", new Player());
        creatorMap.put("Tile", new Tile());
        creatorMap.put("Word", new Word());
        inMessages.put("passTurn", "passTurn");

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
    public void outMessages(String key){
        if(key != null){
            try{
                outToServer.writeObject(key);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        else {
            throw new RuntimeException("No such key");
        }
    }
    public void inMassages(String key){

    }

}
