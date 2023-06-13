package test_ViewModels;

import BookScrabbleApp.Model.BookScrabbleGuestFacade;
import BookScrabbleApp.Model.GameData.Board;
import BookScrabbleApp.Model.GameData.Player;
import BookScrabbleApp.Model.GameData.Tile;
import BookScrabbleApp.Model.GameLogic.HostCommunicationHandler;
import BookScrabbleApp.Model.GameLogic.MyServer;
import BookScrabbleApp.ViewModel.BS_Guest_ViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.*;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;

public class testGuestViewModel {


    private BS_Guest_ViewModel guestViewModel;
    private BS_Guest_ViewModel guestViewModelMock;
    private BookScrabbleGuestFacade guestFacade;
    private BookScrabbleGuestFacade guestFacadeMock;

    @BeforeEach
    public void setUp() {
        try {
            guestViewModelMock = new BS_Guest_ViewModel() {

                @Override
                public void setPlayerProperties(String name) {
                    System.out.println("setPlayerProperties test passed");
                }

                @Override
                public void initializeUpdateMap() {
                    Map<String, String> updateMap = new HashMap<>();
                    updateMap.put("hand update", "hand update");
                    updateMap.put("board update", "board update");
                    updateMap.put("scores update", "scores update");
                    updateMap.put("turn update", "turn update");
                    updateMap.put("end game update", "end game update");
                    updateMap.put("challenge update", "challenge update");
                    updateMap.put("wordsForChallenge", "wordsForChallenge");

                }

                @Override
                public void endGame() {
                    System.out.println("endGame test passed");
                }

                @Override
                public void update(Observable o, Object arg) {
                    System.out.println("update test passed");
                }

            };

            Field guestViewModelField = BS_Guest_ViewModel.class.getDeclaredField("guestViewModel");
            guestViewModelField.setAccessible(true);
            guestViewModelField.set(guestViewModel, guestViewModelMock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            guestFacade = new BookScrabbleGuestFacade();
            guestViewModel = new BS_Guest_ViewModel();
            Field guestFacadeField = BS_Guest_ViewModel.class.getDeclaredField("guestFacade");
            guestFacadeField.setAccessible(true);
            guestFacadeField.set(guestViewModel, guestFacade);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        guestFacadeMock = new BookScrabbleGuestFacade() {
            @Override
            public void openSocket(String ip, int port) {
                System.out.println("openSocket test passed");
            }

            @Override
            public String[] getPlayersScores() {
                String[] playersScores = {"1", "2", "3", "4"};
                return playersScores;

            }

            @Override
            public void passTurn(int playerIndex) {
                System.out.println("passTurn test passed");
            }

            @Override
            public void tryPlaceWord(String word, int x, int y, boolean isVertical) {
                System.out.println("tryPlaceWord test passed");
            }

            @Override
            public void challengeWord(String word) {
                System.out.println("challengeWord test passed");
            }

            @Override
            public void setBoard(Tile[][] boardTiles) {
                System.out.println("setBoard test passed");
            }

            @Override
            public void setPlayerProperties(String name) {
                System.out.println("setPlayerProperties test passed");
            }

            @Override
            public void endGame() {
                System.out.println("endGame test passed");
            }

            @Override
            public boolean isHost() {
                return false;
            }

            @Override
            public void update(Observable o, Object arg) {
                System.out.println("update");
            }

            @Override
            public Socket getSocket() {
                return guestFacade.getSocket();
            }

            @Override
            public List<Tile> getCurrentPlayerHand() {
                List<Tile> hand = new ArrayList<>();
                hand.add(new Tile('a', 1));
                hand.add(new Tile('b', 2));
                hand.add(new Tile('c', 3));
                hand.add(new Tile('d', 4));
                hand.add(new Tile('e', 5));
                hand.add(new Tile('f', 6));
                hand.add(new Tile('g', 7));
                return hand;
            }
        };


    }

    @Test
    public void testSetBoard() {
        // TODO: Implement the test for setBoard method
        assertEquals(' ', guestViewModel.viewableBoard.get(0).get(0).getLetter());
        assertEquals(0, guestViewModel.viewableBoard.get(0).get(0).getScore());
        Tile[][] board = new Tile[15][15];
        for (int i = 0; i < 15; i++) {
            board[i] = new Tile[15];
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Tile('a', 1);
            }
        }
        guestFacade.setBoard(board);
        guestViewModel.setBoard();
        assertEquals('a', guestViewModel.viewableBoard.get(0).get(0).getLetter());
        assertEquals(1, guestViewModel.viewableBoard.get(0).get(0).getScore());


    }

    @Test
    public void testSetHand() {
        // TODO: Implement the test for setHand method
        guestFacadeMock.getCurrentPlayerHand();
        guestViewModel.setHand();
    }

    @Test
    public void testSetScore() throws NoSuchFieldException, IllegalAccessException {
        String[] playersScores = {"1", "2", "3", "4"};
        setField(guestFacadeMock, "getPlayersScore", playersScores);

        // Call the method under test
        guestViewModel.setScore();

        // Verify that the view model correctly sets the score
        assertEquals(playersScores.length, guestViewModel.viewableScores.size());
        for (int i = 0; i < playersScores.length; i++) {
            assertEquals(playersScores[i], guestViewModel.viewableScores.get(i));
        }
    }

    private void setField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    public void testOpenSocket() {
        // TODO: Implement the test for openSocket method
        HostCommunicationHandler communicationHandlerMock = new HostCommunicationHandler() {
            @Override
            public String messagesFromGameServer() {
                return "String passed";
            }

            @Override
            public void messagesToGameServer(String message) {
                System.out.println(message);
            }
        };
        MyServer serverMock = new MyServer(4444, communicationHandlerMock) {
            @Override
            public void updateAll(String message) {
                System.out.println(message);
            }

            @Override
            public void updateSpecificPlayer(String id, Object obj) {
                System.out.println((String) obj);
            }
        };
        guestFacade.openSocket("localhost", 4444);
    }

    @Test
    public void testTryPlaceWord() {
        guestFacadeMock.tryPlaceWord("word", 0, 0, true);
    }

    @Test
    public void testPassTurn() {
        guestFacadeMock.passTurn(0);
    }

    @Test
    public void testChallengeRequest() {
        guestFacadeMock.challengeWord("word");
    }

    @Test
    public void testEndGame() {
        guestFacadeMock.endGame();
    }

    @Test
    public void testInitializeProperties() {
        guestViewModel.initializeProperties();

        // Assert that the properties are initialized correctly
        assertNull(guestViewModel.hostIp.get());
        assertNull(guestViewModel.hostPort.get());
        assertEquals(7, guestViewModel.viewableHand.size());
        assertEquals(15, guestViewModel.viewableBoard.size());
        assertEquals(15, guestViewModel.viewableBoard.get(0).size());
        assertEquals(0, guestViewModel.viewableScores.size());
        assertNull(guestViewModel.challengeWord.get());
        assertEquals(0, guestViewModel.viewableWordsForChallenge.size());
        assertNull(guestViewModel.winnerProperty.get());
    }

    @Test
    public void testInitializeUpdateMap() throws NoSuchFieldException, IllegalAccessException {
//        Field updatesMapField = guestViewModelMock.getClass().getDeclaredField("updatesMap");
//        updatesMapField.setAccessible(true);
//        Map<String, String> updatesMap = (Map<String, String>) updatesMapField.get(guestViewModelMock);

//        assertEquals(7, updatesMap.size());
//        updateMap.put("hand update", "hand update");
//        updateMap.put("board update", "board update");
//        updateMap.put("scores update", "scores update");
//        updateMap.put("turn update", "turn update");
//        updateMap.put("end game update", "end game update");
//        updateMap.put("challenge update", "challenge update");
//        updateMap.put("wordsForChallenge", "wordsForChallenge");
//        assertEquals("hand update", updatesMap.get("hand update"));
//        assertEquals("board update", updatesMap.get("board update"));
//        assertEquals("scores update", updatesMap.get("scores update"));
//        assertEquals("turn update", updatesMap.get("turn update"));
//        assertEquals("end game update", updatesMap.get("end game update"));
//        assertEquals("challenge update", updatesMap.get("challenge update"));

        // Assert that the updatesMap is initialized correctly

    }
}


