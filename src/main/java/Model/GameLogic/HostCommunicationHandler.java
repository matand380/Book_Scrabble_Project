package Model.GameLogic;

import Model.BS_Host_Model;
import Model.GameData.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class HostCommunicationHandler implements ClientHandler {


    Map<String, Runnable> invocationMap = new HashMap<>();
    Map<String, String> outMessagesMap = new HashMap<>();
    Map<String, String> inMessagesMap = new HashMap<>();


    ObjectOutputStream out;
    ObjectInputStream in;

    public HostCommunicationHandler() {
        //methods that can be invoked by the host

        //host messages to guest
        outMessagesMap.put("isFound", "challengeFailed");
        outMessagesMap.put("isNotFound", "challengeSucceeded");
        outMessagesMap.put("isGameOver", "gameOver");

        // TODO: 04/05/2023 the host needs to send the board, bag, and player to the guest


        //guest messages to host


    }


    public String handleRequests(String key) {
        String[] message = key.split(":");
        String methodName = message[0];

        switch (methodName) {
            case "passTurn":
                int player_id = Integer.parseInt(message[1]);
                return BS_Host_Model.getModel().passTurn(player_id);
            case "addPlayer":
                Player p = new Player();
                p.set_name(message[1]);
                BS_Host_Model.getModel().addPlayer(p);
            case "tryPlaceWord":
                String word = message[2];
                int row = Integer.parseInt(message[3]);
                int col = Integer.parseInt(message[4]);
                String direction = message[5];
                boolean isHorizontal = direction.equals("horizontal");
                Tile[] tiles = new Tile[word.length()];
                for (int i = 0; i < word.length(); i++) {
                    //todo: think about this
                }
                Word w = new Word(tiles, row, col, isHorizontal);
                BS_Host_Model.getModel().tryPlaceWord(w);

        }
        if (invocationMap.containsKey(message[1])) {
            invocationMap.get(message[1]).run();
        } else inMessages(key);
        return methodName;
    }


    private void inMessages(String key) {
        // TODO: 03/05/2023 read the key. convention is player id, method name, parameters(if any). delimiter is ":"
        // TODO: 03/05/2023 example: 1:tryPlaceWord:word:row:col:direction

    }


    @Override
    public void handleClient(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        String s = null;
        try {
            s = (String) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String key = handleRequests(s);
        try {
            outputStream.writeObject(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


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

    public void outMessages(Object key) {
        if (key != null) {
            if (key instanceof String) {
                String message = (String) key;
            }
        }


    }
}


