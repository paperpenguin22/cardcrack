import java.io.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.HashMap;
import javafx.scene.layout.BorderPane;

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

        topBar.getChildren().addAll(searchLabel, searchField);

        homeLayout.setTop(topBar);
        homeLayout.setBottom(calendarButton);

        calendarButton.setOnAction(e -> {
            Accounts account = new Accounts(Login.getName(), Login.getEmail(), Login.getPassword());
            Calendar calendarView = new Calendar(primaryStage, account.getCalendar());
            Calendar.calendar();
        });

        primaryStage.setScene(new Scene(homeLayout));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}