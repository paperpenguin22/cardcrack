import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        createLoginWindow();
    }

    private void createLoginWindow() {
        primaryStage.setTitle("Login");
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginLayout.getChildren().addAll(new Label("Username:"), userField,
                new Label("Password:"), passField, loginButton, registerButton);

        loginButton.setOnAction(e -> {
            String username = userField.getText();
            // Password checking removed
            if (users.containsKey(username)) {
                showAlert("Login successful!");
                // Proceed further as needed
            } else {
                showAlert("Invalid credentials.");
            }
        });

        registerButton.setOnAction(e -> createRegisterWindow());

        primaryStage.setScene(new Scene(loginLayout, 300, 200));
        primaryStage.show();
    }

    private void createRegisterWindow() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register");
        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(20));

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button registerButton = new Button("Create Account");
        Button backButton = new Button("Back to Login");

        registerLayout.getChildren().addAll(new Label("New Username:"), userField,
                new Label("New Password:"), passField, registerButton, backButton);

        registerButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Fields cannot be empty.");
                return;
            }

            if (users.containsKey(username)) {
                showAlert("Username already exists.");
            } else {
                users.put(username, password);
                showAlert("Account created! Please log in.");
                registerStage.close();
            }
        });

        backButton.setOnAction(e -> {
            registerStage.close();
            createLoginWindow();
        });

        registerStage.setScene(new Scene(registerLayout, 300, 200));
        registerStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
