package Model;

import Model.GameData.*;
import Model.GameLogic.ClientCommunicationHandler;

import java.net.Socket;
import java.util.List;
import java.util.Observable;

import static java.lang.System.out;

public class BS_Guest_Model extends Observable implements BS_Model {
    private static BS_Guest_Model model_instance = null;
    Socket socket;
    Board board;
    Tile.Bag bag;
    Player player;
    ClientCommunicationHandler communicationHandler = new ClientCommunicationHandler();

    Tile[][] boardTiles;

    private BS_Guest_Model() {
        board = (Board) communicationHandler.getInstance("Board");
        bag = (Tile.Bag) communicationHandler.getInstance("Bag");
        player = (Player) communicationHandler.getInstance("Player");
    }

    public static BS_Guest_Model getModel() {
        if (model_instance == null)
            return model_instance = new BS_Guest_Model();
        return model_instance;
    }

    @Override
    public void passTurn() {
        communicationHandler.outMessages("passTurn");
    }

    @Override
    public void tryPlaceWord() {
        communicationHandler.outMessages("tryPlaceWord");
    }


    @Override
    public void challengeWord() {
        communicationHandler.outMessages("challengeWord");

    }

    @Override
    public void setCurrentPlayerIndex(int index) {

    }

    @Override
    public void setGameOver(boolean isGameOver) {

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
        return new int[0];
    }

    @Override
    public String getWinner() {
        return null;
    }

    @Override
    public boolean isHost() {
        return false;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    public void setBoard(Tile[][] boardTiles) {
        this.boardTiles = boardTiles;
        setChanged();
        notifyObservers();
    }
}
