package test_Comunication;

import Model.BS_Guest_Model;
import Model.BS_Host_Model;
import Model.GameData.Board;
import Model.GameData.Player;
import Model.GameData.Tile;
import Model.GameData.Word;
import Model.GameLogic.ClientCommunicationHandler;
import Model.GameLogic.HostCommunicationHandler;
import Model.GameLogic.MyServer;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class TestHostModel {

    BS_Host_Model hostModelMock;

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
    MyServer serverMock = new MyServer(4444, communicationHandlerMock){
        @Override
        public void updateAll(String message) {
            System.out.println(message);
        }
        @Override
        public void updateSpecificPlayer(String id, Object obj) {
            System.out.println(obj);
        }
    };


    @Before
    public void setup() {
        hostModelMock = BS_Host_Model.getModel();
        serverMock.start();
    }

    @Test
    public void testGetPlayer() {
        Player player = hostModelMock.getPlayer();
        assertEquals(0, player.get_index());
        player.set_name("test");
        assertEquals("test", player.get_name());
        player.set_score(10);
        assertEquals(10, player.get_score());
        player.set_hand(null);
        assertNull(player.get_hand()); //check for null exception
    }

    @Test
    public void testSetGetChallengeInfo(){
        String challengeInfo = "0:word";
        Method setChallengeInfo;
        Method getChallengeInfo;
        try {
            setChallengeInfo = BS_Host_Model.getModel().getClass().getDeclaredMethod("setChallengeInfo", String.class);
            getChallengeInfo = BS_Host_Model.getModel().getClass().getDeclaredMethod("getChallengeInfo");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);}

        setChallengeInfo.setAccessible(true);
        getChallengeInfo.setAccessible(true);

        try{
            setChallengeInfo.invoke(hostModelMock,challengeInfo);
            assertEquals(challengeInfo, getChallengeInfo.invoke(hostModelMock));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testGetCommunicationServer() {
        assertNotNull(hostModelMock.getCommunicationServer());
    }

    @Test
    public void testGetPlayerToSocketID() {
        assertNotNull(hostModelMock.getPlayerToSocketID());
    }

    @Test
    public void testGetGameSocket() {
        assertNull(hostModelMock.getGameSocket());
    }

    @Test
    public void testIsHost() {
        assertTrue(hostModelMock.isHost());

    }

    @Test
    public void testOpenSocket(){
        String ip="127.0.0.1";
        int port=4444;
        Method openSocket;
        try {
            openSocket = BS_Host_Model.getModel().getClass().getDeclaredMethod("openSocket", String.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        openSocket.setAccessible(true);
        try {
            openSocket.invoke(hostModelMock, ip, port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void validateIpPort() {

        String ip = "127.0.0.1";
        int port = 4444;
        Method validateIpPort;
        try {
            validateIpPort = BS_Host_Model.getModel().getClass().getDeclaredMethod("validateIpPort", String.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        validateIpPort.setAccessible(true);
        try {
            assertTrue((boolean) validateIpPort.invoke(hostModelMock, ip, port));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testAddPlayer(){
        hostModelMock.getPlayers().clear();

        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        Player player4 = new Player();
        hostModelMock.addPlayer(player1);
        assertEquals(1, hostModelMock.getPlayers().size());
        hostModelMock.addPlayer(player2);
        hostModelMock.addPlayer(player3);
        hostModelMock.addPlayer(player4);
        assertEquals(4, hostModelMock.getPlayers().size());
    }

    @Test
    public void testGetPlayers(){
        hostModelMock.getPlayers().clear();
        hostModelMock.setPlayerProperties("test");
        assertEquals(1, hostModelMock.getPlayers().size());
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        Player player4 = new Player();
        hostModelMock.addPlayer(player1);
        assertEquals(2, hostModelMock.getPlayers().size());
        hostModelMock.addPlayer(player2);
        hostModelMock.addPlayer(player3);
        hostModelMock.addPlayer(player4);
        assertEquals(5, hostModelMock.getPlayers().size());
        //return a list of players
        //check if the return is a list
        assertTrue(hostModelMock.getPlayers() instanceof List<Player>);
    }

    @Test
    public void testSetNextPlayerIndex(){
        hostModelMock.setNextPlayerIndex(0);
        assertEquals(1, hostModelMock.getCurrentPlayerIndex());
        hostModelMock.setNextPlayerIndex(1);
        assertEquals(2, hostModelMock.getCurrentPlayerIndex());
        hostModelMock.setNextPlayerIndex(2);
        assertEquals(3, hostModelMock.getCurrentPlayerIndex());
        hostModelMock.setNextPlayerIndex(3);
        assertEquals(0, hostModelMock.getCurrentPlayerIndex());
    }

    @Test
    public void getCurrentPlayerIndex(){
        assertEquals(1, hostModelMock.getCurrentPlayerIndex());
    }
    @Test
    public void testStartNewGame(){
        hostModelMock.setPlayerProperties("test");
        hostModelMock.startNewGame();

        assertEquals(0, hostModelMock.getPlayer().get_index());
        assertEquals(7, hostModelMock.getPlayer().get_hand().size());
    }

    @Test
    public void testPassTurn() {
        assertEquals(0, hostModelMock.getPlayer().get_index());
        hostModelMock.passTurn(hostModelMock.getCurrentPlayerIndex());
        assertEquals(1, hostModelMock.getCurrentPlayerIndex());
    }

    @Test
    public void testPassTurnTryPlace(){
        hostModelMock.startNewGame();
        hostModelMock.setPlayerProperties("test");
        hostModelMock.addPlayer(new Player());
        int playerIndex = hostModelMock.getCurrentPlayerIndex();
        hostModelMock.passTurnTryPlace(playerIndex);
        assertEquals("passTurn:" + (playerIndex+1), "passTurn:" + hostModelMock.getCurrentPlayerIndex());
        hostModelMock.getPlayers().clear();
    }

    @Test
    public void testTryPlaceWord() {}

    @Test
    public void testUpdateBoard() {
        Method updateBoard;
        Gson Gson = new Gson();
        // TODO: 25/05/2023 addword to Board
//        Board.getBoard().placeWord(mk'mkm;lkm);
        String message = Gson.toJson(hostModelMock.getBoardState());

        try {
            updateBoard = BS_Host_Model.getModel().getClass().getDeclaredMethod("updateBoard");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        updateBoard.setAccessible(true);
        try {
            Tile[][] board = Gson.fromJson(message, Tile[][].class);
            assertArrayEquals(hostModelMock.getBoardState(),board);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testIsFull(){
        hostModelMock.getPlayers().clear();
        assertFalse(hostModelMock.isFull());
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        hostModelMock.addPlayer(player1);
        hostModelMock.addPlayer(player2);
        hostModelMock.addPlayer(player3);
        assertEquals(3, hostModelMock.getPlayers().size());
    }

    @Test
    public void testGetMaxScore() {
        hostModelMock.getPlayers().get(0).set_score(100);
        hostModelMock.getPlayers().get(0).set_name("test");
        hostModelMock.getPlayers().get(1).set_score(90);
        hostModelMock.getPlayers().get(3).set_score(80);
        hostModelMock.getPlayers().get(2).set_score(70);
        assertEquals("0:test", hostModelMock.getMaxScore());

        hostModelMock.getPlayers().get(0).set_score(110);
        hostModelMock.getPlayers().get(1).set_score(110);
        hostModelMock.getPlayers().get(3).set_score(105);
        assertEquals("0:test", hostModelMock.getMaxScore());

        hostModelMock.getPlayers().get(3).set_score(120);
        hostModelMock.getPlayers().get(0).set_score(120);
        hostModelMock.getPlayers().get(1).set_score(112);
        hostModelMock.getPlayers().get(1).set_score(113);
        assertEquals("3:Default", hostModelMock.getMaxScore());

    }

    @Test
    public void testGetCommunicationHandler() {
        assertNotNull(hostModelMock.getCommunicationHandler());
    }

    @Test
    public void testGetCurrentPlayerHand() {
        hostModelMock.setPlayerProperties("test");
        Tile[] tiles = new Tile[7];
        for (int i = 0; i < 7; i++) {
            tiles[i] = new Tile('R', 1);
        }
        hostModelMock.getPlayer().set_hand(Arrays.asList(tiles));
        List<Tile> hand = hostModelMock.getCurrentPlayerHand();
        assertEquals(7, hand.size());
        assertEquals('R', hand.get(0).getLetter());
    }

    @Test
    public void testSetPlayerProperties() {
        hostModelMock.setPlayerProperties("test");
        assertEquals("test", hostModelMock.getPlayer().get_name());
    }

//    @Test
//    public void testChallengeWord() {
//        String word = "word";
//        String message = "challengeWord:" + hostModelMock.getPlayer().get_index() + ":" + word;
//        Player player = new Player();
//        player.set_name("test");
//        hostModelMock.setPlayerProperties("hostTest");
//        hostModelMock.addPlayer(player);
//
//        Tile[] tiles = new Tile[4];
//        tiles[0] = new Tile('W', 4);
//        tiles[1] = new Tile('O', 1);
//        tiles[2] = new Tile('R', 1);
//        tiles[3] = new Tile('D', 2);
//
//        Word word1 = new Word(tiles, 0, 0, false);
//        Board.getBoard().placeWord(word1);
//
//        assertFalse(hostModelMock.challengeWord(word,Integer.toString(hostModelMock.getCurrentPlayerIndex())));
//
//        // TODO: Implement test for challengeWord method
//        //print the board
//    }
}
