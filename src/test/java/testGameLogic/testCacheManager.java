package testGameLogic;

import GameServer.CacheManager;
import GameServer.LRU;


public class testCacheManager {
    public static void main(String[] args) {
        testCacheManager t = new testCacheManager();
        t.testCacheManager();
        System.out.println("testCacheManager-done");
    }

    /**
     * The testCacheManager function tests the CacheManager class.
     * It creates a new CacheManager object with 3 slots and an LRU replacement policy,
     * then queries for three nonexistent keys. The result should be false for all of them,
     * so if any are true it prints out an error message .
     * Then it adds three keys to the cache manager and queries again; this time they should all be true,
     * so if any are false it prints out another error message .
     * Finally, it checks that when a key is added to a full cache manager (with no room left),
     * the least recently used key is removed from the
     *
     * @return True if the test passes and false otherwise
     */
    public void testCacheManager() {
        CacheManager exists = new CacheManager(3, new LRU());
        boolean b = exists.query("a");
        b |= exists.query("b");
        b |= exists.query("c");

        if (b) System.out.println("wrong result for CacheManager first queries");

        exists.add("a");
        exists.add("b");
        exists.add("c");

        b = exists.query("a");
        b &= exists.query("b");
        b &= exists.query("c");

        if (!b) System.out.println("wrong result for CacheManager second queries");

        boolean bf = exists.query("d"); // false, LRU is "a"
        exists.add("d");
        boolean bt = exists.query("d"); // true
        bf |= exists.query("a"); // false
        exists.add("a");
        bt &= exists.query("a"); // true, LRU is "b"

        if (bf || !bt) System.out.println("wrong result for CacheManager last queries");

    }

}
