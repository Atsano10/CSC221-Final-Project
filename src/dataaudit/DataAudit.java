import java.util.ArrayList;

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
     * Finds transations that are the same
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
     * @return list of anamoly entries
     * 
     * @author Haoyang 
     */
    public ArrayList<AuditFinding> findAnomalies(Budget budgetFile){};
    /**
     * Asks user which categories they want to leave out in audit
     * 
     * @return categories chosen or empty array
     * 
     * @author Haoyang
     */
    public void chooseExcludedCategories() {
        return new ArrayList<>();
    }
    /**
     * print what the audit found
     * @param records array of bad data
     * 
     * @author Joseph
     */
    public void printFindings(AuditFinding[] records) {};
    
    /**
     * Record of the flagged transaction from an audit
     * @param entry which transaction was marked
     * @param type was it anamoly or duplicate
     * @param reason explaination of flagging
     * 
     * @author Joseph
     */
    public record AuditFinding(String entry, FindingType type, String reason) {};
    /**
     * Mark a transaction as a duplicate or anamoly/outlier
     * @author Hamet
     */
    public enum FindingType { DUPLICATE, ANOMALY };

    
}
