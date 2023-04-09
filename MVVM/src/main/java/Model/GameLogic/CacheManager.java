package test;

import java.math.BigInteger;
import java.util.HashSet;

public class CacheManager {

     int cacheSize;
    CacheReplacementPolicy crp;
    HashSet<String> cache = new HashSet<>();

    public CacheManager(int cacheSize, CacheReplacementPolicy crp) {
        this.cacheSize = cacheSize;
        this.crp = crp;
    }

    public void add(String word) {
        if (cache.contains(word)) {
            crp.add(word);
        } else {
            if (cache.size() == cacheSize) {
                String removed = crp.remove();
                cache.remove(removed);
            }
            cache.add(word);
            crp.add(word);
        }
    }

    public boolean query(String word) {
        return cache.contains(word);
    }

}
