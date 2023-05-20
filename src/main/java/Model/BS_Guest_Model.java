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
    Tile[][] tileBoard = new Tile[15][15];
    Tile[][] tempBoard = new Tile[15][15];
    Player player;
    ClientCommunicationHandler communicationHandler;

    /**
     * The BS_Guest_Model function is a constructor that initializes the player and playersScores variables.
     */
    private BS_Guest_Model() {
        player = new Player();
        playersScores = new String[0];
    }

    /**
     * The getModel function is a static function that returns the singleton instance of the BS_Guest_Model class.
     * This allows for only one instance of this model to be created, and it can be accessed from anywhere in the program.
     * @return The singleton instance of the bs_guest_model class
     */
    public static BS_Guest_Model getModel() {
        return BS_Guest_ModelHolder.BSGuestModelInstance;
    }

    /**
     * The setPlayersScores function is used to set the scores of all players in the game.
     * @param playersScores Set the playersScores array
     */
    public void setPlayersScores(String[] playersScores) {
        this.playersScores = playersScores;
        getPlayer().set_score(Integer.parseInt(playersScores[getPlayer().get_index()]));
    }

    /**
     * The openSocket function is used to open a socket connection with the host.
     * @param  ip Set the ip address of the host
     * @param  port Connect to the host on this port
     */
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

    /**
     * The validateIpPort function takes in a String ip and an int port.
     * It returns true if the ip is a valid IPv4 address and the port is within range (0-65535).
     * Otherwise, it returns false.
     * @param  ip Validate the ip address
     * @param  port Validate the port number
     * @return False if the ip or port is invalid, otherwise it returns true
     */
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

    /**
     * The passTurn function is used to send a message to the host that the player has no more moves to make and wants to move his turn to the next player.
     * pass the turn from one player to another.
     * @param  playerIndex Tell the server which player is passing their turn
     */
    @Override
    public void passTurn(int playerIndex) {
        communicationHandler.outMessages("passTurn:" + playerIndex);
    }

    /**
     * The tryPlaceWord function is used to send a message to the host that the player has want to placed a word on the board.
     * @param  word Pass the word that is being placed on the board.
     * @param  x Determine the x coordinate of the word.
     * @param  y Determine the y-coordinate of the word.
     * @param  isVertical Determine whether the word is placed vertically or horizontally.
     */
    public void tryPlaceWord(String word, int x, int y, boolean isVertical) {
        String message = word + ":" + x + ":" + y + ":" + isVertical;
        String playerIndex = String.valueOf(player.get_index());
        communicationHandler.outMessages("tryPlaceWord:" + playerIndex + ":" + message);
    }

    /**
     * The challengeWord function is used to challenge a word that has been played by another player.
     * The function takes in the word that is being challenged as a parameter and sends it to the host.
     * @param  word Send the word to be challenged
     */
    public void challengeWord(String word) {
        String playerIndex = String.valueOf(player.get_index());
        communicationHandler.outMessages("challengeWord:" + playerIndex + ":" + word);
    }

    /**
     * The isHost function returns a boolean value of false because I am the Guest.
     *
     * @return False
     */
    @Override
    public boolean isHost() {
        return false;
    }

    /**
     * The setBoard function is used to set (update) the board of the game.
     * @param boardTiles Set the tileBoard variable
     */
    public void setBoard(Tile[][] boardTiles) {
        this.tileBoard = boardTiles;
        setChanged();
        notifyObservers("board:");
    }

    /**
     * The getSocket function returns the socket that is used to connect to the server.
     * @return The socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * The getPlayer function returns the player object.
     * @return A player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * The setPlayerProperties function sets the player's name to the given string.
     * @param  name Set the name of the player
     */
    @Override
    public void setPlayerProperties(String name) {
        this.player.set_name(name);
    }

    /**
     * The getCommunicationHandler function returns the communicationHandler object.
     * @return A clientCommunicationHandler object
     */
    public ClientCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    /**
     * The getCurrentPlayerScore function returns the current player's score.
     * @return The score of the current player
     */
    @Override
    public int getCurrentPlayerScore() {
        return player.get_score();
    }

    /**
     * The getCurrentPlayerHand function returns the current player's hand List of Tile.
     * @return The current player's hand
     */
    @Override
    public List<Tile> getCurrentPlayerHand() {
        return player.get_hand();
    }

    /**
     * The getBoardState function returns the current state of the board.
     * @return A 2d array of tile objects
     */
    @Override
    public Tile[][] getBoardState() {
        return tileBoard;
    }

    private static class BS_Guest_ModelHolder {
        private static final BS_Guest_Model BSGuestModelInstance = new BS_Guest_Model();
    }

    /**
     * The endGame function is called when the game ends.
     * It sends a message to the host that it has ended, and then closes all connections with the host.
     */
    public void endGame() {
        communicationHandler.outMessages("endGame:" + player.get_socketID());
        communicationHandler.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
