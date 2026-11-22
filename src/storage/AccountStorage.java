package storage;

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
/**
     * Private constructor that prevents creation of an AccountStorage object.
     *
     * @author Ivan Chukharev
     */
    private AccountStorage() {
    }
 /**
     * Checks whether an account with the given username exists.
     *
     * @param username account username
     * @return true if the account exists; false if it does not or the account
     *         file cannot be read
     *
     * @author Ivan Chukharev
     */
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
 /**
     * Gets the stored credentials for a user.
     *
     * @param username account username
     * @return the user's stored credentials, or null if the user does not exist
     *         or the account file cannot be read
     *
     * @author Ivan Chukharev
     */
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
 /**
     * Saves a new user account. Existing accounts are never overwritten.
     *
     * @param username account username
     * @param hashedPassword the user's hashed password
     * @param question the user's security question
     * @param hashedAnswer the user's hashed security answer
     * @return true if the account was saved; false if the username is taken,
     *         an argument is null, or the account file cannot be written
     *
     * @author Ivan Chukharev
     */
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
  /**
     * Replaces a user's stored password hash.
     *
     * @param username account username
     * @param hashedPassword the new hashed password
     * @return true if the password was updated; false if the user does not
     *         exist, an argument is null, or the account file cannot be written
     *
     * @author van Chukharev
     */
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
 /**
     * Replaces a user's security question and hashed answer.
     *
     * @param username account username
     * @param question the new security question
     * @param hashedAnswer the new hashed security answer
     * @return true if the question and answer were updated; false if the user
     *         does not exist, an argument is null, or the account file cannot
     *         be written
     *
     * @author Ivan Chukharev
     */
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
/**
     * Removes a user's account record. Budget files are deleted separately by
     * {@link BudgetStorage#deleteAllBudgets(String)}.
     *
     * @param username account username
     * @return true if the account was removed; false if the user does not
     *         exist or the account file cannot be written
     *
     * @author Ivan Chukharev
     */
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
/**
     * Reads every account from the account file.
     *
     * @return map of usernames to stored credentials; empty if the file does
     *         not exist yet
     * @throws IOException if the file cannot be read or a record is malformed
     *
     * @author Ivan Chukharev
     */    

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
 /**
     * Writes every account to the account file, replacing its contents.
     * Writes to a temporary file first so a failed write cannot corrupt the
     * existing file.
     *
     * @param users map of usernames to stored credentials
     * @throws IOException if the file cannot be written
     *
     * @author Ivan Chukharev
     */
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
 /**
     * Encodes a field as URL-safe Base64 so tabs and line breaks in it cannot
     * break the file format.
     *
     * @param value the text to encode
     * @return the encoded text
     *
     * @author Ivan Chukharev
     */
    private static String encodeField(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
 /**
     * Decodes a field written by {@link #encodeField(String)}.
     *
     * @param value the encoded text
     * @return the original text
     * @throws IllegalArgumentException if the text is not valid Base64
     *
     * @author Ivan Chukharev
     */
    private static String decodeField(String value) {
        return new String(Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8);
    }
}
