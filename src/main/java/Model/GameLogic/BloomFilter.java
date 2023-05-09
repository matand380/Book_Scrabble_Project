package Model.GameLogic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


public class BloomFilter {

    BitSet bitSet;
    MessageDigest md1;
    MessageDigest md2;
    BigInteger bigInteger;


    List<MessageDigest> mdList;

    int size;

    /**
     * The BloomFilter function takes in a string and hashes it using the hash functions
     * that were passed into the constructor. It then sets each of those bits to 1.
     *<p>
     * @param size size Set the size of the bitset
     * @param hashFunctions hashFunctions Pass in a variable number of strings
     *
     *
     */
    public BloomFilter(int size, String... hashFunctions) {
        this.size = size;
        bitSet = new BitSet();

        try {
            mdList = new ArrayList<>();
            for (String hashFunction : hashFunctions) {
                mdList.add(MessageDigest.getInstance(hashFunction));
            }
        } catch (Exception e) {
            System.out.println("problem with hash function");
        }
    }

    /**
        * The add function takes in a string and hashes it using the hash functions
        * that were passed into the constructor. It then sets each of those bits to 1.
        *<p>
        * @param word word The word to be added to the bloom filter
     *
        */
    public void add(String word) {

        for (MessageDigest md: mdList)
        {
            byte[] toHash = md.digest(word.getBytes());
            bigInteger = new BigInteger(1,toHash);
            int hashValue = Math.abs(bigInteger.abs().intValue()) % size;
            bitSet.set(hashValue, true);

        }

    }

    /**
     * The contains function takes in a string and returns true if the bloom filter contains that string.
     *<p>
     *
     * @param word word Get the hash value of a word
     *
     * @return True if the bloom filter contains the word
     *
     */
    public boolean contains(String word) {

        for (MessageDigest md: mdList)
        {
            byte[] toHash = md.digest(word.getBytes());
            bigInteger = new BigInteger(1,toHash);
            int hashValue = Math.abs(bigInteger.abs().intValue()) % size;
            if (!bitSet.get(hashValue))
                return false;
        }
        return true;
    }

    /**
     * The toString function returns a string representation of the BitSet.
     *<p>
     *
     * @return A string of all the bits in the bitset
     *
     */
    @Override
    public String toString() {
        StringBuilder bitsString = new StringBuilder(bitSet.length());
        for (int i = 0; i < bitSet.length(); i++) {
            bitsString.append(bitSet.get(i) ? 1 : 0);
        }
        return bitsString.toString();
    }


}
