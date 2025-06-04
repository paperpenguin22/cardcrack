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
        createLoginWindow();
    }

    private void createLoginWindow() {
        primaryStage.setTitle("Login");
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(200));

        TextField userField = new TextField();
        TextField emailField = new TextField(); // Email field for login
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginLayout.getChildren().addAll(
                new Label("Username:"), userField,
                new Label("Email:"), emailField, // Add email label and field
                new Label("Password:"), passField,
                loginButton, registerButton
        );

        // Login button does nothing
        loginButton.setOnAction(e -> {
            //add the login code
        });

        // Register button opens registration window
        registerButton.setOnAction(e -> createRegisterWindow());

        primaryStage.setScene(new Scene(loginLayout, 300, 250));
        primaryStage.show();
    }

    private void createRegisterWindow() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register");
        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(200));

        TextField userField = new TextField();
        TextField emailField = new TextField(); // Email field for registration
        PasswordField passField = new PasswordField();
        Button registerButton = new Button("Create Account");
        Button backButton = new Button("Back to Login");

        registerLayout.getChildren().addAll(
                new Label("New Username:"), userField,
                new Label("Email:"), emailField, // Add email label and field
                new Label("New Password:"), passField,
                registerButton, backButton
        );

        // Register button does nothing
        registerButton.setOnAction(e -> {
            //add the registering code 
        });

        // Back button closes register window and returns to login window
        backButton.setOnAction(e -> {
            registerStage.close();
            createLoginWindow();
        });

        registerStage.setScene(new Scene(registerLayout, 600, 400));
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
