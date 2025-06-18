import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.*;

public class Main extends Application {
    public static Stage primaryStage;
    public static Accounts account;
    private static Login loginInstance;
    private static boolean darkMode = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLogin();
    }

    public static void showLogin() {
        loginInstance = new Login(primaryStage, new Main());
        loginInstance.showLoginWindow();
    }

    public static void homePage() {
        if (primaryStage == null) {
            System.err.println("Error: primaryStage is null.");
            return;
        }

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

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);

        ToggleButton darkModeToggle = new ToggleButton("Dark Mode");
        darkModeToggle.setSelected(darkMode);
        darkModeToggle.setOnAction(e -> {
            darkMode = darkModeToggle.isSelected();
            applyTheme(root);
        });

        Label clockLabel = new Label();
        clockLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> timeZoneSelector = new ComboBox<>();
        List<String> zones = Arrays.asList(
            "America/Toronto",
            "America/New_York",
            "Europe/London",
            "Europe/Paris",
            "Asia/Tokyo",
            "Australia/Sydney",
            "UTC"
        );
        timeZoneSelector.getItems().addAll(zones);
        timeZoneSelector.setValue("America/Toronto");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String zoneId = timeZoneSelector.getValue();
            try {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of(zoneId));
                clockLabel.setText("Current time: " + now.format(formatter));
            } catch (DateTimeException ex) {
                clockLabel.setText("Invalid timezone");
            }
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();

        topBar.getChildren().addAll(clockLabel, timeZoneSelector, darkModeToggle);

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
                        LocalDate selectedDate = date;
                        jumpToDateBtn.setOnAction(ev -> {
                            Calendar calView = new Calendar(primaryStage, account.getName());
                            calView.showCalendarForDate(selectedDate);
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
        openCalendar.setOnAction(ev -> {
            Calendar calView = new Calendar(primaryStage, account.getName());
            calView.showCalendar();
        });

        Button friendRequestsBtn = new Button("Inbox");
        friendRequestsBtn.setOnAction(ev -> {
            FriendRequestsView.open(primaryStage, account, new Main());
        });

        Button friendSearchBtn = new Button("Search Friends");
        friendSearchBtn.setOnAction(ev -> {
            FriendSearchView.open(primaryStage, account, new Main());
        });

        Button leaderboardBtn = new Button("Leaderboard");
        leaderboardBtn.setOnAction(ev -> {
            LeaderboardView.open(primaryStage, account, new Main());
        });

        Button subjectsBtn = new Button("Subjects");
        subjectsBtn.setOnAction(ev -> {
            SubjectsView.open(primaryStage, account, new Main());
        });

        Button flashcardsBtn = new Button("Flashcards / Quiz");
        flashcardsBtn.setOnAction(ev -> {
            FlashcardsQuizView.open(primaryStage, account, new Main());
        });

        // RED Logout Button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        logoutBtn.setOnAction(ev -> {
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

        root.setTop(topBar);
        root.setLeft(leftPane);
        root.setRight(rightPane);

        applyTheme(root);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void applyTheme(Pane root) {
        if (darkMode) {
            root.setStyle("-fx-background-color: #121212;");
            setNodeTextColor(root, "-fx-text-fill: white;");
            setButtonStyle(root, "-fx-background-color: #333333; -fx-text-fill: white;");
            setComboBoxStyle(root, "-fx-background-color: #333333; -fx-text-fill: white;");
            setToggleButtonStyle(root, "-fx-background-color: #333333; -fx-text-fill: white;");
        } else {
            root.setStyle("-fx-background-color: white;");
            setNodeTextColor(root, "-fx-text-fill: black;");
            setButtonStyle(root, "-fx-background-color: lightgray; -fx-text-fill: black;");
            setComboBoxStyle(root, "-fx-background-color: white; -fx-text-fill: black;");
            setToggleButtonStyle(root, "-fx-background-color: lightgray; -fx-text-fill: black;");
        }
    }

    private static void setNodeTextColor(Pane root, String style) {
        root.lookupAll(".label").forEach(node -> node.setStyle(style));
    }

    private static void setButtonStyle(Pane root, String style) {
        root.lookupAll(".button").forEach(node -> {
            if (!(node instanceof Button) || !"Logout".equals(((Button) node).getText())) {
                node.setStyle(style);
            }
        });
    }

    private static void setComboBoxStyle(Pane root, String style) {
        root.lookupAll(".combo-box").forEach(node -> node.setStyle(style));
    }

    private static void setToggleButtonStyle(Pane root, String style) {
        root.lookupAll(".toggle-button").forEach(node -> node.setStyle(style));
    }
}
