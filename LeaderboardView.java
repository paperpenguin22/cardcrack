import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

public class LeaderboardView {
    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Leaderboard");
        root.getChildren().add(title);

        List<String> friends = account.getFriends();

        if (friends.isEmpty()) {
            root.getChildren().add(new Label("You have no friends to display."));
        } else {
            VBox leaderboardBox = new VBox(5);
            Random rnd = new Random();

            for (String friendEmail : friends) {
                int score = rnd.nextInt(1000); // Dummy score
                Label friendLabel = new Label(friendEmail + ": " + score + " points");
                leaderboardBox.getChildren().add(friendLabel);
            }
            root.getChildren().add(leaderboardBox);
        }

        Button backButton = new Button("Back to Homepage");
        backButton.setOnAction(e -> mainApp.homePage());
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 450, 300));
        stage.setTitle("Leaderboard");
        stage.show();
    }
}
