// cschumacher_09132026_mod1_2_csd420
// https://github.com/LittleIowaBoy/csd-420/tree/main

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RandomCards displays four randomly selected cards from a 52-card deck and
 * lets the user deal a new hand via a Refresh button wired up with a lambda
 * expression for the button's event handler.
 */
public class RandomCards extends Application {

    private static final int CARD_COUNT = 4;
    private static final int DECK_SIZE = 52;

    private final ImageView[] cardViews = new ImageView[CARD_COUNT];

    @Override
    public void start(Stage primaryStage) {
        // Create an HBox to display the four card images
        HBox cardBox = new HBox(10);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setPadding(new Insets(15));

        // Initialize ImageViews and add to the HBox
        for (int i = 0; i < CARD_COUNT; i++) {
            cardViews[i] = new ImageView();
            cardViews[i].setFitWidth(100);
            cardViews[i].setFitHeight(140);
            cardViews[i].setPreserveRatio(true);
            cardBox.getChildren().add(cardViews[i]);
        }

        // Create the refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 16 6 16;");

        // Use a Lambda Expression for the button click event handler
        refreshButton.setOnAction(e -> refreshCards());

        // HBox for bottom button container to center it with padding
        HBox buttonBox = new HBox(refreshButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 10, 20, 10));

        // Create BorderPane layout
        BorderPane root = new BorderPane();
        root.setCenter(cardBox);
        root.setBottom(buttonBox);

        // Initial card deal
        refreshCards();

        // Configure and display the scene and stage
        Scene scene = new Scene(root, 480, 260);
        primaryStage.setTitle("Random Card Picker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Randomly picks 4 unique cards from the deck of 52 and updates the ImageViews.
     */
    private void refreshCards() {
        // Create a list of card numbers 1 through 52
        List<Integer> deck = new ArrayList<>(DECK_SIZE);
        for (int i = 1; i <= DECK_SIZE; i++) {
            deck.add(i);
        }

        // Shuffle the deck to pick 4 random distinct cards
        Collections.shuffle(deck);

        for (int i = 0; i < CARD_COUNT; i++) {
            int cardNumber = deck.get(i);
            Image cardImage = loadCardImage(cardNumber);
            cardViews[i].setImage(cardImage);
        }
    }

    /**
     * Helper method to load the card image file from "cards/" or "module-1/cards/".
     *
     * @param cardNumber Card number between 1 and 52
     * @return JavaFX Image instance
     */
    private Image loadCardImage(int cardNumber) {
        String fileName = cardNumber + ".png";

        // Check local cards directory first, then module-1/cards directory
        File cardFile = new File("cards/" + fileName);
        if (!cardFile.exists()) {
            cardFile = new File("module-1/cards/" + fileName);
        }

        if (cardFile.exists()) {
            return new Image(cardFile.toURI().toString());
        } else {
            // Fallback to relative URL string
            return new Image("file:cards/" + fileName);
        }
    }

    /**
     * Entry point for the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
