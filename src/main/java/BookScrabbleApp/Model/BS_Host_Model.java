package BookScrabbleApp.Model;

import BookScrabbleApp.Model.GameData.*;
import BookScrabbleApp.Model.GameLogic.HostCommunicationHandler;
import BookScrabbleApp.Model.GameLogic.MyServer;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Pattern;

import com.google.gson.Gson;


public class BS_Host_Model extends Observable implements BS_Model {
    private final AtomicBoolean challengeActivated = new AtomicBoolean(false);
    public ArrayList<Word> currentPlayerWords;
    public int currentPlayerIndex = 0;
    public boolean gameIsOver = false;
    public System.Logger hostLogger = System.getLogger("HostLogger");
    HostCommunicationHandler communicationHandler = new HostCommunicationHandler();
    MyServer communicationServer;
    Socket gameSocket;
    Board board;
    Tile.Bag bag;
    Player player;
    Map<String, String> playerToSocketID = new HashMap<>();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    String challengeInfo;
    Queue<Player> scoresManager = new PriorityQueue<>(Comparator.comparingInt(Player::get_score).reversed());
    String[] PlayersScores;
    private List<Player> players;

    /**
     * The BS_Host_Model function is a singleton class that creates the host model for the game.
     */
    private BS_Host_Model() {

        //Game data initialization
        board = Board.getBoard();
        bag = Tile.Bag.getBag();
        players = new ArrayList<>();
        player = new Player();


        //for testing
//        System.out.println("Pick host port number : ");
//        Scanner scanner = new Scanner(System.in);
//        int port = scanner.nextInt();
        communicationServer = new MyServer(23346, communicationHandler);
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

    public Queue<Player> getScoresManager() {
        return scoresManager;
    }

    /**
     * The getPlayer function returns the player object.
     * <p>
     *
     * @return The player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * The getChallengeInfo function returns the challengeInfo variable.
     * <p>
     *
     * @return A string containing the challenge info
     * @see BS_Host_Model#setChallengeInfo(String)
     */
    String getChallengeInfo() {
        return challengeInfo;
    }

    /**
     * The setChallengeInfo function sets the challengeInfo variable to a new value.
     * <p>
     *
     * @param challengeInfo Set the challenge info variable
     */
    void setChallengeInfo(String challengeInfo) {
        this.challengeInfo = challengeInfo;
    }

    /**
     * The getCommunicationServer function returns the communicationServer object.
     * <p>
     *
     * @return The communicationserver object
     */
    public MyServer getCommunicationServer() {
        return communicationServer;
    }

    /**
     * The getPlayerToSocketID function returns a map of player names to socket IDs.
     * <p>
     *
     * @return A map of the player names to their socket ids
     */
    public Map<String, String> getPlayerToSocketID() {
        return playerToSocketID;
    }

    /**
     * The getGameSocket function returns the gameSocket variable.
     * <p>
     *
     * @return The gamesocket variable
     */
    public Socket getGameSocket() {
        return gameSocket;
    }

    /**
     * The openSocket function opens a socket connection to the server.
     * <p>
     *
     * @param ip   Set the ip address of the server
     * @param port Specify the port number of the server
     */
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

    /**
     * The validateIpPort function takes in a String ip and an int port.
     * It returns true if the ip is a valid IPv4 address and the port is within range (0-65535).
     * Otherwise, it returns false.
     * <p>
     *
     * @param ip   Store the ip address of the server
     * @param port Validate the port number
     * @return A boolean
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
     * The getPlayers function returns a list of players.
     * <p>
     *
     * @return A list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * The addPlayer function adds a player to the game.
     * <p>
     *
     * @param p Add a player to the players arraylist
     */

    public void addPlayer(Player p) {
        this.players.add(p);
        this.scoresManager.add(p);

    }

    /**
     * The getCurrentPlayerIndex function returns the index of the current player.
     * <p>
     *
     * @return The current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * The setNextPlayerIndex function sets the currentPlayerIndex to the next player in line.
     * <p>
     *
     * @param index Determine which player is the current player
     */
    public void setNextPlayerIndex(int index) {
        currentPlayerIndex = (index + 1) % players.size();
    }

    /**
     * The startNewGame function is used to start the game.
     * It gives each player 7 tiles, sorts the tiles, and sets the player with the smallest tile as the current player.
     * and then gives each player's hand to 7 tiles.
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
                setChanged();
                notifyObservers("hand updated");
            }


        });

    }

    /**
     * The passTurn function is called when a player passes their turn.
     * It increments the pass counter, and if it reaches the maximum, then the game ends.
     * If not, it sets the next player as currentPlayerIndex and updates all clients with this information.
     * <p>
     *
     * @param playerIndex Determine which player is passing their turn
     */
    @Override
    public void passTurn(int playerIndex) {
        setNextPlayerIndex(currentPlayerIndex);
        board.setPassCounter(board.getPassCounter() + 1);
        if (isGameOver()) {
            gameIsOver = true;
            return;
        }
        communicationServer.updateAll("turnPassed:" + getCurrentPlayerIndex());// notify all clients
        setChanged();
        notifyObservers("turnPassed:" + getCurrentPlayerIndex());
    }

    /**
     * The passTurnTryPlace function is called after a player tries to place a word on the board.
     * It sets the next player index to be the current player index, and then checks if the game is over.
     * If it isn't, it updates all clients with a passTurn message containing the currentPlayerIndex.
     *
     * @param playerIndex Determine which player is passing the turn
     */
    public void passTurnTryPlace(int playerIndex) {
        setNextPlayerIndex(playerIndex);
        BS_Host_Model.getModel().board.setPassCounter(0);
        if (isGameOver()) {
            gameIsOver = true;
            return;
        }
        communicationServer.updateAll("turnPassed:" + getCurrentPlayerIndex());// notify all clients
        setChanged();
        notifyObservers("turnPassed:" + getCurrentPlayerIndex());
    }

    /**
     * The tryPlaceWord function is used to check if a word can be placed on the board.
     * If it can, then the function returns true and updates the score of the player who played that word.
     * Otherwise, it returns false and does not update any scores or change anything else in game state.
     * The function starts the process with the tryPlaceWord function in the Board class.
     * If a word can be placed, then the function updates all players with a list of words for a challenge.
     * If a challenge has been activated, then the function checks if the word is in the dictionary.
     * If it is,
     * then the function updates the score of the player who played the word and fines the challenger.
     * If it isn't, then the function updates the score of the challenger.
     * If a challenge has not been activated,
     * then the function updates the score of the player
     * who played the word and sends an updated board to all clients.
     *
     * <p>
     *
     * @param stringWord Get the word that the player is trying to place
     */
    public void tryPlaceWord(String stringWord, int row, int col, boolean direction) {
        char[] buildWord = stringWord.toCharArray();
        Player current = getPlayers().get(currentPlayerIndex);
        Tile[] tiles = new Tile[stringWord.length()];
        for (int i = 0; i < stringWord.length(); i++) {
            tiles[i] = current.charToTile(buildWord[i]);
        }
        Word word = new Word(tiles, row, col, direction);
        int score = Board.getBoard().tryPlaceWord(word);
        if (score > 0) {

            // build the string of words for a challenge, send it to all clients and notify host viewModel
            StringBuilder sb = new StringBuilder();
            for (Word w : currentPlayerWords) {
                sb.append(w.toString());
                sb.append(",");
            }
            String words = sb.toString();
            BS_Host_Model.getModel().communicationServer.updateAll("wordsForChallenge:" + currentPlayerWords.size() + ":" + words);
            setChanged();
            notifyObservers("wordsForChallenge:" + currentPlayerWords.size() + ":" + words);
            //if the current player is the host, then the host's viewModel wan't display the challenge words

            LockSupport.parkNanos(10000000000L); //park for 10 seconds
            if (challengeActivated.get()) {
                //execute challengeWord method
                boolean result = false;
                String challengerIndex = this.getChallengeInfo().split(":")[0];
                String forChallenge = this.challengeInfo.split(":")[1];
                result = challengeWord(forChallenge, challengerIndex);

                if (result) {
                    placeAndComplete7(word.toString());
                    players.get(currentPlayerIndex).set_score(players.get(currentPlayerIndex).get_score() + score);
                    updateBoard();
                    updateScores();

                    if (isGameOver()) {
                        gameIsOver = true;
                        communicationServer.updateAll("endGame");
                        setChanged();
                        notifyObservers("endGame");
                        return;
                    }
                } else {
                    String id = playerToSocketID.get(players.get(currentPlayerIndex).get_name());
                    if (id != null)
                        communicationServer.updateSpecificPlayer(id, "challengeSuccess");
                    else {
                        setChanged();
                        notifyObservers("challengeSuccess");
                    }
                    passTurn(currentPlayerIndex);
                }

                challengeActivated.set(false);
                currentPlayerWords.clear();
                return;
            }
            //if no challenge happened, update the board and complete the hand
            placeAndComplete7(word.toString());
            players.get(currentPlayerIndex).set_score(players.get(currentPlayerIndex).get_score() + score);
            updateBoard();
            updateScores();
            if (isGameOver()) {
                gameIsOver = true;
                return;
            }
            passTurnTryPlace(currentPlayerIndex);
            currentPlayerWords.clear();

        }
        // score == 0 means that the word cannot be placed on the board
        else {
            String id = playerToSocketID.get(players.get(currentPlayerIndex).get_name());
            if (id != null)
                communicationServer.updateSpecificPlayer(id, "invalidWord");
            else {
                setChanged();
                notifyObservers("invalidWord");
            }
        }

    }

    /**
     * The updateBoard function is called when the board has changed.
     * It converts the tiles on the board to a JSON string and sends it to all clients.
     * <p>
     */
    private void updateBoard() {
        Gson gson = new Gson();
        String json = gson.toJson(Board.getBoard().getTiles());
        communicationServer.updateAll("tileBoard:" + json);
        setChanged();
        notifyObservers("tileBoard updated");
        //for testing
        System.out.println("host tileBoard");
        System.out.println(TestHelper.formatTiles(BS_Host_Model.getModel().getBoardState()));
    }

    /**
     * The isFull function checks to see if the game is full (total of 4 players).
     * <p>
     *
     * @return True if the players array has 4 objects in it
     */
    public boolean isFull() {
        return players.size() == 4;
    }

    /**
     * The endGame function is called when the game has ended.
     * This method is called when the host presses the end game button (which is only enabled after all clients have exited their games).
     * It shuts down the executor, closes the communication server, and notifies all clients that they can exit their games.
     * <p>
     */
    void endGame() {
        executor.shutdown();
        getCommunicationServer().close();
        notifyAll(); // notify the main that the game is over
        // 16/05/2023 server will be notified about the winner;
        // 16/05/2023 its close button will be disabled while clientsMap is not empty
        //16/05/2023 when clientsMap will be empty the button will be enabled
        // 16/05/2023 press the button will execute the endGame method

    }

    /**
     * The challengeWord function is called when a player challenges another player's word.
     * The function sends the challenged word to the GameServer, which checks if it is valid or not.
     * If it is valid, then the challenger loses 10 points and if invalid, then they gain 10 points.
     * <p>
     *
     * @param word  Send the word to be challenged to the game server
     * @param index Identify the player who is challenging
     * @return A boolean value
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

    /**
     * The updateScores function is used to update the scores of all players in the game.
     * <p>
     */
    private void updateScores() {
        String[] scores = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            scores[i] = String.valueOf(players.get(i).get_score());
        }
        Gson gson = new Gson();
        String playersScores = gson.toJson(scores);
        communicationServer.updateAll("playersScores:" + playersScores);
        setChanged();
        notifyObservers("playersScores updated");
    }

    /**
     * The sortAndSetID function sorts the players in ascending order by their tileLottery value,
     * and then sets each player's index to be equal to their index in the list.
     */
    public void sortAndSetIndex() {
        players.sort(Comparator.comparing(Player::getTileLottery));
        for (int i = 0; i < players.size(); i++) {
            players.get(i).set_index(i);
        }
        setChanged();
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
     * This function is called when a player has placed their tiles
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
            setChanged();
            notifyObservers("hand updated");
        }
    }

    /**
     * The isHost function returns true if the current player is a host.
     * <p>
     *
     * @return A boolean value
     */
    @Override
    public boolean isHost() {
        return true;
    }

    /**
     * The isGameOver function checks if the game is over.
     * The game is over when all players pass their turns or there are no more tiles in the bag, and one of the players has no tiles left.
     * <p>
     *
     * @return True when the game is over
     */
    public boolean isGameOver() {
        boolean isGameOver = false;
        if (board.getPassCounter() == getPlayers().size()) //all the players pass turns
            isGameOver = true;
        if (Tile.Bag.getBag().size() == 0)
            for (Player p : players)
                if (p.get_hand().isEmpty()) {
                    isGameOver = true;
                    break;
                }
        if (isGameOver) {
            communicationServer.updateAll("winner:" + getMaxScore());
            setChanged();
            notifyObservers("winner:" + getMaxScore());
        }
        return isGameOver;
    }


    /**
     * The getMaxScore function returns the player with the highest score.
     * <p>
     *
     * @return A string that contains the index of the player with the highest score and their name
     */

    public String getMaxScore() {
        Player winner = scoresManager.poll();
        if (winner == null) {
            return "No winner";
        } else {
            return winner.get_index() + ":" + winner.get_name();
        }

    }


    /**
     * The getCommunicationHandler function returns the communicationHandler object.
     * <p>
     *
     * @return The communication handler object
     */
    public HostCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    /**
     * The getCurrentPlayerScore function returns the current player's score.
     * <p>
     *
     * @return The score of the current player
     */
    @Override
    public int getCurrentPlayerScore() {
        return player.get_score();
    }

    /**
     * The getCurrentPlayerHand function returns the current player's hand.
     * <p>
     *
     * @return The current player's hand
     */
    @Override
    public List<Tile> getCurrentPlayerHand() {
        return player.get_hand();
    }

    /**
     * The getBoardState function returns the current state of the board.
     * <p>
     *
     * @return The board state
     */
    @Override
    public Tile[][] getBoardState() {
        return Board.getBoard().getTiles();
    }

    /**
     * The setPlayerProperties function sets the player's name, socketID, and tileLottery.
     * <p>
     *
     * @param name Set the name of the player
     */
    @Override
    public void setPlayerProperties(String name) {
        player.set_name(name);
        player.set_socketID(null);
        player.setTileLottery();
        players.add(player);
    }

    /**
     * The requestChallengeActivation function is called when a player requests to activate a challenge.
     * The function sets the challengeInfo variable to the challengerIndex:word that was sent from the client,
     * and then checks if there is already an active challenge. If there isn't, it sets the flag for an active
     * challenge to be true. If there is already an active challenge, it sends back a message saying so and does nothing else.
     * <p>
     *
     * @param challengeInfo Set the challengeinfo variable
     */
    public void requestChallengeActivation(String challengeInfo) {
        String challengerIndex = this.getChallengeInfo().split(":")[0];
        // Set the challengeRequested flag to indicate a challenge is requested
        if (challengeActivated.get()) {
            if ((players.get(Integer.parseInt(challengerIndex)).get_socketID() != null)) {
                communicationServer.updateSpecificPlayer(players.get(Integer.parseInt(challengerIndex)).get_socketID(), "challengeAlreadyActivated");
            } else {
                setChanged();
                notifyObservers("challengeAlreadyActivated");
            }
        } else {
            challengeActivated.set(true);
            this.setChallengeInfo(challengeInfo);

        }
    }

    /**
     * The getPlayersScores function returns an array of strings, each string containing the score of a player.
     * <p>
     *
     * @return An array of strings
     */
    public String[] getPlayersScores() {
        String[] scores = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            scores[i] = String.valueOf(players.get(i).get_score());
        }
        return scores;
    }

    private static class HostModelHelper {
        public static final BS_Host_Model model_instance = new BS_Host_Model();
    }
}



