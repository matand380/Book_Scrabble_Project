package GameServer;

public class Main {
    public static void main(String[] args) {
        GameServer gameServer = new GameServer(20500, new BookScrabbleHandler());
        System.out.println(gameServer.ip());
        DictionaryManager dictionaryManager = DictionaryManager.get();
        gameServer.start();
    }
}