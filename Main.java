import java.io.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Main extends Application {
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login");
        Login loginApp = new Login(primaryStage, this);
        loginApp.showLoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void homePage(){
        primaryStage.setTitle("Home Page");

        BorderPane homeLayout = new BorderPane();
        homeLayout.setPadding(new Insets(20));

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));

        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();
        Button calendarButton = new Button("Calendar");

        Label clockLabel = new Label();
        clockLabel.setStyle("-fx-background-color: #d3d3d3; " +"-fx-border-color: #555555; " + "-fx-border-width: 1.5; " +"-fx-padding: 4 8 4 8; " +"-fx-border-radius: 6; " +"-fx-background-radius: 6;");
        updateClock(clockLabel); // Initialize the clock
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock(clockLabel)));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(searchLabel, searchField, spacer, clockLabel);

        homeLayout.setTop(topBar);
        homeLayout.setBottom(calendarButton);

        calendarButton.setOnAction(e -> {
            try {
                File saveFile = new File("accounts.txt");
                Accounts account = Accounts.load(Login.getName(), saveFile);

                if (account != null) {
                    Calendar calendarView = new Calendar(primaryStage, account);
                    Calendar.calendar(); // This probably needs to use the account or eventMap inside the class
                } else {
                    System.err.println("Account not found or failed to load.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        primaryStage.setScene(new Scene(homeLayout));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    private void updateClock(Label clockLabel) {
        ZoneId zoneId = ZoneId.of("America/Toronto"); // Time zone for Ottawa
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm z \nMMM.dd.yyyy");
        
        clockLabel.setText(now.format(formatter));
    }
    
}