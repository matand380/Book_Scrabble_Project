package GameServer;

public class Main {
    /**
     * The main function of the game server.
     * @param //String[] args Pass command line arguments to the program
     */
    public static void main(String[] args) {
        int port=20500;
        GameServer gameServer = new GameServer(port, new BookScrabbleHandler());
        System.out.println("The game server IP is   :   "+gameServer.ip());
        System.out.println("The game server port is :   "+port);
        DictionaryManager dictionaryManager = DictionaryManager.get();
        gameServer.start();
        dictionaryManager.query("aa");
    }
}