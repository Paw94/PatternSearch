package main.Logic;

import main.Alerts.AlertsFXML;
import main.Alerts.AlertsFXML;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class TextManipulation {

    public static void permuteWordsHeap(String pattern, String separator, PatternMatcher pm) {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(pattern.split(separator)));
        int n = result.size();

        File permutationFile = new File("src\\resources\\PermutationFiles\\Permutations.txt");

        try {
            PrintWriter writer = new PrintWriter(permutationFile, "UTF-8");
            heapsAlgorithm(n, result, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            AlertsFXML.errorAlert("File not found", "The specified filepath is invalid: " + permutationFile.getPath());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            AlertsFXML.errorAlert("File is not supported", "Unsupported encoding in file: " + permutationFile.getPath());
            e.printStackTrace();
        }
    }


    public static ArrayList<String> permutateTerminals(String pattern) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }

    public static ArrayList<String> permutateCharacters(String pattern) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }


    public static void heapsAlgorithm(int n, ArrayList<String> elements, PrintWriter writer) {
        if (n == 1) {
            writer.println(createLineFromArray(elements));
        } else {
            for (int i = 0; i < n; i++) {
                heapsAlgorithm(n - 1, elements, writer);
                if (n % 2 == 0) {
                    swap(elements, n - 1, i);
                } else {
                    swap(elements, n - 1, 0);
                }
            }
        }
    }

    private static <T> void swap(List<T> l, int i, int j) {
        Collections.<T>swap(l, i, j);
    }

    public static ArrayList<String> getPermutationsFromFile(File file) {
        ArrayList<String> permutations = new ArrayList<>();
        StringBuilder fileString = new StringBuilder();
        String part;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                part = scanner.nextLine();
                if (part.contains(";;")) {
                    fileString.append(part);
                } else {
                    fileString.append(part).append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        permutations.clear();
        Collections.addAll(permutations, fileString.toString().split(";;"));

        return permutations;
    }

    public static String createLineFromArray(ArrayList<String> permutation) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < permutation.size() - 1; i++) {
            line.append(permutation.get(i)).append("");
        }
        line.append(permutation.get(permutation.size() - 1)).append(";;");
        return line.toString();
    }

    public static String getStringFromFile(File file) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(file.toPath()));
        } catch (FileNotFoundException e) {
            AlertsFXML.errorAlert("File not found", "The specified filepath is invalid: " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static long countCharacters(File file) {
        BufferedReader reader = null;
        long lineCount = 0, wordCount = 0, charCount = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String currentLine = reader.readLine();
            while (currentLine != null) {
                lineCount++;
                String[] words = currentLine.split(" ");
                wordCount = wordCount + words.length;
                for (String word : words) {
                    charCount = charCount + word.length();
                }
                currentLine = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
        return charCount;
    }
}