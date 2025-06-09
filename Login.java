import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.HashMap;
import java.io.*;

public class Login {
    private final Stage primaryStage;
    private final Main mainApp;
    public static String username;
    public static String password;
    public static String email;

    public Login(Stage primaryStage, Main mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
    }

    public void showLoginWindow() {
        primaryStage.setTitle("Login");
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(200));
        

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        TextField passTextField = new TextField();
  
        passTextField.setVisible(false);
        
        passField.setPrefWidth(2000);
        passTextField.setPrefWidth(2000);
        
        StackPane passwordStack = new StackPane();
        passwordStack.setPrefWidth(2000);
        passwordStack.getChildren().addAll(passField, passTextField);
        
        CheckBox showPasswordBox = new CheckBox("👁");
        showPasswordBox.setStyle("-fx-font-size: 14px;");

        HBox passwordBox = new HBox(5, passwordStack, showPasswordBox);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        
        // Toggle password visibility
        showPasswordBox.setOnAction(e -> {
            if (showPasswordBox.isSelected()) {
                passTextField.setText(passField.getText());
                passTextField.setVisible(true);
                passField.setVisible(false);
            } else {
                passField.setText(passTextField.getText());
                passField.setVisible(true);
                passTextField.setVisible(false);
            }
        });
        
        loginLayout.getChildren().addAll(
            new Label("Username:"), userField,
            new Label("Password:"), passField, loginButton, registerButton
        );

        loginButton.setOnAction(e -> {
            this.username = userField.getText();
            this.password = passField.getText();

            File saveFile = new File("accounts.txt");
            try {
                Accounts account = Accounts.load(username, saveFile);
                if (account != null && account.getPassword().equals(password)) {
                    this.email = account.getEmail();
                    showAlert("Login successful!");
                    mainApp.homePage();
                } else {
                    showAlert("Invalid username or password.");
                }
            } catch (Exception ex) {
                showAlert("Error loading account");
                ex.printStackTrace();
            }
        });

        registerButton.setOnAction(e -> showRegisterWindow());

        primaryStage.setScene(new Scene(loginLayout));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void showRegisterWindow() {
        primaryStage.setTitle("Register");
        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(200));

        TextField userField = new TextField();
        TextField emailField = new TextField();

        PasswordField passField = new PasswordField();
        TextField passTextField = new TextField();
        passTextField.setVisible(false);

        passField.setPrefWidth(2000);
        passTextField.setPrefWidth(2000);

        StackPane passwordStack = new StackPane();
        passwordStack.setPrefWidth(2000);
        passwordStack.getChildren().addAll(passField, passTextField);

        CheckBox showPasswordBox = new CheckBox("👁");
        showPasswordBox.setStyle("-fx-font-size: 14px;");

        HBox passwordBox = new HBox(5, passwordStack, showPasswordBox);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label emailErrorLabel = new Label("Enter valid email");
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        emailErrorLabel.setVisible(false);

        // Add email validation listener
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("@") && newValue.contains(".")) {
                emailField.setStyle("-fx-text-box-border: #cccccc; -fx-focus-color: #0093ff;");
                emailErrorLabel.setVisible(false);
            } else {
                emailField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
                emailErrorLabel.setVisible(true);
            }
        });

        // Toggle password visibility
        showPasswordBox.setOnAction(e -> {
            if (showPasswordBox.isSelected()) {
                passTextField.setText(passField.getText());
                passTextField.setVisible(true);
                passField.setVisible(false);
            } else {
                passField.setText(passTextField.getText());
                passField.setVisible(true);
                passTextField.setVisible(false);
            }
        });

        registerLayout.getChildren().addAll(
            new Label("Username:"), userField,
            new Label("Email:"), emailField, emailErrorLabel,
            new Label("Password:"), passField,
            registerButton, loginButton
        );

        registerButton.setOnAction(e -> {
            this.username = userField.getText();
            this.email = emailField.getText();
            this.password = passField.getText();

            File saveFile = new File("accounts.txt");
            try {
                Accounts testAccount = Accounts.load(username, saveFile);
                if (testAccount != null) {
                    if (email.equals(testAccount.getEmail())) {
                        showAlert("Email already in use.");
                    }
                } else {
                    Accounts account = new Accounts(username, email, password);
                    Accounts.save(account);
                    showAlert("Account creation successful!");
                    mainApp.homePage();
                }
            } catch (Exception ex) {
                showAlert("Error loading account");
                ex.printStackTrace();
            }
        });

        loginButton.setOnAction(e -> showLoginWindow());

        primaryStage.setScene(new Scene(registerLayout));
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

    public static String getName() {return username;}

    public static String getPassword() {return password;}

    public static String getEmail() {return email;}
}
