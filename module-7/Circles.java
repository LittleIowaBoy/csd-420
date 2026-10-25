// cschumacher_10252026_mod7_1_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Circles displays four JavaFX Circle shapes styled entirely through the
 * external style sheet "mystyle.css" instead of through Java code, matching
 * the StyleTest example early in Chapter 31 of Liang's "Introduction to Java
 * Programming":
 *   - Circle 1 uses the "plaincircle" style class (white fill, black stroke).
 *   - Circle 2 uses the "plaincircle" and "circleborder" style classes
 *     together, layering a thicker dashed stroke on top of the plain look.
 *   - Circle 3 uses the "redcircle" ID selector (red fill and stroke).
 *   - Circle 4 uses the "greencircle" ID selector (green fill and stroke).
 *
 * A self-test runs first, verifying that each circle is assigned the
 * expected style class(es) or id before the window is shown, confirming the
 * styling hooks are wired up correctly independent of CSS loading/rendering.
 */
public class Circles extends Application {

    private static final double RADIUS = 40;

    @Override
    public void start(Stage primaryStage) {
        HBox pane = createCirclePane();

        Scene scene = new Scene(pane, 480, 160);
        scene.getStylesheets().add(getClass().getResource("mystyle.css").toExternalForm());

        primaryStage.setTitle("Circles - Module 7 Programming Assignment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Builds the HBox containing the four styled circles: one plain circle,
     * one plain circle with an added decorative border, one circle styled
     * by its "redcircle" id, and one circle styled by its "greencircle" id.
     *
     * @return HBox laid out with the four circles, centered with spacing
     */
    private HBox createCirclePane() {
        Circle plain = new Circle(RADIUS);
        plain.getStyleClass().add("plaincircle");

        Circle bordered = new Circle(RADIUS);
        bordered.getStyleClass().addAll("plaincircle", "circleborder");

        Circle red = new Circle(RADIUS);
        red.setId("redcircle");

        Circle green = new Circle(RADIUS);
        green.setId("greencircle");

        HBox pane = new HBox(20, plain, bordered, red, green);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    /**
     * Verifies that each circle created for the display is assigned the
     * expected style class(es) or id, independent of CSS loading/rendering.
     * Constructing a Circle and setting its style class/id does not require
     * the JavaFX toolkit to be running, so this check can run as a plain
     * self-test before launch() starts the toolkit.
     */
    private static void runSelfTest() {
        System.out.println("Running self-test on circle style assignments...");

        Circle plain = new Circle(RADIUS);
        plain.getStyleClass().add("plaincircle");

        Circle bordered = new Circle(RADIUS);
        bordered.getStyleClass().addAll("plaincircle", "circleborder");

        Circle red = new Circle(RADIUS);
        red.setId("redcircle");

        Circle green = new Circle(RADIUS);
        green.setId("greencircle");

        boolean plainOk = plain.getStyleClass().equals(List.of("plaincircle"));
        boolean borderedOk = bordered.getStyleClass().equals(List.of("plaincircle", "circleborder"));
        boolean redOk = "redcircle".equals(red.getId());
        boolean greenOk = "greencircle".equals(green.getId());

        if (plainOk && borderedOk && redOk && greenOk) {
            System.out.println("Self-test PASSED (all 4 circles have the expected style class/id)\n");
        } else {
            System.out.println("Self-test FAILED:");
            System.out.println("  plain styleClass:    " + plain.getStyleClass() + " (expected [plaincircle])");
            System.out.println("  bordered styleClass: " + bordered.getStyleClass()
                    + " (expected [plaincircle, circleborder])");
            System.out.println("  red id:              " + red.getId() + " (expected redcircle)");
            System.out.println("  green id:            " + green.getId() + " (expected greencircle)\n");
        }
    }

    /**
     * Entry point for the JavaFX application. Runs the self-test first, then
     * launches the GUI.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        runSelfTest();
        launch(args);
    }
}
