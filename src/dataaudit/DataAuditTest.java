import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DataAuditTest {
    public static void main(String[] args) {
        List<BudgetTest> txns = List.of(
            new BudgetTest(LocalDate.of(2026, 1, 15), "Compensation", 4000),
            new BudgetTest(LocalDate.of(2026, 2, 15), "Compensation", 4000),
            new BudgetTest(LocalDate.of(2026, 3, 15), "Compensation", 40000), // anomaly
            new BudgetTest(LocalDate.of(2026, 3, 5),  "Food", -200),
            new BudgetTest(LocalDate.of(2026, 3, 5),  "Food", -200),          // duplicate
            new BudgetTest(LocalDate.of(2026, 3, 5),  "Food", -45)            // not a duplicate
        );
        Budget budget = new Budget(2026, txns);

        DataAudit audit = new DataAudit();
        System.out.println(audit.findDuplicates(budget));
        System.out.println(audit.findAnomalies(budget, Set.of()));
    }
}