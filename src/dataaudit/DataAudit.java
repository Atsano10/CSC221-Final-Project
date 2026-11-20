package dataaudit; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
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
                    printFindings(duplicates);
					System.out.println();
                    break;
                
                case "3": 
                    System.out.println("\nFinding anomalies...");
                    ArrayList<AuditFinding> anomalies = findAnomalies(budgetFile, excludedCategories);
                    printFindings(anomalies);
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
     * 
     * Finds outlier data
     * 
     * @param budgetFile transaction data
     * @return list of anomaly entries
     * 
     * @author Hamet 
     */
    public static ArrayList<AuditFinding> findAnomalies(Budget budgetFile, ArrayList<String> exclusion){
        ArrayList<AuditFinding> anamolies = new ArrayList<>();
        double median;
        List<BudgetTransaction> copy = new ArrayList<>(budgetFile.transactions());
        for (int i = 0; i < copy.size(); i++) {
            if (exclusion.contains(copy.get(i).category())) {
                copy.remove(i);
                i--;
                continue;
            }
        }
        Collections.sort(copy, (a, b) -> Integer.compare(Math.abs(a.amount()), Math.abs(b.amount())));
        if (copy.size() % 2 == 0) {
            median = (Math.abs(copy.get((copy.size() / 2) - 1).amount()) + Math.abs(copy.get(copy.size() / 2).amount())) / 2.0;
        }
        else {
            median = Math.abs(copy.get(copy.size() / 2).amount());
        }
        double bound = ANOMALYBOUND * median;
        for (int i = 0; i < copy.size(); i++) {
            if (Math.abs(copy.get(i).amount()) > bound) {
                FindingType type = FindingType.ANOMALY;
                String reason = String.format("Amount %d is out of normal range", copy.get(i).amount());
                AuditFinding temp = new AuditFinding(copy.get(i).category(), type, reason );
                anamolies.add(temp);
            }
        }
        return anamolies;
    };
   /**Asks the user which categories to exclude from anomaly detection.
     * @return list of excluded categories
     * @author Joseph
     */
public static ArrayList<String> chooseExcludedCategories(Scanner scanner) 
{
	ArrayList<String> excluded = new ArrayList<>();
    System.out.println("Enter categories you want to exclude:");
    System.out.println("Type DONE when you are finished.");
    while (true) 
	{
        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        if (category.equalsIgnoreCase("DONE")) 
		{
			break;
        }
		if (!category.isEmpty()) 
		{
            excluded.add(category);
        }
    }
    return excluded;
}
    
    /** This will print all strange transactions found during the audit.
     * @param records array of flagged transactions
     * @author Joseph
     */
public static void printFindings(ArrayList<AuditFinding> records)
{
	System.out.println("===== ! Data Audit Results ! =====");
	if (records == null || records.isEmpty())
	{
        System.out.println("There were no strange transactions found.");
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
        System.out.println("There were no strange transactions found.");
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

