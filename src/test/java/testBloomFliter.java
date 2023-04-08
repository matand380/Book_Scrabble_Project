public class testBloomFliter {
    /**
     * The testBloomFilter function tests the BloomFilter class.
     * It creates a new BloomFilter object with 256 bits, using MD5 and SHA-256 as hash functions.
     * Then it adds each word in "the quick brown fox jumps over the lazy dog" to the filter,
     * and checks that all of these words are found in the filter.
     * Finally, it checks that none of these words plus an exclamation point is
     * found in the filter (since they were not added).
     * If any test fails, an error message is printed out to standard output.
     *
     * @return A string of bits that represent the bloom filter

     */
    public static void testBloomFilter() {
        BloomFilter bf =new BloomFilter(256,"MD5","SHA1");
        String[] words = "the quick brown fox jumps over the lazy dog".split(" ");
        for(String w : words)
            bf.add(w);

        if(!bf.toString().equals("0010010000000000000000000000000000000000000100000000001000000000000000000000010000000001000000000000000100000010100000000010000000000000000000000000000000110000100000000000000000000000000010000000001000000000000000000000000000000000000000000000000000001"))
            System.out.println("problem in the bit vector of the bloom filter ");

        boolean found=true;
        for(String w : words)
            found &= bf.contains(w);

        if(!found)
            System.out.println("problem finding words that should exist in the bloom filter ");

        found=false;
        for(String w : words)
            found |= bf.contains(w+"!");

        if(found)
            System.out.println("problem finding words that should not exist in the bloom filter ");
    }
    public static void main(String[] args) {
        testBloomFilter();
        System.out.println("testBloomFilter-done");
    }

}
