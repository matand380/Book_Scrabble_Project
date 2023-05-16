package Model;

import Model.GameData.Board;
import Model.GameData.Player;
import Model.GameData.Tile;
import Model.GameData.Word;
import Model.GameLogic.HostCommunicationHandler;
import Model.GameLogic.MyServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;


public class BS_Host_Model extends Observable implements BS_Model {
    public ArrayList<Word> currentPlayerWords;
    public int currentPlayerIndex = 0;
    HostCommunicationHandler communicationHandler = new HostCommunicationHandler();
    MyServer communicationServer;
    Socket gameSocket;
    Board board;
    Tile.Bag bag;
    Player player;
    Map<String, String> playerToSocketID = new HashMap<>();
    private final List<Player> players;
    private boolean isGameOver;

    public BS_Host_Model() {

        //Game data initialization
        setGameOver(false);
        board = Board.getBoard();
        bag = Tile.Bag.getBag();
        players = new ArrayList<>();
        player = new Player();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter local/remote server ip: ");
        String ip = scanner.nextLine();
        System.out.println("Enter local/remote server port: ");
        int port = scanner.nextInt();

        openSocket(ip, port); //copy local server ip + server port
        // System.out.println("Enter server port number : ");
        //    Scanner scanner = new Scanner(System.in);
        //  int port = scanner.nextInt();
        communicationServer = new MyServer(port, communicationHandler);
        //    System.out.println("Server local ip: " + communicationServer.ip() + "\n" + "Server public ip: " + communicationServer.getPublicIp() + "\n" + "Server port: " + port);
        communicationServer.start();

        //only for testing

    }

    /**
     * The getModel function is a static function that returns the singleton instance of the BS_Host_Model class.
     * If no instance exists, it creates one and then returns it.
     * <p>
     */
    public static BS_Host_Model getModel() {
        return HostModelHelper.model_instance;
    }
    public int getBagSize() {return bag.size();}

    public Map<String, String> getPlayerToSocketID() {
        return playerToSocketID;
    }

    public Socket getGameSocket() {
        return gameSocket;
    }

    public void openSocket(String ip, int port) {
        if (validateIpPort(ip, port)) {
            try {
                gameSocket = new Socket(ip, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Invalid ip or port");
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

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setNextPlayerIndex(int index) {
        currentPlayerIndex = (index + 1) % players.size();

    }

    /**
     * The startNewGame function is used to reset the game.
     * It returns all the tiles from each player's tileLottery back into the bag,
     * and then completes each player's hand to 7 tiles.
     */

    public void startNewGame() {
        Gson gson = new Gson();
        sortAndSetIndex();
        players.forEach(p -> {
            String id = p.completeTilesTo7();
            Tile[] tiles = p.get_hand().toArray(new Tile[p.get_hand().size()]);

            if (id != null) {
                String json = gson.toJson(tiles);
                communicationServer.updateSpecificPlayer(id, "hand:" + json);
            }

        });

    }

    @Override
    public void passTurn(int playerIndex) {
        setNextPlayerIndex(currentPlayerIndex);
        board.passCounter++;
        isGameOver();
        communicationServer.updateAll("passTurn:" + getCurrentPlayerIndex());// notify all clients
        hasChanged();
        notifyObservers("passTurn:" + getCurrentPlayerIndex());// notify host viewModel if im the next player
    }

    /**
     * The tryPlaceWord function is used to try and place a word on the board.
     * If the word can be placed, it will return true and add the score of that player.
     * Otherwise, it will return false and not change anything.
     *
     * @param word Pass the word that is being placed on the board
     * @return The score of the word (if it is valid)
     */
    public void tryPlaceWord(Word word) {
        int score = Board.getBoard().tryPlaceWord(word);
        if (score > 0) {
            // TODO: 06/05/2023 challenge pop up if someone press challenge activate challengeWord method
            StringBuilder sb = new StringBuilder();
            for (Word w : currentPlayerWords) {
                sb.append(w.toString());
                sb.append(":");
            }
            String words = sb.toString();
            BS_Host_Model.getModel().communicationServer.updateAll("wordsForChallenge:" + currentPlayerWords.size() + ":" + words);
            hasChanged();
            notifyObservers("wordsForChallenge:" + words);
            currentPlayerWords.clear();
            // TODO: 11/05/2023 wait for challenge response
            // TODO: 11/05/2023 if challenge didnt happen place the word
            //if(!isChallenge) {// implement isChallenge
            placeAndComplete7(word.toString());
            // TODO: 15/05/2023 add Gson of the Tile[][] to the updateAll
            ////////////////////////////////////////////
            Gson gson = new Gson();
            String json = gson.toJson(Board.getBoard().getTiles());
            communicationServer.updateAll("tileBoard:" + json);
            ////////////////////////////////////////////

            // }
            // TODO: 11/05/2023 check isGameOver
            isGameOver();
            players.get(currentPlayerIndex).set_score(players.get(currentPlayerIndex).get_score() + score);
            BS_Host_Model.getModel().communicationServer.updateAll("tryPlaceWord:" + currentPlayerIndex + ":" + score);
            hasChanged();
            notifyObservers("tryPlaceWord:" + currentPlayerIndex + ":" + score);
            //only for testing
            System.out.println("tryPlaceWord:" + currentPlayerIndex + ":" + score);

        } else {
            BS_Host_Model.getModel().communicationServer.updateAll("tryPlaceWord:" + currentPlayerIndex + ":" + "0");
            hasChanged();
            notifyObservers("tryPlaceWord:" + currentPlayerIndex + ":" + "0");
        }
    }

    /**
     * The tryPlaceWord function is used to try and place a word on the board.
     * If the word can be placed, it will return true and add the score of that player.
     * Otherwise, it will return false and not change anything.
     *
     * @param word Get the word that was placed on the board
     * @return The score of the word if it was placed successfully
     */

    public String challengeWord(String word, String index) {
        int PlayerIndex = Integer.parseInt(index);
        BS_Host_Model.getModel().getCommunicationHandler().messagesToGameServer("C:" + word);
        String response = BS_Host_Model.getModel().getCommunicationHandler().messagesFromGameServer();
        System.out.println(response); // TODO: 14/05/2023 remove this line, only for testing
        String[] splitResponse = response.split(":");
        if (splitResponse[0].equals("C")) {
            if (splitResponse[1].equals("true")) {
                players.get(PlayerIndex).set_score(players.get(PlayerIndex).get_score() - 10);
                placeAndComplete7(word); // FIXME: 14/05/2023 it raises an NullPointerException if currentPlayerWords is empty
                // TODO: 11/05/2023 send hand to the player;
                isGameOver();
                communicationServer.updateAll(Board.getBoard().getTiles());
                hasChanged();
                notifyObservers("board:");
            } else if (splitResponse[1].equals("false")) {
                players.get(PlayerIndex).set_score(players.get(PlayerIndex).get_score() + 10);
            }
            hasChanged();
            notifyObservers("challengeWord:" + PlayerIndex + players.get(PlayerIndex).get_score());
            communicationServer.updateAll("challengeWord:" + PlayerIndex + players.get(PlayerIndex).get_score());
        } else {
            System.out.println("Error: dictionaryLegal");
        }
        return "";

    }

    /**
     * The sortAndSetID function sorts the players in ascending order by their tileLottery value,
     * and then sets each player's ID to be equal to their index in the list.
     * Return Void
     */
    public void sortAndSetIndex() {
        players.sort(Comparator.comparing(Player::getTileLottery));
        for (int i = 0; i < players.size(); i++) {
            players.get(i).set_index(i);
        }
        hasChanged();
        notifyObservers("sortAndSetIndex" + player.get_index()); // this is host index
        StringBuilder allPlayers = new StringBuilder();
        for (Player p : players) {
            allPlayers.append(p.get_index()).append(",").append(p.get_socketID()).append(":");
        }
        String allPlayersString = allPlayers.toString();

        BS_Host_Model.getModel().communicationServer.updateAll("sortAndSetIndex:" + players.size() + ":" + allPlayersString);

    }

    /**
     * The placeAndComplete7 function is used to place a word on the board and then complete the player's hand
     * to 7 tiles.
     * It does this by removing all the letters in that word from their hand, and then completing
     * their hand with new tiles from the bag.
     * This function is called when a player has placed all of their tiles
     * on one turn, so they do not need to draw any more tiles after placing them.
     */
    private void placeAndComplete7(String word) {
        Board.getBoard().placeWord(currentPlayerWords.get(0));
        char[] wordChars = word.toCharArray();
        players.get(currentPlayerIndex).get_hand().removeIf(tile -> {
            for (char c : wordChars) {
                if (tile.letter == c) {
                    return true;
                }
            }
            return false;
        });
        players.get(currentPlayerIndex).completeTilesTo7();
    }

    @Override
    public boolean isHost() {
        return true;
    }

    public boolean isGameOver() {
        if (board.passCounter == getPlayers().size()) //all the players pass turns
            isGameOver = true;
        if (Tile.Bag.getBag().size() == 0) for (Player p : players)
            if (p.get_hand().size() == 0) {
                isGameOver = true;
                break;
            }
        if (isGameOver) {
            communicationServer.updateAll("gameOver:" + getMaxScore());
            // TODO: 11/05/2023 hasChanged() + notifyObservers()
        }
        return isGameOver;
    }

    @Override
    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public String getMaxScore() {
        Player winner = players.stream().max(Comparator.comparing(Player::get_score)).get();
        return winner.get_index() + ":" + winner.get_name();
    }

    public HostCommunicationHandler getCommunicationHandler() {
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
        return Board.getBoard().getTiles();
    }

    @Override
    public void setPlayerProperties(String name) {
        player.set_name(name);
        player.set_socketID(null);
        player.setTileLottery();
        players.add(player);
    }

    private static class HostModelHelper {
        public static final BS_Host_Model model_instance = new BS_Host_Model();
    }


}


