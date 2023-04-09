package test;

import java.util.LinkedHashMap;
import java.util.Map;

public class LFU implements CacheReplacementPolicy {

    int maxSize;
    Map<String, Integer> lfuMap = new LinkedHashMap<>();

    public LFU() {
    }

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

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
