package test_Comunication;

import Model.BS_Guest_Model;
import Model.BS_Host_Model;
import Model.GameData.Board;
import Model.GameData.Player;
import Model.GameData.Tile;
import Model.GameData.Word;

import java.util.Scanner;

////TODOS
//todo:if address is already in use give the option to change the port
//TODO: update score after place word
//TODO: limit the number of clients
//todo: no handler for try place word at ClientCommunicationHandler


public class testCommunication_Guest_Host_Model {


    static int localPortForClientToConnect = 23455;
    static int ServerPortToConnect = 65535;


    public static void main(String[] args) {
        System.out.println("Enter the local port:" + localPortForClientToConnect);
        //create host and guest
        BS_Host_Model host = startCommunication_CreatHost();
        host.startNewGame();

        BS_Guest_Model clientA = startCommunication_CreatGuest();

        //test start from here

        //Word w=new Word(get("HORN"), 7, 5, false);
        System.out.println("choose a word to place");
        Scanner scanner = new Scanner(System.in);
        String word = scanner.nextLine();

//        clientA.tryPlaceWord(word.toUpperCase(), 7, 5, false);

        test_ScoreUpdates(word.toUpperCase());
//        test_ScoreUpdates(new Word(get("HORN"), 7, 5, false));
//
//        test_ScoreUpdates(new Word(get("_OUSE"), 7, 5, true));
        //or this:
        //test_ScoreUpdates(new Word(get("HOUSE"), 7, 5, true));

//        test_ScoreUpdates(new Word(get("_OCK"), 7, 7, true));

//        test_StartGame_State();
//
//        test_WordCounter();
//        test_PassTurns();
        System.out.println("Done");

    }

    private static void test_ScoreUpdates(String w) {
        //BSP-77
        //still need to check way the score isn't updated after the play
        //part of guest model test if we will separate them

        BS_Host_Model host = BS_Host_Model.getModel();
        BS_Guest_Model clientA = BS_Guest_Model.getModel();

        int score = host.getPlayers().get(host.getCurrentPlayerIndex()).get_score();
        int currantTurn = host.getCurrentPlayerIndex();

        if (host.getPlayers().get(host.getCurrentPlayerIndex()).get_index() == host.getPlayer().get_index()) {
            //host.requestChallengeActivation(host.getPlayers().get(host.getCurrentPlayerIndex()) + "HORN");
            host.tryPlaceWord(new Word(get(w), 7, 5, false));
            if (score != host.getPlayers().get(host.getCurrentPlayerIndex()).get_score())
                System.out.println("problem with the score update for the host turn");
        } else {
            clientA.tryPlaceWord(w, 7, 5, false);
            if (score != host.getPlayers().get(host.getCurrentPlayerIndex()).get_score())
                System.out.println("problem with the score update for the client turn");
        }

        System.out.println("host score: " + host.getPlayer().get_score());
        System.out.println("clientA score: " + clientA.getPlayer().get_score());

        //passTurn test
        if (currantTurn == host.getCurrentPlayerIndex())
            System.out.println("problem with the turn update after the play");

    }

    //local methods for testing
    public static BS_Host_Model startCommunication_CreatHost() {
        BS_Host_Model host = BS_Host_Model.getModel();  //here you will be asked to assign a random port for the Host Server
        host.openSocket("127.0.0.1", ServerPortToConnect); //assign ip and port of the Game Server
        //TODO: get the name from the user instead of hard coding it as we did here

//        System.out.println("Enter the Host name: ");
//        Scanner scanner = new Scanner(System.in);
//        String name = scanner.next();
        host.setPlayerProperties("Host");

        Thread t = new Thread(() -> BS_Host_Model.getModel().getCommunicationServer().start());
        t.start();

        try {
            Thread.sleep(2000); //wait for server to start
        } catch (InterruptedException e) {
            System.out.println("sleep failed");
        }
        return host;
    }

    public static BS_Guest_Model startCommunication_CreatGuest() {

        BS_Guest_Model client = BS_Guest_Model.getModel();
        //TODO: get the name from the user instead of hard coding it
        client.setPlayerProperties("Client");
        //choose the same port as you chose for the host the ip should be 127.0.0.1 - don't change it;
        //todo:ask the user to put the host port to connect ,we might need a list of ports (games) to his choice
//        System.out.println("Enter the HostPort to connect: ");
//        Scanner scanner = new Scanner(System.in);
//        int port = scanner.nextInt();
        client.openSocket("127.0.0.1", localPortForClientToConnect);
        client.getCommunicationHandler().setCom();


        try {
            Thread.sleep(2000); //wait for server to start
        } catch (InterruptedException e) {
            System.out.println("sleep failed");
        }
        return client;
    }

    public static void test_StartGame_State() {
        //check if all the players got tiles
        for (Player p : BS_Host_Model.getModel().getPlayers()) {
            if (p.get_hand().size() != 7) {
                System.out.println("problem in -check if all the players got tiles");
            }
        }
        //the bag had the right amount of tiles after the players got tiles
        if (Tile.Bag.getBag().size() != (98 - (BS_Host_Model.getModel().getPlayers().size() * 7)))
            System.out.println("problem in -number of tile in bag after that loop");

        if (Board.getBoard().getPassCounter() != 0)
            System.out.println("problem in -passCounter");

        //the players got a tile and the play order is right(sortAntSetIndex)
        //check if playing order is right according to the method sortAndSetIndex
        for (int i = 0; i < BS_Host_Model.getModel().getPlayers().size() - 1; i++) {
            if (BS_Host_Model.getModel().getPlayers().get(i).getTileLottery().compareTo(BS_Host_Model.getModel().getPlayers().get(i + 1).getTileLottery()) > 0)
                System.out.println("problem in -sortAndSetIndex");
        }
    }

    public static void test_PassTurns() {
        //todo: change the variable "gameIsOver" in isGameOver method to true
        BS_Host_Model host = BS_Host_Model.getModel();
        for (int i = 0; i <= BS_Host_Model.getModel().getPlayers().size() + 1; i++) {
            host.passTurn(host.currentPlayerIndex);
        }
        if (!host.gameIsOver)
            System.out.println("the game should be over now");
    }

    public static Tile[] get(String s) {
        Tile[] ts = new Tile[s.length()];
        int i = 0;
        for (char c : s.toCharArray()) {
            ts[i] = Tile.Bag.getBag().getTile(c);
            i++;
        }
        return ts;
    }

    //check word counter after adding a word
    //check passCounter
    //after type pass passCounter is 1
    //after all the players type pass game is over

    public static String formatTiles(Tile[][] tiles) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                sb.append(tiles[i][j] == null ? "-" : tiles[i][j].getLetter());
                if (j < tiles[i].length - 1) {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void test_WordCounter() {//need to rewrite
        BS_Host_Model host = BS_Host_Model.getModel();
        Word w1 = new Word(get("BORN"), 7, 5, false);
        Word w2 = new Word(get("BORN"), 2, 2, false);
        Word w3 = new Word(get("BORN"), 3, 3, false);
        host.tryPlaceWord(w1);
        System.out.println(formatTiles(host.getBoardState()));
        host.tryPlaceWord(w2);
        System.out.println(formatTiles(host.getBoardState()));
        host.tryPlaceWord(w3);
        System.out.println(formatTiles(host.getBoardState()));

        if (Board.getBoard().getWordCounter() != 3)
            System.out.println("problem with the word counter");

    }
}

