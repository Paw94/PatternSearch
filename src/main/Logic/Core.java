package main.Logic;

import java.util.ArrayList;

public class Core {
    private static Core instance = null;

    private Grammar grammar;
    private String text;
    private String wordSeparator;
    private String terminalSymbolsSeparator;
    private Integer mode;
    private String algorithm;
    private ArrayList<String> terminalsToPermute;

    public ArrayList<String> getTerminalsToPermute() {
        return terminalsToPermute;
    }

    public void setTerminalsToPermute(ArrayList<String> terminalsToPermute) {
        this.terminalsToPermute = terminalsToPermute;
    }

    private String pattern;

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
        }
        return instance;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWordSeparator() {
        return wordSeparator;
    }

    public void setWordSeparator(String wordSeparator) {
        this.wordSeparator = wordSeparator;
    }

    public String getTerminalSymbolsSeparator() {
        return terminalSymbolsSeparator;
    }

    public void setTerminalSymbolsSeparator(String terminalSymbolsSeparator) {
        this.terminalSymbolsSeparator = terminalSymbolsSeparator;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
