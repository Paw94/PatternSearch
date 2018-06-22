package main.Logic;

import java.util.ArrayList;

public class Alphabet {
    private ArrayList<String> characters;

    public Alphabet() {
        characters = new ArrayList<>();
    }

    public Alphabet(ArrayList<String> characters) {
        this.characters = new ArrayList<>(characters);
    }

    public void addCharacter(String character) {
        this.characters.add(character);
    }

    public void displayAlphabet() {
        if (!characters.isEmpty()) {
            System.out.println("Defined alphabet:");
            for (int i = 0; i < characters.size(); i++) {
                System.out.println(characters.get(i));
            }
        }
    }

    public ArrayList<String> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<String> characters) {
        this.characters = characters;
    }

}
