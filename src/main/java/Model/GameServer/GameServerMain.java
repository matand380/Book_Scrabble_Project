package Model.GameServer;

public class GameServerMain {
    public static void main(String[] args) {
        int port = 8080;
        ClientHandler clientHandler = new BookScrabbleHandler();
        MyServer gameServer = new MyServer(8080, clientHandler);
        DictionaryManager dictionaryManager = DictionaryManager.get();

        gameServer.start();

    }
}