import java.io.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.HashMap;

public class Main extends Application {
    private static final HashMap<String, String> users = new HashMap<>();
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login");
        createLoginWindow();
    }

    private void createLoginWindow() {
        primaryStage.setTitle("Login");
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(200));

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginLayout.getChildren().addAll(
            new Label("Username:"), userField,
            new Label("Password:"), passField, loginButton, registerButton
        );

        //Log in button
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            File saveFile = new File("accounts.txt");
            try{
                Accounts account = Accounts.load(username, saveFile);
                if(account != null && account.getPassword().equals(password)){
                    showAlert("Login successful!");
                } else {
                    showAlert("Invalid username or password.");
                }
            }
            catch (Exception ex){
                showAlert("Error loading account");
                ex.printStackTrace();
            }
        });

        // Register button opens registration window
        registerButton.setOnAction(e -> createRegisterWindow());

        Scene loginScene = new Scene(loginLayout);
        primaryStage.setScene(loginScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void createRegisterWindow() {
        primaryStage.setTitle("Register");
        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(200));

        TextField userField = new TextField();
        TextField emailField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");

        registerLayout.getChildren().addAll(
            new Label("Username:"), userField,
            new Label("Email:"), emailField,
            new Label("Password:"), passField, loginButton
        );

        // Register button opens registration window
        loginButton.setOnAction(e -> createLoginWindow());

        Scene registerScene = new Scene(registerLayout);
        primaryStage.setScene(registerScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}