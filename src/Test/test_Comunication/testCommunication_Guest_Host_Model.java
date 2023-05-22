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
//todo: host.tryPlaceWord(w1); not comparing between the hand and the rack
//todo: words arnt at the correct position when placing a word


public class testCommunication_Guest_Host_Model {
    static int localPortForClientToConnect = 23455;
    static int ServerPortToConnect = 12345;

    public static void main(String[] args) {
        System.out.println("Enter the local port:" + localPortForClientToConnect);
        //create host and guest
        BS_Host_Model host = BS_Host_Model.getModel();
        startCommunication_CreatHost();

        BS_Guest_Model clientA = BS_Guest_Model.getModel();
        startCommunication_CreatGuest("clientA");

        host.startNewGame();
        emptyTestChallengeWord();

        test_ScoreUpdates();
        test_ScoreUpdates();
        test_ScoreUpdates();
        test_ScoreUpdates();
        test_ScoreUpdates();
        test_ScoreUpdates();
        test_ScoreUpdates();
        test_ScoreUpdates();

        test_ScoreUpdates();

        test_StartGame_State();
        test_WordCounter();
        test_PassTurns();
        System.out.println("Done");
    }

    public static void emptyTestChallengeWord(){
        BS_Host_Model host = BS_Host_Model.getModel();
        BS_Guest_Model clientA = BS_Guest_Model.getModel();
        boolean b=host.challengeWord("BBB","0");
        if(b)
            System.out.println("problem with Challenge worked for eligible word");

        b=host.challengeWord("COMMAND","0");
        if(!b)
            System.out.println("problem with Challenge worked for non eligible word");

        b=host.challengeWord("","0");
        if(!b)
            System.out.println("problem with Challenge worked for empty word");

    }

    private static void testChallengeWord(){
        BS_Host_Model host = BS_Host_Model.getModel();
        BS_Guest_Model clientA = BS_Guest_Model.getModel();


        System.out.println("enter the parameters of the word that was last placed");
        Word wordPlaced= getWordFromTheUser();


        for (Word w1 : Board.getBoard().getWords(wordPlaced))
            System.out.println(w1.toString());
        System.out.println("choose a word to challenge from the board");
        Word wordToChallenge= getWordFromTheUser();
        printScores();

        if(host.getPlayers().get(host.getCurrentPlayerIndex()).get_index()==host.getPlayer().get_index())
            host.challengeWord(wordToChallenge.toString().toUpperCase(), Integer.toString(host.getPlayers().get(host.getCurrentPlayerIndex()).get_index()));
        else
            clientA.challengeWord(wordToChallenge.toString().toUpperCase());

        printScores();


        //print the word that created after the turn
        //once matan and eviater will fix the getter for the word on board and the code for the update of the board
//        for (Word w1 : Board.getBoard().getWords(Board.getBoard().getwordOnBoard[Board.getBoard().getwordOnBoard.length - 1]))
//            System.out.println(w1.toString());


    }

    private static void test_ScoreUpdates() {
        BS_Host_Model host = BS_Host_Model.getModel();
        BS_Guest_Model clientA = BS_Guest_Model.getModel();

        int score = host.getPlayers().get(host.getCurrentPlayerIndex()).get_score();
        int currantTurn = host.getCurrentPlayerIndex();

        System.out.println("the current player name is: " + host.getPlayers().get(host.getCurrentPlayerIndex()).get_name());
        for (Tile t : host.getPlayers().get(host.getCurrentPlayerIndex()).get_hand())
            System.out.print(t.getLetter() + " ");
        System.out.println("\n");


        System.out.println("choose a word to place or type : pass or challenge");
        Scanner scanner = new Scanner(System.in);
        String w = scanner.nextLine();
        if (w.equals("pass"))
            host.passTurn(host.currentPlayerIndex);
        if (w.equals("challenge"))
            testChallengeWord();

        else {
            System.out.println("choose a row :");
            int row = scanner.nextInt();
            System.out.println("choose a col :");
            int col = scanner.nextInt();
            System.out.println("choose true for vertical or false for horizontal :");
            boolean vertical = scanner.nextBoolean();

            if (host.getPlayers().get(host.getCurrentPlayerIndex()).get_index() == host.getPlayer().get_index()) {
                //host.requestChallengeActivation(host.getPlayers().get(host.getCurrentPlayerIndex()) + "HORN");
                host.tryPlaceWord(new Word(get(w.toUpperCase()), row, col, vertical));
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                clientA.tryPlaceWord(w.toUpperCase(), row, col, vertical);

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (score != host.getPlayers().get(host.getCurrentPlayerIndex()).get_score())
                    System.out.println("problem with the score update for the client turn");
            }
            System.out.println("host score: " + host.getPlayer().get_score());
            System.out.println("clientA score: " + clientA.getPlayer().get_score());

        }
        //print passTurns
        System.out.println("PassCounter: " + Board.getBoard().getPassCounter());
        if (host.gameIsOver) {
            System.out.println("*************\ngame is over\n(the boolean gameIsOver is true)\n*************\n");
        }
    }

    //local methods for testing
    public static void startCommunication_CreatHost() {
        BS_Host_Model host = BS_Host_Model.getModel();  //here you will be asked to assign a random port for the Host Server
        host.openSocket("127.0.0.1", ServerPortToConnect);
        host.setPlayerProperties("Host");
        Thread t = new Thread(() -> BS_Host_Model.getModel().getCommunicationServer().start());
        t.start();
        try {
            Thread.sleep(2000); //wait for server to start
        } catch (InterruptedException e) {
            System.out.println("sleep failed");
        }
    }

    public static void startCommunication_CreatGuest(String name) {

        BS_Guest_Model clientA = BS_Guest_Model.getModel();
        clientA.setPlayerProperties(name);
        clientA.openSocket("127.0.0.1", localPortForClientToConnect);
        clientA.getCommunicationHandler().setCom();

        try {
            Thread.sleep(2000); //wait for server to start
        } catch (InterruptedException e) {
            System.out.println("sleep failed");
        }
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
            //Tile.Bag.getBag()._quantitiesCounter[c-'A']++;
            i++;
        }
        return ts;
    }

    public static String formatTiles(Tile[][] tiles) {
        StringBuilder sb = new StringBuilder();
        for (Tile[] tile : tiles) {
            for (int j = 0; j < tile.length; j++) {
                sb.append(tile[j] == null ? "-" : tile[j].getLetter());
                if (j < tile.length - 1) {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static void test_WordCounter() {//need to rewrite
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

    public static Word getWordFromTheUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter the word :");
        String w = scanner.next();
        System.out.println("enter the row :");
        int row = scanner.nextInt();
        System.out.println("enter the col :");
        int col = scanner.nextInt();
        System.out.println("choose true for vertical or false for horizontal :");
        boolean vertical = scanner.nextBoolean();
        return new Word(get(w.toUpperCase()), row, col, vertical);
    }

    public static void getMaxScoreHost(){

        BS_Host_Model host = BS_Host_Model.getModel();
        host.getMaxScore();
    }

    public static void printScores(){
        System.out.println("--------------------------------------------------");
        for(Player p: BS_Host_Model.getModel().getPlayers()){
            System.out.println(p.get_name() + " score: " + p.get_score());
        }
        System.out.println("--------------------------------------------------");
    }
}

