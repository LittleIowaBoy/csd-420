// cschumacher_09192026_mod2_2_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * WriteRandomData generates an array of five random integers and an array of
 * five random double values, then writes both arrays to "cschumacher datafile.dat".
 * If the file does not already exist it is created; if it does exist, the new
 * data is appended to the end of the file rather than overwriting it.
 */
public class WriteRandomData {

    private static final int ARRAY_SIZE = 5;
    private static final String FILE_NAME = "cschumacher datafile.dat";

    public static void main(String[] args) {
        int[] randomInts = generateRandomInts();
        double[] randomDoubles = generateRandomDoubles();

        writeDataToFile(randomInts, randomDoubles);
    }

    /**
     * Builds an array of five random integers in the range 0-99.
     *
     * @return array of five random ints
     */
    private static int[] generateRandomInts() {
        Random random = new Random();
        int[] values = new int[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            values[i] = random.nextInt(100);
        }
        return values;
    }

    /**
     * Builds an array of five random double values in the range 0.0-99.99.
     *
     * @return array of five random doubles
     */
    private static double[] generateRandomDoubles() {
        Random random = new Random();
        double[] values = new double[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            // Round to 2 decimal places for readability
            values[i] = Math.round(random.nextDouble() * 10000) / 100.0;
        }
        return values;
    }

    /**
     * Appends the given integer and double arrays to the data file, creating
     * the file first if it does not already exist.
     *
     * @param ints    array of random integers to write
     * @param doubles array of random doubles to write
     */
    private static void writeDataToFile(int[] ints, double[] doubles) {
        // The boolean "true" argument tells FileWriter to append rather than overwrite
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            StringBuilder intLine = new StringBuilder("Integers:");
            for (int value : ints) {
                intLine.append(" ").append(value);
            }

            StringBuilder doubleLine = new StringBuilder("Doubles:");
            for (double value : doubles) {
                doubleLine.append(" ").append(value);
            }

            writer.write(intLine.toString());
            writer.write(System.lineSeparator());
            writer.write(doubleLine.toString());
            writer.write(System.lineSeparator());

            System.out.println("Data written to " + FILE_NAME);
            System.out.println(intLine);
            System.out.println(doubleLine);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
