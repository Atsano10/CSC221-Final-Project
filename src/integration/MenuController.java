package integration;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import accounts.AccountManager;
import accounts.SecurityQuestion;
import dataaudit.DataAudit;
import reports.Reports;
import storage.Budget;
import storage.BudgetStorage;
import storage.BudgetTransaction;

/**
  * Displays the main user menu and actions post authentication such as file reading
  *
  * @author Bilal Ansar
  * @author Apostolos Tsano
  * @author Junyu Chen
  */

public class MenuController {

   /*
	* Local class scanner used for any time user input is required.
	* Only closed when program exits.
	*/
	public static final Scanner input = new Scanner(System.in);

	/**
	 * Starts the whole application by pointing where to start. It keeps showing the 
	 * main menu until the user decides to choose Exit.
	 * 
	 * @param args command-line arguments
	 * @throws IOException
	 * 
	 * @author Apostolos Tsano
	 */

	public static void main(String[] args)throws IOException{
		while(true){
			displayMainMenu();
		}
	}

	
  	/** 
	* Prints the options available to the user for the main menu.
	*
	* @author Bilal Ansar
 	* @throws IOException when uploading a file through budget options and that file does not exist.
	*/
	public static void displayMainMenu() throws IOException {
		if (AccountManager.getCurrentUser() == null) {
			System.out.println("Welcome to Personal Finance Manager!\n");
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Help");
			System.out.println("4. Exit");
			doMenuAction(getMenuSelection(4));
		} else {
			System.out.println("Welcome, " + AccountManager.getCurrentUser() + "!\n");
			System.out.println("1. Budget actions");
			System.out.println("2. Options");
			System.out.println("3. Help");
			System.out.println("4. Exit");
			doMenuAction(getMenuSelection(4));
		} 
	}
	
  /** 
    * Asks the user to choose a menu option anywhere from 1 to [number of menu options].
	* 
	* @param The number of choices the menu will have. This allows this function to be used for menus with varying choice sizes.
	* @return An int from 1 - [number of menu options] chosen by the user.
	* @author Bilal Ansar
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
    * If applicable, prints the next menu based on selection parameter,
    * otherwise does the relevant menu action by calling methods from other classes
	*
	* @param The selection the user made fetched by the getMenuSelection method
	* @author Bilal Ansar
 	* @throws IOException 
	*/
	static void doMenuAction(int selection) throws IOException {
		if (AccountManager.getCurrentUser() == null) {
			switch (selection) {
				case 1 -> handleRegister();
				case 2 -> handleLogin();
				case 3 -> handleHelp();
				case 4 -> handleExit();
			}
		} else {
			switch (selection) {
				case 1 -> handleBudgetActions();
				case 2 -> handleDeleteYear();
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
	* @return The file path which is set by the user.
	* 
	* @author Apostolos Tsano
    */
	static String promptForFilePath() {
		
		while (true) {
			System.out.println("Enter the path of the file you wish to look for: ");
			String path = input.nextLine().trim();

			// Removes quotes.
			if (path.startsWith("\"") && path.endsWith("\"") && path.length() > 1) {
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
    * @author Bilal Ansar
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
    * This will simply print a help menu to explain the function of the program.
	*
	* @author Bilal Ansar
	*/
	static void handleHelp() {
		System.out.println("\nTODO: Help menu goes here.\n");
	}
	
  /**
	* Prints the options available to the currently logged in user.
	*
	* Change password will call handleChangePassword()
	* Delete account will call handleDeleteAccount()
	* Logout will call handleLogout()
	*
	* @author Bilal Ansar
	*/
	static void handleOptions() {
		System.out.println("1. Change password");
		System.out.println("2. Delete account");
		System.out.println("3. Logout");
		System.out.println("4. Go back");

		int selection = getMenuSelection(5);

		switch (selection) {
			case 1 -> handleChangePassword();
			case 2 -> handleDeleteAccount();
			case 3 -> handleLogout();
			case 4 -> System.out.println();
		}
	}
	
    /**
    * Handles user login, by prompting the user for username and password. Uses AccountManager
	* to check credentials and if valid sets the current user and returns to main menu, 
	* otherwise shows error message.
    *
	* @author Junyu Chen
	*/
	static void handleLogin() {
		System.out.println("Enter username: ");
		String username = input.nextLine();
		
		System.out.println("Enter password: ");
		String password = input.nextLine();

		if (AccountManager.signIn(username, password)) {
			System.out.println("\nLogin successful!\n");
		} else {
			System.err.println("\nInvalid username or password.\n");
	 	}
	}

  /**
	* This will print the budget options avaliable to the user.
	*
    * Upload a file will call handleReadFIle()
	* Load a year will call handleLoadYear()
	* Delete a year will call handleDeleteYear()
	*
	* @author Bilal Ansar
	*/
	static void handleBudgetActions() throws IOException {
		System.out.println("1. Upload a file");
		System.out.println("2. Load a year");
		System.out.println("3. Delete a year");
		
		int selection = getMenuSelection(3);
		
		switch (selection) {
			case 1 -> handleReadFile();
			case 2 -> handleLoadYear();
			case 3 -> handleDeleteYear();
		}
	}
	
	/**
	 * This will skip blank lines to find the header line of the to-be-read file.
	 *
	 * @author Bilal Ansar
	*/
	static String readNonBlankLine(BufferedReader br) throws IOException {
	    String line;
	    while ((line = br.readLine()) != null) {
	        if (!line.isBlank()) {
	            return line;
	        }
	    }
	    return null;
	}
	
	/**
     * Handles reading data from a user-specified file.
     *
     * Gets the file path from {@link #promptForFilePath()}.
     * Reads the file line by line, parses year, category and amount to store them in a objects from the Storage modules' classes.
     * If the file cannot be found, displays an error message.
     *
     * @see #promptForFilePath()
     * 
     * @author Bilal Ansar
	 * @throws IOException 
     */
	static void handleReadFile() throws IOException {
		
		String path = promptForFilePath();
		
		Path file = Path.of(path);
		String line;
		String header = null;
		int year = 0;
		LocalDate date;
		String category = "";
		int amount = 0;
		
		List<BudgetTransaction> transactions = new ArrayList<>();
		
		try (BufferedReader br = Files.newBufferedReader(file)) {
			
			header = readNonBlankLine(br);
			
			if (header == null) { System.err.println("The file contains no header."); }
			
		} catch (IOException e) {
			System.err.println("the file does not exist.");
			return;
		}
			 
		try (BufferedReader br = Files.newBufferedReader(file)) {
			 
			readNonBlankLine(br);
			
			while ((line = br.readLine()) != null) {
				 
			String[] parts = line.split(",");
			 
			year = Integer.parseInt(parts[0].substring(6));
			 
			String yr = parts[0].substring(6);
			String day = parts[0].substring(3, 5);
			String month = parts[0].substring(0, 2);
			 
			String tempDate = yr + "-" + month + "-" + day;
			 
			date = LocalDate.parse(tempDate);
			category = parts[1];
			amount = Integer.parseInt(parts[2]);
			 
			storage.BudgetTransaction transaction = new storage.BudgetTransaction(date, category, amount);
	
			transactions.add(transaction);
			}
			 
		} catch (IOException e) {
			System.err.println("the file does not exist.");
			return;
		}
		
		Budget budget = new Budget(year, transactions);
		BudgetStorage.saveBudget(AccountManager.getCurrentUser(), budget, false);
		
		System.out.println("\nYour file was succesfully read!\n");
		
		handleAudit(budget);
		
		String report = Reports.formatSummaryAnalysis(budget);
		
		System.out.println(report);
	}

  /**
	* This will call the runAudit method from the Data Audit module
	*
	* @author Bilal Ansar
	*/
	static void handleAudit(Budget budget) {
		DataAudit.runAudit(budget);
	}
	
  /**
    * Handles deleting all data for a specified year.
    *
    * Prompts the user to enter the target year to load.
    * Loads the matching year record (if there is one).
    * Notifies the user whether the year was loaded or not found.
    *
    * @author Bilal Ansar
    */
	static void handleLoadYear() {
		
		System.out.println("\nEnter the year you want to load:");
		int targetYear;
		try {
			targetYear = input.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("\nInvalid year input.\n");
			return;
		}
		
		
		
		if (!BudgetStorage.budgetExists(AccountManager.getCurrentUser(), targetYear)) {
			System.err.println("\nThere is no budget for the specified year.\n");
			return;
		}
		
		Budget budgetToLoad = BudgetStorage.getBudget(AccountManager.getCurrentUser(), targetYear); 
		
		System.out.println("Year found! Would you like to load it here or on to a file?\n");
		
		System.out.println("1. Load it here");
		System.out.println("2. Onto a file");
		
		
		int selection = getMenuSelection(2);
		
		switch (selection) {
			case 1: 
				String report = Reports.formatSummaryAnalysis(budgetToLoad);
				System.out.println("\nYear loaded!\n");
				System.out.println(report);
				break;
			case 2:
				break;
			}
	}

  /**
    * Handles deleting all data for a specified year.
    *
    * Prompts the user to enter the target year to delete.
    * Removes matching year record.
    * Notifies the user whether the year was deleted or not found.
    *
    * @author Junyu Chen
	* @throws IOException 
    */
	static void handleDeleteYear() throws IOException {
		
		System.out.println("\nEnter the year you want to delete:");
		int targetYear;
		try {
			targetYear = input.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("\nInvalid year input.\n");
			return;
		}
		
		if (BudgetStorage.budgetExists(AccountManager.getCurrentUser(), targetYear)) {
			BudgetStorage.deleteBudget(AccountManager.getCurrentUser(), targetYear);
		} else {
			System.err.println("\nThere is no budget for the specified year.\n");
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
	* If the user confirms, then all the data associated with the account is deleted, and the user is then logged out.
	* If the user types n or no, no further changes are made.
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
