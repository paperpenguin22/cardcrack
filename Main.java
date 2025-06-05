import java.io.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.HashMap;

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
        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(200));
        
        TextField searchField = new TextField();
        Button calendarButton = new Button("Calendar");

        homeLayout.getChildren().addAll(
            new Label("Search:"), searchField,
            calendarButton
        );

        calendarButton.setOnAction(e -> System.exit(0));

        primaryStage.setScene(new Scene(homeLayout));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}