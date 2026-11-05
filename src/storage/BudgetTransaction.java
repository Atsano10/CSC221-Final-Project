import java.time.LocalDate;

/**
 * Represents one income or expense transaction in an annual budget.
 *
 * @author 
 *	Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public record BudgetTransaction(
        LocalDate date,
        String category,
        int amount) {
}