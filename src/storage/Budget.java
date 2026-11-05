import java.util.List;

/**
 * Represents a user's annual income and expense budget.
 *
 * A budget contains the calendar year and a list of transactions.
 *
 * @author 
 *	Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public record Budget(int year, List<BudgetTransaction> transactions) {

    /**
     * Creates a budget and stores a copy of its transactions.
     *
     * @param year the calendar year of the budget
     * @param transactions the list of budget transactions
     */
    public Budget {
        transactions = List.copyOf(transactions);
    }
}