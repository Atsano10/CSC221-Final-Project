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
			System.out.println("\nWelcome to Personal Finance Manager!\n");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Help");
			System.out.println("4. Exit");
			doMenuAction(getMenuSelection(4));
		} else {
			System.out.println("\nWelcome, " + AccountManager.getCurrentUser() + "!\n");
			System.out.println("1. ");
			System.out.println("2. ");
			System.out.println("3. Options");
			System.out.println("4. Exit");
			doMenuAction(getMenuSelection(4));
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
				System.out.println("Please pick a number from 1 to " + choices + ".");
				input.nextLine();
				continue;
			} 
			if (selection > choices || selection < 1) {
				System.out.println("Please pick a number from 1 to " + choices + ". Press Enter to retry.");
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
		switch (selection) {
			case 1 -> handleRegister();
			case 2 -> handleLogin();
			case 3 -> System.out.println("\nTODO: Help menu goes here.\n");
			case 4 -> handleExit();
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
				input.close();
				return path;
			} 
			System.out.println("Path cannot be empty. Try again.");
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
			SecurityQuestion question = null;
			String answer = null;
		
			System.out.println("Your username must be at least 3 characters and at most 20 characters.");
			System.out.println("Choose a username: ");
			username = input.nextLine();
			System.out.println("Your password must be at least 8 characters long.");
			System.out.println("Choose a password: ");
			password = input.nextLine();

			System.out.println("Pick a security question: \n");
		
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
			
			System.out.println("\nYour answer must be at least 2 characters long.");
			System.out.println("Enter the answer for your security question: ");
			answer = input.nextLine();
		
			if (AccountManager.registerUser(username, password, question, answer)) {
				System.out.println("Account created!");
				AccountManager.signIn(username, password);
				break;
			} else {
				System.out.println("There was a problem with one or more of your choices. Would you like to try again?");
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
			System.out.println("Invalid username or password.\n");
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
					System.out.println("Skipping invalid line: " + line);
					continue;
				}
				try {
					Integer.parseInt(parts[0].trim());
					Double.parseDouble(parts[1].trim());
					recordList.add(line);
					count++;
				} catch (NumberFormatException e) {
					System.out.println("Skipping bad number line: " + line);
				}
			}
			System.out.println("File read complete. Total records loaded: " + count);
		} catch (java.io.FileNotFoundException e) {
			System.out.println("Error: File not found at the given path.");
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
			System.out.println("Warning: No data loaded yet. Please load a file first.");
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
			System.out.println("Warning: No data loaded yet. Please load a file first.");
			return;
		}
		
		System.out.println("Enter the year you want to delete:");
		int targetYear;
		try {
			targetYear = input.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("Invalid year input.");
			input.close();
			return;
		}
		input.close();

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
			System.out.println("No records found for year " + targetYear);
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
			System.out.println("Error: No user logged in.");
			return;
		}
		System.out.println("Enter the answer to your security question: ");
		String answer = input.nextLine();
		System.out.println("Enter your new password:");
		String newPassword = input.nextLine();
		System.out.println("Confirm your new password:");
		String confirmPassword = input.nextLine();
		
		input.close();

		if (!newPassword.equals(confirmPassword)) {
			System.out.println("New password and confirmation do not match. Password change cancelled.");
			return;
		}
		
		boolean success = AccountManager.changePassword(AccountManager.getCurrentUser(), answer, newPassword);
		if (success) {
			System.out.println("Password updated successfully!");
		} else {
			System.out.println("One or more of your inputs was invalid. Password update failed.");
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
	        input.close();
	        return;
	    }

		// If yes
	    System.out.println("Enter your password: ");
	    String password = input.nextLine();
	    String question = AccountManager.getSecurityQuestion();
	    System.out.println(question != null ? question : "Enter your security answer: ");
	    String answer = input.nextLine();
	
	    if (AccountManager.deleteUser(password, answer)) {
	        // Somehow delete the data stored for the username.
	        System.out.println("Your account has been deleted.");
	        handleLogout();
	        input.close();
	    } else {
	        System.out.println("Deletion failed: incorrect password or security answer.");
	        input.close();
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
			System.out.println("\nNo one is logged in.");
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
	        AccountManager.signOut();
	    }
		input.close();
	    System.out.println("\nGoodbye, hope to see you soon!");
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
        System.out.println("\nPlease enter y or n: ");
    }
}
}
