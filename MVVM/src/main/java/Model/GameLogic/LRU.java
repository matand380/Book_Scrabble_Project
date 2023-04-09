package test;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class LRU implements CacheReplacementPolicy {


    LinkedHashSet<String> lruSet = new LinkedHashSet<>();

    public LRU() {
    }

    int maxSize;


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


    @Override
    public String remove() {

        Iterator<String> iterator = lruSet.iterator();
        String last = iterator.next();
        lruSet.remove(last);
        return last;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
