//package test_View;
//
//import BookScrabbleApp.View.GameWindowController;
//import BookScrabbleApp.View.TileField;
//import javafx.beans.property.SimpleStringProperty;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestGameWindowController {
//
//    @Test
//    public void testShowChallengePopup() {
//        GameWindowController controller = new GameWindowController();
//        List<SimpleStringProperty> wordsForChallenge = new ArrayList<>();
//        wordsForChallenge.add(new SimpleStringProperty("Word1"));
//        wordsForChallenge.add(new SimpleStringProperty("Word2"));
//
//        controller.showChallengePopup(wordsForChallenge);
//
//        // TODO: Verify the expected behavior of the showChallengePopup() method
//        // For example, you can check if the popup window is displayed correctly
//        // and the correct actions are performed when interacting with the popup
//    }
//
//    @Test
//    public void testPassTurn() {
//        // Create an instance of GameWindowController and any required dependencies
//        GameWindowController controller = new GameWindowController();
//        controller.
//
//
//        // Call the method
//        controller.passTurn(indexCurrentPlayer);
//
//        // Verify the expected behavior of the passTurn() method
//        Assertions.assertTrue(controller.getTryPlaceBtn().isDisabled());
//        Assertions.assertTrue(controller.getPassTurnBtn().isDisabled());
//
//        // Change the indexCurrentPlayer to a different value
//        indexCurrentPlayer = "1";
//
//        // Call the method again
//        controller.passTurn(indexCurrentPlayer);
//
//        // Verify the expected behavior of the passTurn() method
//        Assertions.assertFalse(controller.getTryPlaceBtn().isDisabled());
//        Assertions.assertFalse(controller.getPassTurnBtn().isDisabled());
//    }
//
//    @Test
//    public void testRemoveFromYourWord() {
//        // Create an instance of GameWindowController and any required dependencies
//        GameWindowController controller = new GameWindowController();
//        List<TileField> wordForTryPlace = new ArrayList<>();
//        TileField tile1 = new TileField();
//        tile1.setTileRow(1);
//        tile1.setTileCol(1);
//        tile1.setLetter(new SimpleStringProperty("A"));
//        wordForTryPlace.add(tile1);
//        TileField tile2 = new TileField();
//        tile2.setTileRow(2);
//        tile2.setTileCol(2);
//        tile2.setLetter(new SimpleStringProperty("B"));
//        wordForTryPlace.add(tile2);
//        TileField tile3 = new TileField();
//        tile3.setTileRow(3);
//        tile3.setTileCol(3);
//        tile3.setLetter(new SimpleStringProperty("C"));
//        wordForTryPlace.add(tile3);
//
//        // Call the method to remove a specific tile from wordForTryPlace
//        controller.removeFromYourWord(tile2);
//
//        // Verify that the tile is removed from wordForTryPlace
//        Assertions.assertEquals(2, wordForTryPlace.size());
//        Assertions.assertTrue(wordForTryPlace.contains(tile1));
//        Assertions.assertTrue(wordForTryPlace.contains(tile3));
//        Assertions.assertFalse(wordForTryPlace.contains(tile2));
//
//        // Try to remove a tile that does not exist in wordForTryPlace
//        TileField nonExistingTile = new TileField();
//        nonExistingTile.setTileRow(4);
//        nonExistingTile.setTileCol(4);
//        nonExistingTile.setLetter(new SimpleStringProperty("D"));
//        controller.removeFromYourWord(nonExistingTile);
//
//        // Verify that wordForTryPlace remains unchanged
//        Assertions.assertEquals(2, wordForTryPlace.size());
//        Assertions.assertTrue(wordForTryPlace.contains(tile1));
//        Assertions.assertTrue(wordForTryPlace.contains(tile3));
//        Assertions.assertFalse(wordForTryPlace.contains(nonExistingTile));
//    }
//
//    // Add more test methods for the remaining functions in GameWindowController
//
//}
//
