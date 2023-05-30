package BookScrabbleApp.ViewModel;

import BookScrabbleApp.Model.BS_Host_Model;

import java.util.*;
import java.util.function.*;

public class BS_ViewModel extends Observable implements Observer {

    private Map<String, Consumer<String>> viewCommandsFromClient = new HashMap<>();

    public BS_ViewModel() {
        viewCommandsFromClient.put("hand updated", message -> {
            //The hand of the player is updated

            // TODO: 30/05/2023 update the hand in the view
        });

        // FIXME: 30/05/2023: tileBoard or board?
        viewCommandsFromClient.put("tileBoard updated", message -> {
            //The board is updated
            // TODO: 30/05/2023 update the board in the view
        });

        viewCommandsFromClient.put("passTurn", message -> {
            //The turn is updated
            // TODO: 30/05/2023 update the turn in the view
            // TODO: 30/05/2023 test if the player is the current player and if so, enable the buttons
        });

        viewCommandsFromClient.put("wordsForChallenge", message -> {
            //Words for challenge is coming
            // TODO: 30/05/2023 update the player with the words challenge in the view
        });

        viewCommandsFromClient.put("playersScores", message -> {
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

    String winner;

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
}
