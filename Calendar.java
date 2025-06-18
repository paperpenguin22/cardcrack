import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.*;

public class Calendar {
    private Stage stage;
    private Accounts account;
    private Map<LocalDate, List<CalendarEvent>> eventMap;
    private YearMonth currentMonth;
    private GridPane calendarGrid;
    private Label monthLabel;

    public Calendar(Stage stage, String username) {
        this.stage = stage;
        try {
            this.account = Accounts.load(username);
            this.eventMap = account.getCalendar();
        } catch (Exception e) {
            e.printStackTrace();
            this.account = null;
            this.eventMap = new HashMap<>();
        }
        currentMonth = YearMonth.now();
    }

    public void showCalendar() {
        stage.setTitle("Calendar - " + currentMonth.getMonth() + " " + currentMonth.getYear());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        monthLabel = new Label(currentMonth.getMonth() + " " + currentMonth.getYear());
        monthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

        reloadAccountAndEvents();
        buildCalendarGrid(currentMonth);

        Button prevMonth = new Button("<");
        Button nextMonth = new Button(">");

        prevMonth.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
            reloadAccountAndEvents();
            buildCalendarGrid(currentMonth);
        });

        nextMonth.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
            reloadAccountAndEvents();
            buildCalendarGrid(currentMonth);
        });

        Button backToHome = new Button("Back to Home");
        backToHome.setOnAction(e -> Main.homePage());

        HBox navRow = new HBox(10, prevMonth, nextMonth, backToHome);
        navRow.setPadding(new Insets(5));

        VBox topLayout = new VBox(10, monthLabel, navRow);
        topLayout.setPadding(new Insets(10));

        VBox centerLayout = new VBox(10, calendarGrid);
        centerLayout.setPadding(new Insets(10));

        root.setTop(topLayout);
        root.setCenter(centerLayout);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void reloadAccountAndEvents() {
        try {
            if (account != null) {
                account = Accounts.load(account.getName());
                eventMap = account.getCalendar();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildCalendarGrid(YearMonth month) {
        calendarGrid.getChildren().clear();

        DayOfWeek[] daysOfWeek = DayOfWeek.values();
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i].toString().substring(0, 3));
            dayLabel.setStyle("-fx-font-weight: bold;");
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstOfMonth = month.atDay(1);
        int firstDayColumn = (firstOfMonth.getDayOfWeek().getValue() + 6) % 7;
        int daysInMonth = month.lengthOfMonth();

        int col = firstDayColumn;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = month.atDay(day);

            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setMinSize(100, 100);
            dayBtn.setWrapText(true);
            dayBtn.setStyle("-fx-border-color: black; -fx-font-size: 14; -fx-text-alignment: center;");

            List<CalendarEvent> events = eventMap.getOrDefault(currentDate, Collections.emptyList());
            if (!events.isEmpty()) {
                dayBtn.setText(day + "\nEvents: " + events.size());
                dayBtn.setStyle(dayBtn.getStyle() + "-fx-background-color: lightblue;");
                dayBtn.setTooltip(new Tooltip(events.size() + " event(s)"));
            }

            final LocalDate thisDate = currentDate;
            dayBtn.setOnAction(e -> showDayEvents(thisDate));

            calendarGrid.add(dayBtn, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void showDayEvents(LocalDate date) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Events on " + date);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

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
            } else {
                showAlert("Please select an event to remove.");
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
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");

        TextField unitField = new TextField();
        unitField.setPromptText("Unit");

        TextField typeField = new TextField();
        typeField.setPromptText("Type (e.g., Test, Assignment)");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(4);

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

    public void showCalendarForDate(LocalDate date) {
        showDayEvents(date);
    }

    public void showMonth(LocalDate targetDate) {
        this.currentMonth = YearMonth.from(targetDate);
        showCalendar();
    }
}