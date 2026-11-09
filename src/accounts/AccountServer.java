/**
 * Provides account management and auth utilities.
 * 
 * @author Darren
 */
public final class AccountServer {

	// Username of signed in user. null when no one is signed in.
	private static String currentUser = null;

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
	 * @return True if the username and password match, to successfully sign into
	 *         the program.
	 * 
	 * @author Darren
	 */
	public static boolean signIn(String username, String password) {

		// Catches if someone is already signed in.
		if (currentUser != null || username == null || password == null) {
			return false;
		}

		// If verifyPassword returns false, return false for signIn.
		if (!verifyPassword(password, storedHash)) {
			return false;
		}

		// Missing check for whether or not the username is actually in storage.
		// Check in with storage team to see their methods that would work with storage
		// to grab the information
    
		// if (/* Insert Storage function here*/) {
		// 	return false;
    // }
		
		
		// If nothing catches, signIn returns true.
		currentUser = username.trim();
		return true;

	}

	/**
	 * Ends user session without deleting information.
	 * 
	 * @author Darren
	 */
	public static void signOut() {
		currentUsername = null;
	}

	/**
	 * Registers a user, setting up their username, password, and security
	 * question/answer.
	 * 
	 * @param username The username the user has created.
	 * @param password The password that the user wants to set for their account.
	 * @param question The security question that the user wants to use.
	 * @param answer The plaintext answer to the security question.
	 * @return True if the user's account was successfully registered.
	 * 
	 * @author Vic
	 */
	public static boolean registerUser(String username, String password, 
										String question, String answer) 
	{

		// Checks that the arguments are not null.
		if ((username == null) || (password == null)
				|| (question == null) || (answer == null))
		{
			return false;
		}

		// Checks if someone is currently signed in.
		if ((currentUser != null)) {
			return false;
		}

		/*
		Need a checker with storage team for if the username already exists. 
		If the username does exist, then registerUser should fail. 
		 */

		String hashedPassword = hashPassword(password);
		String hashedAnswer = hashPassword(answer.trim());
    String name = username.trim();

		/*
		Need a method with storage team to actually store username, hashed password,
		security question, and hashed answer to the security question. 
		 */ 

		currentUser = name;

		return true;
	}

	/**
	 * Removes a user account from our service if that is the user account signed
	 * in.
	 * 
	 * NOTE: deleteUser may have to be overloaded in the future to allow for 
	 * a user to delete through signing in with the security question instead.
	 * This depends on if we believe that should be used as another option or 
	 * become the main option for the verification before deleting a user.
	 * 
	 * @param username The username that is to be deleted.
	 * @param password The plaintext password of this deleted account.
	 * @return True if the user's account was successfully deleted.
	 * 
	 * @author Vic
	 */
	public static boolean deleteUser(String username, String password) {

		// Checks that arguments are not null.
		if ((username == null) || (password == null)){
			return false;
		}

		// Checks that the current user matches the one being deleted.
		if (currentUser == null 
				|| !currentUser.equals(username))
		{
			return false;
		}

		// User must use their password to actually delete their account.
		// Look at NOTE above for possibly changes. 

		/*
		Need a method from storage to actually access the hashed password 
		of a given user. 
		*/
		String storedHash;

		if (!verifyPassword(password, storedHash)) {
			return false;
		}

		/*
		Need a method from storage team to actually delete the user data.
		*/

		currentUser = null;

		return true;
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