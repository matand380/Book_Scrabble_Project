public class testsPatam1 {
    /**
     * The main function is used to test the other classes in this package.
     * It creates an instance of each class and then calls its main function.
     * The main function prints out an error message if any of these
     * conditions are not met.
     *
     * @return Void
     *
     */
    public static void main(String[] args) {
        new testLRU();
        new testDictionary();
        new testCacheManager();
        new testBloomFliter();
        new testIOSearch();
        new testLFU();
        new testBag();
        new testBoard();
        System.out.println("testsPatam1-done");

    }
}

