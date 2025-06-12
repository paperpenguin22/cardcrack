import java.io.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.List;

public class Calendar {
  private static Stage stage;
  private static Map<LocalDate, List<CalendarEvent>> eventMap;
  private static YearMonth currentMonth;
  private static Accounts account;

  public Calendar(Stage stage, Accounts account) {
      Calendar.stage = stage;
      Calendar.account = account;
      Calendar.eventMap = account.getCalendar();
      Calendar.currentMonth = YearMonth.now();
  }


  public static void calendar() {
    stage.setTitle("Calendar");
    BorderPane root = new BorderPane();
    GridPane grid = new GridPane();

    grid.setPadding(new Insets(10));
    grid.setHgap(5);
    grid.setVgap(5);

    LocalDate firstOfMonth = currentMonth.atDay(1);
    int daysInMonth = currentMonth.lengthOfMonth();
    int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();

    int row = 0;
    int column = dayOfWeekValue % 7;

    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate thisDate = currentMonth.atDay(day);

      Button dayButton = new Button(String.valueOf(day));
      dayButton.setMinSize(100, 100);
      dayButton.setStyle("-fx-border-color: black; -fx-font-size: 14;");

      // Add event label if one exists
      if (eventMap.containsKey(thisDate)) {
          List<CalendarEvent> events = eventMap.get(thisDate);
          if (!events.isEmpty()) {
              String label = day + "\n" + events.get(0).getSubject();
              if (events.size() > 1) {
                  label += " +";
              }
              dayButton.setText(label);
              dayButton.setStyle("-fx-font-size: 12; -fx-border-color: black; -fx-text-alignment: center;");
          }
      }

      // On click, open the Add Question screen
      int date = day;
      dayButton.setOnAction(e -> calendarAction(date, thisDate));

      grid.add(dayButton, column, row);
      column++;
      if (column == 7) {
        column = 0;
        row++;
      }
    }

    root.setCenter(grid);
    stage.setScene(new Scene(root, 800, 500));
    stage.show();
  }

  private static void calendarAction(int date, LocalDate thisDate){
    VBox root = new VBox(10);
    root.setPadding(new Insets(10));
    
    Label title = new Label("Month" + date);

    ListView<CalendarEvent> eventListView = new ListView<>();
    List<CalendarEvent> events = account.getCalendar().getOrDefault(date, List.of());
    eventListView.getItems().addAll(events);

    Button removeBtn = new Button("Remove Event");
    removeBtn.setOnAction(e -> {
      CalendarEvent selected = eventListView.getSelectionModel().getSelectedItem();
      if(selected != null){
        account.getCalendar().get(date).remove(date);
        eventListView.getItems().remove(selected);
        try {
          Accounts.save(account);
        } catch (IOException a) {
          a.printStackTrace();
        }
      }
    });

    Button addQuestionBtn = new Button("Add Question");
    addQuestionBtn.setOnAction(e -> {
        openAddQuestionScreen(thisDate);
    });

    HBox buttons = new HBox(10, addQuestionBtn, removeBtn);

    root.getChildren().addAll(title, eventListView, buttons);
    
    stage.setScene(new Scene(root, 400, 400));
    stage.centerOnScreen();
    stage.show();
  }

  private static void openAddQuestionScreen(LocalDate selectedDate){
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));

    Label title = new Label("Add Question - " + selectedDate.toString());
    TextField subjectField = new TextField();
    subjectField.setPromptText("Subject");

    TextField unitField = new TextField();
    unitField.setPromptText("Unit");

    TextField typeField = new TextField();
    typeField.setPromptText("Type (e.g., Test, Assignment)");

    TextArea descArea = new TextArea();
    descArea.setPromptText("Description");

    Button backBtn = new Button("Back to Calendar");
    backBtn.setOnAction(e -> calendar());

    Button addBtn = new Button("Add to Calendar");
    addBtn.setOnAction(e -> {
      account.addToCalendar(selectedDate, subjectField.getText(), unitField.getText(), typeField.getText(), descArea.getText());
      try {
        Accounts.save(account);
      } catch (IOException a) {
        a.printStackTrace();
      }
      calendar();
    });

    layout.getChildren().addAll(title, subjectField, unitField, typeField, descArea, addBtn, backBtn);

    stage.setScene(new Scene(layout, 400, 400));
    stage.show();
  }
}
