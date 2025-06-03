import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class Accounts{
    private String email;
    private String name;
    private String password;
    private Map <String, Map <String, List <String> > > subjects; //Nested Map - Subjects, Units, Questions
    private List <String> friends;
    private Map <LocalDate, CalendarEvent> calendar;
    
    static final String saveFile = "accounts.txt";

    public Accounts(String email, String name, String password){
        this.email = email;
        this.name = name;
        this.password = password
    }

    public void addQuestion(String subject, String unit, String question){
        subjects.putIfAbsent(subject, new HashMap<>());
        subjects.get(subjects).putIfAbsent(unit, new ArrayList<>());
        subjects.get(subjects).get(unit).add(question);
    }

    public void addToCalandar(LocalDate date, String subject, String unit, String type, String description){
        calandar.put(date, New CalendarEvent(subject, unit, type, description));
        //Accounts.addCalendarEvent(LocalDate.of(2025, 6, 20), "Subject", "Unit", "Type", "Description");
    }

    public static void save(Account account){
        List<String> lines = Files.exists(saveFile.toPath()) ? 
    }
}