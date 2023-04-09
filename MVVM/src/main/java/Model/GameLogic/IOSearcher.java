package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IOSearcher {
    public static boolean search(String word, String...fileNames) throws IOException {
        List<String> fileNamesList = new ArrayList<>();
        Collections.addAll(fileNamesList, fileNames);
        for (String fileName : fileNamesList) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
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
