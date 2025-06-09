import java.io.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public class Calendar {
  private static Stage stage;
  private static Map<LocalDate, CalendarEvent> eventMap;
  private static YearMonth currentMonth;

  public Calendar(Stage stage, Map<LocalDate, CalendarEvent> eventMap) {
    Calendar.stage = stage;
    Calendar.eventMap = eventMap;
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
        CalendarEvent event = eventMap.get(thisDate);
        dayButton.setText(day + "\n" + event.getSubject());
        dayButton.setStyle("-fx-font-size: 12; -fx-border-color: black; -fx-text-alignment: center;");
      }

      // On click, open the Add Question screen
      dayButton.setOnAction(e -> openAddQuestionScreen(thisDate));

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
      Accounts account = new Accounts(Login.getName(), Login.getEmail(), Login.getPassword());
      account.addToCalendar(selectedDate, subjectField.getText(), unitField.getText(), typeField.getText(), descArea.getText());
      try {
        Accounts.save(account);
      } catch (IOException a) {
        a.printStackTrace();
      }
    });

    layout.getChildren().addAll(title, subjectField, unitField, typeField, descArea, addBtn, backBtn);

    stage.setScene(new Scene(layout, 400, 400));
    stage.show();
  }
}
