package test_ViewModels;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import BookScrabbleApp.Model.*;
import BookScrabbleApp.Model.GameData.*;
import BookScrabbleApp.ViewModel.*;
import javafx.beans.property.*;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class testHostViewModel {

    private BS_Host_ViewModel viewModel;
    private BS_Host_ViewModel viewModelMock;

    private Map<String, Consumer<String>> testUpdateMap;

    @Before
    public void setUp() throws IllegalAccessException, NoSuchFieldException {
        viewModel = new BS_Host_ViewModel();
        Field updateMapField = viewModel.getClass().getDeclaredField("updatesMap");
        updateMapField.setAccessible(true);

        viewModel.initializeUpdateMap();
        testUpdateMap = (Map<String, Consumer<String>>) updateMapField.get(viewModel);


        viewModelMock = new BS_Host_ViewModel() {

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

            @Override
            public void startNewGame() {
                System.out.println("start game called");
            }
        };
        viewModel.initializeProperties();
    }

    @Test
    public void testInitializeProperties() {

        assertNotNull(viewModel.viewableHand);
        assertEquals(7, viewModel.viewableHand.size());

        assertNotNull(viewModel.viewableBoard);
        assertEquals(15, viewModel.viewableBoard.size());
        for (List<ViewableTile> row : viewModel.viewableBoard) {
            assertEquals(15, row.size());
        }

        assertNotNull(viewModel.viewableScores);

        assertNotNull(viewModel.viewableName);

        assertNotNull(viewModel.ip);

        assertNotNull(viewModel.port);
    }

    @Test
    public void testInitializeUpdateMap() throws NoSuchFieldException, IllegalAccessException {
        Field updateMapField = viewModel.getClass().getDeclaredField("updatesMap");
        updateMapField.setAccessible(true);

        viewModel.initializeUpdateMap();
        Map<String, String> updatesMap = (Map<String, String>) updateMapField.get(viewModel);

        assertNotNull(updatesMap);
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

        assertTrue(updatesMap.containsKey("endGameHost"));
        assertNotNull(updatesMap.get("endGameHost"));
    }

    @Test
    public void testSetWordsForChallenge() {
        viewModel.setWordsForChallenge(Arrays.asList("word1", "word2", "word3"));

        assertNotNull(viewModel.viewableWordsForChallenge);
        assertEquals(3, viewModel.viewableWordsForChallenge.size());

        List<String> expected = new ArrayList<>();

        for (SimpleStringProperty word : viewModel.viewableWordsForChallenge) {
            expected.add(word.getValue());
        }

        assertEquals(3, expected.size());
        assertTrue(expected.contains("word1"));
        assertTrue(expected.contains("word2"));
        assertEquals("word3", expected.get(2));
    }

    @Test
    public void testSetViewableName() {
        // Set up test data
        String[] playersNames = {"John", "Jane", "Alice"};
        StringBuilder message = new StringBuilder();
        message.append("playersName:");
        message.append(playersNames.length);
        for (String name : playersNames) {
            message.append(":");
            message.append(name);
        }

        viewModel.hostFacade.getPlayers().clear();

        for (int i = 0; i < 3; i++) {
            viewModel.hostFacade.getPlayers().add(new Player());
            viewModel.hostFacade.getPlayers().get(i).set_name(playersNames[i]);
        }

        testUpdateMap.get("playersName").accept(message.toString());


        // Verify the result
        List<SimpleStringProperty> viewableNames = viewModel.getViewableNames();
        assertEquals("John", viewableNames.get(0).getValue());
        assertEquals("Jane", viewableNames.get(1).getValue());
        assertEquals("Alice", viewableNames.get(2).getValue());
    }

    @Test
    public void testSetBoard() {

        viewModel.viewableBoard = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            viewModel.viewableBoard.add(new ArrayList<>());
            for (int j = 0; j < 15; j++) {
                viewModel.viewableBoard.get(i).add(null);
            }
        }
        viewModel.viewableBoard.get(0).set(0, new ViewableTile('A', 1));
        viewModel.viewableBoard.get(7).set(7, new ViewableTile('C', 2));
        viewModel.viewableBoard.get(3).set(5, new ViewableTile('B', 3));

        viewModel.setBoard();


        // Verify that the viewableBoard has been updated correctly
        assertEquals('A', viewModel.getViewableBoard().get(0).get(0).getLetter());
        assertEquals(1, viewModel.getViewableBoard().get(0).get(0).get_score());
        assertNotNull(viewModel.getViewableBoard().get(3).get(5));
        assertEquals('C', viewModel.getViewableBoard().get(7).get(7).getLetter());
        assertEquals(2, viewModel.getViewableBoard().get(7).get(7).get_score());
    }

    @Test
    public void testSetHand() {

        List<Tile> hand = new ArrayList<>();
        hand.add(new Tile('A', 1));
        hand.add(new Tile('B', 3));
        hand.add(new Tile('C', 3));
        hand.add(new Tile('D', 2));
        viewModel.hostFacade.getPlayer().set_hand(hand);

        viewModel.setHand();

        List<ViewableTile> expected = viewModel.getViewableHand();

        assertEquals("A", expected.get(0).letterProperty().get());
        assertEquals("3", expected.get(1).scoreProperty().get());
        assertEquals("3", expected.get(2).scoreProperty().get());
        assertEquals("D", expected.get(3).letterProperty().get());
    }

    @Test
    public void testSetScore() {
        String[] scores = {"30", "50", "20", "10"};
        for (int i = 0; i < 4; i++) {
            viewModel.hostFacade.getPlayers().add(new Player());
            viewModel.hostFacade.getPlayers().get(i).set_score(Integer.parseInt(scores[i]));
        }

        viewModel.setScore();

        List<SimpleStringProperty> expected = viewModel.getViewableScores();
        assertEquals(4, expected.size());
        assertEquals("30", expected.get(0).getValue());
        assertEquals("50", expected.get(1).getValue());
        assertEquals("20", expected.get(2).getValue());
        assertEquals("10", expected.get(3).getValue());
    }

    @Test
    public void testOpenSocket() {
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
        viewModel.ip.set("localhost");
        viewModel.port.set("1234");

        // Wait for the connection to be opened
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify the result
        assertEquals("localhost", hostIpCheck[0]);
        assertEquals(1234, hostPortCheck[0]);
        assertEquals("localhost", viewModel.ip.get());
        assertEquals("1234", viewModel.port.get());
        assertTrue(connectionOpened.get());
    }

    @Test
    public void testTryPlaceWord() {
        String word = "FARM";
        int row = 7;
        int col = 9;
        boolean isVertical = true;

        viewModelMock.tryPlaceWord(word, row, col, isVertical);
    }

    @Test
    public void testPassTurn() {
        viewModelMock.passTurn();
    }

    @Test
    public void testStartNewGame() {
        viewModelMock.startNewGame();
    }

    @Test
    public void testChallengeRequest() {
        viewModel.hostFacade.getPlayers().clear();
        viewModel.hostFacade.getPlayer().set_name("John");
        viewModel.hostFacade.getPlayer().set_index(3);

        viewModel.challengeRequest("ChallengeTest");

        assertEquals("3", viewModel.hostFacade.getChallengeInfo().split(":")[0]);
        assertEquals("ChallengeTest", viewModel.hostFacade.getChallengeInfo().split(":")[1]);
    }
}
