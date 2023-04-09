package Model.GameLogic;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {

    PrintWriter out;
    Scanner in;

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        in = new Scanner(inFromclient);
        out = new PrintWriter(outToClient);
        String line = in.nextLine();
        String[] books = line.substring(1).split(",");
        if (line.charAt(0) == 'Q') {
            if (DictionaryManager.get().query(books)) {
                out.println("true\n");
            } else {
                out.println("false\n");
            }
        } else if (line.charAt(0) == 'C') {
            if (DictionaryManager.get().challenge(books)) {
                out.println("true\n");
            } else {
                out.println("false\n");
            }
        }


    }

    @Override
    public void close() {
        in.close();
        out.close();
    }

}
