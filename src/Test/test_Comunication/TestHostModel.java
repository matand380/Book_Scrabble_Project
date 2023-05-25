package test_Comunication;

import Model.BS_Guest_Model;
import Model.BS_Host_Model;
import Model.GameData.Player;
import Model.GameData.Tile;
import Model.GameLogic.ClientCommunicationHandler;
import Model.GameLogic.HostCommunicationHandler;
import Model.GameLogic.MyServer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
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
        public void updateAll(Object message) {
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
        String challengeInfo = "12, 13, 14, 15, 16, 17, 18, 19, 20";
        Method setChallengeInfo;
        Method getChallengeInfo;
        try {
            setChallengeInfo = BS_Host_Model.getModel().getClass().getDeclaredMethod("setChallengeInfo", String[].class);
            getChallengeInfo = BS_Host_Model.getModel().getClass().getDeclaredMethod("getChallengeInfo");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);}

        setChallengeInfo.setAccessible(true);
        getChallengeInfo.setAccessible(true);

        try{
            setChallengeInfo.invoke(hostModelMock, (Object) challengeInfo.split(", "));
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
        assertNull(hostModelMock.getPlayerToSocketID());
    }

    @Test
    public void testGetGameSocket() {
        assertNotNull(hostModelMock.getGameSocket());
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
        assertEquals(0, hostModelMock.getPlayers().size());
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
        //return a list of players
        //check if the return is a list
        assertEquals(hostModelMock.getPlayers(), instanceOf(List.class));
    }

    @Test
    public void testSetNextPlayerIndex(){
        hostModelMock.setNextPlayerIndex(1);
        assertEquals(1, hostModelMock.getPlayer().get_index());
        hostModelMock.setNextPlayerIndex(2);
        assertEquals(2, hostModelMock.getPlayer().get_index());
        hostModelMock.setNextPlayerIndex(3);
        assertEquals(3, hostModelMock.getPlayer().get_index());
        hostModelMock.setNextPlayerIndex(0);
        assertEquals(0, hostModelMock.getPlayer().get_index());
    }

    @Test
    public void getCurrentPlayerIndex(){
        assertEquals(0, hostModelMock.getCurrentPlayerIndex());
    }
    @Test
    public void testStartNewGame(){
        hostModelMock.startNewGame();
        assertEquals(0, hostModelMock.getPlayer().get_index());
        assertEquals(7, hostModelMock.getPlayer().get_hand().size());
    }

    @Test
    public void testPassTurn() {
        assertEquals(0, hostModelMock.getPlayer().get_index());
        hostModelMock.passTurn(hostModelMock.getCurrentPlayerIndex());
        assertEquals(1, hostModelMock.getPlayer().get_index());
    }

    @Test
    public void testPassTurnTryPlace(){
        int playerIndex = hostModelMock.getCurrentPlayerIndex();
        hostModelMock.passTurn(playerIndex);
        assertEquals("passTurn:" + playerIndex+1, "passTurn:" + playerIndex);

    }

//    @Test
//    public void testTryPlaceWord() {}

    @Test
    public void testUpdateBoard() {
        Method updateBoard;
        String word = "word";
        int x = 0;
        int y = 0;
        boolean isVertical = false;
        String message = "tileBoard:word";

        try {
            updateBoard = BS_Host_Model.getModel().getClass().getDeclaredMethod("updateBoard", String.class, int.class, int.class, boolean.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        updateBoard.setAccessible(true);
        try {
            assertEquals(message,updateBoard.invoke(hostModelMock, word, x, y, isVertical));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testIsFull(){
        assertFalse(hostModelMock.isFull());
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        hostModelMock.addPlayer(player1);
        hostModelMock.addPlayer(player2);
        hostModelMock.addPlayer(player3);
        assertEquals(4, hostModelMock.getPlayers().size());
    }


//    @Test
//    public void testChallengeWord() {
//        String word = "word";
//        assertEquals(,hostModelMock.challengeWord(word, Integer.toString(hostModelMock.getCurrentPlayerIndex())));
//
//        communicationHandlerMock.outMessages("challengeWord:" + hostModelMock.getPlayer().get_index() + ":" + word);
//        assertEquals("challengeWord:0:word", "challengeWord:" + hostModelMock.getPlayer().get_index() + ":" + word);
//        // TODO: Implement test for challengeWord method
//    }

   @Test
   public void testGetMaxScore() {
        assertEquals(0, Integer.parseInt(hostModelMock.getMaxScore()));
        hostModelMock.getPlayers().get(0).set_score(100);
        hostModelMock.getPlayers().get(1).set_score(90);
        hostModelMock.getPlayers().get(3).set_score(80);
        hostModelMock.getPlayers().get(2).set_score(70);
        assertEquals(100, Integer.parseInt(hostModelMock.getMaxScore()));
    }


    @Test
    public void testGetCommunicationHandler() {
        assertNull(hostModelMock.getCommunicationHandler());
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
//    public void testRequestChallengeActivation(){}
}
