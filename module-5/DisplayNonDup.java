// cschumacher_10112026_mod5_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DisplayNonDup reads every word from "collection_of_words.txt", removes
 * duplicate words (ignoring case), and displays the unique words first in
 * ascending alphabetical order and then in descending alphabetical order.
 * A self-test runs first on a small in-memory word list with a known
 * duplicate pattern to confirm the duplicate-removal and sorting logic work
 * correctly before the real file is processed.
 */
public class DisplayNonDup {

    private static final String FILE_NAME = "collection_of_words.txt";

    public static void main(String[] args) {
        runSelfTest();
        runProgram();
    }

    /**
     * Reads the word file, removes duplicate words, and prints the unique
     * words in both ascending and descending alphabetical order.
     */
    private static void runProgram() {
        List<String> words = readWords(FILE_NAME);
        if (words.isEmpty()) {
            System.out.println("No words were read from " + FILE_NAME + ".");
            return;
        }

        List<String> uniqueWords = removeDuplicates(words);

        System.out.println("Words read from " + FILE_NAME + ": " + words.size());
        System.out.println("Unique (non-duplicate) words: " + uniqueWords.size());

        List<String> ascending = new ArrayList<>(uniqueWords);
        Collections.sort(ascending);
        System.out.println("\nUnique words in ascending order:");
        System.out.println(ascending);

        List<String> descending = new ArrayList<>(uniqueWords);
        descending.sort(Collections.reverseOrder());
        System.out.println("\nUnique words in descending order:");
        System.out.println(descending);
    }

    /**
     * Reads every word from the given text file, stripping punctuation and
     * normalizing each word to lowercase so that duplicates are detected
     * regardless of case or surrounding punctuation.
     *
     * @param fileName path of the text file to read
     * @return list of words in the order they appear in the file, possibly
     *         containing duplicates
     */
    private static List<String> readWords(String fileName) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String token : line.split("\\s+")) {
                    // Strip punctuation so "word." and "word" are treated as the same word
                    String word = token.replaceAll("[^a-zA-Z']", "").toLowerCase();
                    if (!word.isEmpty()) {
                        words.add(word);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + fileName + ": " + e.getMessage());
        }
        return words;
    }

    /**
     * Returns a new list containing every distinct value from the given
     * list, preserving the order in which each value first appeared.
     *
     * @param words source list that may contain duplicate words
     * @return new list with duplicate words removed
     */
    private static List<String> removeDuplicates(List<String> words) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            // Only add the word if it hasn't already been copied into the result
            if (!result.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }

    /**
     * Verifies duplicate removal and both sort orders against a small
     * in-memory word list with a known, hand-checked expected result.
     */
    private static void runSelfTest() {
        System.out.println("Running self-test on a small sample word list...");

        List<String> sample = List.of("banana", "apple", "cherry", "apple", "Banana", "date", "cherry");
        List<String> normalized = new ArrayList<>();
        for (String word : sample) {
            normalized.add(word.toLowerCase());
        }

        List<String> uniqueWords = removeDuplicates(normalized);

        List<String> ascending = new ArrayList<>(uniqueWords);
        Collections.sort(ascending);

        List<String> descending = new ArrayList<>(uniqueWords);
        descending.sort(Collections.reverseOrder());

        List<String> expectedAscending = List.of("apple", "banana", "cherry", "date");
        List<String> expectedDescending = List.of("date", "cherry", "banana", "apple");

        if (ascending.equals(expectedAscending) && descending.equals(expectedDescending)) {
            System.out.println("Self-test PASSED (unique words: " + uniqueWords.size() + ")\n");
        } else {
            System.out.println("Self-test FAILED:");
            System.out.println("  ascending:  " + ascending);
            System.out.println("  expected:   " + expectedAscending);
            System.out.println("  descending: " + descending);
            System.out.println("  expected:   " + expectedDescending + "\n");
        }
    }
}
