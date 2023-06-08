package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.Model.BookScrabbleGuestFacade;
import javafx.beans.property.*;

import java.util.*;
import java.util.function.*;

public class BS_Guest_ViewModel extends Observable implements Observer, BS_ViewModel {

    //ip and port of the host
    public SimpleStringProperty hostIp; //ip of the host
    public SimpleStringProperty hostPort; //port of the host

    //player properties
    BookScrabbleGuestFacade guestFacade;
    public List<ViewableTile> viewableHandGuest; //hand of the player
    public List<List<ViewableTile>> viewableBoard; //game board
    public List<SimpleStringProperty> viewableScores; //score array
    public SimpleStringProperty challengeWord; //word for challenge
    public List<SimpleStringProperty> viewableWordsForChallenge; //words for challenge
    public StringProperty winnerProperty; //winner of the game

    //map of the commands from the client
    private Map<String, Consumer<String>> updatesMap = new HashMap<>();

    /**
     * The BS_Guest_ViewModel function is the constructor for the BS_Guest_ViewModel class.
     * It initializes a new BookScrabbleGuestFacade object and adds itself as an observer to it.
     * It also calls two other functions,
     * 1. initializeProperties() initializes all the properties of the class.
     * 2. initializeUpdateMap() initializes the updatesMap, which is a map of all the updates from the guest.
     */
    public BS_Guest_ViewModel() {
        guestFacade = new BookScrabbleGuestFacade();
        guestFacade.addObserver(this);
        initializeProperties();
        initializeUpdateMap();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BookScrabbleGuestFacade) {
            String key = (String) arg;
            String[] message = key.split(":");
            String command = message[0];
            if (updatesMap.containsKey(command))
                updatesMap.get(command).accept(key);
            else
                System.out.println("Command not found");
        }
    }

    /**
     * The setBoard function is used to update the viewableBoard with the current state of
     * the board. This function is called by HostFacade whenever a change has been made to
     * the board, and it notifies all observers that there has been a change.
     */
    @Override
    public void setBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                viewableBoard.get(i).get(j).setLetter(guestFacade.getBoardState()[i][j].getLetter());
                viewableBoard.get(i).get(j).setScore(guestFacade.getBoardState()[i][j].getScore());
            }
        }
        setChanged();
        notifyObservers("board updated");
    }

    /**
     * The setHand function is used to update the viewable hand of the guest player.
     */
    @Override
    public void setHand() {
        for (int i = 0; i < guestFacade.getPlayer().get_hand().size(); i++) {
            viewableHandGuest.get(i).setLetter(guestFacade.getPlayer().get_hand().get(i).getLetter());
            viewableHandGuest.get(i).setScore(guestFacade.getPlayer().get_hand().get(i).getScore());
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
        for (int i = 0; i < guestFacade.getPlayersScores().length; i++) {
            viewableScores.get(i).setValue(guestFacade.getPlayersScores()[i]);
        }
        setChanged();
        notifyObservers("scores updated");
    }


    /**
     * The openSocket function opens a socket connection to the host.
     */
    @Override
    public void openSocket() {
        guestFacade.openSocket(hostIp.get(), Integer.parseInt(hostPort.get()));
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
        guestFacade.tryPlaceWord(word, row, col, isVertical);
    }

    /**
     * The passTurn function is called when the player clicks on the &quot;Pass&quot; button.
     * It calls a function in the hostFacade that will pass turn to another player.
     */
    @Override
    public void passTurn() {
        int playerIndex = guestFacade.getPlayer().get_index();
        guestFacade.passTurn(playerIndex);
    }

    /**
     * The challengeRequest function is called when the user clicks on the challenge button.
     * It sends a request to the server to activate a challenge, and then clears all of the viewable words for challenge.
     * @param challengeWord Pass the challenge word to the server
     */
    @Override
    public void challengeRequest(String challengeWord) {
        int playerIndex = guestFacade.getPlayer().get_index();
        String challengeRequest = playerIndex + ":" + challengeWord;
        guestFacade.challengeWord(challengeRequest);
        viewableWordsForChallenge.clear();
    }

    /**
     * The endGame function is called when the game has ended, after winner was displayed.
     * It calls a function in GuestFacade that will end the game (the &quot;End Game&quot; button will be enabled only when all clients are disconnected).
     */
    @Override
    public void endGame() {
        guestFacade.endGame();
    }

    /**
     * The setPlayerProperties function is used to set the player properties for a guest.
     *
     * @param  name Set the player's name
     */
    @Override
    public void setPlayerProperties(String name) {
        guestFacade.setPlayerProperties(name);
    }

    @Override
    public void setWordsForChallenge(List<String> wordsList) {
        for (int i = 0; i < wordsList.size(); i++) {
            viewableWordsForChallenge.add(new SimpleStringProperty(wordsList.get(i)));
        }
        setChanged();
        notifyObservers("wordsForChallenge updated");
    }

    /**
     * The initializeProperties function initializes the properties of the guest player.
     * It sets up all the viewable tiles for both the board and hand, as well as setting up a list to hold scores.
     * The function also initializes a string property that will be used to display the score of all the players,
     * Finally, it creates an arraylist that will hold words for challenges.
     */
    @Override
    public void initializeProperties() {
        //initialize the updatesMap
        updatesMap = new HashMap<>();

        //ip and port of the host
        hostIp = new SimpleStringProperty();
        hostPort = new SimpleStringProperty();

        //player properties
        viewableHandGuest = new ArrayList<>();
        viewableBoard = new ArrayList<>();
        viewableScores = new ArrayList<>();

        //challenge properties
        challengeWord = new SimpleStringProperty();
        viewableWordsForChallenge = new ArrayList<>();

        //winner property
        winnerProperty = new SimpleStringProperty();

        //initialize the hand of the player
        for (int i = 0; i < 7; i++) {
            viewableHandGuest.add(new ViewableTile(' ', 0));
        }

        //initialize the board
        for (int i = 0; i < 15; i++) {
            viewableBoard.add(new ArrayList<>());
            for (int j = 0; j < 15; j++) {
                viewableBoard.get(i).add(new ViewableTile(' ', 0));
            }
        }
    }

    /**
     * The initializeUpdateMap function is a function that initializes the viewCommandsFromClient map.
     * The viewCommandsFromClient map is a HashMap that maps String keys to Consumer&lt;String&gt; values.
     * The initializeUpdateMap function adds key-value pairs to the viewCommandsFromClient map,
     * where each key-value pair represents an action and its corresponding method call.
     */
    @Override
    public void initializeUpdateMap() {
        updatesMap.put("hand updated", message -> {
            //The hand of the player is updated
            // TODO: 30/05/2023 update the hand in the view
            setHand();
        });

        // FIXME: 30/05/2023: tileBoard or board?
        updatesMap.put("tileBoard updated", message -> {
            //The board is updated
            // TODO: 30/05/2023 update the board in the view
            setBoard();
        });

        updatesMap.put("turnPassed", message -> {
            //The turn is updated
            // TODO: 30/05/2023 update the turn in the view
            // TODO: 30/05/2023 test if the player is the current player and if so, enable the buttons
            String[] messageSplit = message.split(":");
            int playerIndex = Integer.parseInt(messageSplit[1]);
            //The turn is updated
            setChanged();
            notifyObservers("turnPassed:" + playerIndex);
        });

        updatesMap.put("wordsForChallenge", message -> {
            //Words for challenge is coming
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

        updatesMap.put("winner", message -> {
            // TODO: 30/05/2023 pop up the winner in the view
            String[] messageSplit = message.split(":");
            int playerIndex = Integer.parseInt(messageSplit[1]);
            String winner = messageSplit[2];
            String score = guestFacade.getPlayersScores()[playerIndex];
            winnerProperty.setValue("The winner is: " + winner + "with a score of " + score);
            setChanged();
            notifyObservers("winner updated");
        });

        updatesMap.put("endGame", message -> {
            endGame();
            setChanged();
            notifyObservers("endGame");
        });

        updatesMap.put("invalidWord", message -> {
            //The word is invalid
            setChanged();
            notifyObservers("invalidWord");
        });

        updatesMap.put("challengeAlreadyActivated", message -> {
            //The challenge is already activated
            setChanged();
            notifyObservers("challengeAlreadyActivated");
        });

        updatesMap.put("challengeSuccess", message -> {
            //send to the view challenge is successful
            setChanged();
            notifyObservers("challengeSuccess");
        });

        // FIXME: 30/05/2023 check if we need this function
        updatesMap.put("sortAndSetIndex", message -> {
            //The index of the player is updated
            //??????
        });
    }
}