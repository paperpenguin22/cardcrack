import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class FriendSearchView {
    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Friend Search Page");
        root.getChildren().add(title);

        HBox searchBox = new HBox(10);
        Label emailLabel = new Label("Friend Email:");
        TextField emailField = new TextField();
        Button searchBtn = new Button("Send Friend Request");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        searchBtn.setOnAction(e -> {
            String emailToSend = emailField.getText().trim().toLowerCase();
            if (emailToSend.isEmpty()) {
                messageLabel.setText("Please enter an email.");
                return;
            }
            if (emailToSend.equals(account.getEmail())) {
                messageLabel.setText("You cannot friend yourself.");
                return;
            }
            if (account.getFriends().contains(emailToSend)) {
                messageLabel.setText("This user is already your friend.");
                return;
            }
            if (account.hasFriendRequestSentTo(emailToSend)) {
                messageLabel.setText("You have already sent a friend request to this user.");
                return;
            }

            // Load the account to check if it exists
            Accounts targetAccount;
            try {
                targetAccount = Accounts.load(emailToSend);
                if (targetAccount == null) {
                    messageLabel.setText("No account found with this email.");
                    return;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                messageLabel.setText("Error loading account.");
                return;
            }

            // Add friend request sent for this account
            account.addFriendRequestSent(emailToSend, ""); // no name
            try {
                Accounts.save(account);
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Friend request sent to " + emailToSend);
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Failed to send friend request.");
            }
        });

        searchBox.getChildren().addAll(emailLabel, emailField, searchBtn);
        root.getChildren().addAll(searchBox, messageLabel);

        Button backButton = new Button("Back to Homepage");
        backButton.setOnAction(e -> mainApp.homePage());
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 450, 200));
        stage.setTitle("Find Friends");
        stage.show();
    }
}
