package main.Logic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class PatternSearchingUtil {
    public static ArrayList<Integer> KnuthMorissPratt(String text, String pattern) {
        return Naive(text,pattern);
    }

    public static ArrayList<Integer> Naive(String text, String pattern) {
        ArrayList<Integer> matchedPositions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i < n - m + 1; i++) {
            if (text.substring(i, i + m).equals(pattern))
                matchedPositions.add(i);
        }
        return matchedPositions;
    }

    public static ArrayList<Integer> NaiveBackwards(String text, String pattern) {
        ArrayList<Integer> matchedPositions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        for (int i = n; i > m - 1; i--) {
            if (text.substring(i - m, i).equals(pattern))
                matchedPositions.add(i - m);
            //System.out.println(text.substring(i-m, i) + " " + i + " " + (i - m));
        }
        return matchedPositions;
    }

    public static ArrayList<Integer> KarpRabin(String text, String pattern) {
        ArrayList<Integer> matchedPositions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int radix = 256;
        long prime = generatePrime();

        long RM = 1;
        for (int i = 1; i <= m - 1; i++)
            RM = (radix * RM) % prime;

        long patternHash = computeHash(pattern, m, radix, prime);
        long textHash = computeHash(text, m, 256, prime);

        //check for match at offset 0
        if ((patternHash == textHash) && checkString(text, pattern, 0)) {
            matchedPositions.add(0);
        }
        // check for hash match; if hash match, check for exact match
        for (int i = m; i < n; i++) {
            // Remove leading digit, add trailing digit, check for match.
            textHash = (textHash + prime - RM * text.charAt(i - m) % prime) % prime;
            textHash = (textHash * radix + text.charAt(i)) % prime;
            // match
            int offset = i - m + 1;
            if ((patternHash == textHash) && checkString(text, pattern, offset)) {
                matchedPositions.add(offset);
            }
        }
        return matchedPositions;
    }

    private static long computeHash(String key, int m, int radix, long prime) {
        long h = 0;
        for (int j = 0; j < m; j++)
            h = (radix * h + key.charAt(j)) % prime;
        return h;
    }

    private static boolean checkString(String text, String pattern, int p) {
        for (int i = 0; i < pattern.length(); i++)
            if (pattern.charAt(i) != text.charAt(i + p))
                return false;
        return true;
    }

    private static long generatePrime() {
        BigInteger prime = BigInteger.probablePrime(31, new Random());
        return prime.longValue();
    }

    public static ArrayList<Integer> BoyerMoore(String text, String pattern) {
        return Naive(text,pattern);
    }
}
