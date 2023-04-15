package testGameData;

import Model.GameData.*;

import java.util.List;

public class testPlayer {
    //test constructor
public void testConstructor(){
        Player p = new Player("test");
        if(!p.get_name().equals("test") || p.get_score() != 0 || p.get_hand().size() != 0){
            System.out.println("testPlayer.testConstructor: failed");
        }

        //print hand
        if(p.get_hand().size() != 0){
            System.out.println("testPlayer.testConstructor: failed");
        }
        p.addTilesTo7();
        if(p.get_hand().size() != 7){
            System.out.println("testPlayer.testConstructor: failed");
        }
        List<Tile> hand = p.get_hand();
        p.addTilesTo7();
        if(p.get_hand()!=hand){
            System.out.println("testPlayer.testConstructor: failed");
        }
    }
   public static void main(String[] args) {
        testPlayer t = new testPlayer();
        t.testConstructor();
        System.out.println("testPlayer-done");

    }

}
