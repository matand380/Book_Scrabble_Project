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
//        invocationMap.put("passTurn", () -> BS_Host_Model.getModel().passTurn());
        invocationMap.put("tryPlaceWord", () -> BS_Host_Model.getModel().tryPlaceWord());
        invocationMap.put("challengeWord", () -> BS_Host_Model.getModel().challengeWord());


        //host messages to guest
        outMessagesMap.put("isFound", "challengeFailed");
        outMessagesMap.put("isNotFound", "challengeSucceeded");
        outMessagesMap.put("isGameOver", "gameOver");

        // TODO: 04/05/2023 the host needs to send the board, bag, and player to the guest


        //guest messages to host


    }

    @Override
    public void handleIn(InputStream inputStream){
        handleRequests(inputStream);
    }

    @Override
    public void handleOut(OutputStream outputStream) {
        try {
            out = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void handleResponses(String s) {

    }

    public void handleRequests(InputStream inputStream) {
        String key;
        try {
            in = new ObjectInputStream(inputStream);
            Object object = in.readObject();
            if (object instanceof String) {
                key = (String) object;
                String[] message = key.split(":");
                String methodName = message[0];
                int player_id = Integer.parseInt(message[1]);

                switch (methodName) {
                    case "passTurn":
                         BS_Host_Model.getModel().passTurn(player_id);
                    case "try":


                }
                if (invocationMap.containsKey(message[1])) {
                    invocationMap.get(message[1]).run();
                } else inMessages(key);
            }


        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

    }


    private void inMessages(String key) {
        // TODO: 03/05/2023 read the key. convention is player id, method name, parameters(if any). delimiter is ":"
        // TODO: 03/05/2023 example: 1:tryPlaceWord:word:row:col:direction

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


