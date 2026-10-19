// cschumacher_10182026_mod6_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.util.Arrays;
import java.util.Comparator;

/**
 * MyBubbleSort provides two generic bubble sort methods: one that sorts an
 * array using the natural ordering of its elements via the Comparable
 * interface, and one that sorts an array using a caller-supplied Comparator.
 * Both methods sort the given array in place with the classic bubble sort
 * algorithm - repeatedly stepping through the array and swapping adjacent
 * out-of-order elements until a full pass makes no swaps.
 *
 * A self-test runs first, verifying both sort methods against known results
 * (checked against java.util.Arrays.sort) for an Integer array, a String
 * array, and an array of a custom Person class sorted both by its natural
 * ordering (name) and by a Comparator (age), including empty and
 * single-element edge cases, before the demonstration output is printed.
 */
public class MyBubbleSort {

    public static void main(String[] args) {
        runSelfTest();
        runDemo();
    }

    /**
     * Sorts the given array into ascending order according to the natural
     * ordering of its elements, using the Comparable interface.
     *
     * @param list array to sort in place
     * @param <E>  element type; must implement Comparable&lt;E&gt;
     */
    public static <E extends Comparable<E>> void bubbleSort(E[] list) {
        boolean swapped = true;
        int lastUnsorted = list.length - 1;

        while (swapped) {
            swapped = false;
            for (int i = 0; i < lastUnsorted; i++) {
                if (list[i].compareTo(list[i + 1]) > 0) {
                    swap(list, i, i + 1);
                    swapped = true;
                }
            }
            // After each full pass, the largest remaining element has bubbled
            // into place at the end, so it no longer needs to be compared.
            lastUnsorted--;
        }
    }

    /**
     * Sorts the given array into the order defined by the given Comparator,
     * using the Comparator interface instead of the elements' natural
     * ordering.
     *
     * @param list       array to sort in place
     * @param comparator defines the ordering used to compare elements
     * @param <E>        element type
     */
    public static <E> void bubbleSort(E[] list, Comparator<? super E> comparator) {
        boolean swapped = true;
        int lastUnsorted = list.length - 1;

        while (swapped) {
            swapped = false;
            for (int i = 0; i < lastUnsorted; i++) {
                if (comparator.compare(list[i], list[i + 1]) > 0) {
                    swap(list, i, i + 1);
                    swapped = true;
                }
            }
            lastUnsorted--;
        }
    }

    /**
     * Swaps the elements at the two given indices of the array.
     *
     * @param list array containing the elements to swap
     * @param i    index of the first element
     * @param j    index of the second element
     * @param <E>  element type
     */
    private static <E> void swap(E[] list, int i, int j) {
        E temp = list[i];
        list[i] = list[j];
        list[j] = temp;
    }

    /**
     * Demonstrates both bubble sort methods: sorting Integer and String
     * arrays by natural ordering (Comparable), and sorting an array of
     * Person objects both by natural ordering (name) and by a Comparator
     * (age).
     */
    private static void runDemo() {
        Integer[] numbers = {38, 5, 91, 12, 47, 0, -6, 23};
        System.out.println("Unsorted integers: " + Arrays.toString(numbers));
        bubbleSort(numbers);
        System.out.println("Sorted integers (Comparable):   " + Arrays.toString(numbers));

        String[] words = {"banana", "apple", "cherry", "date", "fig"};
        System.out.println("\nUnsorted words: " + Arrays.toString(words));
        bubbleSort(words);
        System.out.println("Sorted words (Comparable):       " + Arrays.toString(words));

        Integer[] numbersDesc = {38, 5, 91, 12, 47, 0, -6, 23};
        bubbleSort(numbersDesc, Comparator.reverseOrder());
        System.out.println("\nSame integers sorted descending (Comparator): " + Arrays.toString(numbersDesc));

        Person[] people = {
                new Person("Carol", 41),
                new Person("Alice", 29),
                new Person("Bob", 35),
                new Person("Dave", 29)
        };
        System.out.println("\nUnsorted people: " + Arrays.toString(people));

        Person[] byName = people.clone();
        bubbleSort(byName);
        System.out.println("People sorted by name (Comparable): " + Arrays.toString(byName));

        Person[] byAge = people.clone();
        bubbleSort(byAge, Comparator.comparingInt(Person::getAge));
        System.out.println("People sorted by age (Comparator):  " + Arrays.toString(byAge));
    }

    /**
     * Runs every self-test case and prints an overall PASS/FAIL summary.
     * Each case compares this class's bubbleSort output against
     * java.util.Arrays.sort (or Arrays.sort with the same Comparator) run
     * on an identical copy of the input, which serves as the known-correct
     * expected result.
     */
    private static void runSelfTest() {
        System.out.println("Running self-test...");
        boolean allPassed = true;

        allPassed &= testComparableIntegerSort();
        allPassed &= testComparableStringSort();
        allPassed &= testComparatorDescendingSort();
        allPassed &= testComparablePersonSort();
        allPassed &= testComparatorPersonSort();
        allPassed &= testEmptyAndSingleElementArrays();

        System.out.println(allPassed ? "Self-test PASSED\n" : "Self-test FAILED\n");
    }

    /**
     * Verifies the Comparable bubbleSort overload on an Integer array,
     * including negative values, against Arrays.sort.
     *
     * @return true if the sorted result matched the expected result
     */
    private static boolean testComparableIntegerSort() {
        Integer[] actual = {5, -3, 42, 0, 17, -8, 9};
        Integer[] expected = actual.clone();
        Arrays.sort(expected);

        bubbleSort(actual);

        boolean passed = Arrays.equals(actual, expected);
        reportTest("Comparable Integer sort", passed, expected, actual);
        return passed;
    }

    /**
     * Verifies the Comparable bubbleSort overload on a String array against
     * Arrays.sort.
     *
     * @return true if the sorted result matched the expected result
     */
    private static boolean testComparableStringSort() {
        String[] actual = {"pear", "apple", "mango", "kiwi", "fig"};
        String[] expected = actual.clone();
        Arrays.sort(expected);

        bubbleSort(actual);

        boolean passed = Arrays.equals(actual, expected);
        reportTest("Comparable String sort", passed, expected, actual);
        return passed;
    }

    /**
     * Verifies the Comparator bubbleSort overload sorts an Integer array
     * into descending order, against Arrays.sort with the same Comparator.
     *
     * @return true if the sorted result matched the expected result
     */
    private static boolean testComparatorDescendingSort() {
        Integer[] actual = {5, -3, 42, 0, 17, -8, 9};
        Integer[] expected = actual.clone();
        Arrays.sort(expected, Comparator.reverseOrder());

        bubbleSort(actual, Comparator.reverseOrder());

        boolean passed = Arrays.equals(actual, expected);
        reportTest("Comparator descending Integer sort", passed, expected, actual);
        return passed;
    }

    /**
     * Verifies the Comparable bubbleSort overload on an array of Person
     * objects, using Person's natural ordering (by name), against
     * Arrays.sort.
     *
     * @return true if the sorted result matched the expected result
     */
    private static boolean testComparablePersonSort() {
        Person[] actual = {
                new Person("Carol", 41), new Person("Alice", 29),
                new Person("Bob", 35), new Person("Dave", 29)
        };
        Person[] expected = actual.clone();
        Arrays.sort(expected);

        bubbleSort(actual);

        boolean passed = Arrays.equals(actual, expected);
        reportTest("Comparable Person sort (by name)", passed, expected, actual);
        return passed;
    }

    /**
     * Verifies the Comparator bubbleSort overload on an array of Person
     * objects, sorting by age instead of Person's natural ordering, against
     * Arrays.sort with the same Comparator.
     *
     * @return true if the sorted result matched the expected result
     */
    private static boolean testComparatorPersonSort() {
        Person[] actual = {
                new Person("Carol", 41), new Person("Alice", 29),
                new Person("Bob", 35), new Person("Dave", 29)
        };
        Comparator<Person> byAge = Comparator.comparingInt(Person::getAge);
        Person[] expected = actual.clone();
        Arrays.sort(expected, byAge);

        bubbleSort(actual, byAge);

        boolean passed = Arrays.equals(actual, expected);
        reportTest("Comparator Person sort (by age)", passed, expected, actual);
        return passed;
    }

    /**
     * Verifies both bubbleSort overloads handle degenerate inputs correctly:
     * an empty array (nothing to do) and a single-element array (already
     * sorted).
     *
     * @return true if both edge cases behaved correctly
     */
    private static boolean testEmptyAndSingleElementArrays() {
        Integer[] empty = {};
        bubbleSort(empty);
        boolean emptyPassed = empty.length == 0;

        Integer[] single = {7};
        bubbleSort(single);
        boolean singlePassed = Arrays.equals(single, new Integer[] {7});

        boolean passed = emptyPassed && singlePassed;
        System.out.println("  [" + (passed ? "PASS" : "FAIL") + "] Empty/single-element array edge cases");
        return passed;
    }

    /**
     * Prints a PASS/FAIL line for a self-test case, including the expected
     * and actual arrays when the test fails.
     *
     * @param name     descriptive name of the test case
     * @param passed   whether the test case passed
     * @param expected the known-correct expected array
     * @param actual   the array produced by bubbleSort
     */
    private static void reportTest(String name, boolean passed, Object[] expected, Object[] actual) {
        if (passed) {
            System.out.println("  [PASS] " + name);
        } else {
            System.out.println("  [FAIL] " + name);
            System.out.println("    expected: " + Arrays.toString(expected));
            System.out.println("    actual:   " + Arrays.toString(actual));
        }
    }

    /**
     * Simple immutable value class used to demonstrate sorting objects both
     * by natural ordering (name, via Comparable) and by a Comparator (age).
     */
    private static class Person implements Comparable<Person> {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        int getAge() {
            return age;
        }

        /**
         * Defines the natural ordering of Person objects as ascending order
         * by name.
         */
        @Override
        public int compareTo(Person other) {
            return this.name.compareTo(other.name);
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
}
