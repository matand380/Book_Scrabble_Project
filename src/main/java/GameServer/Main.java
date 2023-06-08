package GameServer;

public class Main {
    public static void main(String[] args) {
        int port =20500;
        GameServer gameServer = new GameServer(port, new BookScrabbleHandler());
        System.out.println("The game server ip is    : "+ gameServer.ip());
        System.out.println("The game server port is  : "+ port);
        DictionaryManager dictionaryManager = DictionaryManager.get();
        gameServer.start();
    }
}