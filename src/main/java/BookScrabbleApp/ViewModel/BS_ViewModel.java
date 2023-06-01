package BookScrabbleApp.ViewModel;

public interface BS_ViewModel {

    void setBoard();

    void setHand();

    void setScore();

    void initializeProperties();

    void tryPlaceWord(String word, int row, int col, boolean isVertical);

    void passTurn(int playerIndex);

    void setPlayerProperties(String name);

    void challengeRequest(String challengeWord);

    void openSocket();

    void endGame();

    void initializeUpdateMap();


}
