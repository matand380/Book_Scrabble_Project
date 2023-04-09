package Model.GameLogic;

import java.io.*;


public class Dictionary {
    String[] fileNames;
    CacheReplacementPolicy LRU; //for existing words
    CacheManager cacheWithLRU;
    CacheReplacementPolicy LFU; //for non-existing words
    CacheManager cacheWithLFU;
    BloomFilter filter;

    public Dictionary(String... fileName) {
        fileNames = new String[fileName.length];
        for (int i = 0; i < fileName.length; i++) {
            fileNames[i] = fileName[i];
        }
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
