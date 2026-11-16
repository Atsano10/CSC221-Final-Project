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

class MenuController {
	
  /** 
	* Prints the options available to the user for the main menu
	*
	* @author Bilal
	*/
	static void displayMainMenu() {
		if (AccountManager.getCurrentUser() == null) {
			System.out.println("Welcome to Personal Finance Manager!\n");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Help");
			System.out.println("4. Exit");
			MenuController.doMenuAction(MenuController.getMenuSelection(4));
		} else {
			System.out.println("Welcome, " + AccountManager.getCurrentUser() + "!\n");
			System.out.println("1. ");
			System.out.println("2. ");
			System.out.println("3. Options");
			System.out.println("4. Exit");
			MenuController.doMenuAction(MenuController.getMenuSelection(4));
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
		int selection;
		
		System.out.println("\nSelect: ");
	
		while (true) {
			Scanner input = new Scanner(System.in);
			try {
				 selection = input.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Try again.");
				continue;
			} 
			if (selection > choices || selection < 1) {
				System.out.println("Try again.");
				continue;
			} input.close();
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
			case 1 -> MenuController.handleRegister();
			case 4 -> MenuController.handleExit();
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
		return path;
	}
	
 /**
   * Calls this function if the user selects register. This method takes input from the user 
   * for username, password, security question, and answer. Then it passes these as parameters 
   * to the registerUser method from the AccountManager class.
   *
   * @author Bilal
   */
	static void handleRegister() {
		String username = null;
		String password = null;
		SecurityQuestion question = null;
		String answer = null;
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Choose a username: ");
		username = input.nextLine();
		System.out.println("Choose a password: ");
		password = input.nextLine();
		
		int i = 0;
		for (SecurityQuestion sq : SecurityQuestion.values()) {
			i++;
			System.out.println(i + ". " + sq.getQuestionText());
		} 
		
		int selection = MenuController.getMenuSelection(i)

		switch (selection) {
			case 1 -> question = SecurityQuestion.FIRST_PET;
			case 2 -> question = SecurityQuestion.MOTHER_MAIDEN_NAME;
			case 3 -> question = SecurityQuestion.BIRTH_CITY;
			case 4 -> question = SecurityQuestion.ELEMENTARY_SCHOOL;
			case 5 -> question = SecurityQuestion.FIRST_CAR;
		}

		System.out.println("Answer for your security question: ");
		answer = input.nextLine();
		
		input.close();
		
		AccountManager.registerUser(username, password, question, answer);
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
		
	}

	/**
     * Handles the deletion of the currently logged in account.
	 *
	 * Prompts the user to confirm the deletion using {@link #promptYesOrNo()}.
	 * If the user confirms, then all the data associated with the account is deleted, and the user is logged out.
	 * If the user clicks "no", no further changes are made.
	 *
	 * @see #promptYesOrNo()
	 * @see #handleLogout()
     * 
     * @author Apostolos Tsano
     */
	static void handleDeleteAccount() {

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
	
	}
}
