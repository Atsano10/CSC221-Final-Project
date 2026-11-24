package storage;

import java.time.LocalDate;
import java.util.Set;

/**
 * Represents one income or expense transaction in an annual budget.
 *
 * @param date transaction date
 * @param category approved income or expense category
 * @param amount whole-dollar amount; income is positive and expenses are negative
 *
 * @author 
 *	Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public record BudgetTransaction(LocalDate date, String category, int amount) {

    private static final Set<String> INCOME_CATEGORIES =
            Set.of("Compensation", "Allowance", "Investments");

    private static final Set<String> EXPENSE_CATEGORIES =
            Set.of("Home", "Utilities", "Food", "Appearance", "Work",
                    "Education", "Transportation", "Entertainment",
                    "Professional Services");

    /**
     * Creates a transaction after checking its basic data.
     *
     * "Other" may be either income or expense, so its sign is not restricted
     * by category. All other categories have a required sign.
     * @author Ivan Chukharev
     */
    public BudgetTransaction {
        if (date == null) {
            throw new IllegalArgumentException("Transaction date cannot be null.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be blank.");
        }
        if (amount == 0) {
            throw new IllegalArgumentException("Amount cannot be zero.");
        }

        category = category.trim();

        if (!INCOME_CATEGORIES.contains(category)
                && !EXPENSE_CATEGORIES.contains(category)
                && !category.equals("Other")) {
            throw new IllegalArgumentException("Unapproved category: " + category);
        }

        if (INCOME_CATEGORIES.contains(category) && amount < 0) {
            throw new IllegalArgumentException("Income amounts must be positive.");
        }

        if (EXPENSE_CATEGORIES.contains(category) && amount > 0) {
            throw new IllegalArgumentException("Expense amounts must be negative.");
        }
    }
}

