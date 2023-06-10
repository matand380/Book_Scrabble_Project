package BookScrabbleApp.ViewModel;

import javafx.beans.property.*;

import java.util.*;

public interface BS_ViewModel extends Observer {

    void setBoard();

    void setHand();

    void setScore();

    void initializeProperties();

    void tryPlaceWord(String word, int row, int col, boolean isVertical);

    void passTurn();

    void setPlayerProperties(String name);

    void challengeRequest(String challengeWord);

    void openSocket();

    void endGame();

    void initializeUpdateMap();

    void setWordsForChallenge(List<String> wordsList);

    void startNewGame();

    int getPlayerIndex();

    Observable getObservable();

    SimpleStringProperty getChallengeWord();
    StringProperty getWinnerProperty();
    List<ViewableTile> getViewableHand();
    List<List<ViewableTile>> getViewableBoard();
    List<SimpleStringProperty> getViewableScores();
    List<SimpleStringProperty> getViewableWordsForChallenge();

    List<SimpleStringProperty> getViewableNames();
}
