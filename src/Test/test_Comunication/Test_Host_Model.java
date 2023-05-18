package test_Comunication;

import Model.BS_Guest_Model;
import Model.BS_Host_Model;
import Model.GameData.Player;
import Model.GameData.Tile;
import Model.GameData.Word;
import java.util.Scanner;



public class Test_Host_Model {

    public static void main(String[] args) {
        BS_Host_Model host=startCommunication_CreatHost();
        //todo:if address is already in use give the option to change the port
        BS_Guest_Model clientA=startCommunication_CreatGuest();

        //test start from here
        host.startNewGame();
        if(!test_StartGame_State())
            System.out.println("test_StartGame_State failed");


        Word w1 = new Word(get("HORN"), 7, 5, false);
		host.requestChallengeActivation(); //if you want to check the challenge method - uncomment this line
        host.tryPlaceWord(w1);
        System.out.println(formatTiles(host.getBoardState()));


        Word w2 = new Word(get("BORN"), 7, 5, false);
        host.tryPlaceWord(w2);
        System.out.println(formatTiles(host.getBoardState()));
        //


        //old test dan and tal
//        BS_Host_Model host = BS_Host_Model.getModel();
//
//        //set name from user
//        System.out.println("Enter your name: ");
//        Scanner scanner = new Scanner(System.in);
//        String name = scanner.next();
//        host.setPlayerProperties(name);
//
//        BS_Guest_Model client1 = BS_Guest_Model.getModel();
//        System.out.println("Please enter the ip address of the host");
//        scanner = new Scanner(System.in);
//        String ip = scanner.nextLine();
//
//        System.out.println("Please enter the port of the host");
//        int port = scanner.nextInt();
//        scanner.close();
//        client1.openSocket(ip, port);
//
//
//        //connect player to the host
//
//
//        //check if the Host model can guest more players
//
//        if(host.isFull())
//            System.out.println("players list is full");
//        else
//            System.out.println("Number of players: "+host.getPlayers().size());
//
//
//
//
//        int bagSize=Tile.Bag.getBag().size();
//        Tile[] tiles = new Tile[4];
//        tiles[0] = new Tile('W', 4);
//        tiles[1] = new Tile('V', 1);
//        tiles[2] = new Tile('I', 1);
//        tiles[3] = new Tile('T', 1);
//        Word word = new Word(tiles, 7, 5, false);
//        host.tryPlaceWord(word);
//        host.challengeWord("WVIT", "0");
//
//        if(bagSize!=Tile.Bag.getBag().size())
//            System.out.println("problem with the bag size after trying " +
//                    "to place and challenge eligible word");
//
//        host.startNewGame();
    }

    //local methods for testing
    public static BS_Host_Model startCommunication_CreatHost() {
        BS_Host_Model host = BS_Host_Model.getModel();  //here you will be asked to assign a random port for the Host Server
        host.openSocket("127.0.0.1", 65535); //assign ip and port of the Game Server
        //TODO: get the name from the user instead of hard coding it as we did here

        System.out.println("Enter the Host name: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        host.setPlayerProperties(name);

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
        client.setPlayerProperties("Tal");
        //choose the same port as you chose for the host the ip should be 127.0.0.1 - don't change it;
        //todo:ask the user to put the host port to connect ,we might need a list of ports (games) to his choice
        System.out.println("Enter the HostPort to connect: ");
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        client.openSocket("127.0.0.1", port);
        client.getCommunicationHandler().setCom();


        try {
            Thread.sleep(2000); //wait for server to start
        } catch (InterruptedException e) {
            System.out.println("sleep failed");
        }
        return client;
    }

    public static boolean test_StartGame_State() {
        //check if all the players got tiles
        for (Player p : BS_Host_Model.getModel().getPlayers()) {
            if (p.get_hand().size() != 7) {
                System.out.println("problem in -check if all the players got tiles");
                return false;
            }
        }


        //number of tile in bag after that loop

        //check passCounter
//check word counter after adding a word

        //the board is empty
        //th bag had the right amount of tiles after the players got tiles
        //the players got a tile and the play order is right(sortAntSetIndex)

        return true;


    }
        //after type pass passCounter is 1
        //after all the plaers type pass game is over

    public static Tile[] get(String s) {
        Tile[] ts=new Tile[s.length()];
        int i=0;
        for(char c: s.toCharArray()) {
            ts[i]=Tile.Bag.getBag().getTile(c);
            i++;
        }
        return ts;
    }

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
}

