package TestBS_Host_Model;

import Model.BS_Host_Model;
import Model.GameData.Tile;
import Model.GameData.Word;

import java.util.Scanner;

public class Test_complete_Host_model {
  public static void main(String[] args) {

        BS_Host_Model host = new BS_Host_Model();
        //set name from user
        Scanner scanner = new Scanner(System.in);
           System.out.println("Enter your name: ");
               String name = scanner.nextLine();
        host.setPlayerProperties(name);

        int bagSize=host.getBagSize();

      Tile[] tiles = new Tile[4];
      tiles[0] = new Tile('W', 4);
      tiles[1] = new Tile('V', 1);
      tiles[2] = new Tile('I', 1);
      tiles[3] = new Tile('T', 1);
      Word word = new Word(tiles, 7, 5, false);
      host.tryPlaceWord(word);
      host.challengeWord("WVIT", "0");

      if(bagSize!=host.getBagSize())
          System.out.println("problem with the bag size after trying " +
                                 "to place and challenge eligible word");

      //try to see if now what we try to check for WVIT the process going to c





      host.startNewGame();
    }
}

