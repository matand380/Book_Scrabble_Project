package test_Models;

import BookScrabbleApp.Model.BS_Guest_Model;
import BookScrabbleApp.Model.GameData.Player;
import BookScrabbleApp.Model.GameData.Tile;
import BookScrabbleApp.Model.GameLogic.ClientCommunicationHandler;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;


public class TestGuestModel {

    BS_Guest_Model guestModelMock;


    ClientCommunicationHandler communicationHandlerMock = new ClientCommunicationHandler() {
        @Override
        public void outMessages(String message) {
            System.out.println(message);
        }
    };

    @Before
    public void setup() {
        guestModelMock = BS_Guest_Model.getModel();
    }

    @Test
    public void validateIpPort() {

        String ip = "127.0.0.1";
        int port = 4444;
        Method validateIpPort;
        try {
            validateIpPort = BS_Guest_Model.getModel().getClass().getDeclaredMethod("validateIpPort", String.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        validateIpPort.setAccessible(true);
        try {
            assertTrue((boolean) validateIpPort.invoke(guestModelMock, ip, port));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void testSetPlayersScores() {
        String[] scores = {"10", "20", "30"};
        guestModelMock.setPlayersScores(scores);
        Player player = guestModelMock.getPlayer();
        assertEquals(10, player.get_score());
    }


    @Test
    public void testIsHost() {
        assertFalse(guestModelMock.isHost());
    }

    @Test
    public void testPassTurn() {
        assertEquals(0, guestModelMock.getPlayer().get_index());
    }

    @Test
    public void testTryPlaceWord() {

        String word = "word";
        int x = 0;
        int y = 0;
        boolean isVertical = false;

        String message = word + ":" + x + ":" + y + ":" + isVertical;
        String playerIndex = String.valueOf(guestModelMock.getPlayer().get_index());
        assertEquals("tryPlaceWord:0:word:0:0:false", "tryPlaceWord:" + playerIndex + ":" + message);
        communicationHandlerMock.outMessages("tryPlaceWord:" + playerIndex + ":" + message);


    }

    @Test
    public void testChallengeWord() {
        String word = "word";
        communicationHandlerMock.outMessages("challengeWord:" + guestModelMock.getPlayer().get_index() + ":" + word);
        assertEquals("challengeWord:0:word", "challengeWord:" + guestModelMock.getPlayer().get_index() + ":" + word);
        // TODO: Implement test for challengeWord method
    }

    @Test
    public void testSetBoard() {
        Tile[][] board = new Tile[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Tile('R', 1);
            }
        }
        guestModelMock.setBoard(board);
        assertArrayEquals(board, guestModelMock.getBoardState());
    }

    @Test
    public void testGetSocket() {
        assertNull(guestModelMock.getSocket());
    }

    @Test
    public void testGetPlayer() {
        Player player = guestModelMock.getPlayer();
        assertEquals(0, player.get_index());
        player.set_name("test");
        assertEquals("test", player.get_name());
        player.set_score(10);
        assertEquals(10, player.get_score());
        player.set_hand(null);
        assertNull(player.get_hand()); //check for null exception
    }

    @Test
    public void testSetPlayerProperties() {
        guestModelMock.setPlayerProperties("test");
        assertEquals("test", guestModelMock.getPlayer().get_name());
    }

    @Test
    public void testGetCommunicationHandler() {
        assertNull(guestModelMock.getCommunicationHandler());
    }

    @Test
    public void testGetCurrentPlayerScore() {
        guestModelMock.setPlayersScores(new String[]{"10", "20", "30"});
        assertEquals(10, guestModelMock.getCurrentPlayerScore());
        assertFalse(guestModelMock.getCurrentPlayerScore() == 20);
    }

    @Test
    public void testGetCurrentPlayerHand() {
        guestModelMock.setPlayerProperties("test");
        Tile[] tiles = new Tile[7];
        for (int i = 0; i < 7; i++) {
            tiles[i] = new Tile('R', 1);
        }
        guestModelMock.getPlayer().set_hand(Arrays.asList(tiles));
        List<Tile> hand = guestModelMock.getCurrentPlayerHand();
        assertEquals(7, hand.size());
        assertEquals('R', hand.get(0).getLetter());
    }

    @Test
    public void testGetBoardState() {
        Tile[][] board = new Tile[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Tile('R', 1);
            }
        }
        guestModelMock.setBoard(board);
        assertArrayEquals(board, guestModelMock.getBoardState());
    }

    @Test
    public void testEndGame() {
        assertNull(guestModelMock.getSocket());
        assertNull(guestModelMock.getCommunicationHandler());
        communicationHandlerMock.outMessages("endGame:" + guestModelMock.getPlayer().get_index());
        assertEquals("endGame:0", "endGame:" + guestModelMock.getPlayer().get_index());
    }

}
