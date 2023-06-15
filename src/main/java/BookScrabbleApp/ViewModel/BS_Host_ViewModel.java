package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.*;
import BookScrabbleApp.Model.GameData.*;
import javafx.beans.property.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;

public class BS_Host_ViewModel extends Observable implements BS_ViewModel {

    public SimpleStringProperty ip;

    public SimpleStringProperty port;

    public SimpleStringProperty challengeWord; //challenge word
    public StringProperty winnerProperty; //winner
    public List<ViewableTile> viewableHand; //player hand
    public List<List<ViewableTile>> viewableBoard; //game board
    public List<SimpleStringProperty> viewableScores; // scores array
    public List<SimpleStringProperty> viewableName; //score array

    public List<SimpleStringProperty> viewableWordsForChallenge; // words for challenge array
    public BookScrabbleHostFacade hostFacade;
    private Map<String, Consumer<String>> updatesMap; //map of all the updates

    ExecutorService executor = Executors.newFixedThreadPool(3);


    /**
     * The BS_Host_ViewModel function is the constructor for the BS_Host_ViewModel class.
     * It initializes a new BookScrabbleHostFacade object and adds itself as an observer to it.
     * It also calls two other functions, initializeProperties() and initializeUpdateMap(), which are explained below.
     * initializeProperties() initializes all the properties of the class.
     * initializeUpdateMap() initializes the updatesMap, which is a map of all the updates.
     */
    public BS_Host_ViewModel() {
        super();
        hostFacade = new BookScrabbleHostFacade();
        hostFacade.addObserver(this);
        initializeProperties();
        initializeUpdateMap();

    }

    /**
     * The initializeUpdateMap function is a function that initializes the updatesMap.
     * The updatesMap is a map that contains all of the possible messages from the facade to this viewModel, and their corresponding actions.
     * For example: if we get an update saying &quot;hand updated&quot;, then we will call setHand() in order to update our hand accordingly.
     */
    @Override
    public void initializeUpdateMap() {
        updatesMap.put("hand updated", message -> {
            //The hand of the player is updated
            setHand();
        });

        updatesMap.put("tileBoard updated", message -> {
            setBoard();
        });

        updatesMap.put("turnPassed", message -> {
            String[] messageSplit = message.split(":");
            int playerIndex = Integer.parseInt(messageSplit[1]);
            //The turn is updated
            setChanged();
            notifyObservers("turnPassed:" + playerIndex);
        });

        updatesMap.put("wordsForChallenge", message -> {
            //"wordsForChallenge:" + currentPlayerWords.size() + ":" + words
            String[] messageSplit = message.split(":");
            int playerIndex = Integer.parseInt(messageSplit[1]);
            int wordsAmount = Integer.parseInt(messageSplit[2]);
            String words = messageSplit[3];
            String[] wordsSplit = words.split(",");
            List<String> wordsList = new ArrayList<>();
            for (int i = 0; i < wordsAmount; i++) {
                wordsList.add(wordsSplit[i]);
            }
            if (playerIndex != hostFacade.getPlayer().get_index()) {
                setWordsForChallenge(wordsList);
                wordsList.clear();
                setChanged();
                notifyObservers("wordsForChallenge updated:" + playerIndex);
            }
            else
            {
                wordsList.clear();
            }
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
            String score = messageSplit[1];
            winnerProperty.setValue("The winner is: " + winner + " with a score of " + score);
            setChanged();
            notifyObservers("winner updated");
        });

        updatesMap.put("endGame", message -> {
            // TODO: 01/06/2023 all the clients are disconnected, view should enable the end game button
            setChanged();
            notifyObservers("endGame");
        });

        updatesMap.put("playersName", message -> {
            String[] messageSplit = message.split(":");
            int size = Integer.parseInt(messageSplit[1]);
            for (int i = 0; i < size; i++) {
                viewableName.add(new SimpleStringProperty());
                viewableName.get(i).setValue(messageSplit[i + 2]);
            }
            setChanged();
            notifyObservers("playersName updated");
        });

        updatesMap.put("challengeSuccess", message -> {
            setChanged();
            notifyObservers("challengeSuccess");
        });

        updatesMap.put("endGameHost", message -> {
            setChanged();
            notifyObservers("endGameHost");
        });
    }

    /**
     * The setWordsForChallenge function takes in a list of strings and sets the value of each viewable word to
     * one of those strings. It then notifies observers that it has changed.
     * <p>
     *
     * @param wordsList Set the words for the challenge
     */
    public void setWordsForChallenge(List<String> wordsList) {
        for (int i = 0; i < wordsList.size(); i++) {
            viewableWordsForChallenge.add(new SimpleStringProperty());
            viewableWordsForChallenge.get(i).setValue(wordsList.get(i));
        }
    }

    /**
     * The setBoard function is used to update the viewableBoard with the current state of
     * the board. This function is called by HostFacade whenever a change has been made to
     * the board, and it notifies all observers that there has been a change.
     * <p>
     */
    @Override
    public void setBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (hostFacade.getBoardState()[i][j] != null) {
                    viewableBoard.get(i).get(j).setLetter(hostFacade.getBoardState()[i][j].getLetter());
                    viewableBoard.get(i).get(j).setScore(hostFacade.getBoardState()[i][j].getScore());
                }
            }
        }
        setChanged();
        notifyObservers("tileBoard updated");
    }

    /**
     * The initializeProperties function initializes all of the properties that are used in the GUI.
     * It also creates a HashMap called updatesMap, which is used to store information about what has been updated
     * and needs to be displayed on the GUI. The function also creates an ArrayList called viewableScores, which stores
     * information about each player's score and name. This list is then bound to a list of textField in MainGUI so that it can be displayed there.
     */
    @Override
    public void initializeProperties() {
        updatesMap = new HashMap<>();
        winnerProperty = new SimpleStringProperty();
        challengeWord = new SimpleStringProperty();
        viewableWordsForChallenge = new ArrayList<>();
        viewableHand = new ArrayList<>();
        ip = new SimpleStringProperty();
        port = new SimpleStringProperty();
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
        viewableName = new ArrayList<>();
        ip = new SimpleStringProperty();
        port = new SimpleStringProperty();
    }

    /**
     * The setHand function is used to update the viewable hand of the player.
     */
    @Override
    public void setHand() {
        for (int i = 0; i < hostFacade.getPlayer().get_hand().size(); i++) {
            viewableHand.get(i).setLetter(hostFacade.getPlayer().get_hand().get(i).getLetter());
            viewableHand.get(i).setScore(hostFacade.getPlayer().get_hand().get(i).getScore());
        }
        setChanged();
        notifyObservers("hand updated");

    }

    /**
     * The setScore function is used to update the score of each player in the game.
     * It does this by iterating through a list of players and updating their scores accordingly.
     * It then notifies all observers that there has been a change in order to update the GUI.
     */
    @Override
    public void setScore() {
        for (int i = 0; i < hostFacade.getPlayers().size(); i++) {
            viewableScores.add(new SimpleStringProperty());
            viewableScores.get(i).setValue(String.valueOf(hostFacade.getPlayers().get(i).get_score()));
        }
        setChanged();
        notifyObservers("scores updated");
    }


    /**
     * The openSocket function opens a socket connection to the server.
     * It does this by taking in the IP address and port number from the GUI and passing them to the HostFacade.
     */
    @Override
    public void openSocket() {
        hostFacade.openSocket(ip.getValue(), Integer.parseInt(port.getValue()));
    }

    /**
     * The tryPlaceWord function is used to place a word on the board.
     * It does this by taking in the word, row, column, and orientation from the GUI and passing them to the HostFacade.
     * <p>
     *
     * @param word       Pass the word that is being placed on the board
     * @param row        Specify the row where the word is to be placed
     * @param col        Determine the column of the board where the word will be placed
     * @param isVertical Determine if the word is placed vertically or horizontally
     */
    @Override
    public void tryPlaceWord(String word, int row, int col, boolean isVertical) {
        executor.submit(()->hostFacade.tryPlaceWord(word, row, col, isVertical));
    }

    /**
     * The passTurn function is called when the player clicks on the &quot;Pass&quot; button.
     * It calls a function in the hostFacade that will pass turn to another player.
     */
    @Override
    public void passTurn() {
        int playerIndex = hostFacade.getPlayer().get_index();
        //will be called from the view
        hostFacade.passTurn(playerIndex);
    }

    /**
     * The setPlayerProperties function is called from the view with the TextField value of the player name.
     * It then calls a function in the hostFacade that will set the name of the player.
     * <p>
     *
     * @param name Set the name of the player
     */
    @Override
    public void setPlayerProperties(String name) {
        //will be called from the view with the TextField value of the player name
        hostFacade.setPlayerProperties(name);

    }

    /**
     * The startNewGame function is called when the user clicks on the &quot;Start New Game&quot; button.
     * It calls a function in HostFacade that will start a new game.
     */
    @Override
    public void startNewGame() {
        hostFacade.startNewGame();
    }

    /**
     * The challengeRequest function is called when the user clicks on the challenge button.
     * It sends a request to the server to activate a challenge, and then clears all the viewable words for challenge.
     *
     * @param challengeWord Pass the challenge word to the server
     */
    @Override
    public void challengeRequest(String challengeWord) {
        int playerIndex = hostFacade.getPlayer().get_index();
        String challengeRequest = playerIndex + ":" + challengeWord;
        hostFacade.requestChallengeActivation(challengeRequest);
        viewableWordsForChallenge.clear(); // TODO: 01/06/2023 check if we need this
    }

    /**
     * The endGame function is called when the game has ended, after winner was displayed.
     * It calls a function in HostFacade that will end the game (the &quot;End Game&quot; button will be enabled only when all clients are disconnected).
     */
    @Override
    public void endGame() {
        hostFacade.endGame();
    }

    /**
     * The update function is called by the observable object when it changes.
     * The update function then calls the appropriate method in this class to handle that change.
     * <p>
     *
     * @param o   Determine the type of observable object that is being passed in
     * @param arg Pass the message from the observable to this observer
     *            private void updateplayerlist(string message) {
     *            string[] playerlist = message
     */
    @Override
    public void update(Observable o, Object arg) {
//        if (o instanceof BookScrabbleHostFacade) {
        String message = (String) arg;
        String[] messageSplit = message.split(":");
        String updateType = messageSplit[0];
        System.out.println("HostViewModel ---- updateType: " + updateType);
        if (updatesMap.containsKey(updateType)) {
          executor.submit(()->  updatesMap.get(updateType).accept(message));
        } else {
            setChanged();
            notifyObservers("Error in updates handling ");
        }
//        }
    }

    @Override
    public int getPlayerIndex() {
        return hostFacade.getPlayer().get_index();
    }

    @Override
    public Observable getObservable() {
        return this;
    }

    @Override
    public SimpleStringProperty getChallengeWord() {
        return this.challengeWord;
    }

    @Override
    public StringProperty getWinnerProperty() {
        return this.winnerProperty;
    }

    @Override
    public List<ViewableTile> getViewableHand() {
        return this.viewableHand;
    }

    @Override
    public List<List<ViewableTile>> getViewableBoard() {
        return this.viewableBoard;
    }

    @Override
    public List<SimpleStringProperty> getViewableScores() {
        return this.viewableScores;
    }

    @Override
    public List<SimpleStringProperty> getViewableWordsForChallenge() {
        return this.viewableWordsForChallenge;
    }

    public void startHostServer(int port) {
        hostFacade.setCommunicationServer(port);
        new Thread(() -> hostFacade.getCommunicationServer().start()).start();
    }

    @Override
    public List<SimpleStringProperty> getViewableNames() {
        return this.viewableName;
    }

    @Override
    public void unPark(){
        hostFacade.unPark();
    }

    @Override
    public boolean isHost(){
        return true;
    }
}
