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
	 * Registers a user, setting up their username, password, and security question/answer.
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
	 * Removes a user account from our service if that is the user account signed in.
	 * 
	 * @param username The username the user has created.
	 * @return True if the user's account was successfully deleted.
	 * 
	 * @author Vic
	 */
	public static boolean deleteUser(String username) {
		return false;
	}
}