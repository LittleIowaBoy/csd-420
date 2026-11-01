// cschumacher_11012026_mod8_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * ThreeThreads launches three concurrent threads that each generate random
 * characters from a distinct character set - lowercase letters, digits, and
 * special symbols - and display them one at a time in a shared JavaFX
 * TextArea as soon as each character is produced. Because the three threads
 * run concurrently and each character is pushed to the TextArea the instant
 * it is generated (via Platform.runLater, since only the JavaFX Application
 * Thread may touch the scene graph), the resulting text interleaves letters,
 * digits, and symbols unpredictably rather than printing each set as its own
 * block, e.g. "179$fq*fg1k1k2%1m4".
 *
 * Each thread produces at least 10,000 characters (CHAR_COUNT).
 *
 * A self-test runs first, verifying the character pools, the random
 * character picker, the producer loop shared by all three threads, and
 * genuine concurrent/thread-safe execution across real threads - all
 * independent of the JavaFX toolkit - before the window is shown and the
 * three display threads are started.
 */
public class ThreeThreads extends Application {

    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] DIGITS = "0123456789".toCharArray();
    private static final char[] SPECIALS = "!@#$%&*".toCharArray();
    private static final int CHAR_COUNT = 10_000;

    @Override
    public void start(Stage primaryStage) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(640, 420);

        Label status = new Label("Generating " + (CHAR_COUNT * 3) + " characters across 3 threads...");

        BorderPane root = new BorderPane(textArea);
        root.setTop(status);
        root.setPadding(new Insets(10));
        BorderPane.setMargin(status, new Insets(0, 0, 10, 0));

        Scene scene = new Scene(root, 660, 480);
        primaryStage.setTitle("ThreeThreads - Module 8 Programming Assignment");
        primaryStage.setScene(scene);
        primaryStage.show();

        startDisplayThreads(textArea, status);
    }

    /**
     * Starts the three producer threads - letters, digits, and special
     * characters - each pushing CHAR_COUNT random characters, one at a
     * time, onto the JavaFX Application Thread for display in textArea. A
     * fourth, lightweight "watcher" thread joins all three producers and
     * then updates the status label once every character has been
     * generated and queued for display.
     */
    private void startDisplayThreads(TextArea textArea, Label status) {
        Consumer<Character> displaySink = c -> Platform.runLater(() -> textArea.appendText(String.valueOf(c)));

        Thread letterThread = new Thread(
                () -> runProducer(LETTERS, CHAR_COUNT, new Random(), displaySink), "LetterThread");
        Thread digitThread = new Thread(
                () -> runProducer(DIGITS, CHAR_COUNT, new Random(), displaySink), "DigitThread");
        Thread specialThread = new Thread(
                () -> runProducer(SPECIALS, CHAR_COUNT, new Random(), displaySink), "SpecialCharThread");

        for (Thread t : List.of(letterThread, digitThread, specialThread)) {
            t.setDaemon(true);
            t.start();
        }

        Thread watcher = new Thread(() -> {
            awaitAll(letterThread, digitThread, specialThread);
            Platform.runLater(() -> status.setText(
                    "Done: generated " + (CHAR_COUNT * 3) + " characters (" + CHAR_COUNT + " each) across 3 threads."));
        }, "WatcherThread");
        watcher.setDaemon(true);
        watcher.start();
    }

    /**
     * Repeatedly picks a random character from pool and hands it to sink,
     * one character at a time, count times. This loop is shared by all
     * three display threads (only the pool differs), and by the self-test,
     * which substitutes a non-GUI sink to verify the loop's behavior
     * without needing the JavaFX toolkit running.
     *
     * @param pool   the characters this thread may choose among
     * @param count  how many characters to generate
     * @param random source of randomness (thread-confined; each thread uses its own instance)
     * @param sink   receives each character as soon as it is generated
     */
    private static void runProducer(char[] pool, int count, Random random, Consumer<Character> sink) {
        for (int i = 0; i < count; i++) {
            sink.accept(pickRandomChar(pool, random));
        }
    }

    /**
     * Returns one randomly selected character from pool.
     *
     * @param pool   non-empty array of candidate characters
     * @param random source of randomness
     * @return a character drawn uniformly at random from pool
     */
    private static char pickRandomChar(char[] pool, Random random) {
        return pool[random.nextInt(pool.length)];
    }

    /**
     * Blocks until every thread in threads has finished, restoring the
     * interrupt status if interrupted while waiting.
     */
    private static void awaitAll(Thread... threads) {
        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------------------------------------------------------------------
    // Self-test
    // ---------------------------------------------------------------------

    /**
     * Verifies, independent of the JavaFX toolkit:
     *  1. each character pool has the expected size/content and CHAR_COUNT
     *     meets the required 10,000-per-thread minimum;
     *  2. pickRandomChar always returns a character that belongs to the
     *     given pool, across many trials, for all three pools;
     *  3. runProducer (the loop shared by all three display threads)
     *     generates exactly count characters, every one drawn from pool;
     *  4. running the three producers concurrently on real threads, each
     *     writing through a thread-safe sink, is race-free: the combined
     *     output has exactly 3 * count characters, and each thread's own
     *     per-source counter (AtomicInteger) ends at exactly count, with no
     *     characters lost or double-counted.
     */
    private static void runSelfTest() {
        System.out.println("Running self-test on ThreeThreads character generation...");
        boolean allPassed = true;

        allPassed &= check("LETTERS has 26 lowercase letters",
                LETTERS.length == 26 && new String(LETTERS).equals("abcdefghijklmnopqrstuvwxyz"));
        allPassed &= check("DIGITS has 10 digits",
                DIGITS.length == 10 && new String(DIGITS).equals("0123456789"));
        allPassed &= check("SPECIALS has 7 special characters",
                SPECIALS.length == 7 && new String(SPECIALS).equals("!@#$%&*"));
        allPassed &= check("CHAR_COUNT meets the 10,000-per-thread minimum", CHAR_COUNT >= 10_000);

        Random random = new Random();
        for (char[] pool : new char[][] { LETTERS, DIGITS, SPECIALS }) {
            boolean inPool = true;
            for (int i = 0; i < 2_000; i++) {
                if (indexOf(pool, pickRandomChar(pool, random)) < 0) {
                    inPool = false;
                    break;
                }
            }
            allPassed &= check("pickRandomChar(\"" + new String(pool) + "\", ...) always stays in its pool", inPool);
        }

        for (char[] pool : new char[][] { LETTERS, DIGITS, SPECIALS }) {
            List<Character> collected = new ArrayList<>();
            runProducer(pool, 500, new Random(), collected::add);
            boolean countOk = collected.size() == 500;
            boolean contentOk = collected.stream().allMatch(c -> indexOf(pool, c) >= 0);
            allPassed &= check("runProducer(\"" + new String(pool) + "\", 500, ...) yields exactly 500 in-pool characters",
                    countOk && contentOk);
        }

        allPassed &= check("three concurrent producer threads are race-free", runConcurrencySelfTest());

        if (allPassed) {
            System.out.println("Self-test PASSED (all checks succeeded)\n");
        } else {
            System.out.println("Self-test FAILED: see FAIL lines above\n");
        }
    }

    /**
     * Starts three real threads - one per character pool - each running
     * runProducer with a sink that increments a per-thread AtomicInteger
     * and appends to a shared thread-safe list. Joins all three threads and
     * verifies no characters were lost or miscounted, which would indicate
     * a race condition in the shared sink/list.
     *
     * @return true if the combined and per-thread counts are exactly correct
     */
    private static boolean runConcurrencySelfTest() {
        final int count = 500;
        List<Character> shared = new CopyOnWriteArrayList<>();
        AtomicInteger letters = new AtomicInteger();
        AtomicInteger digits = new AtomicInteger();
        AtomicInteger specials = new AtomicInteger();

        Thread t1 = new Thread(() -> runProducer(LETTERS, count, new Random(),
                c -> { shared.add(c); letters.incrementAndGet(); }));
        Thread t2 = new Thread(() -> runProducer(DIGITS, count, new Random(),
                c -> { shared.add(c); digits.incrementAndGet(); }));
        Thread t3 = new Thread(() -> runProducer(SPECIALS, count, new Random(),
                c -> { shared.add(c); specials.incrementAndGet(); }));

        t1.start();
        t2.start();
        t3.start();
        awaitAll(t1, t2, t3);

        return shared.size() == count * 3
                && letters.get() == count
                && digits.get() == count
                && specials.get() == count;
    }

    private static int indexOf(char[] pool, char c) {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static boolean check(String description, boolean condition) {
        System.out.println("  [" + (condition ? "PASS" : "FAIL") + "] " + description);
        return condition;
    }

    /**
     * Entry point for the JavaFX application. Runs the self-test first,
     * then launches the GUI, which starts the three display threads.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        runSelfTest();
        launch(args);
    }
}

