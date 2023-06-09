package BookScrabbleApp.Model.GameData;

import BookScrabbleApp.Model.BS_Host_Model;
import BookScrabbleApp.Model.GameData.Tile.Bag;


public class TestHelper {


    public static void testBag() {
        Bag b = Tile.Bag.getBag();
        Bag b1 = Tile.Bag.getBag();
        if (b1 != b)
            System.out.println("your Bag in not a Singleton (-5)");

        int[] q0 = b.getQuantities();
        q0[0] += 1;
        int[] q1 = b.getQuantities();
        if (q0[0] != q1[0] + 1)
            System.out.println("getQuantities did not return a clone (-5)");

        for (int k = 0; k < 9; k++) {
            int[] qs = b.getQuantities();
            Tile t = b.getRand();
            int i = t.letter - 'A';
            int[] qs1 = b.getQuantities();
            if (qs1[i] != qs[i] - 1)
                System.out.println("problem with getRand (-1)");

            b.put(t);
            b.put(t);
            b.put(t);

            if (b.getQuantities()[i] != qs[i])
                System.out.println("problem with put (-1)");
        }

        if (b.getTile('a') != null || b.getTile('$') != null || b.getTile('A') == null)
            System.out.println("your getTile is wrong (-2)");

    }


    public static Tile[] get(String s) {
        Tile[] ts = new Tile[s.length()];
        int i = 0;
        for (char c : s.toCharArray()) {
            ts[i] = Bag.getBag().getTile(c);
            i++;
        }
        return ts;
    }

    public static void testBoard() {
        Board b = Board.getBoard();
        if (b != Board.getBoard())
            System.out.println("board should be a Singleton (-5)");


        Bag bag = Bag.getBag();
        Tile[] ts = new Tile[10];
        for (int i = 0; i < ts.length; i++)
            ts[i] = bag.getRand();

        Word w0 = new Word(ts, 0, 6, true);
        Word w1 = new Word(ts, 7, 6, false);
        Word w2 = new Word(ts, 6, 7, true);
        Word w3 = new Word(ts, -1, 7, true);
        Word w4 = new Word(ts, 7, -1, false);
        Word w5 = new Word(ts, 0, 7, true);
        Word w6 = new Word(ts, 7, 0, false);

        if (!b.boardLegal(w5) || !b.boardLegal(w6))
            System.out.println("your boardLegal function is wrong (-10)");

        for (Tile t : ts)
            bag.put(t);

        Word horn = new Word(get("HORN"), 7, 5, false);
        if (b.tryPlaceWord(horn) != 14)
            System.out.println("problem in placeWord for 1st word (-10)");
        System.out.println(formatTiles(b.getTiles()));


        Word farm = new Word(get("FA_M"), 5, 7, true);
        if (b.tryPlaceWord(farm) != 9)
            System.out.println("problem in placeWord for 2ed word (-10)");
        System.out.println(formatTiles(b.getTiles()));

        Word paste = new Word(get("PASTE"), 9, 5, false);
        if (b.tryPlaceWord(paste) != 25)
            System.out.println("problem in placeWord for 3ed word (-10)");
        System.out.println(formatTiles(b.getTiles()));

        Word mob = new Word(get("_OB"), 8, 7, false);
        int mobpoint = b.tryPlaceWord(mob);
        if (mobpoint != 18)
            System.out.println("mob point sould be 18");
        System.out.println(formatTiles(b.getTiles()));

        Word bit = new Word(get("BIT"), 10, 4, false);
        int bitpoint = b.tryPlaceWord(bit);
        if (bitpoint != 22)
            System.out.println("bitpoint should be 22 (-15)");
        System.out.println(formatTiles(b.getTiles()));

        Word bit2 = new Word(get("S_TA"), 9, 4, true);
        if (b.tryPlaceWord(bit2) != 28)
            System.out.println("SBTA should be 28 (-15)");
        System.out.println(formatTiles(b.getTiles()));

        Word bit3 = new Word(get("A_ONE"), 11, 3, false);
        if (b.tryPlaceWord(bit3) != 26)
            System.out.println("ATONE should be 26 (-15)");
        System.out.println(formatTiles(b.getTiles()));

    }

    //		testBag(); // 30 points
//		testBoard(); // 70 points
//		System.out.println("Game Data done");
    //TODO : move to host test
    public static void main(String[] args) {

        BS_Host_Model host = BS_Host_Model.getModel();  //here you will be asked to assign a port for the Host Server
        host.openSocket("127.0.0.1", 20500); //assign ip and port of the Game Server


        System.out.println(host.getMaxScore());


        Thread t = new Thread(() -> BS_Host_Model.getModel().getCommunicationServer().start());
        t.start();

        try {
            Thread.sleep(8000); //wait for server to start
        } catch (InterruptedException e) {
            System.out.println("sleep failed");
        }
        host.startNewGame();
//        BS_Guest_Model client = BS_Guest_Model.getModel();
//        client.setPlayerProperties("matan");
//        //choose the same port as you chose in host C'tor, the ip should be 127.0.0.1 - don't change it;
//        client.openSocket("127.0.0.1", 65533);
//        client.getCommunicationHandler().setCom();
//
//
//        try {
//            Thread.sleep(2000); //wait for server to start
//        } catch (InterruptedException e) {
//            System.out.println("sleep failed");
//        }
//
//        //test start from here
//        host.startNewGame();
//
//
//        //force the current player to be the client
//        int current = host.getCurrentPlayerIndex();
//        if (host.getPlayers().get(host.currentPlayerIndex).get_socketID() == null)
//            host.setNextPlayerIndex(current);
//
//        //print the hand of the client
//        Tile[] hand = new Tile[7];
//        for (int i = 0; i < 7; i++) {
//            hand[i] = host.getPlayers().get(host.currentPlayerIndex).get_hand().get(i);
//        }
//        for (Tile tile : hand) {
//            System.out.print(tile.letter + " ");
//        }
//        //choose word to place from client's hand
//        //be aware to choose valid word!!!
//        System.out.println("choose word to place");
//        Scanner sc = new Scanner(System.in);
//        String word = sc.nextLine();
//
//        //send request to host
//        client.tryPlaceWord(word.toUpperCase(), 7, 5, false);
//
//        //you can run without debug mode to see the result (only if you have a valid word)

    }

    public static String formatTiles(Tile[][] tiles) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                sb.append(tiles[i][j] == null ? "-" : tiles[i][j].letter);
                if (j < tiles[i].length - 1) {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
