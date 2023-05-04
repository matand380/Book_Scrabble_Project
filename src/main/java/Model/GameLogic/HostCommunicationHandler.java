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
        invocationMap.put("passTurn", () -> BS_Host_Model.getModel().passTurn());
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
    public void handleClient(InputStream inputStream, OutputStream outputStream) {

    }

    private void inMessages(String key) {
        // TODO: 03/05/2023 read the key. convention is player id, method name, parameters(if any). delimiter is ","
        // TODO: 03/05/2023 example: 1,tryPlaceWord, word, x, y, direction

        if (invocationMap.containsKey(key)) {
            invocationMap.get(key).run();
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


