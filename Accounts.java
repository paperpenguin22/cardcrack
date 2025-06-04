import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class Accounts{
    private String email;
    private String name;
    private String password;
    private Map<String, Map<String, List<String>>> subjects; //Nested Map - Subjects, Units, Questions
    private List<String> friends;
    private Map<LocalDate, CalendarEvent> calendar;
    
    static final Path saveFile = Paths.get("accounts.txt");

    public Accounts(String email, String name, String password){
        this.email = email;
        this.name = name;
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

    public void addToCalandar(LocalDate date, String subject, String unit, String type, String description){
        calendar.put(date, new CalendarEvent(subject, unit, type, description));
        //Accounts.addCalendarEvent(LocalDate.of(2025, 6, 20), "Subject", "Unit", "Type", "Description");
    }

    public static void save(Accounts account){
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

        updated.add(account.email);
        updated.add(account.name);
        updated.add(account.password);

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

    public static Accounts load(String name, File saveFile){
        if(!saveFile.exists())
            return null;
        
        List<String> lines = Files.readAllLines(saveFile.toPath());
        for(int i = 0; i < lines.size(); i++){
            if(lines.get(i).equals(name)){
                String email = lines.get(++i);
                String password = lines.get(++i);
                Accounts account = new Accounts(name, email, password);
                i++;

                while(i < lines.size() && !lines.get(i).equals("exit")){
                    String line = lines.get(i);

                    if(line.startsWith("Subject: ")){
                        String subject = line.substring(9);    //"Subjects: " is 9 characters - it gets the word after
                        i++;
                        
                        while(i < lines.size() && lines.get(i).startsWith("Unit: ")){
                            String unit = lines.get(i++).substring(6);

                            while(i < lines.size() && lines.get(i).startsWith("Question: "))
                                account.addQuestion(subject, unit, lines.get(i++).substring(10));
                        }
                        continue;
                    }

                    if(line.startsWith("Test: ")){
                        String[] parts = line.substring(6).split("\\|", 2);
                        LocalDate date = LocalDate.parse(parts[0]);
                        CalendarEvent event = CalendarEvent.fromString(parts[1]);
                        account.calendar.put(date, event);
                    }
                    i++;
                }
                return account;
            }
        }
        return null;
    }

    public String getPassword() {
        return password;
    }
}