package GameServer;

import java.util.LinkedHashMap;
import java.util.Map;

public class LFU implements CacheReplacementPolicy {

    int maxSize;
    Map<String, Integer> lfuMap = new LinkedHashMap<>();

    /**
     * The LFU function is a cache replacement policy that removes the least frequently used item.
     * It does this by keeping track of how many times an item has been accessed,
     * and removing the one with the lowest count.
     */
    public LFU() {
    }

    /**
     * The add function adds a word to the LFU cache.
     *<p>
     *
     * @param  word Add a word to the map
     *
     *
     */
    @Override
    public void add(String word) {

        if (lfuMap.containsKey(word)) {
            lfuMap.put(word, lfuMap.get(word) + 1);
        } else {
            if (lfuMap.size() == 100) {
                remove();
            }
            lfuMap.put(word, 1);
        }


    }

    /**
     * The remove function removes the least frequently used item from the cache.
     *<p>
     *
     *
     * @return The key of the element with the lowest frequency
     *
     */
    @Override
    public String remove() {
        String minKey = null;
        int minFreq = Integer.MAX_VALUE;

        for (Map.Entry<String,Integer> e : lfuMap.entrySet())
        {
            if (e.getValue() < minFreq)
            {
                minKey = e.getKey();
                minFreq = e.getValue();
            }
        }
        lfuMap.remove(minKey);
        return minKey;
    }

    /**
     * The setMaxSize function sets the maximum size of the queue.
     *<p>
     *
     * @param maxSize Set the maximum size of the array
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
     * @return The maxsize variable
     *
     */
    public int getMaxSize() {
        return maxSize;
    }
}
