import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class Calendar{
  private static Stage stage;
  private static Map<LocalDate, CalendarEvent> eventMap;
  private static YearMonth currentMonth;

  public Calendar(Stage stage, Map<LocalDate, CalendarEvent> eventMap){
    this.stage = stage;
    this.eventMap = eventMap;
    this.currentMonth = YearMonth.now();
  }

  public static void calendar(){
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

    for(int day = 1; day <= daysInMonth; day++){
      VBox dayBox = new VBox();
      dayBox.setPadding(new Insets(5));
      dayBox.setStyle("-fx-border-color: black; -fx-min-width: 100px; -fx-min-height: 100px;");
      dayBox.getChildren().add(new Label(String.valueOf(day)));

      LocalDate thisDate = currentMonth.atDay(day);
      if (eventMap.containsKey(thisDate)){
        CalendarEvent event = eventMap.get(thisDate);
        Label eventLabel = new Label(event.getSubject() + " (" + event.getType() + ")");
        eventLabel.setStyle("-fx-font-size: 10;");
        dayBox.getChildren().add(eventLabel);
      }

      grid.add(dayBox, column, row);
      column++;
      if(column == 7){
        column = 0;
        row++;
      }
    }

    root.setCenter(grid);
    stage.setScene(new Scene(root, 800, 500));
    stage.show();
  }
}