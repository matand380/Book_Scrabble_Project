package Model;

import Model.GameData.Tile;

import java.util.List;
import java.util.Observable;

public class BS_Host_Model extends Observable implements BS_Model {


    @Override
    public void passTurn() {

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
    public Tile[][] getBoardState() {
        return new Tile[0][];
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
