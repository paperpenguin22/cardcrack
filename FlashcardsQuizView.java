import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class FlashcardsQuizView {
    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label("Select Subject for Flashcards");
        ListView<String> subjectsList = new ListView<>();

        for (String subject : account.getSubjects().keySet()) {
            subjectsList.getItems().add(subject);
        }

        root.getChildren().addAll(title, subjectsList);

        subjectsList.setOnMouseClicked(event -> {
            String selectedSubject = subjectsList.getSelectionModel().getSelectedItem();
            if (selectedSubject != null) {
                showUnits(stage, account, mainApp, selectedSubject);
            }
        });

        Button backButton = new Button("Back to Homepage");
        backButton.setOnAction(e -> mainApp.homePage());
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Flashcards & Quiz");
        stage.show();
    }

    private static void showUnits(Stage stage, Accounts account, Main mainApp, String subject) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label("Select Unit in " + subject);
        ListView<String> unitsList = new ListView<>();

        Map<String, List<String>> units = account.getSubjects().get(subject);
        if (units != null) {
            unitsList.getItems().addAll(units.keySet());
        }

        root.getChildren().addAll(title, unitsList);

        unitsList.setOnMouseClicked(event -> {
            String selectedUnit = unitsList.getSelectionModel().getSelectedItem();
            if (selectedUnit != null) {
                startFlashcards(stage, account, mainApp, subject, selectedUnit);
            }
        });

        Button backButton = new Button("Back to Subjects");
        backButton.setOnAction(e -> open(stage, account, mainApp));
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Units");
        stage.show();
    }

    private static void startFlashcards(Stage stage, Accounts account, Main mainApp, String subject, String unit) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        List<String> questions = account.getSubjects().get(subject).get(unit);
        if (questions == null || questions.isEmpty()) {
            root.getChildren().add(new Label("No questions found for " + subject + " > " + unit));
            Button backButton = new Button("Back to Units");
            backButton.setOnAction(e -> showUnits(stage, account, mainApp, subject));
            root.getChildren().add(backButton);

            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Flashcards");
            stage.show();
            return;
        }

        Label questionLabel = new Label();
        Button nextButton = new Button("Next Question");
        final int[] index = {0};

        questionLabel.setText(questions.get(index[0]));

        nextButton.setOnAction(e -> {
            index[0]++;
            if (index[0] >= questions.size()) {
                index[0] = 0; // Loop back to start
            }
            questionLabel.setText(questions.get(index[0]));
        });

        root.getChildren().addAll(questionLabel, nextButton);

        Button backButton = new Button("Back to Units");
        backButton.setOnAction(e -> showUnits(stage, account, mainApp, subject));
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Flashcards");
        stage.show();
    }
}
