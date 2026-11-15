import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Provides storage operations for users' annual budgets.
 *
 * Responsible for creating, reading, updating, and deleting
 * budget objects and maintaining budget data between sessions.
 *
 * @author 
 *	Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public class BudgetStorage {

    /**
     * Loads a user's budget for a specified year.
     *
     * @param userName the user's account name
     * @param year the budget year
     * @return the requested budget, or empty if it does not exist
     * @throws IOException if an error occurs while loading the budget
     */
    public Optional<Budget> loadBudget(String userName, int year)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Saves a user's annual budget.
     *
     * @param userName the user's account name
     * @param budget the budget to save
     * @throws IOException if an error occurs while saving the budget
     */
    public void saveBudget(String userName, Budget budget)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Updates an existing annual budget.
     *
     * @param userName the user's account name
     * @param budget the updated budget
     * @throws IOException if an error occurs while updating the budget
     */
    public void updateBudget(String userName, Budget budget)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Deletes a user's budget for a specified year.
     *
     * @param userName the user's account name
     * @param year the budget year to delete
     * @return true if the budget was deleted
     * @throws IOException if an error occurs while deleting the budget
     */
    public boolean deleteBudget(String userName, int year)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns all years for which the user has stored budgets.
     *
     * @param userName the user's account name
     * @return a list of available budget years
     * @throws IOException if an error occurs while retrieving the years
     */
    public List<Integer> getAvailableYears(String userName)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Checks whether a budget exists for a specified year.
     *
     * @param userName the user's account name
     * @param year the budget year
     * @return true if the budget exists
     */
    public boolean budgetExists(String userName, int year) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Retrieves a user's budget for a specified year.
     *
     * @param userName the user's account name
     * @param year the budget year
     * @return the requested budget, or null if it does not exist
     * @throws IOException if an error occurs while retrieving the budget
     */
    public Budget getBudget(String userName, int year)
            throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}