package Model.GameLogic;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookScrabbleHandler  {

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

    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        in = new Scanner(inFromclient);
        out = new PrintWriter(outToClient);
        String line = in.nextLine();
        String[] books = line.substring(1).split(":");
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

    /**
     * The close function closes the input and output streams.
     *<p>
     *
     *
     */

    public void close() {
        in.close();
        out.close();
    }

}
