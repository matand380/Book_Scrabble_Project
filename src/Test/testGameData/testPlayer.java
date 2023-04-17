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
    public void testHandAfterDraw(){
        Player p = new Player("test");
        p.addTilesTo7();
        List<Tile> hand = p.get_hand();
        p.addTilesTo7();
        if(p.get_hand()!=hand){
            System.out.println("testPlayer.testConstructor: failed");
        }
        p.get_hand().remove(0);
        p.get_hand().remove(1);
        if(p.get_hand().size()!=5)
            System.out.println("after pulling 2 tiles from hand, hand size should be 5");
        p.addTilesTo7();
        if(p.get_hand().size()!=7)
            System.out.println("after pulling 2 tiles from hand, hand size should be 7");

    }
    public void testHandDrawsFromEmptyBag(){
        Player test = new Player("test");
        test.addTilesTo7();
        while(Tile.Bag.getBag().size()!=0){
            Tile.Bag.getBag().remove();
        }
        //while the bag is not empty, remove tiles drawn from the bag

        test.get_hand().remove(0);
        test.get_hand().remove(1);
        if(test.get_hand().size()!=5)
            System.out.println("after pulling 2 tiles from hand, hand size should be 0");
        test.addTilesTo7();
    }
   public static void main(String[] args) {
        testPlayer t = new testPlayer();
        t.testConstructor();
        t.testHandAfterDraw();
        t.testHandDrawsFromEmptyBag();
        System.out.println("testPlayer-done");

    }

}
