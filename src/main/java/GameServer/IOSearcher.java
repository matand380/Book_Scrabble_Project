package GameServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IOSearcher {
    /**
     * The search function takes a String word and an array of Strings fileNames.
     * It returns true if the word is found in any of the files, false otherwise.
     *<p>
     * @param word Search for a word in the files
     * @param fileNames Pass in an array of strings
     * @throws IOException If an input or output exception occurred
     * @return True if the word is found in any of the files
     *
     */
    public static boolean search(String word, String...fileNames) throws IOException {
        List<String> fileNamesList = new ArrayList<>();
        Collections.addAll(fileNamesList, fileNames);
        for (String fileName : fileNamesList) {
//            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/books/"+fileName));
            String readLine = reader.readLine();
            while (readLine != null) {
                if (readLine.contains(word)) {
                    reader.close();
                    return true;
                }
                readLine = reader.readLine();
            }
            reader.close();
        }
        return false;
    }

}
