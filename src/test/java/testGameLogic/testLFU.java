package testGameLogic;

import GameServer.CacheReplacementPolicy;
import GameServer.LFU;


public class testLFU {
    public static void main(String[] args) {
        testLFU t = new testLFU();
        t.testLFU();
        System.out.println("testLFU-done");
    }

    /**
     * The testLFU function tests the LFU cache replacement policy.
     * It adds 5 elements to the cache, and then removes one element from it.
     * If the removed element is not "c", then there is a problem with this implementation of LFU.
     *
     * @return "c" because it is the least frequently used element
     */
    public void testLFU() {
        CacheReplacementPolicy lfu = new LFU();
        lfu.add("a");
        lfu.add("b");
        lfu.add("b");
        lfu.add("c");
        lfu.add("a");

        if (!lfu.remove().equals("c")) System.out.println("wrong implementation for LFU ");
    }
}