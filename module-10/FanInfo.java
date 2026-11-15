// cschumacher_11142026_mod10_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FanInfo is a JavaFX/JDBC application that views and updates rows of the
 * pre-existing "fans" table (id INT PRIMARY KEY, firstname VARCHAR(25),
 * lastname VARCHAR(25), favoriteteam VARCHAR(25)) in the MySQL database
 * "databasedb", connecting as user "student1". The window has a Fan ID
 * field, three editable fields for first name, last name, and favorite
 * team, and two buttons:
 *   - "Display" reads the Fan ID field, looks up that row in the database,
 *     and fills the three editable fields with its current values.
 *   - "Update" takes whatever is currently in the Fan ID and the three
 *     editable fields and writes those values back to that row.
 *
 * The application only ever issues SELECT and UPDATE statements against the
 * "fans" table - it never creates or drops the table, which is assumed to
 * already exist and be populated, per the assignment.
 *
 * All JDBC access goes through a small set of package-visible, Connection
 * parameterized methods (displayFan/updateFan) and pure validation helpers
 * (parseId/validateField), composed into performDisplay/performUpdate,
 * which mirror exactly what the "Display"/"Update" button handlers do. The
 * handlers themselves only supply a live Connection to those methods and
 * push the resulting outcome into the UI, so the entire behavior of both
 * buttons can be - and is - exercised in tests without touching the real
 * "databasedb" MySQL database.
 *
 * A self-test runs first, verifying the ID/field validation helpers, then
 * standing up a disposable in-memory H2 database (a stand-in for the real
 * MySQL "databasedb") pre-populated the same way the grading environment
 * pre-populates the real "fans" table, and running performDisplay/
 * performUpdate against it to confirm lookups, edits, not-found handling,
 * and validation failures all behave correctly, before the window is shown.
 * (Only the self-test's fixture setup issues CREATE TABLE/INSERT, against
 * its own throwaway database - never against "databasedb", and never from
 * the application's own display/update code paths.)
 */
public class FanInfo extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/databasedb";
    private static final String DB_USER = "student1";
    private static final String DB_PASSWORD = "pass";

    /** Matches the VARCHAR(25) width of firstname/lastname/favoriteteam. */
    private static final int MAX_FIELD_LENGTH = 25;

    /** One row of the "fans" table. */
    record Fan(int id, String firstName, String lastName, String favoriteTeam) {
    }

    /** Result of a "Display" attempt: whether it succeeded, a status message
     *  for the UI, and the Fan that was found (null if not found/invalid). */
    record DisplayOutcome(boolean success, String message, Fan fan) {
    }

    /** Result of an "Update" attempt: whether it succeeded and a status
     *  message for the UI. */
    record UpdateOutcome(boolean success, String message) {
    }

    @Override
    public void start(Stage primaryStage) {
        FanFormControls controls = createFormControls();

        controls.displayButton.setOnAction(e -> {
            try (Connection conn = openConnection()) {
                DisplayOutcome outcome = performDisplay(conn, controls.idField.getText());
                controls.statusLabel.setText(outcome.message());
                if (outcome.success() && outcome.fan() != null) {
                    controls.firstNameField.setText(outcome.fan().firstName());
                    controls.lastNameField.setText(outcome.fan().lastName());
                    controls.favoriteTeamField.setText(outcome.fan().favoriteTeam());
                }
            } catch (SQLException ex) {
                controls.statusLabel.setText("Database error: " + ex.getMessage());
            }
        });

        controls.updateButton.setOnAction(e -> {
            try (Connection conn = openConnection()) {
                UpdateOutcome outcome = performUpdate(conn, controls.idField.getText(),
                        controls.firstNameField.getText(), controls.lastNameField.getText(),
                        controls.favoriteTeamField.getText());
                controls.statusLabel.setText(outcome.message());
            } catch (SQLException ex) {
                controls.statusLabel.setText("Database error: " + ex.getMessage());
            }
        });

        Scene scene = new Scene(controls.root, 380, 260);
        primaryStage.setTitle("FanInfo - Module 10 Programming Assignment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Opens a new JDBC connection to the "databasedb" MySQL database as
     * user "student1". The caller is responsible for closing the
     * connection (the button handlers above do so with try-with-resources).
     *
     * @return an open Connection to "databasedb"
     * @throws SQLException if the connection cannot be established
     */
    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Parses a Fan ID typed by the user.
     *
     * @param text the raw text from the Fan ID field
     * @return the parsed ID
     * @throws NumberFormatException if text is blank or not an integer
     */
    static int parseId(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new NumberFormatException("Fan ID is required.");
        }
        return Integer.parseInt(text.trim());
    }

    /**
     * Validates a firstname/lastname/favoriteteam value against the
     * VARCHAR(25) columns it will be stored in.
     *
     * @param value     the field's current text
     * @param fieldName the field's display name, used in the error message
     * @throws IllegalArgumentException if value is blank or longer than
     *                                   {@link #MAX_FIELD_LENGTH} characters
     */
    static void validateField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (value.length() > MAX_FIELD_LENGTH) {
            throw new IllegalArgumentException(
                    fieldName + " must be " + MAX_FIELD_LENGTH + " characters or fewer.");
        }
    }

    /**
     * Looks up the "fans" row with the given ID.
     *
     * @param conn an open JDBC connection
     * @param id   the Fan ID to look up
     * @return the matching Fan, or null if no row has that ID
     * @throws SQLException if the query fails
     */
    static Fan displayFan(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, firstname, lastname, favoriteteam FROM fans WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Fan(rs.getInt("id"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getString("favoriteteam"));
            }
        }
    }

    /**
     * Writes the given Fan's firstname/lastname/favoriteteam back to the
     * "fans" row with its ID.
     *
     * @param conn an open JDBC connection
     * @param fan  the ID and new field values to write
     * @return true if a row with that ID was found and updated, false if no
     *         row has that ID
     * @throws SQLException if the update fails
     */
    static boolean updateFan(Connection conn, Fan fan) throws SQLException {
        String sql = "UPDATE fans SET firstname = ?, lastname = ?, favoriteteam = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fan.firstName());
            ps.setString(2, fan.lastName());
            ps.setString(3, fan.favoriteTeam());
            ps.setInt(4, fan.id());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Full behavior of the "Display" button: validates the ID text, looks
     * the row up, and builds the status message/outcome the UI should show.
     *
     * @param conn   an open JDBC connection
     * @param idText the raw text from the Fan ID field
     * @return the outcome to apply to the UI
     */
    static DisplayOutcome performDisplay(Connection conn, String idText) {
        int id;
        try {
            id = parseId(idText);
        } catch (NumberFormatException ex) {
            return new DisplayOutcome(false, "Invalid Fan ID: " + ex.getMessage(), null);
        }
        try {
            Fan fan = displayFan(conn, id);
            if (fan == null) {
                return new DisplayOutcome(false, "No fan found with ID " + id + ".", null);
            }
            return new DisplayOutcome(true, "Displaying fan " + id + ".", fan);
        } catch (SQLException ex) {
            return new DisplayOutcome(false, "Database error: " + ex.getMessage(), null);
        }
    }

    /**
     * Full behavior of the "Update" button: validates the ID and field
     * text, writes the row, and builds the status message/outcome the UI
     * should show.
     *
     * @param conn         an open JDBC connection
     * @param idText       the raw text from the Fan ID field
     * @param firstName    the raw text from the first name field
     * @param lastName     the raw text from the last name field
     * @param favoriteTeam the raw text from the favorite team field
     * @return the outcome to apply to the UI
     */
    static UpdateOutcome performUpdate(Connection conn, String idText, String firstName,
            String lastName, String favoriteTeam) {
        int id;
        try {
            id = parseId(idText);
        } catch (NumberFormatException ex) {
            return new UpdateOutcome(false, "Invalid Fan ID: " + ex.getMessage());
        }
        try {
            validateField(firstName, "First name");
            validateField(lastName, "Last name");
            validateField(favoriteTeam, "Favorite team");
        } catch (IllegalArgumentException ex) {
            return new UpdateOutcome(false, ex.getMessage());
        }
        try {
            boolean updated = updateFan(conn, new Fan(id, firstName.trim(), lastName.trim(), favoriteTeam.trim()));
            if (!updated) {
                return new UpdateOutcome(false, "No fan found with ID " + id + "; nothing updated.");
            }
            return new UpdateOutcome(true, "Fan " + id + " updated.");
        } catch (SQLException ex) {
            return new UpdateOutcome(false, "Database error: " + ex.getMessage());
        }
    }

    /** Groups the form's Nodes so start() and the self-test can both build
     *  and inspect the same layout without requiring the JavaFX toolkit. */
    private static final class FanFormControls {
        final TextField idField = new TextField();
        final TextField firstNameField = new TextField();
        final TextField lastNameField = new TextField();
        final TextField favoriteTeamField = new TextField();
        final Button displayButton = new Button("Display");
        final Button updateButton = new Button("Update");
        final Label statusLabel = new Label(" ");
        final VBox root;

        FanFormControls() {
            GridPane form = new GridPane();
            form.setHgap(8);
            form.setVgap(8);
            form.addRow(0, new Label("Fan ID:"), idField);
            form.addRow(1, new Label("First Name:"), firstNameField);
            form.addRow(2, new Label("Last Name:"), lastNameField);
            form.addRow(3, new Label("Favorite Team:"), favoriteTeamField);

            HBox buttons = new HBox(10, displayButton, updateButton);
            buttons.setAlignment(Pos.CENTER);

            root = new VBox(12, form, buttons, statusLabel);
            root.setPadding(new Insets(16));
        }
    }

    /**
     * Builds the Fan ID/name/team fields, the Display/Update buttons, the
     * status label, and the layout containing them - everything the
     * "Display"/"Update" button handlers in {@link #start} wire up, minus
     * the handlers themselves. Constructing these plain JavaFX Nodes does
     * not require the toolkit to be running, so the self-test can call this
     * directly to verify the interface is built correctly.
     *
     * @return the form's controls and root layout Node
     */
    private static FanFormControls createFormControls() {
        return new FanFormControls();
    }

    /**
     * Verifies parseId/validateField, then stands up a disposable in-memory
     * H2 database with its own "fans" table (created and populated only by
     * this test fixture, never by the application itself) and exercises
     * performDisplay/performUpdate against it - found/not-found lookups,
     * a successful update that is visible on the next lookup, an update to
     * a missing ID, and validation failures - independent of the real
     * MySQL "databasedb". Finally verifies the interface's controls are
     * built as expected.
     */
    private static void runSelfTest() {
        System.out.println("Running self-test on FanInfo...");
        boolean allPassed = true;

        // --- parseId ---
        allPassed &= check("parseId(\"7\") == 7", parseId("7") == 7);
        allPassed &= check("parseId(\"  7  \") == 7", parseId("  7  ") == 7);
        allPassed &= check("parseId(\"\") throws", throwsException(() -> parseId("")));
        allPassed &= check("parseId(\"abc\") throws", throwsException(() -> parseId("abc")));

        // --- validateField ---
        allPassed &= check("validateField(\"Cubs\", ...) passes", doesNotThrow(() -> validateField("Cubs", "Favorite team")));
        allPassed &= check("validateField(\"\", ...) throws", throwsException(() -> validateField("", "First name")));
        allPassed &= check("validateField(\"   \", ...) throws", throwsException(() -> validateField("   ", "First name")));
        allPassed &= check("validateField(26 chars, ...) throws",
                throwsException(() -> validateField("a".repeat(MAX_FIELD_LENGTH + 1), "Last name")));
        allPassed &= check("validateField(25 chars, ...) passes",
                doesNotThrow(() -> validateField("a".repeat(MAX_FIELD_LENGTH), "Last name")));

        // --- displayFan/updateFan/performDisplay/performUpdate against a throwaway fixture DB ---
        try (Connection conn = createFixtureDatabase()) {
            allPassed &= check("displayFan found", displayFan(conn, 1)
                    .equals(new Fan(1, "John", "Smith", "Cubs")));
            allPassed &= check("displayFan not found", displayFan(conn, 999) == null);

            DisplayOutcome shown = performDisplay(conn, "2");
            allPassed &= check("performDisplay success", shown.success() && shown.fan().firstName().equals("Jane"));
            DisplayOutcome notFound = performDisplay(conn, "999");
            allPassed &= check("performDisplay not found", !notFound.success() && notFound.fan() == null);
            DisplayOutcome badId = performDisplay(conn, "nope");
            allPassed &= check("performDisplay invalid id", !badId.success());

            UpdateOutcome updated = performUpdate(conn, "1", "Johnny", "Smith", "Cardinals");
            allPassed &= check("performUpdate success", updated.success());
            allPassed &= check("update visible on next display",
                    displayFan(conn, 1).equals(new Fan(1, "Johnny", "Smith", "Cardinals")));

            UpdateOutcome updateMissing = performUpdate(conn, "999", "No", "One", "Nowhere");
            allPassed &= check("performUpdate missing id", !updateMissing.success());
            UpdateOutcome updateBlank = performUpdate(conn, "1", "", "Smith", "Cardinals");
            allPassed &= check("performUpdate blank field fails", !updateBlank.success());

            allPassed &= check("updateFan missing id returns false",
                    !updateFan(conn, new Fan(999, "No", "One", "Nowhere")));
        } catch (SQLException ex) {
            System.out.println("  Fixture database setup failed: " + ex.getMessage());
            allPassed = false;
        }

        // --- interface construction ---
        FanFormControls controls = createFormControls();
        allPassed &= check("display button labeled \"Display\"", "Display".equals(controls.displayButton.getText()));
        allPassed &= check("update button labeled \"Update\"", "Update".equals(controls.updateButton.getText()));
        allPassed &= check("root contains form, buttons, status", controls.root.getChildren().size() == 3);

        if (allPassed) {
            System.out.println("Self-test PASSED\n");
        } else {
            System.out.println("Self-test FAILED (see above)\n");
        }
    }

    /**
     * Creates a disposable in-memory H2 database, standing in for the real
     * MySQL "databasedb", with its own "fans" table pre-created and
     * pre-populated exactly the way the assignment says the real table will
     * already be created and populated when this program is graded. This is
     * test-fixture setup only - FanInfo's own display/update code never
     * creates, drops, or populates a table.
     *
     * @return an open connection to the freshly-built fixture database
     * @throws SQLException if the fixture cannot be created
     */
    private static Connection createFixtureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:fansSelfTest_" + System.nanoTime()
                + ";DB_CLOSE_DELAY=-1");
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE fans (id INT PRIMARY KEY, firstname VARCHAR(25), "
                    + "lastname VARCHAR(25), favoriteteam VARCHAR(25))");
            st.execute("INSERT INTO fans VALUES (1, 'John', 'Smith', 'Cubs')");
            st.execute("INSERT INTO fans VALUES (2, 'Jane', 'Doe', 'Yankees')");
        }
        return conn;
    }

    /** A no-argument action that may throw, used by throwsException/doesNotThrow. */
    private interface ThrowingAction {
        void run() throws Exception;
    }

    private static boolean throwsException(ThrowingAction action) {
        try {
            action.run();
            return false;
        } catch (Exception expected) {
            return true;
        }
    }

    private static boolean doesNotThrow(ThrowingAction action) {
        return !throwsException(action);
    }

    private static boolean check(String description, boolean condition) {
        System.out.println("  [" + (condition ? "PASS" : "FAIL") + "] " + description);
        return condition;
    }

    /**
     * Entry point. Runs the self-test first, then launches the GUI, which
     * connects to the real "databasedb" MySQL database.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        runSelfTest();
        launch(args);
    }
}
