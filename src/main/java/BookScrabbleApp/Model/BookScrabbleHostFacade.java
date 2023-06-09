package BookScrabbleApp.Model;

import BookScrabbleApp.Model.GameData.*;
import BookScrabbleApp.Model.GameLogic.*;

import java.net.*;
import java.util.*;
 public class BookScrabbleHostFacade extends Observable implements Observer {
     private BS_Host_Model hostModel;

     public BookScrabbleHostFacade() {
         hostModel = BS_Host_Model.getModel();
         hostModel.addObserver(this);
     }

     //do something methods
     public void passTurn(int playerIndex) {
         hostModel.passTurn(playerIndex);
     }

     public void setChallengeInfo(String challengeInfo) {
         hostModel.setChallengeInfo(challengeInfo);
     }

     public void tryPlaceWord(String word, int x, int y, boolean isVertical) {
         hostModel.tryPlaceWord(word, x, y, isVertical);
     }

     public void openSocket(String ip, int port) {
         hostModel.openSocket(ip, port);
     }

     public void addPlayer(Player p) {
         hostModel.addPlayer(p);
     }

     public void startNewGame() {
         hostModel.startNewGame();
     }

     public void setPlayerProperties(String name) {
         hostModel.setPlayerProperties(name);
     }

     public void requestChallengeActivation(String challengeInfo) {
         hostModel.requestChallengeActivation(challengeInfo);
     }

     public void setNextPlayerIndex(int index) {
         hostModel.setNextPlayerIndex(index);
     }

     public void endGame() {
         hostModel.endGame();
     }


     //get something
     public Socket getGameSocket() {
         return hostModel.getGameSocket();
     }

     public MyServer getCommunicationServer() {
         return hostModel.getCommunicationServer();
     }

     public int getCurrentPlayerIndex() {
         return hostModel.getCurrentPlayerIndex();
     }

     public String getChallengeInfo() {
         return hostModel.getChallengeInfo();
     }

     public int getCurrentPlayerScore() {
         return hostModel.getCurrentPlayerScore();
     }

     public boolean isGameIsOver() {
         return hostModel.gameIsOver;
     }

     public boolean isFull() {
         return hostModel.isFull();
     }

     public Player getPlayer() {
         return hostModel.getPlayer();
     }

     public List<Player> getPlayers() {
         return hostModel.getPlayers();
     }

     public Queue<Player> getScores() {
         return hostModel.getScoresManager();
     }

     public Map<String, String> getPlayerToSocketID() {
         return hostModel.getPlayerToSocketID();
     }

     //VIEW
     //scores array
     public String[] getPlayersScores() {
         return hostModel.getPlayersScores();
     }

     //hand
     public List<Tile> getCurrentPlayerHand() {
         return hostModel.getCurrentPlayerHand();
     }

     //board
     public Tile[][] getBoardState() {
         return hostModel.getBoardState();
     }


     @Override
     public void update(Observable o, Object arg) {
         // everything will be passed to the viewModel
         setChanged();
         String argString = (String) arg;
         System.out.println("HostFacade ---- updateType: " + argString);
         notifyObservers(argString);
     }

     public void unPark() {
         hostModel.unPark();
     }

     public void setCommunicationServer(int port) {
         hostModel.setCommunicationServer(port);
     }
 }
