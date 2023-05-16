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
            p.set_socketID(message[1]);
            p.set_name(message[2]);
            BS_Host_Model.getModel().getPlayerToSocketID().put(p.get_name(), p.get_socketID());
            p.setTileLottery();
            BS_Host_Model.getModel().addPlayer(p);
            BS_Host_Model.getModel().hasChanged();
            BS_Host_Model.getModel().notifyObservers("playersListSize:" + BS_Host_Model.getModel().getPlayers().size());
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
                BS_Host_Model.getModel().challengeWord(word, PlayerIndex);
            }
            // TODO: 06/05/2023 handle challengeWord case
            return "";
        });

        handlers.put("endGame", (message) -> {
            String id = message[1];
            try {
                BS_Host_Model.getModel().getCommunicationServer().clientsMap.get(id).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        });


    }


    public String handleRequests(String key) {

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
            toGameServer.close();
            fromGameServer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void messagesToGameServer(String key) {
        if (key != null) {
            try {
                toGameServer = new PrintWriter(BS_Host_Model.getModel().getGameSocket().getOutputStream());
                toGameServer.println(key);
                toGameServer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String messagesFromGameServer() {
        try {
            fromGameServer = new Scanner(BS_Host_Model.getModel().getGameSocket().getInputStream());
            String key = null;
            StringBuilder sb = new StringBuilder();
            key = fromGameServer.next();
            return key;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}





