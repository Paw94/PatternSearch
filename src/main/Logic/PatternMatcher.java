package main.Logic;

import java.util.ArrayList;

public class PatternMatcher {
    private static Grammar grammar;

    public PatternMatcher(Grammar grammar) {
        PatternMatcher.grammar = grammar;
    }

    public boolean checkString(String pattern) {
        System.out.println("Checking pattern... :" + pattern);
        ArrayList<String>[][] rules = initiateRuleTableForGivenTerminals(grammar, pattern);
        int length = rules.length;

        for (int j = 1; j < length; j++) {
            for (int k = 0; k < (length - j); k++) {
                ArrayList<String> cyk = new ArrayList<>();
                int index1 = j;
                int index2 = k;

                for (int c = 0; c < j; c++) {
                    ArrayList<String> cykLeft = rules[c][k];
                    ArrayList<String> cykRight = rules[--index1][++index2];

                    for (String product : cartesianProduct(cykLeft, cykRight)) {
                        if (cyk.indexOf(product) == -1) {
                            cyk.add(product);
                        }
                    }
                }
                rules[j][k] = cyk;
            }
        }
        return rules[length - 1][0].contains("S");
    }

    private ArrayList<String> cartesianProduct(ArrayList<String> leftSide, ArrayList<String> rightSide) {
        ArrayList<String> product = new ArrayList<>();
        if (!leftSide.isEmpty() && !rightSide.isEmpty()) {
            for (String left : leftSide)
                for (String right : rightSide) {
                    String result = left + right;

                    for (String production : grammar.getProductionsForValue(result)) {
                        if (product.indexOf(production) == -1) product.add(production);
                    }
                }
        }
        return product;
    }

    private void printRules(ArrayList<String>[][] rules, int length) {
        System.out.println("------ CYK TABLE ------");
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(rules[length - 1 - i][j] + " ");
            }
            System.out.println();
        }
    }

    private ArrayList<String>[][] initiateRuleTableForSingleCharacters(Grammar grammar, String pattern) {
        int length = pattern.length();
        ArrayList<String>[][] rules = new ArrayList[length][length];

        for (int i = 0; i < length; i++) {
            rules[0][i] = grammar.getProductionsForValue(String.valueOf(pattern.charAt(i)));
        }
        return rules;
    }

    private ArrayList<String>[][] initiateRuleTableForGivenTerminals(Grammar grammar, String pattern) {
        ArrayList<String> terminalsInPattern = splitTerminals(pattern, grammar);
        int size = terminalsInPattern.size();

        ArrayList<String>[][] rules = new ArrayList[size][size];
        for (int i = 0; i < size; i++) {
            rules[0][i] = grammar.getProductionsForValue(String.valueOf(terminalsInPattern.get(i)));
        }
        return rules;
    }

    public ArrayList<String> splitTerminals(String pattern, Grammar grammar) {
        ArrayList<String> allTerminals = grammar.getTerminals();
        ArrayList<String> terminalsInPattern = new ArrayList<>();
        String partialPatternToProcess = pattern;
        allTerminals.sort((s1, s2) -> s2.length() - s1.length());
        while (pattern.length() != 0 && partialPatternToProcess.length() != 0) {
            if (allTerminals.contains(partialPatternToProcess)) {
                terminalsInPattern.add(partialPatternToProcess);
                pattern = pattern.substring(partialPatternToProcess.length(), pattern.length());
                partialPatternToProcess = pattern;
            } else {
                partialPatternToProcess = pattern.substring(0, partialPatternToProcess.length() - 1);
            }
        }
        return terminalsInPattern;
    }


}
