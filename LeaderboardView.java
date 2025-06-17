import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardView {

    public static void open(Stage stage, Accounts currentAccount, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Leaderboard - Your Friends and You");
        ListView<String> leaderboardList = new ListView<>();

        try {
            // Load all accounts from file
            List<Accounts> allAccounts = loadAllAccounts();

            // Filter friends + current account
            Set<String> friendEmails = new HashSet<>(currentAccount.getFriends());
            friendEmails.add(currentAccount.getEmail()); // include self

            List<Accounts> friendsAndSelf = allAccounts.stream()
                    .filter(acc -> friendEmails.contains(acc.getEmail()))
                    .collect(Collectors.toList());

            if (friendsAndSelf.isEmpty()) {
                leaderboardList.getItems().add("No friends to show");
            } else {
                // Sort descending by points
                friendsAndSelf.sort(Comparator.comparingInt(Accounts::getPoints).reversed());

                int rank = 1;
                for (Accounts acc : friendsAndSelf) {
                    leaderboardList.getItems().add(rank + ". " + acc.getName() + " - " + acc.getPoints());
                    rank++;
                }
            }

        } catch (IOException e) {
            leaderboardList.getItems().add("Error loading leaderboard data.");
            e.printStackTrace();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> mainApp.homePage());

        root.getChildren().addAll(title, leaderboardList, backButton);

        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Leaderboard");
        stage.show();
    }

    /**
     * Loads all accounts from the save file using Accounts.load().
     * This reads the entire file and creates all Accounts objects.
     */
    private static List<Accounts> loadAllAccounts() throws IOException {
        List<Accounts> accountsList = new ArrayList<>();

        // Your saveFile is static in Accounts class
        List<String> lines = Files.readAllLines(Accounts.saveFile);

        for (int i = 0; i < lines.size(); i++) {
            String nameLine = lines.get(i);
            // Skip empty lines or lines that don't look like names (optional)
            if (nameLine.isBlank() || nameLine.equals("exit") || nameLine.startsWith("Friends:")) continue;

            // Load account by name
            Accounts acc = Accounts.load(nameLine);
            if (acc != null) {
                accountsList.add(acc);
            }

            // Skip ahead in the file until next "exit" to avoid duplicate processing
            while (i < lines.size() && !lines.get(i).equals("exit")) {
                i++;
            }
        }

        return accountsList;
    }
}