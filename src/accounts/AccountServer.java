/**
 * Provides account management and auth utilities.
 * 
 * @author Darren
 */
public final class AccountServer {

	/**
	 * Private constructor that prevents creation of an AccountServer object.
	 * 
	 * @author Darren
	 */
	private AccountServer() {

	}

	/**
	 * Authenticates a user and makes a connection when credentials are validated.
	 * 
	 * @param username Unique username the user chose to sign in with
	 * @param password Unique password associated with the username
	 * @author Darren
	 */
	public static boolean signIn(String username, String password) {
		return false;
	}

	/**
	 * Ends user session without deleting information.
	 * 
	 * @author Darren
	 */
	public static void signOut() {

	}

	/**
	 * Registers a user, setting up their username, password, and security
	 * question/answer.
	 * 
	 * @param username The username the user has created.
	 * @param password The password that the user wants to set for their account.
	 * @return True if the user's account was successfully registered.
	 * 
	 * @author Vic
	 */
	public static boolean registerUser(String username, String password) {
		return false;
	}

	/**
	 * Removes a user account from our service if that is the user account signed
	 * in.
	 * 
	 * @param username The username the user has created.
	 * @return True if the user's account was successfully deleted.
	 * 
	 * @author Vic
	 */
	public static boolean deleteUser(String username) {
		return false;
	}

	/**
	 * Returns the user account that is currently signed into the session.
	 * 
	 * @return The UserAccount of the currently signed-in user, or null if no user
	 *         is signed in.
	 * 
	 * @author Aarush
	 */
	public static UserAccount getCurrentUser() {
		return null;
	}

	/**
	 * Hashes a plaintext password with a random salt using the PBKDF2 algorithm.
	 * 
	 * @param password The user's plain text password.
	 * @return A string containing both the salt and hashed password.
	 * 
	 * @author Aarush
	 */
	public static String hashPassword(String password) {
		return null;
	}

	/**
	 * Verifies a plaintext password against a stored PBKDF2 hash.
	 * 
	 * @param password   The plaintext password to verify.
	 * @param storedHash The stored PBKDF2 hash string to compare against.
	 * @return True if the password matches the stored hash, false otherwise.
	 * 
	 * @author Aarush
	 */
	public static boolean verifyPassword(String password, String storedHash) {
		return false;
	}
}