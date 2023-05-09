package Model.GameServer;

import java.util.HashSet;

public class CacheManager {

     int cacheSize;
    CacheReplacementPolicy crp;
    HashSet<String> cache = new HashSet<>();

    /**
     * The CacheManager function is responsible for managing the cache.
     * It will add a new page to the cache if it does not already exist, and update its frequency.
     * If the page exists in the cache, then it will update its frequency and move it to the front of a list.
     * If there are no more spaces left in the cache,
     * then we remove an element from the back of a list based on replacement policy
     * (LRU or LFU).
     *<p>
     * @param  cacheSize Set the size of the cache
     * @param  crp Determine the replacement policy of the cache
     *
     *
     */
    public CacheManager(int cacheSize, CacheReplacementPolicy crp) {
        this.cacheSize = cacheSize;
        this.crp = crp;
    }

    /**
     * The add function adds a word to the cache.
     * If the word is already in
     * the cache, it will be moved to the front of the queue.
     * If not, then
     * if there is a place in our cache, we add it and move it to the front of the queue.
     * Otherwise, we remove an item from the back of the queue and add a new item at the
     * front of the queue.
     * We also update our hashset accordingly so that we can
     * check for membership quickly when adding new items or moving existing ones.
     *<p>
     * @param word Add a word to the cache
     *
     *
     */
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

    /**
     * The query function takes in a string and returns true if the word is in the data structure.
     *<p>
     *
     * @param word Check if the word is in the cache
     *
     * @return True if the word is in the data structure
     *
     */
    public boolean query(String word) {
        return cache.contains(word);
    }

}
