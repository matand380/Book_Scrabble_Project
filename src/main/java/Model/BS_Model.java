package Model;


import Model.GameData.*;
import Model.GameLogic.*;

import java.util.List;


public interface BS_Model {

    // Methods for setting data in the model
    void passTurn();
    void setCurrentPlayerIndex(int index);
    void setGameOver(boolean isGameOver);


    // Methods for getting data from the model
    int getCurrentPlayerScore();
    List<Tile> getCurrentPlayerHand();
    Tile[][] getBoardState();
    int[] getBagState();
    String getWinner();
    boolean isHost();
    boolean isGameOver();
    boolean isConnected();


}