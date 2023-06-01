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

    public List<SimpleStringProperty> viewableWordsForChallenge; // words for challenge array
    BookScrabbleHostFacade hostFacade;
    private Map<String, Consumer<String>> updatesMap; //map of all the updates


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
            //"wordsForChallenge:" + currentPlayerWords.size() + ":" + words
            String[] messageSplit = message.split(":");
            int wordsAmount = Integer.parseInt(messageSplit[1]);
            String words = messageSplit[2];
            String[] wordsSplit = words.split(",");
            List<String> wordsList = new ArrayList<>();
            for (int i = 0; i < wordsAmount; i++) {
                wordsList.add(wordsSplit[i]);
            }
            setWordsForChallenge(wordsList);
            wordsList.clear();

        });

        updatesMap.put("playersScores updated", message -> {
            //The scores of the players are updated
            setScore();
        });

        updatesMap.put("invalidWord", message -> {
            setChanged();
            notifyObservers("invalidWord");
        });

        updatesMap.put("challengeAlreadyActivated", message -> {
            //The challenge is already activated
            setChanged();
            notifyObservers("challengeAlreadyActivated");
        });

        updatesMap.put("winner", message -> {
            // TODO: 30/05/2023 pop up the winner in the view and end game button
            String[] messageSplit = message.split(":");
            int playerIndex = Integer.parseInt(messageSplit[1]);
            String winner = messageSplit[2];
            String score = hostFacade.getPlayersScores()[playerIndex];
            winnerProperty.setValue("The winner is: " + winner + "with a score of " + score);
            setChanged();
            notifyObservers();
        });

        updatesMap.put("endGame", message -> {
            // TODO: 01/06/2023 all the clients are disconnected, view should enable the end game button
            setChanged();
            notifyObservers("endGame");
        });

        // FIXME: 30/05/2023 check if we need this function
        updatesMap.put("sortAndSetIndex", message -> {
            //The index of the player is updated
            //??????
        });

        // FIXME: 30/05/2023 check if we need this function
        updatesMap.put("challengeSuccess", message -> {
            setChanged();
            notifyObservers("challengeSuccess");
        });
    }

    public void setWordsForChallenge(List<String> wordsList) {
        for (int i = 0; i < wordsList.size(); i++) {
            viewableWordsForChallenge.get(i).setValue(wordsList.get(i));
        }
        setChanged();
        notifyObservers();
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
        viewableWordsForChallenge = new ArrayList<>();
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
        // TODO: 01/06/2023 need to take care for the word binding
        hostFacade.tryPlaceWord(word, row, col, isVertical);

    }

    @Override
    public void passTurn(int playerIndex) {
        hostFacade.passTurn(playerIndex);
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
        viewableWordsForChallenge.clear(); // TODO: 01/06/2023 check if we need this
    }

    @Override
    public void endGame() {
        hostFacade.endGame();
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String) arg;
        String[] messageSplit = message.split(":");
        String updateType = messageSplit[0];
        if (updatesMap.containsKey(updateType)) {
        updatesMap.get(updateType).accept(message);
        }
        else {
            setChanged();
            notifyObservers("Error in updates handling ");
        }


    }
}
