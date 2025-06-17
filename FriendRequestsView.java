import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;

public class FriendRequestsView {
    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Friend Requests Inbox");
        root.getChildren().add(title);

        VBox requestsBox = new VBox(5);

        Map<String, java.util.List<String>> requests = account.getFriendRequests();

        if (requests.isEmpty()) {
            requestsBox.getChildren().add(new Label("No friend requests."));
        } else {
            for (Map.Entry<String, java.util.List<String>> entry : requests.entrySet()) {
                String senderEmail = entry.getKey();
                java.util.List<String> senderNames = entry.getValue();
                String displayName = senderNames.isEmpty() ? senderEmail : senderNames.get(0);

                HBox requestRow = new HBox(10);
                Label nameLabel = new Label(displayName + " (" + senderEmail + ")");
                Button acceptBtn = new Button("Accept");
                Button declineBtn = new Button("Decline");

                acceptBtn.setOnAction(e -> {
                    // Add friend and remove request
                    account.addFriend(senderEmail);
                    account.removeFriendRequest(senderEmail);
                    try {
                        Accounts.save(account);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    open(stage, account, mainApp); // refresh
                });

                declineBtn.setOnAction(e -> {
                    account.removeFriendRequest(senderEmail);
                    try {
                        Accounts.save(account);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    open(stage, account, mainApp); // refresh
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
}
