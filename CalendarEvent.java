import java.util.Objects;

public class CalendarEvent{
  private String subject;
  private String unit;
  private String type;  //Assaignment, Unit Test, Exam...
  private String description;

  public CalendarEvent(String subject, String unit, String type, String description){
    this.subject = subject;
    this.unit = unit;
    this.type = type;
    this.description = description;
  }

  public String getSubject(){
    return subject;
  }

  public String getUnit(){
    return unit;
  }

  public String getType(){
    return type;
  }

  public String getDescription(){
    return description;
  }

  @Override
  public String toString() {
    return subject + " " + unit + " " + type + " " + description;
  }

  // Static method to parse a CalendarEvent from a saved string line
  public static CalendarEvent fromString(String line) {
    String[] parts = line.split("\\|", -1); // -1 to keep empty strings if any
    
    if (parts.length != 4) {
      throw new IllegalArgumentException("Invalid calendar event data: " + line);
    }
    
    return new CalendarEvent(parts[0], parts[1], parts[2], parts[3]);
  }
}