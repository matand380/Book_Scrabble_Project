package Model;

import Model.GameData.*;
import Model.GameLogic.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class BS_Host_Model extends Observable implements BS_Model {
    public ArrayList<Word> currentPlayerWords;
    public int currentPlayerIndex = 0;
    HostCommunicationHandler communicationHandler = new HostCommunicationHandler();
    MyServer communicationServer;
    Socket gameSocket;
    Board board;
    Tile.Bag bag;
    Player player;
    private List<Player> players;
    private boolean isGameOver;

    private BS_Host_Model() {

        //Game data initialization
        setGameOver(false);
        board = Board.getBoard();
        bag = Tile.Bag.getBag();
        players = new ArrayList<>();
        Player hostPlayer = new Player();

        //Communication initialization
        try {
            gameSocket = new Socket("localhost", 5555);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Enter port number for communication server: ");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        communicationServer = new MyServer(port, communicationHandler);
        System.out.println("Server local ip: " + communicationServer.ip() + "\n" + "Server public ip: " + communicationServer.getPublicIp() + "\n" + "Server port: " + port);
        communicationServer.start();

        //only for testing
        Player player1 = new Player();
        player1.set_name("player1");
        List<Tile> hand = new ArrayList<>();
        Tile h = bag.getTile('H');
        Tile o = bag.getTile('O');
        Tile r = bag.getTile('R');
        Tile n = bag.getTile('N');
        hand.add(h);
        hand.add(o);
        hand.add(r);
        hand.add(n);
        player1.get_hand().addAll(hand);
        Player player2 = new Player();
        player2.set_name("player2");
        players.add(player1);
        players.add(player2);
        currentPlayerIndex = 0;

    }

    public static BS_Host_Model getModel() {
        return HostModelHelper.model_instance;
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

    @Override
    public void setNextPlayerIndex(int index) {
        currentPlayerIndex = (index + 1) % players.size();

    }

    public int getMaxScore() {
        int max = 0;
        for (Player p : players) {
            if (p.get_score() > max)
                max = p.get_score();
        }
        return max;
    }

    /**
     * The startNewGame function is used to reset the game.
     * It returns all the tiles from each player's tileLottery back into the bag,
     * and then completes each player's hand to 7 tiles.
     */

    public void startNewGame() {
        sortAndSetID();
        //return all the tiles to the bag
//        players.forEach(p -> bag.put(p.getTileLottery()));
        players.forEach(Player::completeTilesTo7);

    }

    @Override
    public void passTurn(int id) {
        setNextPlayerIndex(currentPlayerIndex);
        board.passCounter++;
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
            // TODO: 06/05/2023 if challenge is true fine the challenger, if false give challenger bonus;
            // TODO: 07/05/2023 "challengeWord:"+id+":"+score(after fine or bonus)
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

            players.get(currentPlayerIndex).set_score(players.get(currentPlayerIndex).get_score() + score);

            hasChanged();
            System.out.println("tryPlaceWord:" + currentPlayerIndex + ":" + score);
            notifyObservers("tryPlaceWord:" + currentPlayerIndex + ":" + score);
            // TODO: 06/05/2023 send this message to all clients
            //update viewModel with new score and the currentPlayerWords
            //to be able to update the viewModel we send the name of the method that was called


        } else {
            hasChanged();
            notifyObservers("tryPlaceWord:" + currentPlayerIndex + ":" + "0");
            // TODO: 06/05/2023 send this message to all clients
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

    public String challengeWord(String word, String id) {
        BS_Host_Model.getModel().getCommunicationHandler().messagesToGameServer("C:" + word);
        String response = BS_Host_Model.getModel().getCommunicationHandler().messagesFromGameServer();
        String[] splitResponse = response.split(":");
        if (splitResponse[0].equals("C")) {
            if (splitResponse[1].equals("true")) {
                players.get(Integer.parseInt(id)).set_score(players.get(Integer.parseInt(id)).get_score() - 10);
                Board.getBoard().placeWord(currentPlayerWords.stream().filter(w -> w.toString().equals(word)).findFirst().get());
                hasChanged();
                notifyObservers("board:");
            } else if (splitResponse[1].equals("false")) {
                players.get(Integer.parseInt(id)).set_score(players.get(Integer.parseInt(id)).get_score() + 10);
            }
            hasChanged();
            notifyObservers("challengeWord:" + id + players.get(Integer.parseInt(id)).get_score());
            return "challengeWord:" + id + players.get(Integer.parseInt(id)).get_score();
        } else {
            System.out.println("Error: dictionaryLegal");
            return "";
            // TODO: 06/05/2023 activate the challenge word method in the board
            // need to change challenge in dictionary manager.
        }

    }

    /**
     * The sortAndSetID function sorts the players in ascending order by their tileLottery value,
     * and then sets each player's ID to be equal to their index in the list.
     * return Void
     */
    public void sortAndSetID() {
        players.sort(Comparator.comparing(Player::getTileLottery));
        IntStream.rangeClosed(0, players.size()).forEach(i -> players.set(i, player.set_id(i)));
        hasChanged();
        notifyObservers("sortAndSetID" + player.get_id()); // this is host id
        StringBuilder allPlayers = new StringBuilder();
        for (Player p : players) {
            allPlayers.append(p.get_id()).append(",").append(p.get_name()).append(":");
        }
        String allPlayersString = allPlayers.toString();

        BS_Host_Model.getModel().communicationServer.updateAll("sortAndSetID:" + players.size() + ":" + allPlayersString);

    }

    @Override
    public int getCurrentPlayerScore() {
        return 0;
    }

    @Override
    public List<Tile> getCurrentPlayerHand() {
        return null;
    }

    @Override
    public Tile[][] getBoardState() {
        return Board.getBoard().getTiles();
    }

    @Override
    public int[] getBagState() {
        return Tile.Bag.getBag()._quantitiesCounter;
    }

    @Override
    public String getWinner() {
        return null;
    }

    @Override
    public boolean isHost() {
        return true;
    }

    @Override
    public boolean isGameOver() {
        if (board.passCounter == getPlayers().size()) //all the players pass turns
            isGameOver = true;
        if (Tile.Bag.getBag().size() == 0)
            for (Player p : players)
                if (p.get_hand().size() == 0) {
                    isGameOver = true;
                    break;
                }
        return isGameOver;
    }

    @Override
    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    public HostCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    /**
     * The getModel function is a static function that returns the singleton instance of the BS_Host_Model class.
     * If no instance exists, it creates one and then returns it.
     * <p>
     */
    private static class HostModelHelper {
        public static final BS_Host_Model model_instance = new BS_Host_Model();
    }


}


