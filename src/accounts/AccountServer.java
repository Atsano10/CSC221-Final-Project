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
}