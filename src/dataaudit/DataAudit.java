import java.util.ArrayList;
import java.util.Scanner;
import java.util.Locale.Category;


/**
 * Allows user to audit their data and see any potential anomalies and categorize data 
 * 
 * @author Hamet
 */
public class DataAudit {
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
     * @author Javier
     */
    public void runAudit(Budget budgetFile){};

    /**
     * Finds transactions that are the same
     * 
     * @param budgetFile transaction data
     * @return list of entries for each duplicate
     * 
     * @author Javier
     */
    public ArrayList<AuditFinding> findDuplicates(Budget budgetFile){
        return new ArrayList<>();
    };

    /**
     * 
     * Finds outlier data
     * 
     * @param budgetFile transaction data
     * @return list of anomaly entries
     * 
     * @author Haoyang 
     */
    public ArrayList<AuditFinding> findAnomalies(Budget budgetFile, ArrayList<String> exclusion){
        return new ArrayList<>();
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

    public static void main(String[] args) {
        
    }
}

