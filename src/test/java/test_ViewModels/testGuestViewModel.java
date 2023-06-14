package test_ViewModels;

import BookScrabbleApp.Model.BookScrabbleGuestFacade;
import BookScrabbleApp.Model.GameData.Tile;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import BookScrabbleApp.ViewModel.ViewableTile;
import javafx.beans.property.SimpleStringProperty;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class testGuestViewModel {
    BookScrabbleGuestFacade guestFacade;
    private BS_Guest_ViewModel viewModel;
    private BS_Guest_ViewModel viewModelMock;

    private Map<String, Consumer<String>> updatesMap;

    @BeforeEach
    public void setUp() {
        viewModel = new BS_Guest_ViewModel();
        viewModelMock = new BS_Guest_ViewModel() {

            @Override
            public void tryPlaceWord(String word, int row, int col, boolean isVertical) {
                System.out.println(word + " " + row + " " + col + " " + isVertical);
            }

            @Override
            public void passTurn() {
                System.out.println("pass turn called");
            }

            @Override
            public void challengeRequest(String word) {
                System.out.println("challenge request called: " + word);
            }
        };

        viewModel.initializeProperties();

    }

    @Test
    public void testSetWordsForChallenge() {
        List<String> wordsList = new ArrayList<>();
        wordsList.add("WORD1");
        wordsList.add("WORD2");
        wordsList.add("WORD3");

        viewModel.setWordsForChallenge(wordsList);

        // Verify that the viewableWordsForChallenge list contains the expected words
        List<String> viewableWords = new ArrayList<>();
        for (SimpleStringProperty property : viewModel.getViewableWordsForChallenge()) {
            viewableWords.add(property.getValue());
        }

        assertEquals(3, viewableWords.size());
        assertEquals("WORD1", viewableWords.get(0));
        assertEquals("WORD2", viewableWords.get(1));
        assertEquals("WORD3", viewableWords.get(2));
    }

    @Test
    public void testInitializeUpdateMap() throws NoSuchFieldException, IllegalAccessException {
        Field updatesMapField = viewModel.getClass().getDeclaredField("updatesMap");
        updatesMapField.setAccessible(true);
        updatesMap = (Map<String, Consumer<String>>) updatesMapField.get(viewModel);

        viewModel.initializeUpdateMap();

        // Verify the size and contents of the updatesMap
        assertEquals(12, updatesMap.size());

        assertTrue(updatesMap.containsKey("hand updated"));
        assertNotNull(updatesMap.get("hand updated"));

        assertTrue(updatesMap.containsKey("tileBoard updated"));
        assertNotNull(updatesMap.get("tileBoard updated"));

        assertTrue(updatesMap.containsKey("turnPassed"));
        assertNotNull(updatesMap.get("turnPassed"));

        assertTrue(updatesMap.containsKey("wordsForChallenge"));
        assertNotNull(updatesMap.get("wordsForChallenge"));

        assertTrue(updatesMap.containsKey("playersScores updated"));
        assertNotNull(updatesMap.get("playersScores updated"));

        assertTrue(updatesMap.containsKey("winner"));
        assertNotNull(updatesMap.get("winner"));

        assertTrue(updatesMap.containsKey("endGame"));
        assertNotNull(updatesMap.get("endGame"));

        assertTrue(updatesMap.containsKey("invalidWord"));
        assertNotNull(updatesMap.get("invalidWord"));

        assertTrue(updatesMap.containsKey("challengeAlreadyActivated"));
        assertNotNull(updatesMap.get("challengeAlreadyActivated"));

        assertTrue(updatesMap.containsKey("challengeSuccess"));
        assertNotNull(updatesMap.get("challengeSuccess"));

        assertTrue(updatesMap.containsKey("playersName"));
        assertNotNull(updatesMap.get("playersName"));

        assertTrue(updatesMap.containsKey("gameStart"));
        assertNotNull(updatesMap.get("gameStart"));
    }

    @Test
    public void testTryPlaceWord() {
        String word = "HELLO";
        int row = 3;
        int col = 5;
        boolean isVertical = false;

        viewModelMock.tryPlaceWord(word, row, col, isVertical);

    }
    @Test
    public void testPassTurn() {
        viewModelMock.passTurn();
    }
    @Test
    public void testChallengeRequest() {
        String word = "HELLO";
        viewModelMock.challengeRequest(word);
    }

    @Test
    public void testSetBoard() throws NoSuchFieldException, IllegalAccessException {
        // Set up test data
        Tile[][] boardState = new Tile[15][15];
        boardState[0][0] = new Tile('A', 1);
        boardState[1][1] = new Tile('B', 2);
        viewModel.guestFacade.setBoard(boardState);

        // Perform the action
        viewModel.setBoard();

        // Verify the result
        List<List<ViewableTile>> viewableBoard = viewModel.getViewableBoard();
        assertEquals("A", viewableBoard.get(0).get(0).letterProperty().get());
        assertEquals("1", viewableBoard.get(0).get(0).scoreProperty().get());
        assertEquals("B", viewableBoard.get(1).get(1).letterProperty().get());
        assertEquals("2", viewableBoard.get(1).get(1).scoreProperty().get());
    }


    @Test
    public void testSetHand() throws NoSuchFieldException, IllegalAccessException {


        // Set up test data
        List<Tile> hand = new ArrayList<>();
        hand.add(new Tile('A', 1));
        hand.add(new Tile('B', 2));
        viewModel.guestFacade.getPlayer().set_hand(hand);

        // Perform the action
        viewModel.setHand();

        // Verify the result
        List<ViewableTile> viewableHand = viewModel.getViewableHand();
        assertEquals("A", viewableHand.get(0).letterProperty().get());
        assertEquals("1", viewableHand.get(0).scoreProperty().get());
        assertEquals("B", viewableHand.get(1).letterProperty().get());
        assertEquals("2", viewableHand.get(1).scoreProperty().get());
    }

    @Test
    public void testSetScore() throws NoSuchFieldException, IllegalAccessException {
        // Make guestFacade accessible
        Field guestFacadeField = BS_Guest_ViewModel.class.getDeclaredField("guestFacade");
        guestFacadeField.setAccessible(true);
        BookScrabbleGuestFacade guestFacade = (BookScrabbleGuestFacade) guestFacadeField.get(viewModel);
        // Set up test data
        String[] playerScores = {"10", "20", "30"};
        guestFacade.setPlayersScores(playerScores);

        // Perform the action
        viewModel.setScore();

        // Verify the result
        List<SimpleStringProperty> viewableScores = viewModel.getViewableScores();
        assertEquals("10", viewableScores.get(0).getValue());
        assertEquals("20", viewableScores.get(1).getValue());
        assertEquals("30", viewableScores.get(2).getValue());
    }

    @Test
    public void testSetViewableName() {
        // Set up test data
        String[] playersNames = {"John", "Jane", "Alice"};
        String[] message = new String[playersNames.length + 2];
        message[0] = "playersName";
        message[1] = String.valueOf(playersNames.length);
        System.arraycopy(playersNames, 0, message, 2, playersNames.length);

        // Perform the action
        viewModel.setViewableName(message);

        // Verify the result
        List<SimpleStringProperty> viewableNames = viewModel.getViewableNames();
        assertEquals("John", viewableNames.get(0).getValue());
        assertEquals("Jane", viewableNames.get(1).getValue());
        assertEquals("Alice", viewableNames.get(2).getValue());
    }

    @Test
    public void testOpenSocket() throws NoSuchFieldException, IllegalAccessException {
        // Set up test data
        AtomicBoolean connectionOpened = new AtomicBoolean(false);
        final String[] hostIpCheck = {null};
        final int[] hostPortCheck = {0};


        BookScrabbleGuestFacade guestFacade = new BookScrabbleGuestFacade() {
            @Override
            public void openSocket(String hostIp, int hostPort) {
                hostIpCheck[0] = hostIp;
                hostPortCheck[0] = hostPort;
                connectionOpened.set(true); // Set connectionOpened to true
            }

        };
        guestFacade.openSocket("localhost", 1234);

        // Perform the action
        viewModel.hostIp.set("localhost");
        viewModel.hostPort.set("1234");

        // Wait for the connection to be opened
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify the result
        assertEquals("localhost", hostIpCheck[0]);
        assertEquals(1234, hostPortCheck[0]);
        assertEquals("localhost", viewModel.hostIp.get());
        assertEquals("1234", viewModel.hostPort.get());
        assertTrue(connectionOpened.get());
    }


}
