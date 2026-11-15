import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Provides account management operations and authentication utilities.
 * 
 * @author Darren
 */
public final class AccountManager {

	/**
	 * Defines whether authentication is checking a password or a security answer.
	 * 
	 * @author Vic
	 */
	public enum AuthType {

		// Verify identity using password
		PASSWORD,

		// Verify identity using security question answer
		SECURITY_ANSWER
	}

	// Username of signed in user. null when no one is signed in.
	private static String currentUser = null;

	// PBKDF2 configuration constants
	private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int HASH_ITERATIONS = 65536;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_BYTES = 16;

	/**
	 * Private constructor that prevents creation of an AccountManager object.
	 * 
	 * @author Aarush
	 */
	private AccountManager() {

	}

	/**
	 * Registers a user, setting up their username, password, and security
	 * question and answer.
	 * 
	 * @param username The username the user wants to create.
	 * @param password The plaintext password for the account.
	 * @param question The security question the user chooses.
	 * @param answer   The plaintext answer to the security question.
	 * @return True if the user was successfully registered.
	 * 
	 * @author Vic
	 */
	public static boolean registerUser(String username, String password, SecurityQuestion question, String answer) {

		// Unsure of whether I need to validate the enum itself for if null or not.

		// Validates not null/empty. 
		// Subject to change depending on integration team's decisions.
		String[] strParams = {username, password, answer};

		for (int i = 0; i < strParams.length; i++) {
			String oldArg = strParams[i];
			String newArg = validateString(oldArg);
			if (newArg == null){
				return false;
			}
			strParams[i] = newArg;
		}

		// Validation loop from validation team. 
		// Subject to change depending on integration team's decisions.
		String newUsername = strParams[0];
		String newPassword = strParams[1];
		String newAnswer = strParams[2];

		if (!validationValidateUserName(username))
			return false;
		if (!validationValidatePassword(password))
			return false;
		if (!validationValidateAnswer(answer))
			return false;

		// Verifies that this username does not exist yet.
		if (verifyUsername(newUsername))
			return false;

		// Hashes the necessary text
		String hashedPassword = hashPlaintext(newPassword);
		String hashedAnswer = hashPlaintext(newAnswer);

		// Creates user in persistent record, or fails and stops this operation.
		if (!storageSaveUser(newUsername, hashedPassword, question.getQuestionText(), hashedAnswer))
			return false;

		return true;
	}

	/**
	 * Removes the currently signed-in user account after verifying both their
	 * password and security answer.
	 * 
	 * @param password       The plaintext password to verify.
	 * @param securityAnswer The plaintext security answer to verify.
	 * @return True if the user account was successfully deleted.
	 * 
	 * @author Vic
	 */
	public static boolean deleteUser(String password, String securityAnswer) {

		// Validates not null/empty. 
		// Subject to change depending on integration team's decisions.
		String username = getCurrentUser();
		String[] strParams = {username, password, securityAnswer};

		for (int i = 0; i < strParams.length; i++) {
			String oldArg = strParams[i];
			String newArg = validateString(oldArg);
			if (newArg == null){
				return false;
			}
			strParams[i] = newArg;
		}

		String newUsername = strParams[0];
		String newPassword = strParams[1];
		String newAnswer = strParams[2];

		if (!verifyIdentity(newUsername, newPassword, AuthType.PASSWORD))
			return false;
		if (!verifyIdentity(newUsername, newAnswer, AuthType.SECURITY_ANSWER))
			return false;

		if (username == null) {
			return false;
		}

		return false;
	}

	/**
	 * Signs in a user and starts their session if credentials match.
	 * 
	 * @param username The username of the account.
	 * @param password The plaintext password to verify.
	 * @return True if the user was successfully signed in.
	 * 
	 * @author Darren
	 */
	public static boolean signIn(String username, String password) {
		
		// DO NOT FORGET TO IMPORT VALIDATION IN THE STUBS WHEN VALIDATE IS DONE.
	
		// This will check false for the validation teams methods
		if (validateUsername(username) == false || validatePassword(password) == false) {
			return false;
		}
		
		// We will be trimming personally since they aren't working with the DB, they are only checking if it works or not.
		String validatedUser = username.trim();
		String validatedPass = password.trim();
		
		// 
		if (!verifyIdentity(validatedUser, validatedPass, AuthType.PASSWORD)) {
			return false;
		}
		
		currentUser = validatedUser;
		return true;
	}

	/**
	 * Ends the current user session.
	 * 
	 * @author Darren
	 */
	public static void signOut() {
		currentUser = null;
	}

	/**
	 * Changes a user's password after verifying their security answer.
	 * 
	 * @param username    The username of the account.
	 * @param answer      The plaintext security answer to verify identity.
	 * @param newPassword The new plaintext password to set.
	 * @return True if the password was successfully changed.
	 * 
	 * @author Darren
	 */
	public static boolean changePassword(String username, String answer, String newPassword) {
		return false;
	}

	/**
	 * Updates the security question and answer for the signed-in user after
	 * verifying their password.
	 * 
	 * @param password    The plaintext password to verify identity.
	 * @param newQuestion The new security question to set.
	 * @param newAnswer   The new plaintext security answer to set.
	 * @return True if the security setup was successfully updated.
	 * 
	 * @author Darren
	 */
	public static boolean changeSecuritySetup(String password, String newQuestion, String newAnswer) {
		String username = getCurrentUser();

		if (username == null) {
			return false;
		}

		return false;
	}

	/**
	 * Returns the security question for the currently signed-in user.
	 * 
	 * @return The security question, or null if no user is signed in.
	 * 
	 * @author Ronnie
	 */
	public static String getSecurityQuestion() {
		String username = getCurrentUser();

		if (username == null) {
			return null;
		}

		return null;
	}

	/**
	 * Returns the username of the currently signed-in user.
	 * 
	 * @return The username of the active session, or null if no user is signed in.
	 * 
	 * @author Ronnie
	 */
	public static String getCurrentUser() {
		return currentUser;
	}

	/**
	 * Hashes a plaintext password or answer using the PBKDF2 algorithm with a
	 * random salt.
	 * 
	 * @param plaintext The plaintext string to hash.
	 * @return The salted hash string, or null if input is invalid.
	 * 
	 * @author Aarush
	 * @author Vic
	 */
	static String hashPlaintext(String plaintext) {
		// Checks that the plaintext argument is not null or empty.
		if (plaintext == null || plaintext.isEmpty()) {
			return null;
		}

		try {
			// Generates a cryptographically secure random salt.
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[SALT_BYTES];
			random.nextBytes(salt);

			// Hashes the plaintext with PBKDF2 using the salt.
			KeySpec spec = new PBEKeySpec(plaintext.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
			byte[] hash = factory.generateSecret(spec).getEncoded();

			// Combines salt and hash into a string separated by a colon.
			return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return null;
		}
	}

	/**
	 * Checks if a plaintext string matches a stored PBKDF2 hash using constant-time
	 * comparison.
	 * 
	 * @param plaintext  The plaintext string to check.
	 * @param storedHash The stored hash to compare against.
	 * @return True if the plaintext matches the stored hash, false otherwise.
	 * 
	 * @author Aarush
	 * @author Vic
	 */
	static boolean comparePlaintextToHash(String plaintext, String storedHash) {
		// Checks that the arguments are not null or empty.
		if (plaintext == null || storedHash == null || plaintext.isEmpty() || storedHash.isEmpty()) {
			return false;
		}

		// Checks if both the salt and hash actually exist
		String[] parts = storedHash.split(":");
		if (parts.length != 2) {
			return false;
		}

		try {
			// Decodes the stored salt and hash from Base64.
			byte[] salt = Base64.getDecoder().decode(parts[0]);
			byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

			// Hashes the input plaintext using the stored salt to verify against expected
			// hash.
			KeySpec spec = new PBEKeySpec(plaintext.toCharArray(), salt, HASH_ITERATIONS, expectedHash.length * 8);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
			byte[] candidateHash = factory.generateSecret(spec).getEncoded();

			// Compares the generated hash with the stored hash using constant-time
			// comparison.
			return MessageDigest.isEqual(candidateHash, expectedHash);
		} catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			return false;
		}
	}

	/**
	 * Checks if a username exists in storage.
	 * 
	 * @param username The username to check.
	 * @return True if the username exists in storage, false otherwise.
	 * 
	 * @author Aarush
	 */
	static boolean verifyUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}
		return storageFindUser(username.trim());
	}

	/**
	 * Verifies a user's identity by checking their password or security answer
	 * against storage.
	 * 
	 * @param username  The username to check.
	 * @param plaintext The plaintext password or security answer.
	 * @param authType  The type of credential being checked.
	 * @return True if the credentials match, false otherwise.
	 * 
	 * @author Aarush
	 */
	static boolean verifyIdentity(String username, String plaintext, AuthType authType) {
		if (username == null || plaintext == null || authType == null) {
			return false;
		}

		if (!verifyUsername(username)) {
			return false;
		}

		// Calls storage stub to access the stored hash for this user.
		StoredUser user = storageGetUserInfo(username);
		if (user == null) {
			return false;
		}

		String storedHash;
		if (authType == AuthType.PASSWORD) {
			storedHash = user.hashedPassword();
		} else {
			storedHash = user.hashedAnswer();
		}

		return comparePlaintextToHash(plaintext, storedHash);
	}

	// TODO: Consider changing the return values to not return null but instead,
	// raise an error so that logic on "if null" does not have to be rewritten
	// every time this function is called to validate general string inputs.
	// ANOTHER consideration could be to write another helper function that accepts
	// any number of arguments through varargs and returns a string ArrayList 
	// that will raise an error to avoid rewriting the loop.
	// Subject to change depending on integration team's decisions.
	/**
	 * Checks that a string is not null, empty, or only whitespace, and returns
	 * the trimmed string.
	 * 
	 * @param input The string to validate.
	 * @return The trimmed string, or null if invalid.
	 * 
	 * @author Ronnie
	 */
	static String validateString(String input) {
		// Checks if the input string is null, empty, or whitespace-only.
		if (input == null || input.trim().isEmpty()) {
			return null;
		}
		return input.trim();
	}

	// ---------------- STORAGE STUBS NEEDED -----------------

	// Container for user credentials returned from storage.
	static record StoredUser(String hashedPassword, String question, String hashedAnswer) {
	}

	// Checks if a username exists in storage.
	private static boolean storageFindUser(String username) {
		return false;
	}

	// Gets user credentials and security question/answer from storage.
	private static StoredUser storageGetUserInfo(String username) {
		return null;
	}

	// Saves a new user record to storage.
	private static boolean storageSaveUser(String username, String hashedPassword,
			String question, String hashedAnswer) {
		return false;
	}

	// Updates the stored password for a user.
	private static boolean storageUpdatePass(String username, String hashedPassword) {
		return false;
	}

	// Updates the security question and answer for a user.
	private static boolean storageUpdateQnA(String username, String question,
			String hashedAnswer) {
		return false;
	}

	// Deletes a user record from storage.
	private static boolean storageDeleteUser(String username) {
		return false;
	}

	// --------------- VALIDATION STUBS NEEDED ---------------

	// Validates username requirements with validation team.
	private static boolean validationValidateUserName(String username) {
		return false;
	}

	// Validates password requirements with validation team.
	private static boolean validationValidatePassword(String password) {
		return false;
	}

	// Validates security answer requirements with validation team.
	private static boolean validationValidateAnswer(String answer) {
		return false;
	}
}
