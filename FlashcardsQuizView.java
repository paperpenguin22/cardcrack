import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class FlashcardsQuizView {

    public static void open(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));
        Label title = new Label("Select Subject");
        ListView<String> subjectsList = new ListView<>();

        subjectsList.getItems().addAll(account.getSubjects().keySet());
        subjectsList.setOnMouseClicked(e -> {
            String subject = subjectsList.getSelectionModel().getSelectedItem();
            if (subject != null) showUnits(stage, account, mainApp, subject);
        });

        Button addButton = new Button("Add Question");
        addButton.setOnAction(e -> showAddForm(stage, account, mainApp));

        Button back = new Button("Back");
        back.setOnAction(e -> mainApp.homePage());

        root.getChildren().addAll(title, subjectsList, addButton, back);
        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Flashcards");
        stage.show();
    }

    private static void showUnits(Stage stage, Accounts account, Main mainApp, String subject) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));
        Label label = new Label("Select Unit in " + subject);
        ListView<String> unitList = new ListView<>(javafx.collections.FXCollections.observableArrayList(
                account.getSubjects().get(subject).keySet()));

        unitList.setOnMouseClicked(e -> {
            String unit = unitList.getSelectionModel().getSelectedItem();
            if (unit != null) startQuiz(stage, account, mainApp, subject, unit);
        });

        Button quizAll = new Button("Quiz Entire Subject");
        quizAll.setOnAction(e -> startSubjectQuiz(stage, account, mainApp, subject));

        Button back = new Button("Back");
        back.setOnAction(e -> open(stage, account, mainApp));

        root.getChildren().addAll(label, unitList, quizAll, back);
        stage.setScene(new Scene(root, 400, 450));
        stage.setTitle("Units");
        stage.show();
    }

    private static void startSubjectQuiz(Stage stage, Accounts account, Main mainApp, String subject) {
        List<String> allQuestions = new ArrayList<>();
        Map<String, List<String>> units = account.getSubjects().get(subject);
        if (units != null) {
            for (List<String> qList : units.values()) {
                allQuestions.addAll(qList);
            }
        }

        if (allQuestions.isEmpty()) {
            VBox root = new VBox(10, new Label("No questions in this subject."), new Button("Back"));
            ((Button) root.getChildren().get(1)).setOnAction(e -> showUnits(stage, account, mainApp, subject));
            root.setPadding(new javafx.geometry.Insets(10));
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
            return;
        }

        startQuiz(stage, account, mainApp, subject, "All Units", allQuestions);
    }

    private static void startQuiz(Stage stage, Accounts account, Main mainApp, String subject, String unit) {
        List<String> qaList = new ArrayList<>(account.getSubjects().get(subject).get(unit));
        startQuiz(stage, account, mainApp, subject, unit, qaList);
    }

    private static void startQuiz(Stage stage, Accounts account, Main mainApp, String subject, String unit, List<String> qaList) {
        if (qaList == null || qaList.isEmpty()) {
            VBox root = new VBox(10, new Label("No questions in this unit."), new Button("Back"));
            ((Button) root.getChildren().get(1)).setOnAction(e -> showUnits(stage, account, mainApp, subject));
            root.setPadding(new javafx.geometry.Insets(10));
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
            return;
        }

        Collections.shuffle(qaList);
        Iterator<String> iter = qaList.iterator();
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));
        Label qLabel = new Label();
        TextField input = new TextField();
        Label feedback = new Label();
        Button submit = new Button("Submit");

        final long[] start = {System.currentTimeMillis()};
        final String[] currentQA = {iter.next()};
        qLabel.setText("Q: " + currentQA[0].split("\\|")[0]);

        submit.setOnAction(e -> {
            long elapsed = System.currentTimeMillis() - start[0];
            String userAnswer = input.getText().trim();
            String actualAnswer = currentQA[0].split("\\|").length > 1 ? currentQA[0].split("\\|")[1] : "";

            if (userAnswer.equalsIgnoreCase(actualAnswer)) {
                int score = Math.max(1, 10 - (int) (elapsed / 1000));
                int bonus = new Random().nextInt(5) + score;
                account.addPoints(bonus);
                feedback.setText("Correct! +" + bonus + " points. Total: " + account.getPoints());

                try {
                    Accounts.save(account);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    feedback.setText("Error saving points.");
                }
            } else {
                feedback.setText("Wrong. Correct answer: " + actualAnswer);
            }

            if (iter.hasNext()) {
                currentQA[0] = iter.next();
                qLabel.setText("Q: " + currentQA[0].split("\\|")[0]);
                input.clear();
                start[0] = System.currentTimeMillis();
            } else {
                submit.setDisable(true);
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> showUnits(stage, account, mainApp, subject));

        root.getChildren().addAll(qLabel, input, submit, feedback, back);
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Quiz: " + subject + " - " + unit);
        stage.show();
    }

    private static void showAddForm(Stage stage, Accounts account, Main mainApp) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));
        TextField subject = new TextField();
        subject.setPromptText("Subject");
        TextField unit = new TextField();
        unit.setPromptText("Unit");
        TextField question = new TextField();
        question.setPromptText("Question");
        TextField answer = new TextField();
        answer.setPromptText("Answer");

        Button submit = new Button("Add");
        Label status = new Label();

        submit.setOnAction(e -> {
            if (!subject.getText().isEmpty() && !unit.getText().isEmpty() &&
                !question.getText().isEmpty() && !answer.getText().isEmpty()) {
                account.addQuestion(
                    subject.getText().trim(),
                    unit.getText().trim(),
                    question.getText().trim(),
                    answer.getText().trim()
                );
                try {
                    Accounts.save(account);
                    status.setText("Added successfully!");
                    subject.clear();
                    unit.clear();
                    question.clear();
                    answer.clear();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    status.setText("Failed to save question.");
                }
            } else {
                status.setText("Fill all fields!");
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> open(stage, account, mainApp));

        root.getChildren().addAll(new Label("Add Flashcard:"), subject, unit, question, answer, submit, status, back);
        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Add Question");
        stage.show();
    }
}