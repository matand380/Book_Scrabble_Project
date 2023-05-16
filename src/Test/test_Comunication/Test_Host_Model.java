package test_Comunication;

import Model.BS_Guest_Model;
import Model.BS_Host_Model;
import Model.GameData.Tile;
import Model.GameData.Word;

import java.util.Scanner;


public class Test_Host_Model {
    public static void main(String[] args) {

        BS_Host_Model host = BS_Host_Model.getModel();

        //set name from user
        System.out.println("Enter your name: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        host.setPlayerProperties(name);

        BS_Guest_Model client1 = BS_Guest_Model.getModel();
        System.out.println("Please enter the ip address of the host");
        scanner = new Scanner(System.in);
        String ip = scanner.nextLine();

        System.out.println("Please enter the port of the host");
        int port = scanner.nextInt();
        scanner.close();
        client1.openSocket(ip, port);


        //connect player to the host


        //check if the Host model can guest more players

        if(host.isFull())
            System.out.println("players list is full");
        else
            System.out.println("Number of players: "+host.getPlayers().size());




        int bagSize=Tile.Bag.getBag().size();
        Tile[] tiles = new Tile[4];
        tiles[0] = new Tile('W', 4);
        tiles[1] = new Tile('V', 1);
        tiles[2] = new Tile('I', 1);
        tiles[3] = new Tile('T', 1);
        Word word = new Word(tiles, 7, 5, false);
        host.tryPlaceWord(word);
        host.challengeWord("WVIT", "0");

        if(bagSize!=Tile.Bag.getBag().size())
            System.out.println("problem with the bag size after trying " +
                    "to place and challenge eligible word");







        host.startNewGame();
    }
}
