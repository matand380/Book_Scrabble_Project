package GameServer;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {

    PrintWriter out;
    Scanner in;

    boolean stop = false;

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
     * <p>
     *
     * @param inFromclient inFromclient Read the input from the client
     * @param outToClient  outToClient Send the response to the client
     */
    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        while (!stop) {
            in = new Scanner(inFromclient);
            out = new PrintWriter(outToClient);
            String line = in.nextLine();
            String word = line.split(":")[line.split(":").length - 1];
            System.out.println(word);
            String directoryPath = "src/main/resources/books/";
            List<String> booksList = new ArrayList<>();
            Path pathBooks = Paths.get(directoryPath);
            try {
                Files.walk(pathBooks).forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        booksList.add(filePath.getFileName().toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            booksList.add(word);
            String[] books = booksList.toArray(new String[booksList.size()]);
            if (line.charAt(0) == 'Q') {
                if (DictionaryManager.get().query(books)) {
                    out.println("Q:true\n");
                    System.out.println("Q:true\n");
                    out.flush();
                } else {
                    out.println("Q:false\n");
                    System.out.println("Q:false\n");
                    out.flush();
                }
            } else if (line.charAt(0) == 'C') {
                if (DictionaryManager.get().challenge(books)) {
                    out.println("C:true");
                    System.out.println("C:true\n");
                    out.println("\n");
                    out.flush();
                } else {
                    out.println("C:false\n");
                    System.out.println("C:false\n");
                    out.flush();
                }
            }
        }


    }


    public void close() {
        in.close();
        out.close();
    }

}
