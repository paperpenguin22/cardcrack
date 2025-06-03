import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.time.LocalDate;

public class Accounts{
    private String email;
    private String name;
    private String password;
    private Map <String, Map <String, List <String> > > subjects; //Nested Map - Subjects, Units, Questions
    private List <String> friends;
    private Map <LocalDate, TestInfo> calendar;
    
    static final String saveFile = "accounts.txt";

    public static void save(Account account){
        
    }
}