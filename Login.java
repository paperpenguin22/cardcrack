import java.io.*;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login {
    private final Stage primaryStage;
    private final Main mainApp;
    public static String username;
    public static String password;
    public static String email;

    private static boolean isDarkMode = false;

    // Make emailErrorLabel a field to access in applyThemeToChildren
    private Label emailErrorLabel;

    public Login(Stage primaryStage, Main mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
    }

    public void showLoginWindow() {
        primaryStage.setTitle("Login");

        VBox loginLayout = new VBox(20);
        loginLayout.setPadding(new Insets(100));
        loginLayout.setPrefWidth(400);
        loginLayout.setPrefHeight(600);
        loginLayout.setOpacity(0);
        loginLayout.setAlignment(Pos.CENTER);

        ImageView imageView = null;
        try {
            FileInputStream input = new FileInputStream("logo.png");
            Image image = new Image(input);
            imageView = new ImageView(image);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            System.err.println("Logo not found.");
        }

        Label nameLabel = new Label("cardcrack");
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label titleLabel = new Label("Welcome Back");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(350);
        userField.setStyle("-fx-font-size: 16px;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle("-fx-font-size: 16px;");

        TextField passTextField = new TextField();
        passTextField.setPromptText("Password");
        passTextField.setVisible(false);
        passTextField.setStyle("-fx-font-size: 16px;");

        StackPane passwordStack = new StackPane(passField, passTextField);
        passwordStack.setMaxWidth(350);

        CheckBox showPasswordBox = new CheckBox("Show Password");
        showPasswordBox.setStyle("-fx-font-size: 14px;");

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

        loginButton.setPrefWidth(150);
        registerButton.setPrefWidth(150);
        loginButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        registerButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        loginButton.setOnAction(e -> {
            this.username = userField.getText();
            this.password = passField.isVisible() ? passField.getText() : passTextField.getText();

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
                showAlert("Error loading account.");
                ex.printStackTrace();
            }
        });

        registerButton.setOnAction(e -> showRegisterWindow());

        ToggleButton darkModeToggle = new ToggleButton("Dark Mode");
        darkModeToggle.setSelected(isDarkMode);
        darkModeToggle.setStyle("-fx-font-size: 14px;");

        darkModeToggle.setOnAction(e -> {
            isDarkMode = darkModeToggle.isSelected();
            applyTheme(loginLayout);
        });

        if (imageView != null) {
            loginLayout.getChildren().add(imageView);
            loginLayout.getChildren().add(nameLabel);
        }
        loginLayout.getChildren().addAll(
                titleLabel,
                userField, passwordStack,
                showPasswordBox,
                loginButton, registerButton,
                darkModeToggle
        );

        applyTheme(loginLayout);

        Scene loginScene = new Scene(loginLayout, 500, 700);
        primaryStage.setScene(loginScene);
        primaryStage.show();

        animateFadeIn(loginLayout);
    }

    public void showRegisterWindow() {
        primaryStage.setTitle("Register");

        VBox registerLayout = new VBox(20);
        registerLayout.setPadding(new Insets(80, 100, 80, 100));
        registerLayout.setPrefWidth(400);
        registerLayout.setPrefHeight(750);
        registerLayout.setOpacity(0);
        registerLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(350);
        userField.setStyle("-fx-font-size: 16px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(350);
        emailField.setStyle("-fx-font-size: 16px;");

        // Assign your emailErrorLabel field here
        emailErrorLabel = new Label("Enter a valid email address");
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setPadding(new Insets(0, 0, 10, 5));

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle("-fx-font-size: 16px;");

        TextField passTextField = new TextField();
        passTextField.setPromptText("Password");
        passTextField.setVisible(false);
        passTextField.setStyle("-fx-font-size: 16px;");

        StackPane passwordStack = new StackPane(passField, passTextField);
        passwordStack.setMaxWidth(350);

        CheckBox showPasswordBox = new CheckBox("Show Password");
        showPasswordBox.setStyle("-fx-font-size: 14px;");

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

        registerButton.setPrefWidth(150);
        loginButton.setPrefWidth(150);
        registerButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        loginButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

        ToggleButton darkModeToggle = new ToggleButton("Dark Mode");
        darkModeToggle.setSelected(isDarkMode);
        darkModeToggle.setStyle("-fx-font-size: 14px;");

        darkModeToggle.setOnAction(e -> {
            isDarkMode = darkModeToggle.isSelected();
            applyTheme(registerLayout);
            // Re-apply the custom style after theme change to prevent overwrite:
            emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
        });

        registerButton.setDisable(true);

        emailField.textProperty().addListener((obs, oldText, newText) -> {
            validateRegistration(userField.getText(), newText, passField.getText(), registerButton);
            boolean valid = isValidEmail(newText);
            emailErrorLabel.setVisible(!valid);
            emailField.setStyle(valid ? "" : "-fx-border-color: red; -fx-border-radius: 15;");
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
            this.password = passField.isVisible() ? passField.getText() : passTextField.getText();

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

        registerLayout.getChildren().addAll(
                titleLabel,
                userField,
                emailField,
                emailErrorLabel,
                passwordStack,
                showPasswordBox,
                registerButton,
                loginButton,
                darkModeToggle
        );

        applyTheme(registerLayout);
        // Set the custom style after applying theme to prevent overwriting
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");

        Scene registerScene = new Scene(registerLayout, 500, 750);
        primaryStage.setScene(registerScene);
        primaryStage.show();

        animateFadeIn(registerLayout);
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
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();

        if (isDarkMode) {
            dialogPane.setStyle(
                    "-fx-background-color: #222;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;"
            );
            if (dialogPane.lookup(".content.label") != null)
                dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
            if (dialogPane.lookup(".header-panel") != null)
                dialogPane.lookup(".header-panel").setStyle("-fx-text-fill: white;");
            dialogPane.getButtonTypes().forEach(buttonType -> {
                Button button = (Button) dialogPane.lookupButton(buttonType);
                button.setStyle(
                        "-fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 5;"
                );
            });
        } else {
            dialogPane.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-text-fill: black;" +
                    "-fx-font-size: 14px;"
            );
            if (dialogPane.lookup(".content.label") != null)
                dialogPane.lookup(".content.label").setStyle("-fx-text-fill: black;");
            if (dialogPane.lookup(".header-panel") != null)
                dialogPane.lookup(".header-panel").setStyle("-fx-text-fill: black;");
            dialogPane.getButtonTypes().forEach(buttonType -> {
                Button button = (Button) dialogPane.lookupButton(buttonType);
                button.setStyle(
                        "-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-background-radius: 5;"
                );
            });
        }

        alert.showAndWait();
    }

    private void animateFadeIn(Pane pane) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), pane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void applyTheme(Pane pane) {
        if (isDarkMode) {
            pane.setStyle("-fx-background-color: #222;");
            applyThemeToChildren(pane, true);
        } else {
            pane.setStyle("-fx-background-color: #f4f4f4;");
            applyThemeToChildren(pane, false);
        }
    }

    private void applyThemeToChildren(Pane parent, boolean darkMode) {
        for (javafx.scene.Node node : parent.getChildren()) {
            // Recursively apply theme to child panes
            if (node instanceof Pane) {
                applyThemeToChildren((Pane) node, darkMode);
            } else if (node instanceof Label) {
                // Skip emailErrorLabel so its custom style remains
                if (node != emailErrorLabel) {
                    node.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + "; -fx-font-size: 16px;");
                }
            } else if (node instanceof TextField || node instanceof PasswordField) {
                node.setStyle(darkMode ?
                        "-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 16px;" :
                        "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 16px;");
            } else if (node instanceof Button) {
                node.setStyle(darkMode ?
                        "-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 5;" :
                        "-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 5;");
            } else if (node instanceof ToggleButton) {
                node.setStyle(darkMode ?
                        "-fx-background-color: #555; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;" :
                        "-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 5;");
            } else if (node instanceof CheckBox) {
                node.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + "; -fx-font-size: 14px;");
            }
        }
    }

    public static String getName() { return username; }
    public static String getPassword() { return password; }
    public static String getEmail() { return email; }
}
