import java.time.LocalDate;
import java.util.Objects;

public class CalendarEvent {
    private String subject;
    private String unit;
    private String type;  // Assignment, Unit Test, Exam...
    private String description;
    private LocalDate date;

    public CalendarEvent(String subject, String unit, String type, String description, LocalDate date) {
        this.subject = subject;
        this.unit = unit;
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public CalendarEvent(String subject, String unit, String type, String description) {
        this(subject, unit, type, description, null);  // date = null
    }

    // Getters
    public String getSubject() { return subject; }
    public String getUnit() { return unit; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return subject + " " + unit + " " + type + " " + description + " (" + date + ")";
    }

    // Parses a CalendarEvent from a save-string format with 5 pipe-separated parts
    public static CalendarEvent fromString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid calendar event data: " + line);
        }
        LocalDate parsedDate = LocalDate.parse(parts[4].trim());
        return new CalendarEvent(parts[0], parts[1], parts[2], parts[3], parsedDate);
    }

    // Converts the CalendarEvent into a save-string format (pipe-separated)
    public String toSaveString() {
        return subject + "|" + unit + "|" + type + "|" + description + "|" + date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarEvent)) return false;
        CalendarEvent that = (CalendarEvent) o;
        return Objects.equals(subject, that.subject) &&
               Objects.equals(unit, that.unit) &&
               Objects.equals(type, that.type) &&
               Objects.equals(description, that.description) &&
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, unit, type, description, date);
    }
}
