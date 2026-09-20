// cschumacher_09192026_mod2_2_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * ReadRandomData opens "cschumacher datafile.dat" and displays every line of
 * random integer and double data that WriteRandomData has appended to it.
 */
public class ReadRandomData {

    private static final String FILE_NAME = "cschumacher datafile.dat";

    public static void main(String[] args) {
        readDataFromFile();
    }

    /**
     * Reads the data file line by line and prints its contents to the console.
     * If the file cannot be found, a message is displayed instead of an error.
     */
    private static void readDataFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            int entryNumber = 1;
            System.out.println("Contents of " + FILE_NAME + ":");

            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                // Print a blank line after every Integers/Doubles pair for readability
                if (line.startsWith("Doubles:")) {
                    System.out.println("--- Entry " + entryNumber + " end ---");
                    entryNumber++;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }
}
