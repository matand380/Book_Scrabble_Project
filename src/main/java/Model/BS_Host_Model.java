package Model;

import Model.GameData.Board;
import Model.GameData.Player;
import Model.GameData.Tile;
import Model.GameLogic.CommunicationHandler;
import Model.GameLogic.MyServer;

import java.util.List;
import java.util.Observable;

public class BS_Host_Model extends Observable implements BS_Model {

    CommunicationHandler communicationHandler = new CommunicationHandler();
    MyServer server;
    Board board;
    Tile.Bag bag;
    Player player;


    public BS_Host_Model() {
         board = (Board) communicationHandler.getInstance("Board");
            bag = (Tile.Bag) communicationHandler.getInstance("Bag");
            player = (Player) communicationHandler.getInstance("Player");


    }

    public void hostPlayer(String name) {


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
        return bag.getBag()._quantitiesCounter;
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
