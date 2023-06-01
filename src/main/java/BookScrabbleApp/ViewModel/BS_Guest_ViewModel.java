package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.Model.BookScrabbleGuestFacade;

import java.util.*;
import java.util.function.*;

public class BS_Guest_ViewModel extends Observable implements Observer, BS_ViewModel {

    BookScrabbleGuestFacade guestFacade;
    String winner;
    private Map<String, Consumer<String>> viewCommandsFromClient = new HashMap<>();

    public BS_Guest_ViewModel() {
        viewCommandsFromClient.put("hand updated", message -> {
            //The hand of the player is updated

            // TODO: 30/05/2023 update the hand in the view
        });

        // FIXME: 30/05/2023: tileBoard or board?
        viewCommandsFromClient.put("tileBoard updated", message -> {
            //The board is updated
            // TODO: 30/05/2023 update the board in the view
        });

        viewCommandsFromClient.put("turnPassed", message -> {
            //The turn is updated
            // TODO: 30/05/2023 update the turn in the view
            // TODO: 30/05/2023 test if the player is the current player and if so, enable the buttons
        });

        viewCommandsFromClient.put("wordsForChallenge", message -> {
            //Words for challenge is coming
            // TODO: 30/05/2023 update the player with the words challenge in the view
        });

        viewCommandsFromClient.put("playersScores updated", message -> {
            //The scores of the players are updated
            // TODO: 30/05/2023 update the scores in the view
        });

        viewCommandsFromClient.put("invalidWord", message -> {
            //The word is invalid
            // TODO: 30/05/2023 pop up the word is invalid in the view
        });

        viewCommandsFromClient.put("challengeAlreadyActivated", message -> {
            //The challenge is already activated
            // TODO: 30/05/2023 pop up the 'challenge is already activated' in the view
        });

        viewCommandsFromClient.put("winner", message -> {
            // TODO: 30/05/2023 pop up the winner in the view
        });

        viewCommandsFromClient.put("endGame", message -> {
            // TODO: 30/05/2023  show the winner in the view
            // TODO: 30/05/2023  show end game window in the view
        });

        // FIXME: 30/05/2023 check if we need this function
        viewCommandsFromClient.put("sortAndSetIndex", message -> {
            //The index of the player is updated
            //??????
        });

        // FIXME: 30/05/2023 check if we need this function
        viewCommandsFromClient.put("challengeSuccess", message -> {
            //The challenge is successful
            //pop up the challenge is successful in the view
            //?????
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == BS_Host_Model.getModel())
            if (arg instanceof String) {
                String key = (String) arg;
                String[] message = key.split(":");
                String command = message[0];
                if (viewCommandsFromClient.containsKey(command))
                    viewCommandsFromClient.get(command).accept(key);
                else
                    System.out.println("Command not found");
            }


    }

    @Override
    public void setBoard() {

    }

    @Override
    public void setHand() {

    }

    @Override
    public void setScore() {

    }

    @Override
    public void initializeProperties() {

    }

    @Override
    public void openSocket() {

    }

    @Override
    public void tryPlaceWord(String word, int row, int col, boolean isVertical) {

    }

    @Override
    public void challengeRequest(String challengeWord) {

    }

    @Override
    public void endGame() {

    }

    @Override
    public void initializeUpdateMap() {
        //put here all the "put" of the map
    }

    @Override
    public void passTurn(int playerIndex) {

    }

    @Override
    public void setPlayerProperties(String name) {

    }

    @Override
    public void setWordsForChallenge(List<String> wordsList) {

    }

}
