package Model.GameLogic;

import java.util.Iterator;
import java.util.LinkedHashSet;


public class LRU implements CacheReplacementPolicy {


    LinkedHashSet<String> lruSet = new LinkedHashSet<>();

    /**
     * The LRU function is a constructor that creates an empty LinkedHashMap.
     * LRU is a cache replacement policy that removes the least recently used item.
     * It does this by keeping track of the order in which items are accessed,
     * and removing the one that was accessed the longest ago.
     */
    public LRU() {
    }

    int maxSize;


    /**
     * The add function adds a word to the lruSet.
     * If the word is already in the set, it removes it and then adds it again.
     * If there are 400 words in the set, remove() is called to remove one of them.
     *<p>
     * @param word Add a word to the lru cache
     *
     *
     */
    @Override
    public void add(String word) {
        if (lruSet.contains(word)) {
            lruSet.remove(word);
        }
        if (lruSet.size() == 400) {
            remove();
        }
        lruSet.add(word);


    }


    /**
     * The remove function removes the least recently used item from the cache.
     *<p>
     *
     * @return The last element in the set
     *
     */
    @Override
    public String remove() {

        Iterator<String> iterator = lruSet.iterator();
        String last = iterator.next();
        lruSet.remove(last);
        return last;
    }

    /**
     * The setMaxSize function sets the maximum size of the queue.
     *<p>
     *
     * @param maxSize Set the maxsize variable to a specific value
     *
     *
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * The getMaxSize function returns the maximum size of the stack.
     *<p>
     *
     *
     * @return The maximum size of the stack
     *
     */
    public int getMaxSize() {
        return maxSize;
    }
}
