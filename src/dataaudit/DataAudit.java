import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Locale.Category;
import java.util.HashSet;


/**
 * Allows user to audit their data and see any potential anomalies and categorize data 
 * 
 * @author Hamet
 */
public class DataAudit {
    private final double ANOMALYBOUND = 2.5;  

    /**
     * Create data audit object
     * 
     * @author Hamet
     */
    public DataAudit(){};

    /**
     * Goes through data audit menu and asks the user what they want to do (exclude categories, find dupes/anamolies)
     * 
     * @param budgetFile transaction data
     * 
     * @author Haoyang
     */
    public void runAudit(Budget budgetFile){
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
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> excludedCategories = new ArrayList<>();
        boolean running = true;
        while(running){
            System.out.println("Data Audit Menu:");
            System.out.println("1. choose excluded categories");
            System.out.println("2. find duplicates");
            System.out.println("3. find anomalies");
            System.out.println("4. exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();
            switch(choice){
                case "1":
                    excludedCategories = chooseExcludedCategories();
                    System.out.println("Excluded categories:" + excludedCategories);
                    break;

                case "2":
                    System.out.println("Finding duplicates...");
                    ArrayList<AuditFinding> duplicates = findDuplicates(budgetFile);
                    printFindings(duplicates);
                    break;
                
                case "3": 
                    System.out.println("Finding anomalies...");
                    ArrayList<AuditFinding> anomalies = findAnomalies(budgetFile, excludedCategories);
                    printFindings(anomalies);
                    break; 
                     
                case "4":
                    running = false;
                   System.out.println("Exiting Data Audit Menu.");
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
    public ArrayList<AuditFinding> findDuplicates(Budget budgetFile)
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
    public ArrayList<AuditFinding> findAnomalies(Budget budgetFile, ArrayList<String> exclusion){
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
    /**
     * Asks user which categories they want to leave out in audit
     * 
     * @return categories chosen or empty array
     * 
     * @author Haoyang
     */
    
   /**Asks the user which categories to exclude from anomaly detection.
     * @return list of excluded categories
     * @author Joseph
     */
public ArrayList<String> chooseExcludedCategories() 
{
	ArrayList<String> excluded = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
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
public void printFindings(ArrayList<AuditFinding> records)
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

