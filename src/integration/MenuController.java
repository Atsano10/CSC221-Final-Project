package integration;
import java.util.Scanner;
import java.util.InputMismatchException;
import accounts.AccountManager;
import accounts.SecurityQuestion;

/**
  * Displays the main user menu and actions post authentication such as file reading
  *
  * @author Bilal Ansar
  * @author Apostolos Tsano
  * @author Junyu Chen
  */

public class MenuController {
	public static final Scanner input = new Scanner(System.in);
	static java.util.List<String> recordList = new java.util.ArrayList<>();
  /** 
	* Prints the options available to the user for the main menu
	*
	* @author Bilal
	*/
	public static void displayMainMenu() {
		if (AccountManager.getCurrentUser() == null) {
			System.out.println("Welcome to Personal Finance Manager!\n");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Help");
			System.out.println("4. Exit");
			doMenuAction(getMenuSelection(4));
		} else {
			System.out.println("Welcome, " + AccountManager.getCurrentUser() + "!\n");
			System.out.println("1. Upload a file");
			System.out.println("2. Edit a file");
			System.out.println("3. Options");
			System.out.println("4. Help");
			System.out.println("5. Exit");
			doMenuAction(getMenuSelection(5));
		} 
	}
	
  /** 
    * Asks the user to choose a menu option anywhere from 1 to [number of menu options]
	* 
	* @param The number of choices the menu will have. This allows this function to be used for menus with varying choice sizes.
	* @return An int from 1 - [number of menu options] chosen by the user
	* @author Bilal
	*/
	static int getMenuSelection(int choices) {
		int selection = 0;
		
		System.out.println("\nSelect: ");
	
		while (true) {
			try {
				selection = input.nextInt();
				input.nextLine();
			} catch (InputMismatchException e) {
				System.err.println("Please pick a number from 1 to " + choices + ".");
				input.nextLine();
				continue;
			} 
			if (selection > choices || selection < 1) {
				System.err.println("Please pick a number from 1 to " + choices + ".");
				input.nextLine();
				continue;
			} 
			return selection;
		}
	}

  /** 
    * Prints the next menu based on selection parameter if applicable,
    * otherwise does the relevant menu action by calling methods from other classes
	*
	* @param The selection the user made fetched by the getMenuSelection method
	* @author Bilal
	*/
	static void doMenuAction(int selection) {
		if (AccountManager.getCurrentUser() == null) {
			switch (selection) {
				case 1 -> handleRegister();
				case 2 -> handleLogin();
				case 3 -> handleHelp();
				case 4 -> handleExit();
			}
		} else {
			switch (selection) {
				case 1 -> handleReadFile();
				case 2 -> System.out.println("Not functional yet.");
				case 3 -> handleOptions();
				case 4 -> handleHelp();
				case 5 -> handleExit();
			}
		}
	}
	
  /**
	* 
    * Prompts the user to enter the path of the file to be read.
	* 
	*
	* @return The file path which is set by the user.
	* 
	* @author Apostolos Tsano
    */
	static String promptForFilePath() {
		
		while ( true ) {
			System.out.println( "Enter the path of the file you wish to look for: " );
			String path = input.nextLine().trim();

			// Removes quotes.
			if ( path.startsWith("\"") && path.endsWith("\"") && path.length() > 1 ) {
				path = path.substring( 1, path.length() - 1 );
			}
			if (!path.isEmpty()) {
				return path;
			} 
			System.err.println("Path cannot be empty. Try again? y/n");
			if (!isYes()) {
				continue;
			} return "";
    	}
	}
			
	
 /**
   * Calls this function if the user selects register. This method takes input from the user 
   * for username, password, security question, and answer. Then it passes these as parameters 
   * to the registerUser method from the AccountManager class.
   *
   * @author Bilal
   */
	static void handleRegister() {
		while (true) {
			String username = null;
			String password = null;
			String confirmPassword = null;
			SecurityQuestion question = null;
			String answer = null;
		
			System.out.println("\nYour username must be at least 3 characters and at most 20 characters.\n");
			System.out.println("Choose a username: ");
			username = input.nextLine();
			System.out.println("\nYour password must be at least 8 characters long and contain one number and one letter.\n");
			System.out.println("Choose a password: ");
			password = input.nextLine();
			System.out.println("Confirm your password: ");
			confirmPassword = input.nextLine();
			
			if (!password.equals(confirmPassword)) {
				System.err.println("\nYour passwords do not match.\n");
				break;
			}

			System.out.println("\nPick a security question: \n");
		
			int j = 0;
			for (SecurityQuestion sq : SecurityQuestion.values()) {
				j++;
				System.out.println(j + ". " + sq.getQuestionText());
			} 
		
			int selection = getMenuSelection(j);

			switch (selection) {
				case 1 -> question = SecurityQuestion.FIRST_PET;
				case 2 -> question = SecurityQuestion.TASTY_ROCK;
				case 3 -> question = SecurityQuestion.BIRTH_CITY;
				case 4 -> question = SecurityQuestion.ELEMENTARY_SCHOOL;
				case 5 -> question = SecurityQuestion.FAV_POKEMON;
				case 6 -> question = SecurityQuestion.HS_MASCOT;
				case 7 -> question = SecurityQuestion.FAV_ATHLETE;
				case 8 -> question = SecurityQuestion.CHILDHOOD_TEAM;
				case 9 -> question = SecurityQuestion.DONE_DREAMS;
				case 10 -> question = SecurityQuestion.BEST_FRIEND;
				case 11 -> question = SecurityQuestion.WORST_GROUP_PROJECT;
				case 12 -> question = SecurityQuestion.FOX_WORDS;
			}
			
			System.out.println("\nYour answer must be at least 2 characters long.\n");
			System.out.println("Enter the answer for your security question: ");
			answer = input.nextLine();
		
			if (AccountManager.registerUser(username, password, question, answer)) {
				System.out.println("\nAccount created! You are now logged in.\n");
				AccountManager.signIn(username, password);
				break;
			} else {
				System.err.println("\nThere was a problem with one or more of your choices. Try again? y/n");
				if (isYes()) {
					continue;
				} break;
			} 
		}
	}
    /**
    * Handles user login.
    * Prompts user for username and password.
    * Uses AccountManager to check credentials.
    * If valid, sets current user and returns to main menu.
    * Otherwise prints error message.
    *
	* @author Junyu Chen
	*/
	static void handleLogin() {
		System.out.println("Enter username: ");
		String username = input.nextLine();
		
		System.out.println("Enter password: ");
		String password = input.nextLine();

		if (AccountManager.signIn(username, password)) {
			System.out.println("\nLogin successful!");
		} else {
			System.err.println("\nInvalid username or password.\n");
	 	}
	}
	
	/**
     * Handles reading data from a user-specified file.
     *
     * Gets the file path from {@link #promptForFilePath()}.
     * Reads the file line by line, parses year and numeric data pairs, and stores them.
     * Skips invalid or malformed lines without crashing the program.
     * Shows a success message and the total number of loaded records when finished.
     * If the file cannot be found, displays an error message.
     *
     * @see #promptForFilePath()
     * 
     * @author Junyu Chen
     */
	static void handleReadFile() {
		String path = promptForFilePath();
		java.io.File file = new java.io.File(path);
		java.util.List<String> recordList = new java.util.ArrayList<>();

		try (java.util.Scanner fileScan = new java.util.Scanner(file)) {
			int count = 0;
			while (fileScan.hasNextLine()) {
				String line = fileScan.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				String[] parts = line.split(",");
				if (parts.length != 2) {
					System.err.println("Skipping invalid line: " + line);
					continue;
				}
				try {
					Integer.parseInt(parts[0].trim());
					Double.parseDouble(parts[1].trim());
					recordList.add(line);
					count++;
				} catch (NumberFormatException e) {
					System.err.println("Skipping bad number line: " + line);
				}
			}
			System.out.println("\nFile read complete. Total records loaded: " + count);
		} catch (java.io.FileNotFoundException e) {
			System.err.println("\nError: File not found at the given path.\n");
		}
		
	}

	/**
     * Handles checking and displaying data anomalies.
     *
     * Checks if data has been loaded first; if no data exists, prints a warning message.
     * Iterates through stored data and finds values outside the valid range.
     * Prints each anomalous record with its year and value, and counts total anomalies found.
     * If no anomalies are found, informs the user accordingly.
     *
     * @author Junyu Chen
     */
	static void handleAnomalies() {
		if (recordList.isEmpty()) {
			System.err.println("\nWarning: No data loaded yet. Please load a file first.\n");
			return;
		}
		int anomalyCount = 0;
		for (String line : recordList) {
			String[] parts = line.split(",");
			double value = Double.parseDouble(parts[1].trim());
			int year = Integer.parseInt(parts[0].trim());
			if (value < 0 || value > 100000) {
				System.out.println("Anomaly found: Year " + year + ", Value " + value);
				anomalyCount++;
			}
		}
		if (anomalyCount == 0) {
			System.out.println("No anomalies found in the loaded data.");
		} else {
			System.out.println("Total anomalies: " + anomalyCount);
		}
	}

	static void handleHelp() {
		System.out.println("\nTODO: Help menu goes here.\n");
	}

	static void handleOptions() {
		System.out.println("1. Change password");
		System.out.println("2. Change security question.");
		System.out.println("3. Delete account");
		System.out.println("4. Logout");
		System.out.println("5. Go back");

		int selection = getMenuSelection(5);

		switch (selection) {
			case 1 -> handleChangePassword();
			case 2 -> System.err.println("\nNot functional yet.\n");
			case 3 -> handleDeleteAccount();
			case 4 -> handleLogout();
			case 5 -> handleExit();
		}
	}

	/**
     * Handles deleting all data for a specified year.
     *
     * Checks if data has been loaded first; if no data exists, prints a warning message.
     * Prompts the user to enter the target year to delete.
     * Removes all matching year records and their associated data value.
     * Notifies the user whether the year was found and deleted or not found.
     *
     * @author Junyu Chen
     */
	static void handleDeleteYear() {
		if (recordList.isEmpty()) {
			System.err.println("\nNo data loaded yet. Please load a file first.\n");
			return;
		}
		
		System.out.println("Enter the year you want to delete:");
		int targetYear;
		try {
			targetYear = input.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("\nInvalid year input.\n");
			return;
		}

		int beforeSize = recordList.size();
		java.util.Iterator<String> it = recordList.iterator();
		while (it.hasNext()) {
			String line = it.next();
			String[] parts = line.split(",");
			int year = Integer.parseInt(parts[0].trim());
			if (year == targetYear) {
				it.remove();
			}
		}
		int deleted = beforeSize - recordList.size();
		if (deleted > 0) {
			System.out.println("Deleted " + deleted + " record(s) for year " + targetYear);
		} else {
			System.err.println("\nNo records found for year " + targetYear);
		}
	}

	/**
     * Handles changing the current user's password.
     *
     * Prompts the user for the old password and verifies it against the stored password.
     * If the old password matches, asks for a new password and a confirmation of the new password.
     * Updates the stored password only if the new password and confirmation match.
     * Prints success or error messages based on validation results.
     *
     * @author Junyu Chen
     */
	static void handleChangePassword() {
		if (AccountManager.getCurrentUser() == null) {
			System.err.println("\nError: No user logged in.\n");
			return;
		}
		System.out.println("Answer your security question to confirm your identity: ");
		System.out.println(AccountManager.getSecurityQuestion());
		String answer = input.nextLine();
		System.out.println("Enter your new password:");
		String newPassword = input.nextLine();
		System.out.println("Confirm your new password:");
		String confirmPassword = input.nextLine();

		if (!newPassword.equals(confirmPassword)) {
			System.err.println("\nNew password and confirmation do not match. Password change cancelled.\n");
			return;
		}
		
		boolean success = AccountManager.changePassword(AccountManager.getCurrentUser(), answer, newPassword);
		if (success) {
			System.out.println("\nPassword updated successfully!\n");
		} else {
			System.err.println("\nOne or more of your inputs was invalid. Password update failed.\n");
		}
	}

	/**
     * Handles the deletion of the currently logged in account.
	 *
	 * Prompts the user to confirm the deletion using {@link #isYesNo()}.
	 * If the user confirms, then all the data associated with the account is deleted, and the user is logged out.
	 * If the user clicks "no", no further changes are made.
	 *
	 * @see #isYesNo()
	 * @see #handleLogout()
     * 
     * @author Apostolos Tsano
     */
	static void handleDeleteAccount() {
		System.out.println("This permanently deletes your account and all its data.");
	    System.out.println("Are you sure? (y/n)");
	    if (!isYes()) {
	        System.out.println("Account deletion cancelled.");
	        return;
	    }

		// If yes
	    System.out.println("Enter your password: ");
	    String password = input.nextLine();
	    String question = AccountManager.getSecurityQuestion();
	    System.out.println(question != null ? question : "Enter your security answer: ");
	    String answer = input.nextLine();
	
	    if (AccountManager.deleteUser(password, answer)) {
	        System.out.println("\nYour account has been deleted.\n");
	    } else {
	        System.err.println("\nDeletion failed: Incorrect password or security answer.\n");
	    }
	}
	
	/**
	 * Handles the logging out of the current user.
	 *  
	 * Ends the current user session and clears any session data,
	 * then it will take you back to the login screen.
	 *
	 * @see #handleExit()
	 *
	 * @author Apostolos Tsano
	 */
	static void handleLogout() {
		if (AccountManager.getCurrentUser() != null) {
			AccountManager.signOut();
			// Need to clear the data somehow.
			System.out.println("\nYou have logged out.\n");
		} else {
			System.err.println("\nNo one is logged in.\n");
		}
	}

	/**
	 * Handles exiting the program.
	 * 
	 * Displays a closing message and terminates the application.
	 *
	 * @see #handleLogout()
	 *
	 * @author Apostolos Tsano
	 */
	static void handleExit() {
		if (AccountManager.getCurrentUser() != null) {
	        handleLogout();
	    }
		input.close();
	    System.out.println("Goodbye, hope to see you again soon!");
	    System.exit(0);
	}

	/**
	 * Allows the user to make choices.
	 * 
	 * Yes or no prompting,
	 *
	 * @author Apostolos Tsano
	 */
	static boolean isYes() {
    while (true) {
        String response = input.nextLine().trim().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
        	return true;
        }
        if (response.equals("n") || response.equals("no")) {
        	return false;
        }
        System.err.println("\nPlease enter y or n: ");
    }
}
}
