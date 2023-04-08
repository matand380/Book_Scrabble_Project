public class testDictionary {
    public static void testDictionary() {
        Dictionary d = new Dictionary("text1.txt", "text2.txt");
        if (!d.query("is")) //BloomFilter
            System.out.println("problem with dictionarry in query (-5)");
        if (!d.challenge("lazy")) //IOSearcher
            System.out.println("problem with dictionarry in query (-5)");
    }
    public static void main(String[] args) {
        testDictionary();
        System.out.println("testDictionary-done");
    }
}
