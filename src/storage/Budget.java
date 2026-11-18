package storage;

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
     * Creates a budget and verifies that every transaction belongs to its year.
     */
    public Budget {
        if (year < 1) {
            throw new IllegalArgumentException("Year must be a positive number.");
        }
        if (transactions == null) {
            throw new IllegalArgumentException("Transaction list cannot be null.");
        }

        List<BudgetTransaction> copy = new ArrayList<>(transactions.size());

        for (BudgetTransaction transaction : transactions) {
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction cannot be null.");
            }
            if (transaction.date().getYear() != year) {
                throw new IllegalArgumentException(
                        "Every transaction must belong to budget year " + year + ".");
            }
            copy.add(transaction);
        }

        copy.sort(Comparator.comparing(BudgetTransaction::date));
        transactions = List.copyOf(copy);
    }

    /**
     * Returns the number of transactions in this budget.
     *
     * @return transaction count
     */
    public int transactionCount() {
        return transactions.size();
    }

    /**
     * Returns the total income in whole dollars.
     *
     * @return sum of positive income amounts
     */
    public long totalIncome() {
        return transactions.stream()
                .filter(transaction -> transaction.amount() > 0)
                .mapToLong(BudgetTransaction::amount)
                .sum();
    }

    /**
     * Returns the total expenses as a positive whole-dollar amount.
     *
     * @return absolute value of the sum of negative expense amounts
     */
    public long totalExpenses() {
        return -transactions.stream()
                .filter(transaction -> transaction.amount() < 0)
                .mapToLong(BudgetTransaction::amount)
                .sum();
    }

    /**
     * Returns income minus expenses.
     *
     * @return net amount for the year
     */
    public long netTotal() {
        return transactions.stream()
                .mapToLong(BudgetTransaction::amount)
                .sum();
    }
}
