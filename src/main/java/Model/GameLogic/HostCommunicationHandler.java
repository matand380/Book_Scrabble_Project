package Model.GameLogic;

import Model.BS_Host_Model;
import Model.GameData.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;


public class HostCommunicationHandler implements ClientHandler {
    Map<String, Consumer<String[]>> handlers = new HashMap<>();
    PrintWriter out;
    Scanner in;
    PrintWriter toGameServer;
    Scanner fromGameServer;
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    public ExecutorService executor = Executors.newFixedThreadPool(3);


    public HostCommunicationHandler() {
        //put all the methods in the map for being able to invoke them in handleRequests
        handlers.put("passTurn", (message) -> {
            BS_Host_Model.getModel().passTurn(Integer.parseInt(message[1]));
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
        });

        handlers.put("tryPlaceWord", (message) -> {
            String PlayerIndex = message[1];
            String word = message[2];
            int row = Integer.parseInt(message[3]);
            int col = Integer.parseInt(message[4]);
            boolean direction = message[5].equals("true");
            char[] buildWord = word.toCharArray();
            Player current = BS_Host_Model.getModel().getPlayers().stream().filter(p1 -> p1.get_index() == Integer.parseInt(PlayerIndex)).findFirst().get();
            Tile[] tiles = new Tile[word.length()];
            for (int i = 0; i < word.length(); i++) {
                tiles[i] = current.charToTile(buildWord[i]);
            }
            Word w = new Word(tiles, row, col, direction);
            BS_Host_Model.getModel().tryPlaceWord(w);
        });

        handlers.put("challengeWord", (message) -> {
            String PlayerIndex = message[1];
            String word = message[2];
            boolean exist = BS_Host_Model.getModel().currentPlayerWords.stream().anyMatch((w1) -> w1.toString().equals(word));
            if (exist) {
                String challengeInfo = PlayerIndex + ":" + word;
                BS_Host_Model.getModel().requestChallengeActivation(challengeInfo);
//                BS_Host_Model.getModel().challengeWord(word, PlayerIndex);
            }
            else
            {
                BS_Host_Model.getModel().hostLogger.log(System.Logger.Level.ERROR,"Player " + PlayerIndex + " tried to challenge a word that doesn't exist");
            }
            // TODO: 06/05/2023 handle challengeWord case
        });

        handlers.put("endGame", (message) -> {
            String id = message[1];
            try {
                BS_Host_Model.getModel().getCommunicationServer().clientsMap.get(id).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executor.submit(this::handleRequests);
        executor.submit(this::messagesFromGameServer);
    }


    public void handleRequests() {
        while (BS_Host_Model.getModel().getCommunicationServer().isRunning()) {

            try {
                String key = inputQueue.take(); //blocking call
                String[] message = key.split(":");
                String methodName = message[0];
                if (handlers.get(methodName) != null) {
                    handlers.get(methodName).accept(message);
                } else {
                    System.out.println("No handler for method " + methodName);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        while (BS_Host_Model.getModel().getCommunicationServer().isRunning()) {
            in = new Scanner(inputStream);
            out = new PrintWriter(outputStream);
            String key = null;
            try {
                if (in.hasNext()) {
                    key = in.next();
                }
                if (key != null)// Read an object from the server
                {
                    inputQueue.put(key); // Put the received object in the queue
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        in.close();
        out.close();
        toGameServer.close();
        fromGameServer.close();

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
            key = fromGameServer.next();
            return key;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}





