import java.util.Objects;

public class CalendarEvent{
  private String subject;
  private String unit;
  private String type;  //Assaignment, Unit Test, Exam...
  private String description;

  public CalendarEvent(){
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
  
}