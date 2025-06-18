import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class FriendRequestsView {
    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Friend Requests Inbox");
        root.getChildren().add(title);

        VBox requestsBox = new VBox(5);

        Map<String, String> requests = account.getFriendRequests();

        if (requests.isEmpty()) {
            requestsBox.getChildren().add(new Label("No friend requests."));
        } else {
            for (Map.Entry<String, String> entry : requests.entrySet()) {
                String senderEmail = entry.getKey();
                String senderName = entry.getValue();

                String displayName = (senderName == null || senderName.isEmpty()) ? senderEmail : senderName;

                HBox requestRow = new HBox(10);
                Label nameLabel = new Label(displayName + " (" + senderEmail + ")");
                Button acceptBtn = new Button("Accept");
                Button declineBtn = new Button("Decline");

                acceptBtn.setOnAction(e -> {
                    try {
                        account.acceptFriendRequest(senderEmail);
                        FriendRequestsView.open(stage, account, mainApp); // Refresh view
                    } catch (IllegalArgumentException ex) {
                        showAlert("Friend Request Error", ex.getMessage());
                    } catch (IOException ex) {
                        showAlert("IO Error", "Could not process friend request.");
                        ex.printStackTrace();
                    }
                });

                declineBtn.setOnAction(e -> {
                    account.removeFriendRequest(senderEmail);
                    try {
                        Accounts.save(account);
                        FriendRequestsView.open(stage, account, mainApp); // Refresh view
                    } catch (IOException ex) {
                        showAlert("IO Error", "Could not remove friend request.");
                        ex.printStackTrace();
                    }
                });

                requestRow.getChildren().addAll(nameLabel, acceptBtn, declineBtn);
                requestsBox.getChildren().add(requestRow);
            }
        }

        ScrollPane scrollPane = new ScrollPane(requestsBox);
        scrollPane.setFitToWidth(true);
        root.getChildren().add(scrollPane);

        Button backButton = new Button("Back to Homepage");
        backButton.setOnAction(e -> mainApp.homePage());
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 450, 400));
        stage.setTitle("Friend Requests");
        stage.show();
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
