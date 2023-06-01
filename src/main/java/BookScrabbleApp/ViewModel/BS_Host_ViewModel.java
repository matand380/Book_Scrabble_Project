package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.BookScrabbleHostFacade;
import BookScrabbleApp.Model.GameData.Tile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;
import java.util.function.Consumer;

public class BS_Host_ViewModel extends Observable implements Observer, BS_ViewModel {

    public SimpleStringProperty ip;
    public SimpleStringProperty port;

    public SimpleStringProperty challengeWord;
    public StringProperty winnerProperty; //winner
    public List<ViewableTile> viewableHand; //player hand
    public List<List<ViewableTile>> viewableBoard; //game board
    public List<SimpleStringProperty> viewableScores; // scores array

    private Map<String, Consumer<String>> updatesMap;

    BookScrabbleHostFacade hostFacade;

    public BS_Host_ViewModel() {
        super();
        hostFacade = new BookScrabbleHostFacade();
        hostFacade.addObserver(this);
        initializeProperties();
        initializeUpdateMap();

    }

    @Override
    public void initializeUpdateMap() {
        updatesMap.put("hand updated", message -> {
            //The hand of the player is updated
            setHand();
        });

        // FIXME: 30/05/2023: tileBoard or board?
        updatesMap.put("tileBoard updated", message -> {
            setBoard();
        });

        updatesMap.put("passTurn", message -> {
            String[] messageSplit = message.split(":");
            int playerIndex = Integer.parseInt(messageSplit[1]);
            //The turn is updated
            passTurn(playerIndex);
        });

        updatesMap.put("wordsForChallenge", message -> {
            //Words for challenge is coming
            // TODO: 30/05/2023 update the player with the words challenge in the view
        });

        updatesMap.put("playersScores updated", message -> {
            //The scores of the players are updated
            // TODO: 30/05/2023 update the scores in the view
        });

        updatesMap.put("invalidWord", message -> {
            //The word is invalid
            // TODO: 30/05/2023 pop up the word is invalid in the view
        });

        updatesMap.put("challengeAlreadyActivated", message -> {
            //The challenge is already activated
            // TODO: 30/05/2023 pop up the 'challenge is already activated' in the view
        });

        updatesMap.put("winner", message -> {
            // TODO: 30/05/2023 pop up the winner in the view
        });

        updatesMap.put("endGame", message -> {
            // TODO: 30/05/2023  show the winner in the view
            // TODO: 30/05/2023  show end game window in the view
        });

        // FIXME: 30/05/2023 check if we need this function
        updatesMap.put("sortAndSetIndex", message -> {
            //The index of the player is updated
            //??????
        });

        // FIXME: 30/05/2023 check if we need this function
        updatesMap.put("challengeSuccess", message -> {
            //The challenge is successful
            //pop up the challenge is successful in the view
            //?????
        });
    }

    @Override
    public void setBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                viewableBoard.get(i).get(j).setLetter(hostFacade.getBoardState()[i][j].getLetter());
                viewableBoard.get(i).get(j).setScore(hostFacade.getBoardState()[i][j].getScore());
            }
        }
        setChanged();
        notifyObservers();


    }

    @Override
    public void initializeProperties() {
        updatesMap = new HashMap<>();
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
        setChanged();
        notifyObservers();

    }

    @Override
    public void setScore() {
        for (int i = 0; i < hostFacade.getPlayers().size(); i++) {
            viewableScores.get(i).setValue(String.valueOf(hostFacade.getPlayers().get(i).get_score()));
        }
        setChanged();
        notifyObservers();

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
        hostFacade.setPlayerProperties(name);

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
