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
    private Map<String, String> friendRequests = new HashMap<>(); // requesterEmail -> requesterName
    private Map<String, String> friendRequestsSent = new HashMap<>(); // recipientEmail -> recipientName
    private Map<LocalDate, List<CalendarEvent>> calendar = new HashMap<>();

    // Constructor
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

    // Add friend request received (incoming)
    public void addFriendRequest(String requesterEmail, String requesterName) {
        friendRequests.put(requesterEmail.toLowerCase(), requesterName);
    }

    // Add friend request sent (outgoing)
    public void addFriendRequestSent(String recipientEmail, String recipientName) {
        friendRequestsSent.put(recipientEmail.toLowerCase(), recipientName);
    }

    public boolean hasFriendRequestSentTo(String email) {
        return friendRequestsSent.containsKey(email.toLowerCase());
    }

    // Remove friend request received
    public void removeFriendRequest(String requesterEmail) {
        friendRequests.remove(requesterEmail.toLowerCase());
    }

    // Remove friend request sent
    public void removeFriendRequestSent(String recipientEmail) {
        friendRequestsSent.remove(recipientEmail.toLowerCase());
    }

    // Add friend to friend list
    public void addFriend(String friendEmail) {
        friendEmail = friendEmail.toLowerCase();
        if (!friends.contains(friendEmail)) {
            friends.add(friendEmail);
        }
    }

    // Remove friend from friend list
    public void removeFriend(String friendEmail) {
        friends.remove(friendEmail.toLowerCase());
    }

    // Add a calendar event
    public void addCalendarEvent(CalendarEvent event) {
        if (event == null || event.getDate() == null) return;
        calendar.computeIfAbsent(event.getDate(), k -> new ArrayList<>()).add(event);
    }

    // Accept friend request
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

    // Save account data to file
    public static void save(Accounts account) throws IOException {
        List<String> lines = Files.exists(saveFile) ? Files.readAllLines(saveFile) : new ArrayList<>();
        List<String> updated = new ArrayList<>();
        boolean replaced = false;

        for (int i = 0; i < lines.size();) {
            if (lines.get(i).equals(account.name)) {
                replaced = true;
                // Skip old block
                while (i < lines.size() && !lines.get(i).equals("exit")) {
                    i++;
                }
                i++; // skip "exit"

                // Write updated account
                updated.add(account.name);
                updated.add(account.email);
                updated.add(account.password);
                updated.add("Points:" + account.points);

                updated.add("Friends:");
                for (String f : account.friends) updated.add(f);
                updated.add("EndFriends");

                updated.add("FriendRequests:");
                for (var e : account.friendRequests.entrySet())
                    updated.add(e.getKey() + "|" + e.getValue());
                updated.add("EndFriendRequests");

                updated.add("FriendRequestsSent:");
                for (var e : account.friendRequestsSent.entrySet())
                    updated.add(e.getKey() + "|" + e.getValue());
                updated.add("EndFriendRequestsSent");

                // Save calendar events
                updated.add("CalendarEvents:");
                for (var entry : account.calendar.entrySet()) {
                    LocalDate date = entry.getKey();
                    for (CalendarEvent ev : entry.getValue()) {
                        updated.add(date.toString() + "|" + ev.toString());
                    }
                }
                updated.add("EndCalendarEvents");

                updated.add("exit");
            } else {
                updated.add(lines.get(i++));
            }
        }

        if (!replaced) {
            // New account block at end
            updated.add(account.name);
            updated.add(account.email);
            updated.add(account.password);
            updated.add("Points:" + account.points);

            updated.add("Friends:");
            for (String f : account.friends) updated.add(f);
            updated.add("EndFriends");

            updated.add("FriendRequests:");
            for (var e : account.friendRequests.entrySet())
                updated.add(e.getKey() + "|" + e.getValue());
            updated.add("EndFriendRequests");

            updated.add("FriendRequestsSent:");
            for (var e : account.friendRequestsSent.entrySet())
                updated.add(e.getKey() + "|" + e.getValue());
            updated.add("EndFriendRequestsSent");

            updated.add("CalendarEvents:");
            for (var entry : account.calendar.entrySet()) {
                LocalDate date = entry.getKey();
                for (CalendarEvent ev : entry.getValue()) {
                    updated.add(date.toString() + "|" + ev.toString());
                }
            }
            updated.add("EndCalendarEvents");

            updated.add("exit");
        }

        Files.write(saveFile, updated);
    }

    // Load account by email from file
    // Load account by name from file
    public static Accounts load(String name) throws IOException {
        if (!Files.exists(saveFile)) return null;
        name = name.trim();
        List<String> lines = Files.readAllLines(saveFile);

        int i = 0;
        while (i < lines.size()) {
            String currentName = lines.get(i++).trim(); // first line is name
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

                while (i < lines.size() && !lines.get(i).equals("exit")) {
                    String section = lines.get(i).trim();

                    if ("Friends:".equals(section)) {
                        i++;
                        while (i < lines.size() && !"EndFriends".equals(lines.get(i).trim())) {
                            friends.add(lines.get(i).trim());
                            i++;
                        }
                        i++; // skip EndFriends
                    } else if ("FriendRequests:".equals(section)) {
                        i++;
                        while (i < lines.size() && !"EndFriendRequests".equals(lines.get(i).trim())) {
                            String[] parts = lines.get(i).split("\\|", 2);
                            if (parts.length == 2) {
                                friendRequests.put(parts[0].toLowerCase(), parts[1]);
                            }
                            i++;
                        }
                        i++; // skip EndFriendRequests
                    } else if ("FriendRequestsSent:".equals(section)) {
                        i++;
                        while (i < lines.size() && !"EndFriendRequestsSent".equals(lines.get(i).trim())) {
                            String[] parts = lines.get(i).split("\\|", 2);
                            if (parts.length == 2) {
                                friendRequestsSent.put(parts[0].toLowerCase(), parts[1]);
                            }
                            i++;
                        }
                        i++; // skip EndFriendRequestsSent
                    } else if ("CalendarEvents:".equals(section)) {
                        i++;
                        while (i < lines.size() && !"EndCalendarEvents".equals(lines.get(i).trim())) {
                            String line = lines.get(i);
                            String[] parts = line.split("\\|", 2);
                            if (parts.length == 2) {
                                try {
                                    LocalDate date = LocalDate.parse(parts[0]);
                                    CalendarEvent ev = CalendarEvent.fromString(parts[1]);
                                    calendar.computeIfAbsent(date, k -> new ArrayList<>()).add(ev);
                                } catch (Exception e) {
                                    // ignore parse errors for calendar events
                                }
                            }
                            i++;
                        }
                        i++; // skip EndCalendarEvents
                    } else {
                        i++; // skip unknown lines or blank
                    }
                }

                // Skip the "exit" line at end of block
                if (i < lines.size() && lines.get(i).equals("exit")) i++;

                Accounts account = new Accounts(currentName, em, pwd);
                account.points = points;
                account.friends = friends;
                account.friendRequests = friendRequests;
                account.friendRequestsSent = friendRequestsSent;
                account.calendar = calendar;

                return account;
            } else {
                // Skip to next account block
                while (i < lines.size() && !lines.get(i).equals("exit")) {
                    i++;
                }
                if (i < lines.size() && lines.get(i).equals("exit")) i++;
            }
        }
        return null; // account not found
    }

}
