package dataaudit; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;
import storage.Budget;
import storage.BudgetTransaction;


/**
 * Allows user to audit their data and see any potential anomalies and categorize data 
 * 
 * @author Hamet
 */
public class DataAudit {
    private final static double ANOMALYBOUND = 2.5;  

    /**
     * Goes through data audit menu and asks the user what they want to do (exclude categories, find dupes/anamolies)
     * 
     * @param budgetFile transaction data
     * @param scanner shared scanner from whoever calls function
     *
     * @author Haoyang
     */
    public static void runAudit(Budget budgetFile, Scanner scanner){
        //check that the budget file is not empty
        if(budgetFile == null || budgetFile.transactions() == null){
            System.err.println("Error: No budget data is available.");
            return;
        }
        
        // check that there are transactions to audit
        if(budgetFile.transactions().isEmpty()){
            System.out.println("There are no transactions to audit.");
            return;
        }
        ArrayList<String> excludedCategories = new ArrayList<>();
        boolean running = true;
        while(running){
            System.out.println("\n===== Data Audit Menu =====");
            System.out.println("1. Choose Excluded Categories");
            System.out.println("2. Find Duplicates");
            System.out.println("3. Find Anomalies");
            System.out.println("4. Exit");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine().trim();
            switch(choice){
                case "1":
                    excludedCategories = chooseExcludedCategories(scanner);
                    System.out.println("Excluded categories:" + excludedCategories);
					System.out.println();
                    break;

                case "2":
                    System.out.println("\nFinding duplicates...");
                    ArrayList<AuditFinding> duplicates = findDuplicates(budgetFile);
                    printFindings(duplicates, FindingType.DUPLICATE);
					System.out.println();
                    break;
                
                case "3": 
                    System.out.println("\nFinding anomalies...");
                    ArrayList<AuditFinding> anomalies = findAnomalies(budgetFile, excludedCategories);
                    printFindings(anomalies, FindingType.ANOMALY);
					System.out.println();
                    break; 
                     
                case "4":
                    running = false;
                   System.out.println("\nExiting Data Audit Menu.\n");
                    break;

                default:
                    System.out.println("Invalid choice. Please try 1-4.");    
            }
        }
    };

    /**
     * Finds transactions that are the same
     * 
     * @param budgetFile transaction data
     * @return list of entries for each duplicate
     * 
     * @author Javier
     */
    public static ArrayList<AuditFinding> findDuplicates(Budget budgetFile)
{
    ArrayList<AuditFinding> duplicates = new ArrayList<>();
    HashSet<BudgetTransaction> seen = new HashSet<>();

    for (BudgetTransaction transaction : budgetFile.transactions())
    {
        if (!seen.add(transaction))
        {
            String reason = "Transaction has already appeared";
            AuditFinding finding = new AuditFinding(
                transaction.toString(), FindingType.DUPLICATE, reason);

            duplicates.add(finding);
        }
    }

    return duplicates;
}

    /**
     * Finds outlier data for each category independently.
     * Excluded categories are skipped.
     *
     * @param budgetFile transaction data
     * @param exclusion categories to leave out of the anomaly check
     * @return list of anomaly entries
     *
     * @author Hamet
     */
    public static ArrayList<AuditFinding> findAnomalies(Budget budgetFile, ArrayList<String> exclusion){
        ArrayList<AuditFinding> anomalies = new ArrayList<>();


        HashMap<String, List<BudgetTransaction>> byCategory = new HashMap<>();
        for (BudgetTransaction transaction : budgetFile.transactions()) {
            if (exclusion.contains(transaction.category())) {
                continue;
            }
            String category = transaction.category();
            if (!byCategory.containsKey(category)) {
                byCategory.put(category, new ArrayList<>());
            }
            byCategory.get(category).add(transaction);
        }

        for (String category : byCategory.keySet()) {
            List<BudgetTransaction> group = byCategory.get(category);
            double median = medianAmount(group);
            double bound = ANOMALYBOUND * median;

            for (BudgetTransaction transaction : group) {
                if (Math.abs(transaction.amount()) > bound) {
                    String reason = String.format("Amount %d is out of normal range for %s (median %.2f, bound %.2f)",
                            transaction.amount(), category, median, bound);
                    anomalies.add(new AuditFinding(transaction.toString(), FindingType.ANOMALY, reason));
                }
            }
        }
        return anomalies;
    };

    /**
     * Finds the median of the absolute amounts in a list of transactions.
     *
     * @param group transactions from one category
     * @return median absolute amount
     *
     * @author Hamet
     */
    private static double medianAmount(List<BudgetTransaction> group) {
        List<Integer> amounts = new ArrayList<>();
        for (BudgetTransaction transaction : group) {
            amounts.add(Math.abs(transaction.amount()));
        }
        Collections.sort(amounts);

        int size = amounts.size();
        if (size % 2 == 0) {
            return (amounts.get(size / 2 - 1) + amounts.get(size / 2)) / 2.0;
        }
        return amounts.get(size / 2);
    }
   /**Asks the user which categories to exclude from anomaly detection.
     * @return list of excluded categories
     * @author Joseph
     */
public static ArrayList<String> chooseExcludedCategories(Scanner scanner) 
{
	ArrayList<String> excluded = new ArrayList<>();
	String[] categories = {"Compensation", "Allowance", "Investments", "Other", "Home", "Utilities", "Food", "Appearance", "Work", "Education", "Transportation", "Entertainment", "Professional Services"};

    while (true) 
	{
		System.out.println("\nEnter a category you want to exclude:");
		for(int i=0;i<categories.length;i++)
		{
			System.out.println((i+1)+"."+categories[i]);
		}
		System.out.println("0. DONE");
		System.out.println("Enter your choice");
		String input= scanner.nextLine().trim();
		
        if (input.equals("0")) 
		{
			break;
        }
		try 
		{
			int choice= Integer.parseInt(input);
			if(choice<1 || choice>categories.length)
			{
				System.out.println("Invalid choice, Please try again");
				continue;
			}
			String category= categories[choice-1];
			if(!excluded.contains(category))
			{
				excluded.add(category);
				System.out.println(category+" exlcuded ");
			}
			else
			{
				System.out.println("The category is already excluded");
			}
        }
		catch(NumberFormatException e)
		{
			System.out.println("Please enter a valid number");
		}
    }
    return excluded;
}
    
    /** This will print all strange transactions like duplicates or anamolys found during the audit.
     * @param records list of flagged transactions
     * @author Joseph
     */
public static void printFindings(ArrayList<AuditFinding> records, FindingType type)
{
	System.out.println("===== ! Data Audit Results ! =====");
	if (records == null || records.isEmpty())
	{
		if(type == FindingType.DUPLICATE)
		{
			System.out.println("No duplicate transactions found");
		}
		else
		{
			System.out.println("No anomaly transactions found");
		}
        return;
    }
	int count = 0;
	for (AuditFinding finding : records) 
	{
		if (finding != null) 
		{
            count++;
			System.out.println();
            System.out.println("Finding #" + count);
            System.out.println("Transaction: " + finding.entry());
            System.out.println("Type: " + finding.type());
            System.out.println("Reason: " + finding.reason());
            System.out.println("----------------------------");
        }
    }
	if (count == 0) 
	{
       if(type == FindingType.DUPLICATE)
	   {
		   System.out.println("No duplicate transactions found");
	   }
	   else
	   {
		   System.out.println("No anomaly transaction found");
	   }
		
    }
    System.out.println("Total findings is : " + count);
}
    public record AuditFinding(String entry, FindingType type, String reason) {};
    /**
     * Mark a transaction as a duplicate or anomaly/outlier
     * @author Hamet
     */
    public enum FindingType { DUPLICATE, ANOMALY };

}

