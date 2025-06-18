import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class Accounts {
    public static Path saveFile = Path.of("accounts.txt");

    private String name;
    private String email;
    private String password;
    private int points;

    private List<String> friends = new ArrayList<>();
    private Map<String, String> friendRequests = new HashMap<>();
    private Map<String, String> friendRequestsSent = new HashMap<>();
    private Map<LocalDate, List<CalendarEvent>> calendar = new HashMap<>();

    // subject -> unit -> list of question|answer strings
    private Map<String, Map<String, List<String>>> subjects = new HashMap<>();

    public Accounts(String name, String email, String password) {
        this.name = name;
        this.email = email.toLowerCase();
        this.password = password;
        this.points = 0;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<String> getFriends() { return friends; }
    public Map<String, String> getFriendRequests() { return friendRequests; }
    public Map<String, String> getFriendRequestsSent() { return friendRequestsSent; }
    public int getPoints() { return points; }
    public Map<LocalDate, List<CalendarEvent>> getCalendar() { return calendar; }
    public Map<String, Map<String, List<String>>> getSubjects() { return subjects; }

    public void addQuestion(String subject, String unit, String question, String answer) {
        subjects.computeIfAbsent(subject, k -> new HashMap<>())
                .computeIfAbsent(unit, k -> new ArrayList<>())
                .add(question + "|" + answer);
    }

    public void addToCalendar(LocalDate date, String subject, String unit, String type, String description) {
        CalendarEvent newEvent = new CalendarEvent(subject, unit, type, description, date);
        calendar.computeIfAbsent(date, k -> new ArrayList<>()).add(newEvent);
    }

    public void addCalendarEvent(CalendarEvent event) {
        if (event == null || event.getDate() == null) return;
        calendar.computeIfAbsent(event.getDate(), k -> new ArrayList<>()).add(event);
    }

    public void addPoints(int pts) {
        points += pts;
    }

    public void addFriendRequest(String requesterEmail, String requesterName) {
        friendRequests.put(requesterEmail.toLowerCase(), requesterName);
    }

    public void addFriendRequestSent(String recipientEmail, String recipientName) {
        friendRequestsSent.put(recipientEmail.toLowerCase(), recipientName);
    }

    public boolean hasFriendRequestSentTo(String email) {
        return friendRequestsSent.containsKey(email.toLowerCase());
    }

    public void removeFriendRequest(String requesterEmail) {
        friendRequests.remove(requesterEmail.toLowerCase());
    }

    public void removeFriendRequestSent(String recipientEmail) {
        friendRequestsSent.remove(recipientEmail.toLowerCase());
    }

    public void addFriend(String friendEmail) {
        friendEmail = friendEmail.toLowerCase();
        if (!friends.contains(friendEmail)) {
            friends.add(friendEmail);
        }
    }

    public void removeFriend(String friendEmail) {
        friends.remove(friendEmail.toLowerCase());
    }

    public void acceptFriendRequest(String requesterEmail) throws IOException {
        requesterEmail = requesterEmail.toLowerCase();
        if (!friendRequests.containsKey(requesterEmail)) {
            throw new IllegalArgumentException("No friend request from " + requesterEmail);
        }

        Accounts requester = Accounts.load(requesterEmail);
        if (requester == null) {
            throw new IOException("Requester account not found.");
        }

        this.removeFriendRequest(requesterEmail);
        requester.removeFriendRequestSent(this.email);

        this.addFriend(requesterEmail);
        requester.addFriend(this.email);

        Accounts.save(this);
        Accounts.save(requester);
    }

    public static void save(Accounts account) throws IOException {
        List<String> lines = Files.exists(saveFile) ? Files.readAllLines(saveFile) : new ArrayList<>();
        List<String> updated = new ArrayList<>();
        boolean replaced = false;

        for (int i = 0; i < lines.size();) {
            if (lines.get(i).equals(account.name)) {
                replaced = true;
                while (i < lines.size() && !lines.get(i).equals("exit")) i++;
                i++; // skip exit

                writeAccountData(account, updated);
            } else {
                updated.add(lines.get(i++));
            }
        }

        if (!replaced) {
            writeAccountData(account, updated);
        }

        Files.write(saveFile, updated);
    }

    private static void writeAccountData(Accounts account, List<String> out) {
        out.add(account.name);
        out.add(account.email);
        out.add(account.password);
        out.add("Points:" + account.points);

        out.add("Friends:");
        for (String f : account.friends) out.add(f);
        out.add("EndFriends");

        out.add("FriendRequests:");
        for (var e : account.friendRequests.entrySet())
            out.add(e.getKey() + "|" + e.getValue());
        out.add("EndFriendRequests");

        out.add("FriendRequestsSent:");
        for (var e : account.friendRequestsSent.entrySet())
            out.add(e.getKey() + "|" + e.getValue());
        out.add("EndFriendRequestsSent");

        out.add("CalendarEvents:");
        for (var entry : account.calendar.entrySet()) {
            LocalDate date = entry.getKey();
            for (CalendarEvent ev : entry.getValue()) {
                out.add(date.toString() + "|" + ev.toString());
            }
        }
        out.add("EndCalendarEvents");

        out.add("Subjects:");
        for (var sub : account.subjects.entrySet()) {
            for (var unit : sub.getValue().entrySet()) {
                for (String qa : unit.getValue()) {
                    out.add(sub.getKey() + "|" + unit.getKey() + "|" + qa);
                }
            }
        }
        out.add("EndSubjects");

        out.add("exit");
    }

    public static Accounts load(String name) throws IOException {
        if (!Files.exists(saveFile)) return null;
        name = name.trim();
        List<String> lines = Files.readAllLines(saveFile);

        int i = 0;
        while (i < lines.size()) {
            String currentName = lines.get(i++).trim();
            if (i >= lines.size()) break;
            String em = lines.get(i++).trim();
            if (i >= lines.size()) break;
            String pwd = lines.get(i++).trim();

            if (currentName.equalsIgnoreCase(name)) {
                int points = 0;
                if (i < lines.size() && lines.get(i).startsWith("Points:")) {
                    points = Integer.parseInt(lines.get(i).substring(7).trim());
                    i++;
                }

                List<String> friends = new ArrayList<>();
                Map<String, String> friendRequests = new HashMap<>();
                Map<String, String> friendRequestsSent = new HashMap<>();
                Map<LocalDate, List<CalendarEvent>> calendar = new HashMap<>();
                Map<String, Map<String, List<String>>> subjects = new HashMap<>();

                while (i < lines.size() && !lines.get(i).equals("exit")) {
                    String section = lines.get(i).trim();

                    switch (section) {
                        case "Friends:":
                            i++;
                            while (i < lines.size() && !"EndFriends".equals(lines.get(i).trim())) {
                                friends.add(lines.get(i++).trim());
                            }
                            i++;
                            break;
                        case "FriendRequests:":
                            i++;
                            while (i < lines.size() && !"EndFriendRequests".equals(lines.get(i).trim())) {
                                String[] parts = lines.get(i++).split("\\|", 2);
                                if (parts.length == 2) friendRequests.put(parts[0], parts[1]);
                            }
                            i++;
                            break;
                        case "FriendRequestsSent:":
                            i++;
                            while (i < lines.size() && !"EndFriendRequestsSent".equals(lines.get(i).trim())) {
                                String[] parts = lines.get(i++).split("\\|", 2);
                                if (parts.length == 2) friendRequestsSent.put(parts[0], parts[1]);
                            }
                            i++;
                            break;
                        case "CalendarEvents:":
                            i++;
                            while (i < lines.size() && !"EndCalendarEvents".equals(lines.get(i).trim())) {
                                String[] parts = lines.get(i++).split("\\|", 2);
                                if (parts.length == 2) {
                                    try {
                                        LocalDate date = LocalDate.parse(parts[0]);
                                        calendar.computeIfAbsent(date, k -> new ArrayList<>()).add(CalendarEvent.fromString(parts[1]));
                                    } catch (Exception e) {}
                                }
                            }
                            i++;
                            break;
                        case "Subjects:":
                            i++;
                            while (i < lines.size() && !"EndSubjects".equals(lines.get(i).trim())) {
                                String[] parts = lines.get(i++).split("\\|", 3);
                                if (parts.length == 3) {
                                    subjects.computeIfAbsent(parts[0], k -> new HashMap<>())
                                            .computeIfAbsent(parts[1], k -> new ArrayList<>())
                                            .add(parts[2]);
                                }
                            }
                            i++;
                            break;
                        default:
                            i++;
                            break;
                    }
                }

                Accounts account = new Accounts(currentName, em, pwd);
                account.points = points;
                account.friends = friends;
                account.friendRequests = friendRequests;
                account.friendRequestsSent = friendRequestsSent;
                account.calendar = calendar;
                account.subjects = subjects;

                return account;
            } else {
                while (i < lines.size() && !lines.get(i).equals("exit")) i++;
                i++;
            }
        }
        return null;
    }
}
