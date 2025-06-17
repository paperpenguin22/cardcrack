import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.List;

public class SubjectsView {
    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label("Subjects");
        ListView<String> subjectsList = new ListView<>();

        // Load all subjects from account
        for (String subject : account.getSubjects().keySet()) {
            subjectsList.getItems().add(subject);
        }

        root.getChildren().addAll(title, subjectsList);

        // When user clicks a subject, show units for that subject
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
        stage.setTitle("Subjects");
        stage.show();
    }

    private static void showUnits(Stage stage, Accounts account, Main mainApp, String subject) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label("Units in " + subject);
        ListView<String> unitsList = new ListView<>();

        Map<String, List<String>> units = account.getSubjects().get(subject);
        if (units != null) {
            for (String unit : units.keySet()) {
                unitsList.getItems().add(unit);
            }
        }

        root.getChildren().addAll(title, unitsList);

        unitsList.setOnMouseClicked(event -> {
            String selectedUnit = unitsList.getSelectionModel().getSelectedItem();
            if (selectedUnit != null) {
                showQuestions(stage, account, mainApp, subject, selectedUnit);
            }
        });

        Button backButton = new Button("Back to Subjects");
        backButton.setOnAction(e -> open(stage, account, mainApp));
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Units");
        stage.show();
    }

    private static void showQuestions(Stage stage, Accounts account, Main mainApp, String subject, String unit) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label title = new Label("Questions in " + subject + " > " + unit);
        ListView<String> questionsList = new ListView<>();

        List<String> questions = account.getSubjects().get(subject).get(unit);
        if (questions != null) {
            questionsList.getItems().addAll(questions);
        }

        root.getChildren().addAll(title, questionsList);

        Button backButton = new Button("Back to Units");
        backButton.setOnAction(e -> showUnits(stage, account, mainApp, subject));
        root.getChildren().add(backButton);

        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Questions");
        stage.show();
    }
}
