import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Manages reading, writing, and deleting budget files.
 *
 * @author 
 *	Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public class BudgetFileManager {

    /**
     * Reads transactions from a CSV file.
     *
     * @param filePath the path to the CSV file
     * @return a list of transactions read from the file
     * @throws IOException if an error occurs while reading the file
     */
    public List<BudgetTransaction> readCsvFile(Path filePath)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Writes budget transactions to a CSV file.
     *
     * @param filePath the destination file path
     * @param transactions the transactions to write
     * @throws IOException if an error occurs while writing the file
     */
    public void writeCsvFile(
            Path filePath,
            List<BudgetTransaction> transactions)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Creates a new budget file.
     *
     * @param filePath the path of the new file
     * @throws IOException if the file cannot be created
     */
    public void createBudgetFile(Path filePath) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Deletes a budget file.
     *
     * @param filePath the path of the file to delete
     * @return true if the file was deleted
     * @throws IOException if an error occurs while deleting the file
     */
    public boolean deleteBudgetFile(Path filePath) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns the path associated with a user's annual budget.
     *
     * @param userName the user's account name
     * @param year the budget year
     * @return the budget file path
     */
    public Path getFilePath(String userName, int year) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Checks whether a budget file exists.
     *
     * @param filePath the path to check
     * @return true if the file exists
     */
    public boolean fileExists(Path filePath) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
