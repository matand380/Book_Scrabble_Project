package Model;

import Model.GameData.*;
import Model.GameLogic.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class BS_Host_Model extends Observable implements BS_Model {
    private final AtomicBoolean challengeActivated = new AtomicBoolean(false);
    public ArrayList<Word> currentPlayerWords;
    public int currentPlayerIndex = 0;
    public boolean gameIsOver = false;
    HostCommunicationHandler communicationHandler = new HostCommunicationHandler();
    MyServer communicationServer;
    Socket gameSocket;
    Board board;
    Tile.Bag bag;
    Player player;
    Map<String, String> playerToSocketID = new HashMap<>();
    public System.Logger hostLogger = System.getLogger("HostLogger");
    ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Player> players;


    private BS_Host_Model() {

        //Game data initialization
        board = Board.getBoard();
        bag = Tile.Bag.getBag();
        players = new ArrayList<>();
        player = new Player();

        //for testing
        System.out.println("Pick host port number : ");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        communicationServer = new MyServer(port, communicationHandler);
        //    System.out.println("Server local ip: " + communicationServer.ip() + "\n" + "Server public ip: " + communicationServer.getPublicIp() + "\n" + "Server port: " + port);

    }

    /**
     * The getModel function is a static function that returns the singleton instance of the BS_Host_Model class.
     * If no instance exists, it creates one and then returns it.
     * <p>
     */
    public static BS_Host_Model getModel() {
        return HostModelHelper.model_instance;
    }

    public MyServer getCommunicationServer() {
        return communicationServer;
    }

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
            } else {
                hasChanged();
                notifyObservers("hand updated");
            }


        });

    }

    @Override
    public void passTurn(int playerIndex) {
        setNextPlayerIndex(currentPlayerIndex);
        board.setPassCounter(board.getPassCounter() + 1);
        if (isGameOver()) {
            gameIsOver = true;
            communicationServer.updateAll("endGame");
            hasChanged();
            notifyObservers("endGame");
            return;
        }
        communicationServer.updateAll("passTurn:" + getCurrentPlayerIndex());// notify all clients
        hasChanged();
        notifyObservers("passTurn:" + getCurrentPlayerIndex());
    }

    /**
     * The tryPlaceWord function is used to try and place a word on the board.
     * If the word can be placed, it will return true and add the score of that player.
     * Otherwise, it will return false and not change anything.
     *
     * @param word Pass the word that is being placed on the board
     */
    public void tryPlaceWord(Word word) {
        int score = Board.getBoard().tryPlaceWord(word);
        if (score > 0) {

            // build the string of words for a challenge, send it to all clients and notify host viewModel
            StringBuilder sb = new StringBuilder();
            for (Word w : currentPlayerWords) {
                sb.append(w.toString());
                sb.append(":");
            }
            String words = sb.toString();
            BS_Host_Model.getModel().communicationServer.updateAll("wordsForChallenge:" + currentPlayerWords.size() + ":" + words);
            hasChanged();
            notifyObservers("wordsForChallenge:" + words);

            LockSupport.parkNanos(10000000000L);
            if (challengeActivated.get()) {
                //execute challengeWord method
                boolean result = false;
//                try {
                    String forChallenge = currentPlayerWords.get(0).toString(); // TODO: 17/05/2023 need to be fixed
                    // TODO: 17/05/2023  it can be any word from the list, we need to take the one the we received from the client
                   result = challengeWord(forChallenge, String.valueOf(currentPlayerIndex));
//                }
//                catch (ExecutionException | InterruptedException e) {
//                    hostLogger.log(System.Logger.Level.ERROR, "Thread challengeWord interrupted");
//                }

                if (result) {
                    placeAndComplete7(word.toString());
                    updateBoard();

                    if (isGameOver()) {
                        gameIsOver = true;
                        communicationServer.updateAll("endGame");
                        hasChanged();
                        notifyObservers("endGame");
                        return;
                    }
                } else {
                    String id = playerToSocketID.get(players.get(currentPlayerIndex).get_name());
                    if (id != null)
                        communicationServer.updateSpecificPlayer("challengeSuccess", id);
                    else {
                        hasChanged();
                        notifyObservers("challengeSuccess");
                    }
                    passTurn(currentPlayerIndex);
                    // TODO: 17/05/2023 if challenge success the player can try another word it is not passed turn
                }

                challengeActivated.set(false);
                LockSupport.unpark(Thread.currentThread());
                currentPlayerWords.clear(); // TODO: 16/05/2023 check if this is the right place to clear the list
                return;
            }
            /////////////////////////////
            // TODO: 16/05/2023 get an indication from a client if he pressed a challenge or not
            // !there is only one client that can press a challenge.
            // !we handle the first client that pressed challenge
            // !and notify the rest that their request for a challenge is invalid
            /////////////////////////////




            /*
            * if tryPlaceWord returns score > 0, then we send a list of words to all clients
            *  that will be used for a challenge
            *
            * if a client or the host press challenge, then we call [[BS_Host_Model#challengeWord()]] method
            *
            * if challengeWord returns true, then we call placeAndComplete7 method,
            * and we need to update the client that pressed tryPlaceWord that a challenge happened but the challenge returned true
            *
            * if challengeWord returns false, then we pass by the placeAndComplete7 method,
            * and we need to update the client that pressed tryPlaceWord that a challenge happened and the challenge returned false
            * he will need to remove the word from the board
            *
            * if no one pressed a challenge, then we call placeAndComplete7 method
            * we need a positive indicator that no one pressed a challenge

            ! the tryPlaceWord method needs to wait for the challenge response
            *
            *
             */

            //if no challenge happened, update the board and complete the hand
            placeAndComplete7(word.toString());
            players.get(currentPlayerIndex).set_score(score);
            updateBoard();
            updateScores();
            if (isGameOver()) {
                gameIsOver = true;
                communicationServer.updateAll("endGame");
                hasChanged();
                notifyObservers("endGame");
                return;
            }
            passTurn(currentPlayerIndex);
            board.setPassCounter(0);
            currentPlayerWords.clear(); // TODO: 16/05/2023 check if this is the right place to clear the list

        }
    }

    private void updateBoard() {
        Gson gson = new Gson();
        String json = gson.toJson(Board.getBoard().getTiles());
        communicationServer.updateAll("tileBoard:" + json);
        hasChanged();
        notifyObservers("tileBoard updated");
    }

    public boolean isFull() {
        return players.size() == 4;
    }

    private void endGame() {
        executor.shutdown();
        getCommunicationServer().close();
        notifyAll(); // notify the main that the game is over
        // 16/05/2023 server will be notified about the winner;
        // 16/05/2023 its close button will be disabled while clientsMap is not empty
        //16/05/2023 when clientsMap will be empty the button will be enabled
        // 16/05/2023 press the button will execute the endGame method

        // 16/05/2023 this method will be activated after pressing the end game button
        // 16/05/2023 wait that all clients closed their game
        // 16/05/2023 the HOST end game button will be activated after all clients pressed exit in their game
        // 16/05/2023 need to close the game and all resources
    }

    /**
     * The tryPlaceWord function is used to try and place a word on the board.
     * If the word can be placed, it will return true and add the score of that player.
     * Otherwise, it will return false and not change anything.
     *
     * @param word Get the word that was placed on the board
     * @return The score of the word if it was placed successfully
     */

    public boolean challengeWord(String word, String index) {

        //send a challenge to the GameServer and get a response
        int PlayerIndex = Integer.parseInt(index);
        Future<String> f = BS_Host_Model.getModel().executor.submit(() -> {
            BS_Host_Model.getModel().getCommunicationHandler().messagesToGameServer("C:" + word);
            return BS_Host_Model.getModel().getCommunicationHandler().messagesFromGameServer();
        });
        String response = null;
        try {
            response = f.get(); // blocking call
        } catch (InterruptedException | ExecutionException e) {
            hostLogger.log(System.Logger.Level.ERROR, "Thread challengeWord interrupted");
        }
//        BS_Host_Model.getModel().getCommunicationHandler().messagesToGameServer("C:" + word);
//        String response = BS_Host_Model.getModel().getCommunicationHandler().messagesFromGameServer();

        // handle the response
        String[] splitResponse = response.split(":");
        if (splitResponse[0].equals("C")) {
            if (splitResponse[1].equals("true")) {
                players.get(PlayerIndex).set_score(players.get(PlayerIndex).get_score() - 10);
                updateScores();
                return true;
            } else {
                players.get(PlayerIndex).set_score(players.get(PlayerIndex).get_score() + 10);
                updateScores();
                return false;
            }
        } else {
            hostLogger.log(System.Logger.Level.ERROR, "GameServer response in challengeWord is not valid");
            return false;
        }
    }

    private void updateScores() {
        String[] scores = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            scores[i] = String.valueOf(players.get(i).get_score());
        }
        Gson gson = new Gson();
        String playersScores = gson.toJson(scores);
        communicationServer.updateAll("playersScores:" + playersScores);
        hasChanged();
        notifyObservers("playersScores updated");
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
                if (tile.getLetter() == c) {
                    return true;
                }
            }
            return false;
        });
        players.get(currentPlayerIndex).completeTilesTo7();
        String id = players.get(currentPlayerIndex).get_socketID();
        Gson gson = new Gson();
        String json = gson.toJson(players.get(currentPlayerIndex).get_hand());
        communicationServer.updateSpecificPlayer(id, "hand:" + json);
        if (currentPlayerIndex == BS_Host_Model.getModel().player.get_index()) {
            hasChanged();
            notifyObservers("hand:" + json);
        }
    }

    @Override
    public boolean isHost() {
        return true;
    }

    public boolean isGameOver() {
        boolean isGameOver = false;
        if (board.getPassCounter() == getPlayers().size()) //all the players pass turns
            isGameOver = true;
        if (Tile.Bag.getBag().size() == 0)
            for (Player p : players)
                if (p.get_hand().size() == 0) {
                    isGameOver = true;
                    break;
                }
        if (isGameOver) {
            communicationServer.updateAll("winner:" + getMaxScore());
            hasChanged();
            notifyObservers("winner:" + getMaxScore());
            //todo: change the variable "gameIsOver" to true(Matan or eviatar approve next line)
            //gameIsOver = true;
        }
        return isGameOver;
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

    public void requestChallengeActivation() {
        // Set the challengeRequested flag to indicate a challenge is requested
        challengeActivated.set(true);

    }

    private static class HostModelHelper {
        public static final BS_Host_Model model_instance = new BS_Host_Model();
    }
}



