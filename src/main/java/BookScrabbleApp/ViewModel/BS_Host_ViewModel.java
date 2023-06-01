package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.BookScrabbleHostFacade;
import BookScrabbleApp.Model.GameData.Tile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BS_Host_ViewModel extends Observable implements Observer, BS_ViewModel{

    SimpleStringProperty ip;
    SimpleStringProperty port;

    BookScrabbleHostFacade hostFacade;
    StringProperty winnerProperty = new SimpleStringProperty(); //winner
    List<ViewableTile> viewableHand = new ArrayList<>(); //player hand
    List<List<ViewableTile>> viewableBoard; //game board
    List<SimpleStringProperty> viewableScores = new ArrayList<>(); // scores array

    public BS_Host_ViewModel() {
        super();
        hostFacade = new BookScrabbleHostFacade();
        hostFacade.addObserver(this);
        initializeBoard();
        initializeHand();
        initializeScores();


    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void initializeBoard() {
        viewableBoard = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            List<ViewableTile> row = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                row.add(new ViewableTile(hostFacade.getBoardState()[i][j].getLetter(), hostFacade.getBoardState()[i][j].getScore()));
            }
            viewableBoard.add(row);
        }


    }

    @Override
    public void initializeHand() {
        viewableHand = new ArrayList<>();
        for (Tile tile : hostFacade.getCurrentPlayerHand()) {
            viewableHand.add(new ViewableTile(tile.getLetter(), tile.getScore()));
        }

    }

    @Override
    public void initializeScores() {
        viewableScores = new ArrayList<>();
        for (int i = 0; i < hostFacade.getPlayers().size(); i++) {
            viewableScores.add(new SimpleStringProperty(String.valueOf(hostFacade.getPlayers().get(i).get_score())));
        }

    }

    @Override
    public void initializeCommunicationAttributes() {
        ip = new SimpleStringProperty();
        port = new SimpleStringProperty();
    }

    @Override
    public void openSocket() {
        hostFacade.openSocket(ip.get(), Integer.parseInt(port.get()));
    }
}
