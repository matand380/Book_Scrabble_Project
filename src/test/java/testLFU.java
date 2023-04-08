public class testLFU {
    public static void testLFU() {
        CacheReplacementPolicy lfu = new LFU();
        lfu.add("a");
        lfu.add("b");
        lfu.add("b");
        lfu.add("c");
        lfu.add("a");

        if (!lfu.remove().equals("c"))
            System.out.println("wrong implementation for LFU (-10)");
    }
    public static void main(String[] args) {
        testLFU();
        System.out.println("testLFU-done");
    }
}