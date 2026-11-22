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
import dataaudit.DataAudit;
import reports.Reports;
import storage.Budget;
import storage.BudgetStorage;
import storage.BudgetTransaction;
import validations.CsvValidator;
import validations.InputValidator;

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
	* Starts the whole application by pointing where to start. 
	* The while loop only breaks when the user decides to choose Exit from the menu.
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
				case 2 -> handleOptions();
				case 3 -> handleHelp();
				case 4 -> handleExit();
			}
		}
	}
	
  /**
	* 
    * Reads a file path from the user and checks if the file inputted exists. 
	*
	* @return The file path which is set by the user.
	* 
	* @author Apostolos Tsano
    */
	static String promptForFilePath() {

		String path = input.nextLine().trim();
		
		if (!InputValidator.validateFilePath(path)) {
			System.err.println("\nThe file was not found.");
			return "";
		}
		return path;
	}
			
	
  /**
    * Calls this function if the user selects register. This method takes input from the user 
    * for username, password, security question, and answer. Then it passes these as parameters 
    * to the registerUser method from the AccountManager class.
    *
    * @author Bilal Ansar
    */
	static void handleRegister() {
			String username = null;
			String password = null;
			String confirmPassword = null;
			String question = null;
			String answer = null;
		
			while (true) {
				System.out.println("\nYour username must have no spaces.");
				System.out.println("Your username must be no less than 3 characters.");
				System.out.println("Your username must be no more than 20 characters.");
				System.out.println("Choose a username: ");
				username = input.nextLine();

				if (!AccountValidator.validateUserName(username)) {
					System.err.println("Invalid username. Try again.");
					continue;
				} break;
			}
			

			while (true) {
				System.out.println("\nYour password must be no less than 8 characters.");
				System.out.println("Your password must contain one number.");
				System.out.println("Your password must contain one letter.");
				System.out.println("Choose a password: ");
				password = input.nextLine();

				if (!AccountValidator.validatePassword(password)) {
					System.err.println("\nInvalid password. Try again.\n");
					continue;
				}
				
				System.out.println("Confirm your password: ");
				confirmPassword = input.nextLine();
				
				if (!password.equals(confirmPassword)) {
					System.err.println("\nYour passwords do not match. Try again.\n");
					continue;
				} break;
			}

			
			while (true) {	
				System.out.println("\nYour question must be no less than 5 characters.");
				System.out.println("Pick a secret question: ");
				question = input.nextLine();

				if (!AccountValidator.validateQuestion(question)) {
					System.err.println("\nInvalid question. Try again.\n");
					continue;
				} break;
			}

			while (true) {
				System.out.println("\nYour answer must be no less than 2 characters.");
				System.out.println("Enter the answer for your secret question: ");
				answer = input.nextLine();

				if (!AccountValidator.validateSecretAnswer(answer)) {
					System.err.println("Invalid question. Try again.");
					continue;
				} break;
			}
		
			AccountManager.registerUser(username, password, question, answer);
	}

  /**
    * This will print a help menu to explain the function of the program.
	*
	* @author Junyu Chen
	*/
	static void handleHelp() {
		System.out.println("\n===== HELP MENU =====\n");
		System.out.println("Personal Finance Manager helps you track and manage your budget.");
		System.out.println("\nBefore login:");
		System.out.println("- Register: Create a new account with username, password and secret question.");
		System.out.println("- Login: Sign into your existing account.");
		System.out.println("- Help: Show this help information.");
		System.out.println("- Exit: Close the program.");

		System.out.println("\nAfter login:");
		System.out.println("- Budget actions: Upload CSV budget files, generate reports, audit or delete budget years.");
		System.out.println("- Options: Change password, delete your account, or logout.");
		System.out.println("- Help: Show this help information.");
		System.out.println("- Exit: Logout and close the program.");

		System.out.println("\nCSV file requirement:");
		System.out.println("- The header must be: Date,Category,Amount");
		System.out.println("- All transactions in one file must belong to the same year.");
		System.out.println("\n=====================\n");
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

		int selection = getMenuSelection(4);

		switch (selection) {
			case 1 -> handleChangePassword();
			case 2 -> handleDeleteAccount();
			case 3 -> handleLogout();
		}
		return;
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
    * Upload a budget file will call handleReadFile()
    * Generate a report will call handleLoadYear()
    * Check a year will call handleAudit()
    * Delete a budget will call handleDeleteYear()
	*
	* @author Bilal Ansar
	*/
	static void handleBudgetActions() throws IOException {
		System.out.println("1. Upload a budget file");
		System.out.println("2. Generate a report");
		System.out.println("3. Generate an audit");
		System.out.println("4. Delete a budget");
		System.out.println("5. Go back");
		
		int selection = getMenuSelection(5);
		
		switch (selection) {
			case 1 -> handleReadFile();
			case 2 -> handleLoadYear();
			case 3 -> handleAudit();
			case 4 -> handleDeleteYear();
		} return;
	}

	/**
	 * Checks if there are any budgets, in any any year and lists them if true.
	 * 
	 * @return True if any budget exists, otherwise return false.
	 * 
	 * @author Apostolos Tsano
	 */
	static boolean isAnyBudget() {
		List<Integer> years;
        try {
            years = BudgetStorage.getAvailableYears(AccountManager.getCurrentUser()); // Returns the years of every YYYY file it finds. 
        } catch (IOException e) {
            System.err.println("\nThe budgets you have saved could not be read.\n");
            return false;
        }
        if (years.isEmpty()) {
            System.err.println("\nYou have not uploaded any budgets.\n");
            return false;
        }
        System.out.println("\nYour budgets are: " + years);
		return true;
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
		
		System.out.println("\nEnter you budget file's name:");
		String path = promptForFilePath();

		// Stop if the path is not valid.
		if (path.isEmpty()){
			return;
		}
		
		Path file = Path.of(path);
		String line;
		String header = null;
		String category = null;
		int year = 0;
		int month = 0;
		int day = 0;
		int amount = 0;
		String budgetYear = null;
		int count = 0;
		
		List<BudgetTransaction> transactions = new ArrayList<>();
		
		try (BufferedReader br = Files.newBufferedReader(file)) {
			
			header = readNonBlankLine(br);
			
			if (!CsvValidator.validateHeader(header)) {
				System.err.println("\nThe file's header is not valid.\n");
				return;
			}
			
		} catch (IOException e) {
			return;
		}
			 
		try (BufferedReader br = Files.newBufferedReader(file)) {
			 
			readNonBlankLine(br);
			
			while ((line = br.readLine()) != null) {
				 
			String[] parts = line.split(",");
			
			if (!CsvValidator.validateDate(parts[0])) {
				System.err.println("\nThe date is invalid at: " + line);
				System.out.println("\nYou can fix the CSV file before trying again. Would you like to skip this row and continue instead? y/n");
				if (!isYes()) { return; } 
				continue;
			}
			
			if (!CsvValidator.validateCategory(parts[1])) {
				System.err.println("\nThe category is invalid at: " + line);
				System.out.println("\nYou can fix the CSV file before trying again. Would you like to skip this row and continue instead? y/n");
				if (!isYes()) { return; } 
				continue;
			}
			
			if (!CsvValidator.validateAmount(parts[2], parts[1])) {
				System.err.println("\nThe amount is invalid at: " + line);
				System.out.println("\nYou can fix the CSV file before trying again. Would you like to skip this row and continue instead? y/n");
				if (!isYes()) { return; } 
				continue;
			}
			
			category = parts[1];
			amount = Integer.parseInt(parts[2]);
			
			String[] tempDate = parts[0].split("/");
			
			if (!InputValidator.validateYear(tempDate[2])) {
				System.err.println("\nThe year is invalid at: " + line);
				System.out.println("\nYou can fix the CSV file before trying again. Would you like to skip this row and continue instead? y/n");
				if (!isYes()) { return; } 
				continue;
			}
			
			if (count == 0) {
				budgetYear = tempDate[2];
				count++;
			} else {
				if (!budgetYear.equals(tempDate[2])) {
				System.err.println("\nThere are conflicting years in your file.\n");
				return;
				}
			}
			
			year = Integer.parseInt(tempDate[2]);
			month = Integer.parseInt(tempDate[0]);
			day = Integer.parseInt(tempDate[1]);
			
			LocalDate date = LocalDate.of(year, month, day);
			 
			storage.BudgetTransaction transaction = new storage.BudgetTransaction(date, category, amount);
	
			transactions.add(transaction);
			
			}
			 
		} catch (IOException e) {
			System.err.println("\nThe file does not exist.\n");
			return;
		}
		
		if (year == 0) {
			System.err.println("\nThe year for the budget file is invalid.\n");
			return;
		}
		
		Budget budget = new Budget(year, transactions);
		BudgetStorage.saveBudget(AccountManager.getCurrentUser(), budget, false);
		
		System.out.println("\nYour file was uploaded successfully!\n");
	}

  /**
	* This will call the runAudit method from the Data Audit module
	*
	* @author Bilal Ansar
	*/
	static void handleAudit() {
		if (!isAnyBudget()) {
            return;
        }

		System.out.println("\nEnter the year of the budget you want to audit:");
		int targetYear;
		try {
			targetYear = input.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("\nInvalid year input.\n");
			return;
		}
		
		input.nextLine();
		
		Budget budgetToAudit = BudgetStorage.getBudget(AccountManager.getCurrentUser(), targetYear);
		
		if (budgetToAudit == null) {
			System.err.println("\nThere is no data for the specified year.\n");
			return;
		}
		
		System.out.println("\nFile loaded successfully!");
		DataAudit.runAudit(budgetToAudit, input);
	}
	
  /**
    * Generates a report for a year the user picks, and then shows it on screen or saves it to 
	* a file depending on user request.
    *
    * Asks for a year.
	* Builds the report if the budget exists, and tells the user if it doesnt.
    *
    * @author Bilal Ansar
    */
	static void handleLoadYear() {
		if (!isAnyBudget()) {
            return;
        }

		System.out.println("\nEnter the year you need a report for:");
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
		String report = Reports.formatSummaryAnalysis(budgetToLoad);
		
		System.out.println("Would you like to view your report or save it to a file?\n");
		
		System.out.println("1. View it here");
		System.out.println("2. Save it to a file");
		
		int selection = getMenuSelection(2);
		
		switch (selection) {
			case 1: 
				System.out.println("\nHere is your report: \n");
				Reports.displayReport(report);
				break;
			case 2:
				System.out.println("Enter the name of the file you want to export to: ");
				String path = promptForFilePath();
				Reports.exportReport(report, path);
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
		if (!isAnyBudget()) {
            return;
        }
		
		System.out.println("Are you sure you want to delete a budget? y/n");
		if (!isYes()) {
			return;
		}
		
		System.out.println("\nEnter the year of the budget you want to delete:");
		int targetYear;
		try {
			targetYear = input.nextInt();
		} catch (InputMismatchException e) {
			System.err.println("\nInvalid year input.\n");
			return;
		}
		
		if (BudgetStorage.budgetExists(AccountManager.getCurrentUser(), targetYear)) {
			BudgetStorage.deleteBudget(AccountManager.getCurrentUser(), targetYear);
			System.out.println("\nThe budget for the specified year was successfully deleted.\n");
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
