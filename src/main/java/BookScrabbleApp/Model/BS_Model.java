package BookScrabbleApp.Model;


import BookScrabbleApp.Model.GameData.Tile;

import java.util.List;


public interface BS_Model {

    // Methods for setting data in the model
    public void setPlayerProperties(String name);
    void passTurn(int id);


    // Methods for getting data from the model
    int getCurrentPlayerScore();

    List<Tile> getCurrentPlayerHand();

    Tile[][] getBoardState();

    boolean isHost();


}
