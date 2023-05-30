package BookScrabbleApp.Model;

import BookScrabbleApp.Model.GameData.Player;
import BookScrabbleApp.Model.GameData.Tile;
import BookScrabbleApp.Model.GameLogic.ClientCommunicationHandler;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

public class BookScrabbleGuestFacade extends Observable implements Observer {
    private BS_Guest_Model guestModel;

    public BookScrabbleGuestFacade() {
        guestModel = BS_Guest_Model.getModel();
        guestModel.addObserver(this);
    }


    //do something methods
    public void openSocket(String ip, int port) {
        guestModel.openSocket(ip, port);
    }
    public void setPlayersScores(String[] playersScores) {
        guestModel.setPlayersScores(playersScores);
    }
    public void passTurn(int playerIndex) {
        guestModel.passTurn(playerIndex);
    }
    public void tryPlaceWord(String word, int x, int y, boolean isVertical) {
        guestModel.tryPlaceWord(word, x, y, isVertical);
    }
    public void challengeWord(String word) {
        guestModel.challengeWord(word);
    }
    public void setBoard(Tile[][] boardTiles) {
        guestModel.setBoard(boardTiles);
    }
    public void setPlayerProperties(String name) {
        guestModel.setPlayerProperties(name);
    }
    public void endGame() {
        guestModel.endGame();
    }


    //get something
    public boolean isHost() {
        return guestModel.isHost();
    }
    public Socket getSocket() {
        return guestModel.getSocket();
    }
    public Player getPlayer() {
        return guestModel.getPlayer();
    }
    public ClientCommunicationHandler getCommunicationHandler() {
        return guestModel.getCommunicationHandler();
    }
    public int getCurrentPlayerScore() {
        return guestModel.getCurrentPlayerScore();
    }

    //VIEW
    //scores array
    public String[] getPlayersScores() {
        return guestModel.getPlayersScores();
    }
    //hand
    public List<Tile> getCurrentPlayerHand() {
        return guestModel.getCurrentPlayerHand();
    }
    //board
    public Tile[][] getBoardState() {
        return guestModel.getBoardState();
    }


    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
