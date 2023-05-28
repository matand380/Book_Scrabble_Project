package BookScrabbleApp.Model;

import java.net.Socket;
import java.util.*;

import BookScrabbleApp.Model.GameData.*;
import BookScrabbleApp.Model.GameLogic.MyServer;

public class BookScrabbleHostFacade extends Observable implements Observer {
    private BS_Host_Model hostModel;

    public BookScrabbleHostFacade() {
        hostModel = BS_Host_Model.getModel();
        hostModel.addObserver(this);
    }

    public void passTurn(int playerIndex) {
        hostModel.passTurn(playerIndex);
    }


    public Player getPlayer() {
        return hostModel.getPlayer();
    }

    public void tryPlaceWord(Word word) {
        hostModel.tryPlaceWord(word);
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

    public boolean isFull() {
        return hostModel.isFull();
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

    public void setPlayerProperties(String name) {
        hostModel.setPlayerProperties(name);
    }

    public void requestChallengeActivation(String challengeInfo) {
        hostModel.requestChallengeActivation(challengeInfo);
    }

    public Socket getGameSocket() {
        return hostModel.getGameSocket();
    }

    public MyServer getCommunicationServer() {
        return hostModel.getCommunicationServer();
    }

    public int getCurrentPlayerIndex() {
        return hostModel.getCurrentPlayerIndex();
    }

    public void setNextPlayerIndex(int index) {
        hostModel.setNextPlayerIndex(index);
    }

    public String getChallengeInfo() {
        return hostModel.getChallengeInfo();
    }

    public void setChallengeInfo(String challengeInfo) {
        hostModel.setChallengeInfo(challengeInfo);
    }

    public int getCurrentPlayerScore() {
        return hostModel.getCurrentPlayerScore();
    }

    public List<Tile> getCurrentPlayerHand() {
        return hostModel.getCurrentPlayerHand();
    }

    public Tile[][] getBoardState() {
        return hostModel.getBoardState();
    }

    public boolean isGameIsOver() {
        return hostModel.gameIsOver;
    }

    public String[] getPlayersScores() {
        return hostModel.getPlayersScores();
    }

    public void endGame() {
        hostModel.endGame();
    }

    @Override
    public void update(Observable o, Object arg) {
        // everything will be passed to the viewModel
        setChanged();
        notifyObservers(arg);
    }
}
