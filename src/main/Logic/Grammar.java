package main.Logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Grammar {
    private ArrayList<String> terminals;
    private ArrayList<String> nonTerminals;
    private HashMap<String, ArrayList<String>> productions;
    private int initialNonTerminal;
    // flag defines the type of instanced grammar. In compliance with Chomsky hierarchy, flag = 0/1/2/3
    // not used for now
    private int flag;

    public Grammar() {
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();
        productions = new HashMap<>();
        initialNonTerminal = 0;
    }

    public boolean addRule() {


        return true;
    }

    public void displayGrammar() {
        System.out.println("------ NonTerminals ------");
        System.out.println(nonTerminals);
        System.out.println("------ Initial NonTerminal ------");
        System.out.println(nonTerminals.get(initialNonTerminal));
        System.out.println("------ Terminals ------");
        System.out.println(terminals);

        System.out.println("------ Productions ------");
        for (String name : productions.keySet()) {
            String key = name.toString();
            System.out.print(key + " -> ");
            System.out.println(productions.get(name));
        }
    }

    public void addProduction(String variable, ArrayList<String> production) {
        productions.put(variable, production);
    }

    public void addTerminal(String terminal) {
        terminals.add(terminal);
    }

    public void addNonTerminal(String nonTerminal) {
        nonTerminals.add(nonTerminal);
    }

    public ArrayList<String> getProductionsForValue(String value) {
        ArrayList<String> strings = new ArrayList<>();
        for (String key : productions.keySet()) {
            if (productions.get(key).contains(value)) {
                strings.add(key);
            }
        }
        return strings;
    }

    public ArrayList<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(ArrayList<String> terminals) {
        this.terminals = terminals;
    }

    public ArrayList<String> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(ArrayList<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public HashMap<String, ArrayList<String>> getProductions() {
        return productions;
    }

    public void setProductions(HashMap<String, ArrayList<String>> productions) {
        this.productions = productions;
    }

    public int getInitialNonTerminal() {
        return initialNonTerminal;
    }

    public void setInitialNonTerminal(int initialNonTerminal) {
        this.initialNonTerminal = initialNonTerminal;
    }

    public Grammar(ArrayList<String> terminals, ArrayList<String> nonTerminals, HashMap<String, ArrayList<String>> productions, int initialNonTerminal) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.productions = productions;
        this.initialNonTerminal = initialNonTerminal;
    }

    public int getNumberOfRules() {
        int total = 0;
        for (List<String> list : productions.values()) {
            total += list.size();
        }
        return total;
    }

    public int grammarSize() {
        int size = 0;
        for (String nonTerminal : productions.keySet()) {
            ArrayList<String> value = productions.get(nonTerminal);
            size += value.size() + 1;
        }
        return size;
    }
}

