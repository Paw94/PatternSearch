package main.Logic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class PatternSearchingUtil {
    public static ArrayList<Integer> knuthMorrissPratt(String text, String pattern) {
        ArrayList<Integer> matchedPositions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        int KMPNext[] = new int[m + 1];
        int prefixSuffixLength = 0;

        // computing KMPNext
        KMPNext[0] = prefixSuffixLength = -1;
        for (int i = 1; i <= m; i++) {
            while ((prefixSuffixLength > -1) && (pattern.charAt(prefixSuffixLength) != pattern.charAt(i - 1)))
                prefixSuffixLength = KMPNext[prefixSuffixLength];

            ++prefixSuffixLength;

            if ((i == m) || (pattern.charAt(i) != pattern.charAt(prefixSuffixLength)))
                KMPNext[i] = prefixSuffixLength;
            else
                KMPNext[i] = KMPNext[prefixSuffixLength];
        }

        // KMP algorithm
        int prefix = 0;

        for (int i = 0; i < n; i++) {
            while ((prefix > -1) && (pattern.charAt(prefix) != text.charAt(i))) {
                prefix = KMPNext[prefix];
            }
            prefix++;

            if (prefix == m) // when match was found
            {
                matchedPositions.add(i - prefix + 1);
                prefix = KMPNext[prefix];
            }
        }
        return matchedPositions;
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
        ArrayList<Integer> matchedPositions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        int lastChar = 255;
        int firstChar = 0;
        int Last[] = new int[lastChar - firstChar + 1];

        int BMNext[] = new int[m + 1];
        int Pi[] = new int[m + 1];

        // compute Last table
        for (int i = 0; i <= lastChar - firstChar; i++) Last[i] = -1;
        for (int i = 0; i < m; i++) Last[pattern.charAt(i) - firstChar] = i;

        int patternOffset, i, prefixSuffixOffset, j;

        // Stage 1
        for (i = 0; i <= m; i++) BMNext[i] = 0;

        i = m;
        prefixSuffixOffset = m + 1;

        Pi[i] = prefixSuffixOffset;
        while (i > 0) {
            while ((prefixSuffixOffset <= m) && (pattern.charAt(i - 1) != pattern.charAt(prefixSuffixOffset - 1))) {
                if (BMNext[prefixSuffixOffset] == 0) BMNext[prefixSuffixOffset] = prefixSuffixOffset - i;
                prefixSuffixOffset = Pi[prefixSuffixOffset];
            }
            Pi[--i] = --prefixSuffixOffset;
        }
        // Stage 2
        prefixSuffixOffset = Pi[0];
        for (i = 0; i <= pattern.length(); i++) {
            if (BMNext[i] == 0) BMNext[i] = prefixSuffixOffset;
            if (i == prefixSuffixOffset) prefixSuffixOffset = Pi[prefixSuffixOffset];
        }
        // BM algorithm
        patternOffset = i = 0;
        while (i <= n - m) {
            j = m - 1;
            while ((j > -1) && (pattern.charAt(j) == text.charAt(i + j))) j--;
            if (j == -1) {
                while (patternOffset < i) {
                    patternOffset++;
                }
                matchedPositions.add(patternOffset);
                patternOffset++;
                i += BMNext[0];
            } else {
                i += Math.max(BMNext[j + 1], j - Last[text.charAt(i + j) - firstChar]);
            }
        }
        return matchedPositions;
    }
}
