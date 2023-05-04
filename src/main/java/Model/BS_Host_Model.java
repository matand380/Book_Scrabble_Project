package Model;

import Model.GameData.*;
import Model.GameLogic.ClientHandler;
import Model.GameLogic.DictionaryManager;
import Model.GameLogic.HostCommunicationHandler;
import Model.GameLogic.MyServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class BS_Host_Model extends Observable implements BS_Model {
    private static BS_Host_Model model_instance = null;
    public ArrayList<Word> currentPlayerWords;
    HostCommunicationHandler communicationHandler = new HostCommunicationHandler();
    MyServer server;
    Board board;
    Tile.Bag bag;
    Player player;
    private DictionaryManager dictionaryManager;
    private List<Player> players;
    private int currentPlayerIndex;
    private int maxScore;
    private boolean isGameOver;

    private BS_Host_Model() {
        board = Board.getBoard();
        bag = Tile.Bag.getBag();
        players = new ArrayList<>();
        dictionaryManager = DictionaryManager.get();
        // TODO: 04/05/2023 add in the view the option to choose the port number
        //ask the host for port number
        System.out.println("Enter port number: ");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        MyServer server = new MyServer(port, communicationHandler);
        System.out.println("Server local ip: " + server.ip() + "\n" + "Server public ip: " + server.getPublicIp() + "\n" + "Server port: " + port);
        server.start();


    }

    public static BS_Host_Model getModel() {
        if (model_instance == null)
            return model_instance = new BS_Host_Model();
        return model_instance;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    @Override
    public void setNextPlayerIndex(int index) {
        currentPlayerIndex = players.indexOf(currentPlayerIndex + 1) % players.size();

    }

    public int getMaxScore() {
        return maxScore;
    }

    public void startNewGame() {

    }

    @Override
    public void passTurn(int id) throws IOException {
        setNextPlayerIndex(id);
        board.passCounter++;
        hasChanged();
        notifyObservers(getCurrentPlayerIndex());// notify host viewModel about current player
        server.updateAll(String.valueOf(getCurrentPlayerIndex()));

    }


    @Override
    public void tryPlaceWord() {
        int score = Board.getBoard().tryPlaceWord(new Word()); //change this
        if (score > 0) {
            players.get(currentPlayerIndex).set_score(players.get(currentPlayerIndex).get_score() + score);
            hasChanged();
            //update viewModel with new score and the currentPlayerWords
            //to be able to update the viewModel we send the name of the method that was called

        }
        notifyObservers("try successful");
    }


    @Override
    public void challengeWord() {

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
        return board.getBoard();
    }

    @Override
    public int[] getBagState() {
        return bag.getBag()._quantitiesCounter;
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


