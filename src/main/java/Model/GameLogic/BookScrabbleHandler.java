package Model.GameLogic;


import java.io.*;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {

    PrintWriter out;
    Scanner in;

    /**
     * The handleClient function is responsible for handling the client's request.
     * It takes in an InputStream and OutputStream, which are used to communicate with the client.
     * The function reads a line from the input stream, and then parses it into a String array of books.
     * If the first character of this line is 'Q', then we call query on our DictionaryManager object with these books as parameters.
     * If it returns true, we print &quot;true&quot; to our output stream; otherwise we print &quot;false&quot;.
     * This indicates whether there exists a valid dictionary containing all of these words (in order).
     * If the first character of this line is 'C', then we call challenge on our DictionaryManager object with these books as parameters.
     * If it returns true, we print &quot;true&quot; to our output stream; otherwise we print &quot;false&quot;.
     *
     *<p>
     *
     * @param inFromclient inFromclient Read the input from the client
     * @param outToClient outToClient Send the response to the client

     */

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        in = new Scanner(inFromclient);
        out = new PrintWriter(outToClient);
        String line = in.nextLine();
        String word = line.substring(1).split(":")[1];
        System.out.println(word);
        String[] books= null ;
        books[books.length-1]=word;
        // TODO: 09/05/2023 need to load the books name from folder and add them to booksArray
        // TODO: 09/05/2023 line[0] = Q\C, line[1] = Word books[0-n] = books name
        // TODO: 09/05/2023 add the word to booksArray
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
