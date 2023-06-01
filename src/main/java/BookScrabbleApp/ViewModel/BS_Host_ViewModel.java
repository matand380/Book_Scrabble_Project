package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.BookScrabbleHostFacade;
import BookScrabbleApp.Model.GameData.Tile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BS_Host_ViewModel extends Observable implements Observer, BS_ViewModel {

    public SimpleStringProperty ip;
    public SimpleStringProperty port;

    public SimpleStringProperty challengeWord;
    public StringProperty winnerProperty; //winner
    public List<ViewableTile> viewableHand; //player hand
    public List<List<ViewableTile>> viewableBoard; //game board
    public List<SimpleStringProperty> viewableScores; // scores array
    BookScrabbleHostFacade hostFacade;

    public BS_Host_ViewModel() {
        super();
        hostFacade = new BookScrabbleHostFacade();
        hostFacade.addObserver(this);

    }

    @Override
    public void setBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                viewableBoard.get(i).get(j).setLetter(hostFacade.getBoardState()[i][j].getLetter());
                viewableBoard.get(i).get(j).setScore(hostFacade.getBoardState()[i][j].getScore());
            }
        }


    }

    @Override
    public void initializeProperties() {
        winnerProperty = new SimpleStringProperty();
        challengeWord = new SimpleStringProperty();
        viewableHand = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            viewableHand.add(new ViewableTile(' ', 0));
        }
        viewableBoard = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            List<ViewableTile> row = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                row.add(new ViewableTile(' ', 0));
            }
            viewableBoard.add(row);
        }
        viewableScores = new ArrayList<>();
        ip = new SimpleStringProperty();
        port = new SimpleStringProperty();


    }

    @Override
    public void setHand() {
       for (int i = 0; i < hostFacade.getPlayer().get_hand().size(); i++) {
                viewableHand.get(i).setLetter(hostFacade.getPlayer().get_hand().get(i).getLetter());
                viewableHand.get(i).setScore(hostFacade.getPlayer().get_hand().get(i).getScore());
            }

    }

    @Override
    public void setScore() {
        for (int i = 0; i < hostFacade.getPlayers().size(); i++) {
            viewableScores.get(i).setValue(String.valueOf(hostFacade.getPlayers().get(i).get_score()));
        }

    }


    @Override
    public void openSocket() {
        hostFacade.openSocket(ip.getValue(), Integer.parseInt(port.getValue()));
    }

    @Override
    public void tryPlaceWord(String word, int row, int col, boolean isVertical) {

    }

    @Override
    public void passTurn(int playerIndex) {

    }

    @Override
    public void setPlayerProperties(String name) {

    }

    public void startNewGame() {
        hostFacade.startNewGame();
    }

    @Override
    public void challengeRequest(String challengeWord) {
        int playerIndex = hostFacade.getPlayer().get_index();
        String challengeRequest = playerIndex + ":" + challengeWord;
        hostFacade.requestChallengeActivation(challengeRequest);
    }

    @Override
    public void endGame() {
        hostFacade.endGame();
    }
    @Override
    public void update(Observable o, Object arg) {

    }
}
