package Model;

import Model.GameData.*;
import Model.GameLogic.*;

import java.util.*;

public class BS_Host_Model extends Observable implements BS_Model {
    private static BS_Host_Model model_instance = null;
    public ArrayList<Word> currentPlayerWords;
    public int currentPlayerIndex = 0;
    HostCommunicationHandler communicationHandler = new HostCommunicationHandler();
    MyServer server;
    Board board;
    Tile.Bag bag;
    Player player;
    private  DictionaryManager dictionaryManager;
    private  List<Player> players;
    private boolean isGameOver;

    private BS_Host_Model() {
        board = Board.getBoard();
        bag = Tile.Bag.getBag();
        players = new ArrayList<>(); // TODO: 06/05/2023 after all players are added sent the order as indices to the players
        dictionaryManager = DictionaryManager.get();
        // TODO: 04/05/2023 add in the view the option to choose the port number
        //ask the host for port number
        System.out.println("Enter port number: ");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        MyServer server = new MyServer(port, communicationHandler);
        System.out.println("Server local ip: " + server.ip() + "\n" + "Server public ip: " + server.getPublicIp() + "\n" + "Server port: " + port);
        server.start();
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
        if (model_instance == null) return model_instance = new BS_Host_Model();
        return model_instance;
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
        // TODO: 06/05/2023 send to all clients "nextPlayerIndex:"+getCurrentPlayerIndex());

    }

    public int getMaxScore() {
        int max = 0;
        for (Player p : players) {
            if (p.get_score() > max)
                max = p.get_score();
        }
        return max;
    }

    public void startNewGame() {

    }

    @Override
    public String passTurn(int id) {
        setNextPlayerIndex(currentPlayerIndex);
        board.passCounter++;
        hasChanged();
        notifyObservers("passTurn:"+getCurrentPlayerIndex());// notify host viewModel about current player
        // TODO: 06/05/2023 send to all clients "passTurn:"+getCurrentPlayerIndex());
//        server.updateAll(String.valueOf(getCurrentPlayerIndex()));
        return String.valueOf(getCurrentPlayerIndex());


    }


    public void tryPlaceWord(Word word) {
        int score = Board.getBoard().tryPlaceWord(word);
        if (score > 0) {
            // TODO: 06/05/2023 challenge pop up if someone press challange activate challengeWord method
            // TODO: 06/05/2023 if challenge is true fine the challenger, if false give challenger bonus;
            // TODO: 07/05/2023 "challengeWord:"+id+":"+score(after fine or bonus)
            //hasChanged();
            //notifyObservers("challengeWord:"+id+":"+score);
            //after challenge is done pass turn anyway
            //hasChanged();
            //notifyObservers("passTurn:"+getCurrentPlayerIndex());// notify host viewModel about current player

            players.get(currentPlayerIndex).set_score(players.get(currentPlayerIndex).get_score() + score);

            hasChanged();
            System.out.println("tryPlaceWord:"+currentPlayerIndex+":"+score);
            notifyObservers("tryPlaceWord:"+currentPlayerIndex+":"+score);
            // TODO: 06/05/2023 send this message to all clients
            //update viewModel with new score and the currentPlayerWords
            //to be able to update the viewModel we send the name of the method that was called



        } else {
            hasChanged();
            notifyObservers("tryPlaceWord:"+currentPlayerIndex+":"+"0");
            // TODO: 06/05/2023 send this message to all clients
        }
    }


    public void challengeWord(Word word) {
        // TODO: 06/05/2023 activate the challenge word method in the board
        // need to change challane in dictionary manager.

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
    public Board getBoardState() {
        return Board.getBoard();
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
        return false;
    }

    @Override
    public void setGameOver(boolean isGameOver) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }


}


