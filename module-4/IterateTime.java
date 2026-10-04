// cschumacher_10042026_mod4_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * IterateTime compares how long it takes to traverse a LinkedList using an
 * Iterator versus using the get(index) method, for lists of 50,000 and
 * 500,000 random integers. A self-test runs first on a small list with a
 * known sum to confirm that both traversal methods visit every element
 * correctly before the timed runs are performed.
 */
public class IterateTime {

    private static final int SMALL_SIZE = 50_000;
    private static final int LARGE_SIZE = 500_000;
    private static final int SELF_TEST_SIZE = 1_000;
    private static final int MAX_RANDOM_VALUE = 1_000_000;

    public static void main(String[] args) {
        runSelfTest();
        runTimingTest(SMALL_SIZE);
        runTimingTest(LARGE_SIZE);
    }

    /**
     * Builds a LinkedList of the given size filled with random integers, then
     * times and prints how long an Iterator traversal and a get(index)
     * traversal each take to sum every element.
     *
     * @param size number of random integers to store in the list
     */
    private static void runTimingTest(int size) {
        System.out.println("\n=== Testing with " + size + " integers ===");
        LinkedList<Integer> list = buildRandomList(size);

        long iteratorNanos = timeIteratorTraversal(list);
        System.out.println("Iterator traversal time:   " + iteratorNanos / 1_000_000.0 + " ms");

        long getIndexNanos = timeGetIndexTraversal(list);
        System.out.println("get(index) traversal time: " + getIndexNanos / 1_000_000.0 + " ms");
    }

    /**
     * Builds a LinkedList containing the given number of random integers.
     *
     * @param size number of random integers to generate
     * @return LinkedList populated with random integers
     */
    private static LinkedList<Integer> buildRandomList(int size) {
        Random random = new Random();
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt(MAX_RANDOM_VALUE));
        }
        return list;
    }

    /**
     * Traverses the entire list once using an Iterator, summing every value.
     * The sum is printed so the JVM cannot optimize the loop away, and it
     * doubles as a sanity check that every element was actually visited.
     *
     * @param list list to traverse
     * @return elapsed time in nanoseconds
     */
    private static long timeIteratorTraversal(LinkedList<Integer> list) {
        long start = System.nanoTime();
        long sum = 0;
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            sum += it.next();
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("  (iterator sum check: " + sum + ")");
        return elapsed;
    }

    /**
     * Traverses the entire list once using get(index) in a counted loop,
     * summing every value. For a LinkedList this is O(n) per call, making the
     * full traversal O(n^2), unlike the O(n) Iterator traversal above.
     *
     * @param list list to traverse
     * @return elapsed time in nanoseconds
     */
    private static long timeGetIndexTraversal(LinkedList<Integer> list) {
        long start = System.nanoTime();
        long sum = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            sum += list.get(i);
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("  (get(index) sum check: " + sum + ")");
        return elapsed;
    }

    /**
     * Verifies both traversal methods visit every element correctly by
     * summing a small list of known values (0..999) and comparing against
     * the mathematically expected sum.
     */
    private static void runSelfTest() {
        System.out.println("Running self-test on a " + SELF_TEST_SIZE + "-element list...");

        LinkedList<Integer> testList = new LinkedList<>();
        for (int i = 0; i < SELF_TEST_SIZE; i++) {
            testList.add(i);
        }

        long iteratorSum = 0;
        for (int value : testList) {
            iteratorSum += value;
        }

        long getIndexSum = 0;
        for (int i = 0; i < testList.size(); i++) {
            getIndexSum += testList.get(i);
        }

        long expectedSum = (long) SELF_TEST_SIZE * (SELF_TEST_SIZE - 1) / 2;

        if (iteratorSum == expectedSum && getIndexSum == expectedSum) {
            System.out.println("Self-test PASSED (expected sum = " + expectedSum + ")\n");
        } else {
            System.out.println("Self-test FAILED: iteratorSum=" + iteratorSum
                    + ", getIndexSum=" + getIndexSum + ", expected=" + expectedSum + "\n");
        }
    }

    /*
     * ------------------------------------------------------------------
     * Results and analysis (measured sample run on this machine)
     * ------------------------------------------------------------------
     *   Size       | Iterator time | get(index) time   | get(index) vs Iterator
     *   50,000     |     1.70 ms   |     1,630.31 ms    |  ~958x slower
     *   500,000    |     5.07 ms   |   162,778.86 ms    |  ~32,100x slower
     *
     * Iterator traversal time grows roughly LINEARLY with list size: going
     * from 50,000 to 500,000 elements (10x the data) only increased the
     * iterator's time by about 3x. This matches LinkedList's internal
     * structure, where an Iterator follows each node's "next" reference
     * exactly once, giving O(n) total work no matter how large the list is.
     *
     * get(index) traversal time grows roughly QUADRATICALLY with list size:
     * the same 10x increase in data made the get(index) loop about 100x
     * slower (1.63 seconds -> ~163 seconds, nearly 3 minutes). This matches
     * the O(n^2) cost of calling get(index) n times on a LinkedList, since
     * LinkedList has no internal index/array - every get(index) call must
     * walk node-by-node from whichever end (head or tail) is closer to the
     * requested index. Summing all n elements one index at a time therefore
     * costs roughly n * (n / 4) node hops in total, not n.
     *
     * Takeaway: a LinkedList's strength is fast insertion/removal once you
     * already hold a position (e.g., via an Iterator or ListIterator), not
     * random-access reads. Always traverse a LinkedList with an Iterator or
     * an enhanced for-loop instead of get(index) in a counted loop -
     * the gap between the two approaches only gets worse as the list grows.
     * ------------------------------------------------------------------
     */
}
