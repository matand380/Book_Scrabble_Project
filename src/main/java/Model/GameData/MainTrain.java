package Model.GameData;

import Model.BS_Host_Model;
import Model.GameData.Tile.Bag;

public class MainTrain {
	public static void testBag() {
		Bag b=Tile.Bag.getBag();
		Bag b1=Tile.Bag.getBag();
		if(b1!=b)
			System.out.println("your Bag in not a Singleton (-5)");

		int[] q0 = b.getQuantities();
		q0[0]+=1;
		int[] q1 = b.getQuantities();
		if(q0[0]!=q1[0] + 1)
			System.out.println("getQuantities did not return a clone (-5)");

		for(int k=0;k<9;k++) {
			int[] qs = b.getQuantities();
			Tile t = b.getRand();
			int i=t.letter-'A';
			int[] qs1 = b.getQuantities();
			if(qs1[i]!=qs[i]-1)
				System.out.println("problem with getRand (-1)");

			b.put(t);
			b.put(t);
			b.put(t);

			if(b.getQuantities()[i]!=qs[i])
				System.out.println("problem with put (-1)");
		}

		if(b.getTile('a')!=null || b.getTile('$')!=null || b.getTile('A')==null)
			System.out.println("your getTile is wrong (-2)");

	}
	public static Tile[] get(String s) {
		Tile[] ts=new Tile[s.length()];
		int i=0;
		for(char c: s.toCharArray()) {
			ts[i]=Bag.getBag().getTile(c);
			i++;
		}
		return ts;
	}
	public static void testBoard() {
		Board b = Board.getBoard();
		if(b!=Board.getBoard())
			System.out.println("board should be a Singleton (-5)");


		Bag bag = Bag.getBag();
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

		if(!b.boardLegal(w5) || !b.boardLegal(w6))
			System.out.println("your boardLegal function is wrong ");

		for(Tile t : ts)
			bag.put(t);

		Word horn=new Word(get("HORN"), 7, 5, false);
		if(b.tryPlaceWord(horn)!=14)
			System.out.println("problem in placeWord for 1st word");
		System.out.println(formatTiles(b.getTiles()));


		Word farm=new Word(get("FA_M"), 5, 7, true);
		if(b.tryPlaceWord(farm)!=9)
			System.out.println("problem in placeWord for 2ed word");
		System.out.println(formatTiles(b.getTiles()));

		Word paste=new Word(get("PASTE"), 9, 5, false);
		if(b.tryPlaceWord(paste)!=25)
			System.out.println("problem in placeWord for 3ed word");
		System.out.println(formatTiles(b.getTiles()));

		Word mob=new Word(get("_OB"), 8, 7, false);
		int mobpoint = b.tryPlaceWord(mob);
		if(mobpoint!=18)
			System.out.println("mob point should be 18");
		System.out.println(formatTiles(b.getTiles()));

		Word bit=new Word(get("BIT"), 10, 4, false);
		int bitpoint = b.tryPlaceWord(bit);
		if(bitpoint!=22)
			System.out.println("bitpoint should be 22");
		System.out.println(formatTiles(b.getTiles()));

		Word bit2=new Word(get("S_TA"), 9, 4, true);
		if(b.tryPlaceWord(bit2)!=28)
			System.out.println("SBTA should be 28");
		System.out.println(formatTiles(b.getTiles()));

		Word bit3=new Word(get("A_ONE"), 11, 3, false);
		if(b.tryPlaceWord(bit3)!=26)
			System.out.println("ATONE should be 26");
		System.out.println(formatTiles(b.getTiles()));

	}

	public static void main(String[] args) {
//		testBag(); // 30 points
//		testBoard(); // 70 points
//		System.out.println("Game Data done");
		//TODO : move to host test
		BS_Host_Model host = BS_Host_Model.getModel(); // change port

//		host.setPlayerProperties("Eviatar");
		//TODO : move to func in test file

		//try place word
//		Tile[] tiles = new Tile[4];
//		tiles[0] = new Tile('W', 4);
//		tiles[1] = new Tile('V', 1);
//		tiles[2] = new Tile('I', 1);
//		tiles[3] = new Tile('T', 1);
//		Word word = new Word(tiles, 7, 5, false);
//		host.tryPlaceWord(word);
//		host.challengeWord("WVIT", "0");

		//TODO : move to guest test
		////////// guest test
//		BS_Guest_Model client = BS_Guest_Model.getModel();
//		client.setPlayerProperties("matan");
//		client.openSocket("127.0.0.1", 65533);  //copy local server ip + server port
//		client.getCommunicationHandler().setCom();

		host.startNewGame();

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
