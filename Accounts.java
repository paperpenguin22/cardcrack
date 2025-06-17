import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Accounts {
    private String email;
    private String name;
    private String password;
    private Map<String, Map<String, List<String>>> subjects; // Nested Map - Subjects, Units, Questions
    private List<String> friends;
    private Map<String, List<String>> friendRequests;      // Received: senderEmail -> senderNames
    private Map<String, List<String>> friendRequestsSent;  // Sent: recipientEmail -> recipientNames (optional)
    private Map<LocalDate, List<CalendarEvent>> calendar;

    static final Path saveFile = Paths.get("accounts.txt");

    public Accounts(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;

        this.subjects = new HashMap<>();
        this.friends = new ArrayList<>();
        this.friendRequests = new HashMap<>();
        this.friendRequestsSent = new HashMap<>();
        this.calendar = new HashMap<>();
    }

    // Add question to subject/unit
    public void addQuestion(String subject, String unit, String question) {
        subjects.putIfAbsent(subject, new HashMap<>());
        subjects.get(subject).putIfAbsent(unit, new ArrayList<>());
        subjects.get(subject).get(unit).add(question);
    }

    public void addToCalendar(LocalDate date, String subject, String unit, String type, String description) {
        CalendarEvent newEvent = new CalendarEvent(subject, unit, type, description, date);
        List<CalendarEvent> events = calendar.getOrDefault(date, new ArrayList<>());
        events.add(newEvent);
        calendar.put(date, events);
    }

    // Friends management
    public List<String> getFriends() {
        return friends;
    }

    public boolean addFriend(String friendEmail) {
        if (!friends.contains(friendEmail)) {
            friends.add(friendEmail);
            return true;
        }
        return false;
    }

    public boolean removeFriend(String friendEmail) {
        return friends.remove(friendEmail);
    }

    // Received friend requests (from others)
    public Map<String, List<String>> getFriendRequests() {
        return friendRequests;
    }

    public void addFriendRequest(String senderEmail, String senderName) {
        friendRequests.putIfAbsent(senderEmail, new ArrayList<>());
        if (senderName != null && !senderName.isEmpty() && !friendRequests.get(senderEmail).contains(senderName)) {
            friendRequests.get(senderEmail).add(senderName);
        }
    }

    public void removeFriendRequest(String senderEmail) {
        friendRequests.remove(senderEmail);
    }

    public boolean hasFriendRequestFrom(String senderEmail) {
        return friendRequests.containsKey(senderEmail);
    }

    // Sent friend requests (requests this account sent to others)
    public Map<String, List<String>> getFriendRequestsSent() {
        return friendRequestsSent;
    }

    public void addFriendRequestSent(String recipientEmail, String recipientName) {
        friendRequestsSent.putIfAbsent(recipientEmail, new ArrayList<>());
        if (recipientName != null && !recipientName.isEmpty() && !friendRequestsSent.get(recipientEmail).contains(recipientName)) {
            friendRequestsSent.get(recipientEmail).add(recipientName);
        }
    }

    public void removeFriendRequestSent(String recipientEmail) {
        friendRequestsSent.remove(recipientEmail);
    }

    public boolean hasFriendRequestSentTo(String recipientEmail) {
        return friendRequestsSent.containsKey(recipientEmail);
    }

    // ---- Save and Load ----
    public static void save(Accounts account) throws IOException {
        List<String> lines = Files.exists(saveFile) ? Files.readAllLines(saveFile) : new ArrayList<>();

        List<String> updated = new ArrayList<>();
        boolean found = false;

        for (int i = 0; i < lines.size();) {
            if (lines.get(i).equals(account.name)) {
                found = true;
                while (i < lines.size() && !lines.get(i).equals("exit"))
                    i++;
                i++; // skip "exit"
            } else {
                updated.add(lines.get(i++));
            }
        }

        updated.add(account.name);
        updated.add(account.email);
        updated.add(account.password);

        // Save friends list
        updated.add("Friends:");
        for (String friendEmail : account.friends) {
            updated.add(friendEmail);
        }
        updated.add("EndFriends");

        // Save friend requests (received)
        updated.add("FriendRequests:");
        for (Map.Entry<String, List<String>> entry : account.friendRequests.entrySet()) {
            String senderEmail = entry.getKey();
            List<String> senderNames = entry.getValue();
            String namesStr = String.join(",", senderNames);
            updated.add(senderEmail + "|" + namesStr);
        }
        updated.add("EndFriendRequests");

        // Save friend requests sent
        updated.add("FriendRequestsSent:");
        for (Map.Entry<String, List<String>> entry : account.friendRequestsSent.entrySet()) {
            String recipientEmail = entry.getKey();
            List<String> recipientNames = entry.getValue();
            String namesStr = String.join(",", recipientNames);
            updated.add(recipientEmail + "|" + namesStr);
        }
        updated.add("EndFriendRequestsSent");

        // Save subjects, units, questions
        for (String subject : account.subjects.keySet()) {
            updated.add("Subject: " + subject);
            for (String unit : account.subjects.get(subject).keySet()) {
                updated.add("Unit: " + unit);
                for (String q : account.subjects.get(subject).get(unit)) {
                    updated.add("Question: " + q);
                }
            }
        }

        // Save calendar events
        for (Map.Entry<LocalDate, List<CalendarEvent>> entry : account.calendar.entrySet()) {
            for (CalendarEvent event : entry.getValue()) {
                updated.add("Test: " + event.toSaveString());
            }
        }

        updated.add("exit");
        Files.write(saveFile, updated);
    }

    public static Accounts load(String name) throws IOException {
        if (!Files.exists(saveFile)) {
            return null;
        }
        List<String> lines = Files.readAllLines(saveFile);

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(name)) {
                String email = lines.get(++i);
                String password = lines.get(++i);
                Accounts account = new Accounts(name, email, password);
                i++;
                boolean loadingFriends = false;
                boolean loadingFriendRequests = false;
                boolean loadingFriendRequestsSent = false;

                while (i < lines.size() && !lines.get(i).equals("exit")) {
                    String line = lines.get(i);

                    if (line.equals("Friends:")) {
                        loadingFriends = true;
                        loadingFriendRequests = false;
                        loadingFriendRequestsSent = false;
                        i++;
                        continue;
                    }

                    if (line.equals("EndFriends")) {
                        loadingFriends = false;
                        i++;
                        continue;
                    }

                    if (line.equals("FriendRequests:")) {
                        loadingFriendRequests = true;
                        loadingFriends = false;
                        loadingFriendRequestsSent = false;
                        i++;
                        continue;
                    }

                    if (line.equals("EndFriendRequests")) {
                        loadingFriendRequests = false;
                        i++;
                        continue;
                    }

                    if (line.equals("FriendRequestsSent:")) {
                        loadingFriendRequestsSent = true;
                        loadingFriends = false;
                        loadingFriendRequests = false;
                        i++;
                        continue;
                    }

                    if (line.equals("EndFriendRequestsSent")) {
                        loadingFriendRequestsSent = false;
                        i++;
                        continue;
                    }

                    if (loadingFriends) {
                        account.friends.add(line.trim());
                        i++;
                        continue;
                    }

                    if (loadingFriendRequests) {
                        // Format: senderEmail|name1,name2,...
                        String[] parts = line.split("\\|", 2);
                        String senderEmail = parts[0].trim();
                        List<String> senderNames = new ArrayList<>();
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            senderNames = Arrays.asList(parts[1].split(","));
                        }
                        account.friendRequests.put(senderEmail, new ArrayList<>(senderNames));
                        i++;
                        continue;
                    }

                    if (loadingFriendRequestsSent) {
                        // Format: recipientEmail|name1,name2,...
                        String[] parts = line.split("\\|", 2);
                        String recipientEmail = parts[0].trim();
                        List<String> recipientNames = new ArrayList<>();
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            recipientNames = Arrays.asList(parts[1].split(","));
                        }
                        account.friendRequestsSent.put(recipientEmail, new ArrayList<>(recipientNames));
                        i++;
                        continue;
                    }

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
                        if (parts.length < 5) {
                            System.err.println("Invalid calendar event data: " + line);
                            return null;
                        }

                        String subject = parts[0].trim();
                        String unit = parts[1].trim();
                        String type = parts[2].trim();
                        String description = parts[3].trim();
                        LocalDate date;

                        try {
                            date = LocalDate.parse(parts[4].trim());
                        } catch (DateTimeParseException e) {
                            System.err.println("Invalid date format: " + parts[4]);
                            return null;
                        }

                        // Add to calendar
                        account.addToCalendar(date, subject, unit, type, description);
                    }
                    i++;
                }
                return account;
            }
        }
        return null;
    }

    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public Map<LocalDate, List<CalendarEvent>> getCalendar() { return calendar; }
    public Map<String, Map<String, List<String>>> getSubjects() { return subjects; }
}