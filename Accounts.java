import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Accounts{
    private String email;
    private String name;
    private String password;
    private Map<String, Map<String, List<String>>> subjects; //Nested Map - Subjects, Units, Questions
    private List<String> friends;
    private Map<LocalDate, CalendarEvent> calendar;
    
    static final Path saveFile = Paths.get("accounts.txt");

    public Accounts(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;

        this.subjects = new HashMap<>();
        this.friends = new ArrayList<>();
        this.calendar = new HashMap<>();
    }

    public void addQuestion(String subject, String unit, String question){
        subjects.putIfAbsent(subject, new HashMap<>());
        subjects.get(subject).putIfAbsent(unit, new ArrayList<>());
        subjects.get(subject).get(unit).add(question);
    }

    public void addToCalendar(LocalDate date, String subject, String unit, String type, String description){
        calendar.put(date, new CalendarEvent(subject, unit, type, description, date));
        //Accounts.addToCalendar(LocalDate.of(2025, 6, 20), "Subject", "Unit", "Type", "Description");
    }

    public static void save(Accounts account) throws IOException{
        //Compact if else statement
        List<String> lines = Files.exists(saveFile) ? Files.readAllLines(saveFile) : new ArrayList<>();

        List<String> updated = new ArrayList<>();    //Updated - new save file data
        boolean found = false;

        for(int i = 0; i < lines.size(); ){
            if(lines.get(i).equals(account.name)){
                found = true;
                while(i < lines.size() && !lines.get(i).equals("exit"))
                    i++;
                i++; //Skips "exit"
            } else {
                updated.add(lines.get(i++));
            }
        }

        updated.add(account.name);
        updated.add(account.email);
        updated.add(account.password);
        System.out.print("Updated");

        for(String subject: account.subjects.keySet()){
            updated.add("Subject: "  + subject);
            for(String unit: account.subjects.get(subject).keySet()){
                updated.add("Unit: " + unit);
                for(String q : account.subjects.get(subject).get(unit)){
                    updated.add("Question: " + q);
                }
            }
        }

        for (Map.Entry<LocalDate, CalendarEvent> entry : account.calendar.entrySet()) {
            updated.add("Test: " + entry.getKey() + "|" + entry.getValue());
        }

        updated.add("exit");
        Files.write(saveFile, updated);
    }

    public static Accounts load(String name, File saveFile) throws IOException {
        if (!saveFile.exists()) {
            return null;
        }
        List<String> lines = Files.readAllLines(saveFile.toPath());
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(name)) {
                String email = lines.get(++i);
                String password = lines.get(++i);
                Accounts account = new Accounts(name, email, password);
                i++;
                while (i < lines.size() && !lines.get(i).equals("exit")) {
                    String line = lines.get(i);

                    if (line.startsWith("Subject: ")) {
                        String subject = line.substring(9);
                        i++;
                        while (i < lines.size() && lines.get(i).startsWith("Unit: ")) {
                            String unit = lines.get(i++).substring(6);
                            while (i < lines.size() && lines.get(i).startsWith("Question: ")) {
                                account.addQuestion(subject, unit, lines.get(i++).substring(10));
                            }
                        }
                        continue;
                    }
                    if (line.startsWith("Test: ")) {
                        String[] parts = line.substring(6).split("\\|", -1);
                        if (parts.length < 2) {
                            System.err.println("Invalid calendar event data: " + line);
                                return null; // Return null instead of just return
                        }
                        LocalDate date;
                        try {
                            date = LocalDate.parse(parts[0].trim());
                        } catch (DateTimeParseException e) {
                            System.err.println("Invalid date format: " + parts[0]);
                            return null; // Return null instead of just return
                        }
                        String eventData = String.join("|", Arrays.copyOfRange(parts, 1, parts.length));
                        try {
                            CalendarEvent event = CalendarEvent.fromString(eventData);
                            account.addToCalendar(date, event.getSubject(), event.getUnit(), event.getType(), event.getDescription());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Error parsing calendar event: " + e.getMessage());
                        }
                    }
                        i++;
                }
                return account; // Return the populated account
            }
        }
        return null; // Return null if the account was not found
    }

    public String getName() {return name;}

    public String getPassword() {return password;}

    public String getEmail() {return email;}

    public Map<LocalDate, CalendarEvent> getCalendar() {return calendar;}
}