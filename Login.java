import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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

        VBox loginLayout = new VBox(15);
        loginLayout.setPadding(new Insets(100));
        loginLayout.setStyle("-fx-background-color: #f4f4f4;");
        loginLayout.setPrefWidth(400);
        loginLayout.setPrefHeight(600);

        // Logo
        ImageView imageView = null;
        try {
            FileInputStream input = new FileInputStream("logo.png");
            Image image = new Image(input);
            imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Logo not found.");
        }

        Label titleLabel = new Label("Welcome Back");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        TextField passTextField = new TextField();
        passTextField.setPromptText("Password");
        passTextField.setVisible(false);

        StackPane passwordStack = new StackPane(passField, passTextField);
        passwordStack.setMaxWidth(300);

        CheckBox showPasswordBox = new CheckBox("Show Password");
        showPasswordBox.setStyle("-fx-font-size: 12px;");

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

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
        registerButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");

        loginButton.setOnAction(e -> {
            this.username = userField.getText();
            this.password = passField.getText();

            try {
                Accounts account = Accounts.load(username);
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

        loginLayout.setAlignment(javafx.geometry.Pos.CENTER);
        loginLayout.getChildren().addAll(
            imageView, titleLabel,
            userField, passwordStack,
            showPasswordBox,
            loginButton, registerButton
        );

        primaryStage.setScene(new Scene(loginLayout, 500, 700));
        primaryStage.show();
    }

    public void showRegisterWindow() {
        primaryStage.setTitle("Register");

        VBox registerLayout = new VBox(15);
        registerLayout.setPadding(new Insets(100));
        registerLayout.setStyle("-fx-background-color: #f4f4f4;");
        registerLayout.setPrefWidth(400);
        registerLayout.setPrefHeight(700);

        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        Label emailErrorLabel = new Label("Enter a valid email address");
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        emailErrorLabel.setVisible(false);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        TextField passTextField = new TextField();
        passTextField.setPromptText("Password");
        passTextField.setVisible(false);

        StackPane passwordStack = new StackPane(passField, passTextField);
        passwordStack.setMaxWidth(300);

        CheckBox showPasswordBox = new CheckBox("Show Password");

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

        Button registerButton = new Button("Register");
        Button loginButton = new Button("Back to Login");

        registerButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
        loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
        registerButton.setDisable(true);

        emailField.textProperty().addListener((obs, oldText, newText) -> {
            validateRegistration(userField.getText(), newText, passField.getText(), registerButton);
            boolean valid = isValidEmail(newText);
            emailErrorLabel.setVisible(!valid);
            emailField.setStyle(valid ? "" : "-fx-border-color: red;");
        });

        userField.textProperty().addListener((obs, oldText, newText) ->
            validateRegistration(newText, emailField.getText(), passField.getText(), registerButton)
        );

        passField.textProperty().addListener((obs, oldText, newText) ->
            validateRegistration(userField.getText(), emailField.getText(), newText, registerButton)
        );

        registerButton.setOnAction(e -> {
            this.username = userField.getText();
            this.email = emailField.getText();
            this.password = passField.getText();

            try {
                Accounts testAccount = Accounts.load(username);
                if (testAccount != null && email.equals(testAccount.getEmail())) {
                    showAlert("Email already in use.");
                } else {
                    Accounts account = new Accounts(username, email, password);
                    Accounts.save(account);
                    showAlert("Account created successfully!");
                    mainApp.homePage();
                }
            } catch (Exception ex) {
                showAlert("Error saving account.");
                ex.printStackTrace();
            }
        });

        loginButton.setOnAction(e -> showLoginWindow());

        registerLayout.setAlignment(javafx.geometry.Pos.CENTER);
        registerLayout.getChildren().addAll(
            titleLabel,
            userField, emailField, emailErrorLabel,
            passwordStack, showPasswordBox,
            registerButton, loginButton
        );

        primaryStage.setScene(new Scene(registerLayout, 500, 750));
        primaryStage.show();
    }

    private void validateRegistration(String username, String email, String password, Button registerButton) {
        boolean isUsernameEmpty = username == null || username.trim().isEmpty();
        boolean isPasswordEmpty = password == null || password.trim().isEmpty();
        registerButton.setDisable(isUsernameEmpty || isPasswordEmpty || !isValidEmail(email));
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && (email.contains(".c") || email.contains(".C"));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getName() { return username; }
    public static String getPassword() { return password; }
    public static String getEmail() { return email; }
}
