import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendSearchView {

    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Friend Search Page");
        root.getChildren().add(title);

        // --- Friend Search Box ---
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
                messageLabel.setText("You cannot send a friend request to yourself.");
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

            Accounts targetAccount = null;
            try {
                List<String> lines = java.nio.file.Files.readAllLines(Accounts.saveFile);
                for (int i = 0; i < lines.size(); i++) {
                    String possibleName = lines.get(i).trim();
                    if (i + 1 < lines.size()) {
                        String possibleEmail = lines.get(i + 1).trim().toLowerCase();
                        if (possibleEmail.equals(emailToSend)) {
                            targetAccount = Accounts.load(possibleName);
                            break;
                        }
                    }
                    while (i < lines.size() && !lines.get(i).equals("exit")) i++; // skip to next user
                }

                if (targetAccount == null) {
                    messageLabel.setText("No account found with this email.");
                    return;
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                messageLabel.setText("Error loading account.");
                return;
            }


            // Add friend request to sender's sent list
            account.addFriendRequestSent(emailToSend, "");

            // Add friend request to recipient's inbox
            targetAccount.addFriendRequest(account.getEmail(), account.getName());

            try {
                Accounts.save(account);        // Save sender
                Accounts.save(targetAccount);  // Save recipient
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Friend request sent to " + emailToSend);
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Failed to send friend request.");
            }
        });

        searchBox.getChildren().addAll(emailLabel, emailField, searchBtn);
        root.getChildren().addAll(searchBox, messageLabel);

        // --- Current Friends List ---
        Label friendsLabel = new Label("Your Current Friends (Click to Unfriend):");
        root.getChildren().add(friendsLabel);

        VBox friendsBox = new VBox(5);
        List<String> friendEmails = new ArrayList<>(account.getFriends());

        if (friendEmails.isEmpty()) {
            friendsBox.getChildren().add(new Label("You have no friends."));
        } else {
            for (String friendEmail : friendEmails) {
                Button friendBtn = new Button(friendEmail);
                friendBtn.setMaxWidth(Double.MAX_VALUE);
                friendBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Unfriend Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Are you sure you want to unfriend " + friendEmail + "?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                Accounts friendAccount = Accounts.load(friendEmail);
                                if (friendAccount == null) {
                                    messageLabel.setStyle("-fx-text-fill: red;");
                                    messageLabel.setText("Friend account not found.");
                                    return;
                                }

                                account.removeFriend(friendEmail);
                                friendAccount.removeFriend(account.getEmail());

                                Accounts.save(account);
                                Accounts.save(friendAccount);

                                messageLabel.setStyle("-fx-text-fill: green;");
                                messageLabel.setText("You have unfriended " + friendEmail);

                                open(stage, account, mainApp); // Refresh view
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                messageLabel.setStyle("-fx-text-fill: red;");
                                messageLabel.setText("Error unfriending user.");
                            }
                        }
                    });
                });
                friendsBox.getChildren().add(friendBtn);
            }
        }
        root.getChildren().add(friendsBox);

        // Back button
        Button backButton = new Button("Back to Homepage");
        backButton.setOnAction(e -> mainApp.homePage());
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 450, 600));
        stage.setTitle("Find Friends");
        stage.show();
    }
}