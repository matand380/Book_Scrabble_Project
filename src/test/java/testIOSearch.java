import java.io.FileWriter;
import java.io.PrintWriter;

public class testIOSearch {

    /**
     * The testIOSearch function tests the IOSearcher class.
     * It creates two text files, one with a string of words and another with a different string of words.
     * Then it searches for the word "is" in both files and prints out whether or not it was found.
     * Next, it searches for the word "cat" in both files and prints out whether or not it was found.
     *
     * @return False when the word is not found in any of the files and true if it is found
     * @throws Exception
     *
     */
    public static void testIOSearch() throws Exception {
        String words1 = "the quick brown fox \n jumps over the lazy dog";
        String words2 = "A Bloom filter is a space efficient probabilistic data structure, \n conceived by Burton Howard Bloom in 1970";
        PrintWriter out = new PrintWriter(new FileWriter("text1.txt"));
        out.println(words1);
        out.close();
        out = new PrintWriter(new FileWriter("text2.txt"));
        out.println(words2);
        out.close();

        if (!IOSearcher.search("is", "text1.txt", "text2.txt"))
            System.out.println("oyur IOsearch did not found a word");
        if (IOSearcher.search("cat", "text1.txt", "text2.txt"))
            System.out.println("your IOsearch found a word that does not exist");
    }
    public static void main(String[] args) {
        try {
            testIOSearch();
        } catch (Exception e) {
            System.out.println("you got some exception ");
        }
        System.out.println("testIOSearch-done");
    }
}
