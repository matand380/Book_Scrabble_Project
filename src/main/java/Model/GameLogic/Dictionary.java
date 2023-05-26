package Model.GameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Dictionary {
    String[] fileNames;
    CacheReplacementPolicy LRU; //for existing words
    CacheManager cacheWithLRU;
    CacheReplacementPolicy LFU; //for non-existing words
    CacheManager cacheWithLFU;
    BloomFilter filter;

    /**
     * The Dictionary function takes in a list of file names and reads them into the Bloom Filter.
     * It also initializes the LRU and LFU caches, as well as their respective CacheManagers.
     *<p>
     * @param  fileName Pass in a variable number of arguments
     *
     *
     */
    public Dictionary(String... fileName) {
        fileNames = new String[fileName.length];
        System.arraycopy(fileName, 0, fileNames, 0, fileName.length);
        this.LRU = new LRU();
        this.cacheWithLRU = new CacheManager(400, LRU);
        this.LFU = new LFU();
        this.cacheWithLFU = new CacheManager(100, LFU);
        this.filter = new BloomFilter(256, "MD5", "SHA1");
        try {
            for (String name : fileNames) {
                BufferedReader reader = new BufferedReader(new FileReader(name));
                String readLine = reader.readLine();
                while (readLine != null) {
                    String[] words = readLine.split(" ");
                    for (String word : words) {
                        filter.add(word);
                    }
                    readLine = reader.readLine();
                }
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("problem with IO");
        }
    }

    /**
     * The query function takes a string as input and returns true if the word is in the dictionary, false otherwise.
     * It first checks the LRU cache, then the LFU cache, and finally the Bloom Filter.
     *<p>
     *
     * @param  word Query the cache and bloom filter
     *
     * @return True if the word is in the data structure
     *
     */
    public boolean query(String word) {
        if (cacheWithLRU.query(word))
            return true;
        if (cacheWithLFU.query(word))
            return false;

        if (filter.contains(word))
            cacheWithLRU.add(word);
        else
            cacheWithLFU.add(word);

        return filter.contains(word);
    }


    /**
     * The challenge function takes a word as input and returns true if the word is found in any of the files
     * that are being searched.
     * If it is not found, then false is returned.

     *
     * @param word Search for the word in the file
     *
     * @return A boolean value
     *
     */
    public boolean challenge(String word) {
        boolean isFound;

        try {
            isFound = IOSearcher.search(word, fileNames);
        } catch (IOException e) {
            return false;
        }
        return isFound;

    }


}
