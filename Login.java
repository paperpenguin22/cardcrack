import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

        try {
            FileInputStream input = new FileInputStream("unnamed.png");
            Image image = new Image(input);
            //Setting the image view
            ImageView imageView = new ImageView(image);
            //Setting the position of the image
            imageView.setX(1);
            imageView.setY(1);
            //setting the fit height and width of the image view
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            //Setting the preserve ratio of the image view
            imageView.setPreserveRatio(true);
            //Creating a Group object
            Group root = new Group(imageView);
           
        } catch (FileNotFoundException e) {
            System.err.println("Error: Image file not found: " + e.getMessage());
        }
        
    }

    public void showLoginWindow() {
        primaryStage.setTitle("Login");
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(200));

        // Load the image for the logo
        FileInputStream input;
        ImageView imageView = null;
        try {
            input = new FileInputStream("unnamed.png");
            Image image = new Image(input);
            imageView = new ImageView(image);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Image file not found: " + e.getMessage());
        }

        // Create a HBox to align the logo on the left
        HBox logoContainer = new HBox();
        logoContainer.setPadding(new Insets(10)); // Add some padding
        logoContainer.getChildren().add(imageView);

        // Username and password fields
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
            logoContainer, // Add the logo container here
            new Label("Username:"), userField,
            new Label("Password:"), passwordBox, loginButton, registerButton
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

        CheckBox showPasswordBox = new CheckBox("o");
        showPasswordBox.setStyle("-fx-font-size: 14px;");

        HBox passwordBox = new HBox(5, passwordStack, showPasswordBox);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label emailErrorLabel = new Label("Enter valid email");
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");
        emailErrorLabel.setVisible(false);

        // Initially disable register button
        registerButton.setDisable(true);

        // Function to check if all required fields are non-empty


        // Add email validation listener
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRegistration(userField.getText(), newValue, passField.getText(), registerButton);
            if (newValue.contains("@") && (newValue.contains(".C")||newValue.contains(".c"))) {
                emailField.setStyle("-fx-text-box-border: #cccccc; -fx-focus-color: #0093ff;");
                emailErrorLabel.setVisible(false);

            } else {
                emailField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
                emailErrorLabel.setVisible(true);
                registerButton.setDisable(true);
            }
        });

        userField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRegistration(newValue, emailField.getText(), passField.getText(), registerButton);
        });

        passField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRegistration(userField.getText(), emailField.getText(), newValue, registerButton);
        });

        // Toggle password visibility
        showPasswordBox.setOnAction(e -> {
            if (showPasswordBox.isSelected()) {
                passTextField.setText(passField.getText());
                passTextField.setVisible(true);
                passField.setVisible(false);
                validateRegistration(userField.getText(), emailField.getText(), passTextField.getText(), registerButton);
            } else {
                passField.setText(passTextField.getText());
                passField.setVisible(true);
                passTextField.setVisible(false);
                validateRegistration(userField.getText(), emailField.getText(), passField.getText(), registerButton);
            }
        });

        registerLayout.getChildren().addAll(
            new Label("Username:"), userField,
            new Label("Email:"), emailField, emailErrorLabel,
            new Label("Password:"), passwordBox,
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

    private void validateRegistration(String username, String email, String password, Button registerButton) {
        boolean isUsernameEmpty = username == null || username.trim().isEmpty();
        boolean isPasswordEmpty = password == null || password.trim().isEmpty();

        registerButton.setDisable(isUsernameEmpty || isPasswordEmpty || !isValidEmail(email));
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && (email.contains(".C") || email.contains(".c"));
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
