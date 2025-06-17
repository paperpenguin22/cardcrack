import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Main extends Application {
    public static Stage primaryStage;  // global stage
    public static Accounts account;    // currently logged in account
    private static Login loginInstance; // keep a reference to login instance for username retrieval

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLogin(); // start with login screen
    }

    // Show the login screen
    public static void showLogin() {
        loginInstance = new Login(primaryStage, new Main());
        loginInstance.showLoginWindow();
    }

    // Called after successful login/registration
    public static void homePage() {
        if (primaryStage == null) {
            System.err.println("Error: primaryStage is null.");
            return;
        }

        // Load latest account info using username from login instance
        String username = loginInstance.getName();
        try {
            account = Accounts.load(username);
            if (account == null) {
                System.err.println("Error: Account not found for user: " + username);
                showLogin();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            showLogin();
            return;
        }

        primaryStage.setTitle("Study App Home");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Right pane with upcoming events and calendar button
        Label upcomingLabel = new Label("Upcoming Events:");
        VBox eventBox = new VBox(10);

        Map<LocalDate, List<CalendarEvent>> calendar = account.getCalendar();
        if (calendar != null && !calendar.isEmpty()) {
            List<LocalDate> sortedDates = new ArrayList<>(calendar.keySet());
            Collections.sort(sortedDates);

            for (LocalDate date : sortedDates) {
                List<CalendarEvent> events = calendar.get(date);
                if (events != null) {
                    for (CalendarEvent event : events) {
                        Label eventLabel = new Label(
                            date + " - " + event.getSubject() + " (Unit: " + event.getUnit() + ") [" +
                            event.getType() + "]: " + event.getDescription()
                        );

                        Button jumpToDateBtn = new Button("Go to Day");
                        LocalDate selectedDate = date;  // needed for lambda capture
                        jumpToDateBtn.setOnAction(e -> {
                            Calendar calView = new Calendar(primaryStage, account);
                            calView.showCalendarForDate(selectedDate); // updated method name
                        });

                        HBox box = new HBox(10, eventLabel, jumpToDateBtn);
                        box.setPadding(new Insets(5));
                        eventBox.getChildren().add(box);
                    }
                }
            }
        } else {
            eventBox.getChildren().add(new Label("No upcoming events."));
        }

        Button openCalendar = new Button("Open Calendar");
        openCalendar.setOnAction(e -> {
            Calendar calView = new Calendar(primaryStage, account);
            calView.showCalendar(); // updated method name
        });

        // Left pane with buttons for friend requests, friend search, leaderboard, subjects, flashcards
        Button friendRequestsBtn = new Button("Friend Requests");
        friendRequestsBtn.setOnAction(e -> {
            FriendRequestsView.open(primaryStage, account, new Main());
        });

        Button friendSearchBtn = new Button("Search Friends");
        friendSearchBtn.setOnAction(e -> {
            FriendSearchView.open(primaryStage, account, new Main());
        });

        Button leaderboardBtn = new Button("Leaderboard");
        leaderboardBtn.setOnAction(e -> {
            LeaderboardView.open(primaryStage, account, new Main());
        });

        Button subjectsBtn = new Button("Subjects");
        subjectsBtn.setOnAction(e -> {
            SubjectsView.open(primaryStage, account, new Main());
        });

        Button flashcardsBtn = new Button("Flashcards / Quiz");
        flashcardsBtn.setOnAction(e -> {
            FlashcardsQuizView.open(primaryStage, account, new Main());
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            account = null;
            showLogin();
        });
        
        VBox leftPane = new VBox(15,
            friendRequestsBtn,
            friendSearchBtn,
            leaderboardBtn,
            subjectsBtn,
            flashcardsBtn,
            logoutBtn

        );
        leftPane.setPadding(new Insets(10));

        VBox rightPane = new VBox(20, upcomingLabel, eventBox, openCalendar);
        rightPane.setPadding(new Insets(10));

        root.setLeft(leftPane);
        root.setRight(rightPane);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}