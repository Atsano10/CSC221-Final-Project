import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles persistent storage of user account credentials.
 * Passwords and security answers must be hashed before being passed here.
 *
 * @author 
 *  Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public final class AccountStorage {

    private static final Path ACCOUNT_FILE =
            Path.of("data", "accounts", "accounts.dat");

    public static record StoredUser(
            String hashedPassword, String question, String hashedAnswer) {
    }

    private AccountStorage() {
    }

    public static boolean findUser(String username) {
        if (username == null) {
            return false;
        }
        try {
            return readAllUsers().containsKey(username);
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    public static StoredUser getUserInfo(String username) {
        if (username == null) {
            return null;
        }
        try {
            return readAllUsers().get(username);
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean saveUser(String username, String hashedPassword,
            String question, String hashedAnswer) {
        if (username == null || hashedPassword == null
                || question == null || hashedAnswer == null) {
            return false;
        }
        try {
            Map<String, StoredUser> users = readAllUsers();
            if (users.containsKey(username)) {
                return false;
            }
            users.put(username,
                    new StoredUser(hashedPassword, question, hashedAnswer));
            writeAllUsers(users);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean updatePassword(String username, String hashedPassword) {
        if (username == null || hashedPassword == null) {
            return false;
        }
        try {
            Map<String, StoredUser> users = readAllUsers();
            StoredUser oldUser = users.get(username);
            if (oldUser == null) {
                return false;
            }
            users.put(username, new StoredUser(
                    hashedPassword, oldUser.question(), oldUser.hashedAnswer()));
            writeAllUsers(users);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean updateQuestionAndAnswer(String username,
            String question, String hashedAnswer) {
        if (username == null || question == null || hashedAnswer == null) {
            return false;
        }
        try {
            Map<String, StoredUser> users = readAllUsers();
            StoredUser oldUser = users.get(username);
            if (oldUser == null) {
                return false;
            }
            users.put(username, new StoredUser(
                    oldUser.hashedPassword(), question, hashedAnswer));
            writeAllUsers(users);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean deleteUser(String username) {
        if (username == null) {
            return false;
        }
        try {
            Map<String, StoredUser> users = readAllUsers();
            if (users.remove(username) == null) {
                return false;
            }
            writeAllUsers(users);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    private static Map<String, StoredUser> readAllUsers() throws IOException {
        Map<String, StoredUser> users = new HashMap<>();
        if (!Files.exists(ACCOUNT_FILE)) {
            return users;
        }

        for (String line : Files.readAllLines(ACCOUNT_FILE, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            String[] fields = line.split("\\t", -1);
            if (fields.length != 4) {
                throw new IOException("Invalid account record.");
            }

            String username = decodeField(fields[0]);
            String password = decodeField(fields[1]);
            String question = decodeField(fields[2]);
            String answer = decodeField(fields[3]);
            users.put(username, new StoredUser(password, question, answer));
        }
        return users;
    }

    private static void writeAllUsers(Map<String, StoredUser> users)
            throws IOException {
        Path directory = ACCOUNT_FILE.getParent();
        Files.createDirectories(directory);
        Path tempFile = Files.createTempFile(directory, "accounts", ".tmp");

        try {
            ArrayList<String> lines = new ArrayList<>();
            for (Map.Entry<String, StoredUser> entry : users.entrySet()) {
                StoredUser user = entry.getValue();
                lines.add(encodeField(entry.getKey()) + "\t"
                        + encodeField(user.hashedPassword()) + "\t"
                        + encodeField(user.question()) + "\t"
                        + encodeField(user.hashedAnswer()));
            }
            Files.write(tempFile, lines, StandardCharsets.UTF_8);

            try {
                Files.move(tempFile, ACCOUNT_FILE,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tempFile, ACCOUNT_FILE,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static String encodeField(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeField(String value) {
        return new String(Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8);
    }
}
