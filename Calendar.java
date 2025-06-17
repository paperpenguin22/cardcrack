import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class Calendar {
    private Stage stage;
    private Accounts account;
    private Map<LocalDate, List<CalendarEvent>> eventMap;
    private YearMonth currentMonth;

    public Calendar(Stage stage, Accounts account) {
        this.stage = stage;
        this.account = account;
        this.eventMap = account.getCalendar();
        this.currentMonth = YearMonth.now();
    }

    public void showCalendar() {
        try {
            // Reload account data to refresh calendar events
            this.account = Accounts.load(account.getName());
            this.eventMap = account.getCalendar();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setTitle("Calendar - " + currentMonth.getMonth() + " " + currentMonth.getYear());

        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(5);

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        int startColumn = firstOfMonth.getDayOfWeek().getValue() % 7;  // Sunday = 0

        int row = 0;
        int col = startColumn;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);

            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMinSize(100, 100);
            dayButton.setWrapText(true);
            dayButton.setStyle("-fx-border-color: black; -fx-font-size: 14; -fx-text-alignment: center;");

            List<CalendarEvent> events = eventMap.getOrDefault(date, Collections.emptyList());
            if (!events.isEmpty()) {
                dayButton.setText(day + "\nEvents: " + events.size());
            }

            final LocalDate thisDate = date;
            dayButton.setOnAction(e -> showDayEvents(thisDate));

            grid.add(dayButton, col, row);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        Button backToHome = new Button("Back to Home");
        backToHome.setOnAction(e -> Main.homePage());

        VBox centerLayout = new VBox(10, grid, backToHome);
        centerLayout.setPadding(new Insets(10));

        root.setCenter(centerLayout);

        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    private void showDayEvents(LocalDate date) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Events on " + date);
        ListView<String> eventListView = new ListView<>();

        List<CalendarEvent> events = eventMap.getOrDefault(date, new ArrayList<>());
        events.sort(Comparator.comparing(CalendarEvent::getSubject));

        for (CalendarEvent event : events) {
            String eventText = String.format("Subject: %s | Unit: %s | Type: %s | Description: %s",
                    event.getSubject(), event.getUnit(), event.getType(), event.getDescription());
            eventListView.getItems().add(eventText);
        }

        Button addBtn = new Button("Add Event");
        addBtn.setOnAction(e -> openAddEventScreen(date));

        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < events.size()) {
                events.remove(selectedIndex);
                if (events.isEmpty()) {
                    eventMap.remove(date);
                }
                try {
                    Accounts.save(account);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                showDayEvents(date);
            }
        });

        Button backBtn = new Button("Back to Calendar");
        backBtn.setOnAction(e -> showCalendar());

        HBox buttons = new HBox(10, addBtn, removeBtn, backBtn);
        root.getChildren().addAll(title, eventListView, buttons);

        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    private void openAddEventScreen(LocalDate date) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Add Event - " + date);
        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");

        TextField unitField = new TextField();
        unitField.setPromptText("Unit");

        TextField typeField = new TextField();
        typeField.setPromptText("Type (e.g., Test, Assignment)");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");

        Button addBtn = new Button("Add to Calendar");
        addBtn.setOnAction(e -> {
            String subject = subjectField.getText().trim();
            String unit = unitField.getText().trim();
            String type = typeField.getText().trim();
            String desc = descArea.getText().trim();

            if (subject.isEmpty()) {
                showAlert("Subject cannot be empty");
                return;
            }

            account.addToCalendar(date, subject, unit, type, desc);
            try {
                Accounts.save(account);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            showCalendar();
        });

        Button backBtn = new Button("Back to Day Events");
        backBtn.setOnAction(e -> showDayEvents(date));

        layout.getChildren().addAll(title, subjectField, unitField, typeField, descArea, addBtn, backBtn);

        stage.setScene(new Scene(layout, 400, 400));
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showMonth(LocalDate targetDate) {
        this.currentMonth = YearMonth.from(targetDate);
        showCalendar();
    }

  public void showCalendarForDate(LocalDate date) {
      showDayEvents(date);
  }

}
