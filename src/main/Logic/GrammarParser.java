package main.Logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GrammarParser {
    private ArrayList<String> terminals;
    private ArrayList<String> nonTerminals;
    private HashMap<String, ArrayList<String>> productions;
    private int initialNonTerminal;

    public static Grammar parseGrammarFile(String filePath) {
        Grammar parsedGrammar = new Grammar();
        File grammarFile = null;
        Scanner scanner = null;
        String production = "";

        try {
            grammarFile = new File(filePath);
            scanner = new Scanner(grammarFile);

            while (scanner.hasNextLine()) {
                production = scanner.nextLine();
                String[] productionBothSides = production.split("->");
                if (productionBothSides.length == 2) {
                    String leftSide = productionBothSides[0];
                    String[] rightSide = productionBothSides[1].split("\\|");

                    ArrayList<String> parsedProductions = new ArrayList<>();
                    Pattern p = Pattern.compile("([a-z])");

                    for (String part : rightSide) {
                        part = part.replaceAll("<.*>", "");
                        if (!parsedGrammar.getTerminals().contains(part) && !part.equals(""))
                            parsedGrammar.addTerminal(part);
                        //   parsedProductions = new ArrayList<String>(Arrays.asList(rightSide));
                        // parsedProductions.add(part);
                    }
                    leftSide = leftSide.replaceAll("[<|>]", "");

                    for (String part : rightSide) {
                        part = part.replaceAll("[<|>]", "");
                        parsedProductions.add(part);
                    }

                    if (!parsedGrammar.getNonTerminals().contains(leftSide)) parsedGrammar.addNonTerminal(leftSide);
                    parsedGrammar.addProduction(leftSide, parsedProductions);
                }
            }

            scanner.close();

        } catch (IOException ex) {
            System.out.println("Cannot parse grammar file");
        }
        return parsedGrammar;
    }

}
