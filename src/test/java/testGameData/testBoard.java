package testGameData;//test the board class located in the model package

import BookScrabbleApp.Model.GameData.Board;
import BookScrabbleApp.Model.GameData.Tile;
import BookScrabbleApp.Model.GameData.Word;

import static BookScrabbleApp.Model.GameData.TestHelper.get;

public class testBoard {
    /**
     * The testGameData.testBoard function tests the boardLegal and tryPlaceWord functions.
     * It first creates a new Board object, b, and checks to see if it is a singleton.
     * Then it creates an array of 10 tiles from the bag (ts) and uses them to create 6 words: w0-w6.
     * The first 5 words are illegal because they are out of bounds or overlap with other letters on the board;
     * however, w5 w6 should be legal because they do not overlap with any other letters on the board.
     * Next we check that all 5 illegal words return false when we call boardLegal on them.
     * return A score
     */
    public void testBoard() {
        Board b = Board.getBoard();
        if(b!=Board.getBoard())
            System.out.println("board should be a Singleton");

        Tile.Bag bag = Tile.Bag.getBag();
        Tile[] ts=new Tile[10];
        for(int i=0;i<ts.length;i++)
            ts[i]=bag.getRand();

        Word w0=new Word(ts,0,6,true);
        Word w1=new Word(ts,7,6,false);
        Word w2=new Word(ts,6,7,true);
        Word w3=new Word(ts,-1,7,true);
        Word w4=new Word(ts,7,-1,false);
        Word w5=new Word(ts,0,7,true);
        Word w6=new Word(ts,7,0,false);

        if( b.boardLegal(w0))
            System.out.println("your boardLegal function is wrong(w0): " + w0 + " is out of bounds");

        if( b.boardLegal(w1))
            System.out.println("your boardLegal function is wrong(w1): " + w1 + " is out of bounds");

        if( b.boardLegal(w2))
            System.out.println("your boardLegal function is wrong(w2): " + w2 + " is out of bounds");

        if( b.boardLegal(w3))
            System.out.println("your boardLegal function is wrong(w3): " + w3 + " is out of bounds");

        if( b.boardLegal(w4))
            System.out.println("your boardLegal function is wrong(w4): " + w4 + " is out of bounds");

        if( !b.boardLegal(w5))
            System.out.println("your boardLegal function is wrong(w5): " + w5 + " is not out of bounds");

        if( !b.boardLegal(w6))
            System.out.println("your boardLegal function is wrong(w6): " + w6 + " is not out of bounds");

        for(Tile t : ts)
            bag.put(t);

        Word horn=new Word(get("HORN"), 7, 5, false);
        if(b.tryPlaceWord(horn)!=14)
            System.out.println("problem in placeWord for 1st word");

        Word farm=new Word(get("FA_M"), 5, 7, true);
        if(b.tryPlaceWord(farm)!=9)
            System.out.println("problem in placeWord for 2ed word");

        Word paste=new Word(get("PASTE"), 9, 5, false);
        if(b.tryPlaceWord(paste)!=25)
            System.out.println("problem in placeWord for 3ed word");

        Word mob=new Word(get("_OB"), 8, 7, false);
        if(b.tryPlaceWord(mob)!=18)
            System.out.println("problem in placeWord for 4th word");

        Word bit=new Word(get("BIT"), 10, 4, false);
        if(b.tryPlaceWord(bit)!=22)
            System.out.println("problem in placeWord for 5th word ");

        Word illegal=new Word(get("ILLEGAL"), 10, 4, false);
        if(b.tryPlaceWord(illegal)==-1)
            System.out.println("problem in placeWord for illegal word");

        Word illegal2=new Word(get("ILLEGAL"), 7, 5, false);
        if(b.tryPlaceWord(illegal2)==-1)
            System.out.println("problem in placeWord for illegal2 word");

    }
    public static void main(String[] args) {
        testBoard t = new testBoard();
        t.testBoard();
        System.out.println("testsBoard-done");
    }
}
