package Model;

import Model.GameData.*;
import Model.GameLogic.ClientCommunicationHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;
import java.util.regex.Pattern;

public class BS_Guest_Model extends Observable implements BS_Model {
    private static BS_Guest_Model model_instance = null;
    public String[] playersScores;
    Socket socket;
    Tile[][] tileBoard;
    Tile[][] tempBoard;
    Player player;
    ClientCommunicationHandler communicationHandler;
    private BS_Guest_Model() {
        player = new Player();
        playersScores = new String[0];
    }

    public static BS_Guest_Model getModel() {
        return BS_Guest_ModelHolder.BSGuestModelInstance;
    }

    public void setPlayersScores(String[] playersScores) {
        this.playersScores = playersScores;
        getPlayer().set_score(Integer.parseInt(playersScores[getPlayer().get_index()]));
    }


    public void openSocket(String ip, int port) { //button start in the view
        if (validateIpPort(ip, port)) {
            try {
                socket = new Socket(ip, port);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            communicationHandler = new ClientCommunicationHandler();
        }
    }

    private boolean validateIpPort(String ip, int port) {
        // Regular expression for IPv4 address
        String ipv4Regex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
        // Regular expression for port number (1-65535)
        String portRegex = "^([1-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
        // Validate IP address
        if (!Pattern.matches(ipv4Regex, ip)) {
            return false;
        }
        // Validate port number
        return Pattern.matches(portRegex, Integer.toString(port));
    }

    @Override
    public void passTurn(int playerIndex) {
        communicationHandler.outMessages("passTurn:" + playerIndex);
    }

    public void tryPlaceWord(String word, int x, int y, boolean isVertical) {
        String message = word + ":" + x + ":" + y + ":" + isVertical;
        String playerIndex = String.valueOf(player.get_index());
        communicationHandler.outMessages("tryPlaceWord:" + playerIndex + ":" + message);
    }

    public void challengeWord(String word) {
        String playerIndex = String.valueOf(player.get_index());
        communicationHandler.outMessages("challengeWord:" + playerIndex + ":" + word);
    }

    @Override
    public boolean isHost() {
        return false;
    }



    public void setBoard(Tile[][] boardTiles) {
        this.tileBoard = boardTiles;
        setChanged();
        notifyObservers("board:");
    }



    public Socket getSocket() {
        return socket;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayerProperties(String name) {
        this.player.set_name(name);
    }

    public ClientCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    @Override
    public int getCurrentPlayerScore() {
        return player.get_score();
    }

    @Override
    public List<Tile> getCurrentPlayerHand() {
        return player.get_hand();
    }

    @Override
    public Tile[][] getBoardState() {
        return tileBoard;
    }

    private static class BS_Guest_ModelHolder {
        private static final BS_Guest_Model BSGuestModelInstance = new BS_Guest_Model();
    }

    public void endGame() {
        communicationHandler.outMessages("endGame:"+player.get_socketID());
        communicationHandler.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
