import java.time.LocalDate;
import java.util.Objects;

public class CalendarEvent{
  private String subject;
  private String unit;
  private String type;  //Assaignment, Unit Test, Exam...
  private String description;
  private LocalDate date;

  public CalendarEvent(String subject, String unit, String type, String description, LocalDate date){
    this.subject = subject;
    this.unit = unit;
    this.type = type;
    this.description = description;
    this.date = date;
  }

  public CalendarEvent(String subject, String unit, String type, String description) {
    this(subject, unit, type, description, null);  // sets date to null
  }

  public String getSubject(){return subject;}

  public String getUnit(){return unit;}

  public String getType(){return type;}

  public String getDescription(){return description;}

  public LocalDate getDate(){return date;}

  @Override
  public String toString() {
    return subject + " " + unit + " " + type + " " + description;
  }

  // Static method to parse a CalendarEvent from a saved string line
  public static CalendarEvent fromString(String line) {
    String[] parts = line.split("\\|", -1); // -1 to keep empty strings if any
    
    if (parts.length != 5) {
      throw new IllegalArgumentException("Invalid calendar event data: " + line);
    }
    
    return new CalendarEvent(parts[0], parts[1], parts[2], parts[3], LocalDate.parse(parts[4].trim()));
  }
}