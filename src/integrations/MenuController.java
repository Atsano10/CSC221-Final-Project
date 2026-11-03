class MenuController {

	static void displayMainMenu() 
	{
		/* Prints the options available to the user for the main menu
		* @author Bilal
		*/
	}

	static int getMenuSelection() 
	{
		/* Asks the user to choose a menu option anywhere from 1 to [number of menu options]
		* @author Bilal
		*/
		
		return selection;
	}
	
	static String promptYesOrNo() 
	{
		return choice;
	}

	static void doMenuAction(int selection) 
	{
		/* Prints the next menu based on selection parameter
		* @author Bilal
		*/
	}

	static String promptForFilePath() {
		return "";
	}

	static void handleReadFile() {
	}

	static void handleAnomalies() {
	}

	static void handleDeleteYear() {
	}

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
