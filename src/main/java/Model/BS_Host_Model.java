package Model;

import Model.GameData.*;
import Model.GameLogic.DictionaryManager;
import Model.GameLogic.HostCommunicationHandler;
import Model.GameLogic.MyServer;

import java.util.*;

public class BS_Host_Model extends Observable implements BS_Model {
    private static BS_Host_Model model_instance = null;
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

    }

    public static BS_Host_Model getModel() {
        if (model_instance == null)
            return model_instance = new BS_Host_Model();
        return model_instance;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void startNewGame() {

    }

    @Override
    public void passTurn() {

    }

    @Override
    public void tryPlaceWord() {

    }

    @Override
    public void challengeWord() {

    }

    @Override
    public void setCurrentPlayerIndex(int index) {

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


