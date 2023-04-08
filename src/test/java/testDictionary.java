public class testDictionary {
    /**
     * The testDictionary function tests the Dictionary class.
     * It creates a new dictionary object and checks if it contains the words "is" and "lazy".
     *
     * @return True if the query is in the dictionary, and false otherwise
     *

     */
    public static void testDictionary() {
        Dictionary d = new Dictionary("text1.txt", "text2.txt");
        if (!d.query("is")) //BloomFilter
            System.out.println("problem with dictionary in query ");
        if (!d.challenge("lazy")) //IOSearcher
            System.out.println("problem with dictionary in query");
    }
    public static void main(String[] args) {
        testDictionary();
        System.out.println("testDictionary-done");
    }
}
