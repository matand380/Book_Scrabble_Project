package test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Vector;

public class BloomFilter {

    BitSet bitSet;
    MessageDigest md1;
    MessageDigest md2;
    BigInteger bigInteger;


    List<MessageDigest> mdList;

    int size;

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

    public void add(String word) {

        for (MessageDigest md: mdList)
        {
            byte[] toHash = md.digest(word.getBytes());
            bigInteger = new BigInteger(1,toHash);
            int hashValue = Math.abs(bigInteger.abs().intValue()) % size;
            bitSet.set(hashValue, true);

        }

    }

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

    @Override
    public String toString() {
        StringBuilder bitsString = new StringBuilder(bitSet.length());
        for (int i = 0; i < bitSet.length(); i++) {
            bitsString.append(bitSet.get(i) ? 1 : 0);
        }
        return bitsString.toString();
    }


}
