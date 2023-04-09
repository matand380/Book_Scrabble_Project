package Model.GameLogic;


import java.util.HashMap;
import java.util.Map;

public class DictionaryManager {
    private static DictionaryManager dictionaryManager = null;
    Map<String, Dictionary> dictionaryMap;

    private DictionaryManager() {
        dictionaryMap = new HashMap<>();
    }

    public static DictionaryManager get() {
        if (dictionaryManager == null) {
            dictionaryManager = new DictionaryManager();
        }
        return dictionaryManager;
    }

    //every book has its own dictionary
    //word is the searched word
    //the last element in the String is the query
    //if a book not in the map, it is added
    //query will be checked for every book
    //return the fit answer
    public boolean query(String... books) {
        String word = books[books.length - 1];
        for (int i = 0; i < books.length - 1; i++) {
            if (!dictionaryMap.containsKey(books[i])) {
                dictionaryMap.put(books[i], new Dictionary(books[i]));
            }
            if (dictionaryMap.get(books[i]).query(word)) {
                return true;
            }
        }
        return false;
    }

    //every book has its own dictionary
    //word is the searched word
    //the last element in the String is the challenge
    //if a book not in the map, it is added
    //challenge will be checked for every book
    //return the fit answer
    public boolean challenge(String... books) {
        String word = books[books.length - 1];
        for (int i = 0; i < books.length - 1; i++) {
            if (!dictionaryMap.containsKey(books[i])) {
                dictionaryMap.put(books[i], new Dictionary(books[i]));
            }
            if (dictionaryMap.get(books[i]).challenge(word)) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        return dictionaryMap.size();
    }
}
