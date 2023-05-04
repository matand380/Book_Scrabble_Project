package Model;


import Model.GameData.*;
import Model.GameLogic.*;

import java.io.IOException;
import java.util.List;


public interface BS_Model {

    // Methods for setting data in the model
    void passTurn(int id) throws IOException;
    void tryPlaceWord();
    void challengeWord();
    void setNextPlayerIndex(int index);
    void setGameOver(boolean isGameOver);


    // Methods for getting data from the model
    int getCurrentPlayerScore();
    List<Tile> getCurrentPlayerHand();
    Board getBoardState();
    int[] getBagState();
    String getWinner();
    boolean isHost();
    boolean isGameOver();
    boolean isConnected();


}
