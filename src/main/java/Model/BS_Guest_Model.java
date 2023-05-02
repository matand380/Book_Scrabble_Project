package Model;

import Model.GameData.Board;
import Model.GameData.Tile;

import java.util.List;
import java.util.Observable;

public class BS_Guest_Model extends Observable implements BS_Model {

    Board board;
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
