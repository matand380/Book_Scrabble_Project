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
    Board board; //rethink if we need this
    Tile.Bag bag;//rethink if we need this
    Player player; // TODO: 04/05/2023 implement player class and send it to the host

    public ClientCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    ClientCommunicationHandler communicationHandler;
    Tile[][] boardTiles;
    private BS_Guest_Model() {
        socket = new Socket();
        player = new Player();
         communicationHandler = new ClientCommunicationHandler();

    }

    public static BS_Guest_Model getModel() {
        if (model_instance == null)
            return model_instance = new BS_Guest_Model();
        return model_instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void passTurn(int id) {
        communicationHandler.outMessages("passTurn:"+id);
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
    public void setNextPlayerIndex(int index) {

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
    public void setGameOver(boolean isGameOver) {

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
