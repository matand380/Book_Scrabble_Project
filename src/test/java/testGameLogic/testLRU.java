package testGameLogic;

import GameServer.CacheReplacementPolicy;
import GameServer.LRU;


public class testLRU {
    public static void main(String[] args) {
        testLRU t = new testLRU();
        t.testLRU();
        System.out.println("testLRU-done");
    }

    /**
     * The testLRU function tests the LRU class.
     * It adds four elements to the cache, and then removes one element from it.
     * If the removed element is not "b";, then an error message is printed out.
     *
     */
    public void testLRU() {
        CacheReplacementPolicy lru = new LRU();
        lru.add("a");
        lru.add("b");
        lru.add("c");
        lru.add("a");

        if (!lru.remove().equals("b")) System.out.println("wrong implementation for LRU");
    }
}
