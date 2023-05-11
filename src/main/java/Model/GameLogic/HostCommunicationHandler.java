package Model.GameLogic;

import Model.BS_Host_Model;
import Model.GameData.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.function.Function;


public class HostCommunicationHandler implements ClientHandler {
    Map<String, Function<String[], String>> handlers = new HashMap<>();
    ObjectOutputStream out;
    ObjectInputStream in;
    PrintWriter toGameServer;
    Scanner fromGameServer;

    public HostCommunicationHandler() {
        //put all the methods in the map for being able to invoke them in handleRequests
        handlers.put("passTurn", (message) -> {
            BS_Host_Model.getModel().passTurn(Integer.parseInt(message[1]));
            return "";
        });
        handlers.put("addPlayer", (message) -> {
            Player p = new Player();
            p.set_name(message[1]);
            p.setTileLottery();
            p.set_socketID(BS_Host_Model.getModel().getPlayerToSocketID().get(message[1]));
            BS_Host_Model.getModel().addPlayer(p);
            return "";
        });

        handlers.put("tryPlaceWord", (message) -> {
            String PlayerIndex = message[1];
            String word = message[2];
            int row = Integer.parseInt(message[3]);
            int col = Integer.parseInt(message[4]);
            boolean direction = message[5].equals("isVertical");
            char[] buildWord = word.toCharArray();
            Player current = BS_Host_Model.getModel().getPlayers().stream().filter(p1 -> p1.get_index() == Integer.parseInt(PlayerIndex)).findFirst().get();
            Tile[] tiles = new Tile[word.length()];
            for (int i = 0; i < word.length(); i++) {
                tiles[i] = current.charToTile(buildWord[i]);
            }
            Word w = new Word(tiles, row, col, direction);
            BS_Host_Model.getModel().tryPlaceWord(w);
            return "";
        });

        handlers.put("challengeWord", (message) -> {
            String PlayerIndex = message[1];
            String word = message[2];
            boolean exist = BS_Host_Model.getModel().currentPlayerWords.stream().anyMatch((w1) -> w1.toString().equals(word));
            if (exist) {
                String response =  (String) BS_Host_Model.getModel().challengeWord(word, PlayerIndex);
                return "challengeWord:" + PlayerIndex + ":" + response;
            }
            // TODO: 06/05/2023 handle challengeWord case
            return "";
        });

        handlers.put("ping", (message) -> {
            BS_Host_Model.getModel().getPlayerToSocketID().put(message[2],message[1]);
            // TODO: 11/05/2023 think about what to do with identical names
            System.out.println(message[1]+","+message[2]);
            return "";
        });


    }


    public String handleRequests(String key) {
       /* String[] message = key.split(":");
        String methodName = message[0];

        switch (methodName) {
            case "passTurn":
                int player_index = Integer.parseInt(message[1]);
                return BS_Host_Model.getModel().passTurn(player_index);
            case "addPlayer":
                Player p = new Player();
                p.set_name(message[1]);
                BS_Host_Model.getModel().addPlayer(p);
            case "tryPlaceWord":
                String playerIndex = message[1];
                String word = message[2];
                int row = Integer.parseInt(message[3]);
                int col = Integer.parseInt(message[4]);
                boolean direction = message[5].equals("isVertical");
                char[] buildWord = word.toCharArray();
                Player current = BS_Host_Model.getModel().getPlayers().stream().filter(p1 -> p1.get_index() == Integer.parseInt(playerIndex)).findFirst().get();
                Tile[] tiles = new Tile[word.length()];
                for (int i = 0; i < word.length(); i++) {
                    tiles[i] = current.charToTile(buildWord[i]);
                }
                Word w = new Word(tiles, row, col, direction);
                BS_Host_Model.getModel().tryPlaceWord(w);
            case "challengeWord":
                //get from guest challengeWord:playerIndex:word (word is the word that the player wants to challenge)


        }
        return methodName;*/
        String[] message = key.split(":");
        String methodName = message[0];
        Function<String[], String> handler = handlers.get(methodName);
        if (handler != null) {
            return handler.apply(message);
        } else {
            // handle when unknown method is called
            return "";
        }
    }



    @Override
    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        try {
            in = new ObjectInputStream(inputStream);
            out = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String s = null;
        try {
            s = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String key = handleRequests(s);
        if (key.equals(""))
            return;
        if (key.startsWith("challengeWord")) {
            try {
                out.writeObject(key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    @Override
    public void close() { // FIXME: 09/05/2023 closes the wrong streams
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void messagesToGameServer(String key) {
        if (key != null) {
            try{
                toGameServer = new PrintWriter(BS_Host_Model.getModel().getGameSocket().getOutputStream());
                toGameServer.println(key);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String messagesFromGameServer() {
            try{
                fromGameServer = new Scanner(BS_Host_Model.getModel().getGameSocket().getInputStream());
                String key = null;
                while (fromGameServer.hasNextLine()) {
                     key = fromGameServer.nextLine();
                }
                return key;
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }}





