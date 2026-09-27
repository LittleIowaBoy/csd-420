// cschumacher_09272026_mod3_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.util.ArrayList;
import java.util.Random;

/**
 * MakeRandomArray fills an ArrayList with 50 random integers from 1 to 20
 * and uses a generic static method to build a duplicate-free copy of it.
 */
public class MakeRandomArray {

    private static final int LIST_SIZE = 50;
    private static final int MAX_VALUE = 20;

    public static void main(String[] args) {
        ArrayList<Integer> original = generateRandomList();
        ArrayList<Integer> unique = removeDuplicates(original);

        System.out.println("Original list (" + original.size() + " values):");
        System.out.println(original);

        System.out.println("\nList with duplicates removed (" + unique.size() + " values):");
        System.out.println(unique);
    }

    /**
     * Builds an ArrayList of 50 random integers, each between 1 and 20 inclusive.
     *
     * @return ArrayList containing 50 random values, possibly with duplicates
     */
    private static ArrayList<Integer> generateRandomList() {
        Random random = new Random();
        ArrayList<Integer> list = new ArrayList<>(LIST_SIZE);
        for (int i = 0; i < LIST_SIZE; i++) {
            list.add(random.nextInt(MAX_VALUE) + 1);
        }
        return list;
    }

    /**
     * Returns a new ArrayList containing every distinct value from the given
     * list, preserving the order in which each value first appeared.
     *
     * @param list source list that may contain duplicate values
     * @param <E>  the type of elements held in the list
     * @return new ArrayList with duplicate values removed
     */
    public static <E> ArrayList<E> removeDuplicates(ArrayList<E> list) {
        ArrayList<E> result = new ArrayList<>();
        for (E value : list) {
            // Only add the value if it hasn't already been copied into the result
            if (!result.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }
}
