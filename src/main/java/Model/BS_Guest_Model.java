package Model;

import Model.GameData.*;
import Model.GameLogic.CommunicationHandler;

import java.util.List;
import java.util.Observable;

import static java.lang.System.out;

public class BS_Guest_Model extends Observable implements BS_Model {

    Board board;
    Tile.Bag bag;
    Player player;


    CommunicationHandler communicationHandler = new CommunicationHandler();

    BS_Guest_Model() {
        board = (Board) communicationHandler.getInstance("Board");
        bag = (Tile.Bag) communicationHandler.getInstance("Bag");
        player = (Player) communicationHandler.getInstance("Player");
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
}
