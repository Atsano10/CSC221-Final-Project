package storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Standalone check (not part of the application) that the storage layer saves,
 * loads, changes and removes data correctly.
 *
 * Run from the project root:
 *   javac -d out src/validations/*.java src/storage/*.java
 *   java -cp out storage.StorageRoundTripTest
 *
 * Uses throwaway usernames and removes everything it creates.
 *
 * @author Fahim Taleb
 */
public final class StorageRoundTripTest {

    private static final String USER = "zz_roundtrip_test_user";
    private static final String BUDGET_USER = "zz_roundtrip_budget_user";

    private static int passed = 0;
    private static int failed = 0;

    private StorageRoundTripTest() {
    }

    public static void main(String[] args) {
        try {
            testAccounts();
            testMissingData();
            testBudgetRoundTrip();
            testBudgetUpdateAndDelete();
            testInvalidRowsRejected();
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL: unexpected exception " + e);
        } finally {
            cleanup();
        }

        System.out.println();
        System.out.println("Passed: " + passed + "  Failed: " + failed);
        System.exit(failed == 0 ? 0 : 1);
    }

    // ---------------------------------------------------------------- accounts

    private static void testAccounts() {
        AccountStorage.deleteUser(USER);

        check("account not found before save", !AccountStorage.findUser(USER));
        check("account saves", AccountStorage.saveUser(USER, "hash1", "Pet?", "ans1"));
        check("account found after save", AccountStorage.findUser(USER));
        check("duplicate account rejected",
                !AccountStorage.saveUser(USER, "other", "Q", "A"));

        AccountStorage.StoredUser u = AccountStorage.getUserInfo(USER);
        check("account data loads intact", u != null
                && u.hashedPassword().equals("hash1")
                && u.question().equals("Pet?")
                && u.hashedAnswer().equals("ans1"));

        check("password updates", AccountStorage.updatePassword(USER, "hash2"));
        u = AccountStorage.getUserInfo(USER);
        check("new password stored, question kept", u != null
                && u.hashedPassword().equals("hash2")
                && u.question().equals("Pet?"));

        check("question/answer updates",
                AccountStorage.updateQuestionAndAnswer(USER, "Car?", "ans2"));
        u = AccountStorage.getUserInfo(USER);
        check("new question stored, password kept", u != null
                && u.question().equals("Car?")
                && u.hashedAnswer().equals("ans2")
                && u.hashedPassword().equals("hash2"));

        check("account deletes", AccountStorage.deleteUser(USER));
        check("account gone after delete", !AccountStorage.findUser(USER));
    }

    private static void testMissingData() throws IOException {
        check("null username not found", !AccountStorage.findUser(null));
        check("null user info is null", AccountStorage.getUserInfo(null) == null);
        check("null save refused", !AccountStorage.saveUser(null, "h", "q", "a"));
        check("update unknown user refused",
                !AccountStorage.updatePassword("zz_no_such_user", "h"));
        check("delete unknown user refused", !AccountStorage.deleteUser("zz_no_such_user"));

        check("missing budget exists() is false",
                !BudgetStorage.budgetExists(BUDGET_USER, 2001));
        check("missing budget getBudget() is null",
                BudgetStorage.getBudget(BUDGET_USER, 2001) == null);
        check("missing budget update refused",
                !BudgetStorage.updateBudget(BUDGET_USER, sampleBudget(2001)));
        check("no years for unknown user",
                BudgetStorage.getAvailableYears(BUDGET_USER).isEmpty());

        boolean threw = false;
        try {
            BudgetStorage.loadBudget(BUDGET_USER, 2001);
        } catch (IOException e) {
            threw = true;
        }
        check("loading missing budget throws IOException", threw);

        threw = false;
        try {
            BudgetStorage.getBudget("../escape", 2001);
        } catch (RuntimeException e) {
            threw = true;
        }
        check("bad username does not escape or crash getBudget", !threw);
    }

    // ----------------------------------------------------------------- budgets

    private static void testBudgetRoundTrip() throws IOException {
        Budget original = sampleBudget(2002);

        check("budget saves", BudgetStorage.saveBudget(BUDGET_USER, original, false));
        check("budget exists after save", BudgetStorage.budgetExists(BUDGET_USER, 2002));
        check("save without overwrite refuses existing",
                !BudgetStorage.saveBudget(BUDGET_USER, original, false));

        Budget loaded = BudgetStorage.loadBudget(BUDGET_USER, 2002);
        check("loaded year matches", loaded.year() == 2002);
        check("loaded transactions identical (nothing lost)",
                loaded.transactions().equals(original.transactions()));
        check("loaded totals match", loaded.totalIncome() == original.totalIncome()
                && loaded.totalExpenses() == original.totalExpenses());
    }

    private static void testBudgetUpdateAndDelete() throws IOException {
        List<BudgetTransaction> changed = List.of(
                new BudgetTransaction(LocalDate.of(2002, 5, 5), "Investments", 999));
        check("budget updates",
                BudgetStorage.updateBudget(BUDGET_USER, new Budget(2002, changed)));
        check("update persisted",
                BudgetStorage.loadBudget(BUDGET_USER, 2002).transactions().equals(changed));

        BudgetStorage.saveBudget(BUDGET_USER, sampleBudget(2003), false);
        check("available years listed in order",
                BudgetStorage.getAvailableYears(BUDGET_USER).equals(List.of(2002, 2003)));

        check("budget deletes", BudgetStorage.deleteBudget(BUDGET_USER, 2002));
        check("budget gone after delete", !BudgetStorage.budgetExists(BUDGET_USER, 2002));
        check("delete all budgets", BudgetStorage.deleteAllBudgets(BUDGET_USER));
        check("no years left", BudgetStorage.getAvailableYears(BUDGET_USER).isEmpty());
    }

    private static void testInvalidRowsRejected() throws IOException {
        Path file = BudgetFileManager.getFilePath(BUDGET_USER, 2004);

        expectRejected(file, "blank line",
                "Date,Category,Amount\n01/01/2004,Food,-5\n\n01/02/2004,Food,-6\n");
        expectRejected(file, "too few columns",
                "Date,Category,Amount\n01/01/2004,Food\n");
        expectRejected(file, "too many columns",
                "Date,Category,Amount\n01/01/2004,Food,-5,extra\n");
        expectRejected(file, "bad date",
                "Date,Category,Amount\n13/45/2004,Food,-5\n");
        expectRejected(file, "bad category",
                "Date,Category,Amount\n01/01/2004,Snacks,-5\n");
        expectRejected(file, "decimal amount",
                "Date,Category,Amount\n01/01/2004,Food,-5.50\n");
        expectRejected(file, "wrong sign",
                "Date,Category,Amount\n01/01/2004,Food,5\n");
        expectRejected(file, "wrong header",
                "Date,Type,Amount\n01/01/2004,Food,-5\n");

        // A valid file must still load.
        Files.createDirectories(file.getParent());
        Files.writeString(file,
                "Date,Category,Amount\n01/01/2004,Food,-5\n01/02/2004,Compensation,100\n",
                StandardCharsets.UTF_8);
        try {
            check("valid file loads with 2 rows",
                    BudgetFileManager.readCsvFile(file).transactionCount() == 2);
        } catch (IOException e) {
            check("valid file loads with 2 rows (" + e.getMessage() + ")", false);
        }
    }

    // ----------------------------------------------------------------- helpers

    private static Budget sampleBudget(int year) {
        return new Budget(year, List.of(
                new BudgetTransaction(LocalDate.of(year, 1, 15), "Compensation", 3000),
                new BudgetTransaction(LocalDate.of(year, 2, 1), "Home", -1200),
                new BudgetTransaction(LocalDate.of(year, 2, 28), "Food", -85),
                new BudgetTransaction(LocalDate.of(year, 12, 31), "Other", -40)));
    }

    private static void expectRejected(Path file, String label, String content)
            throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
        boolean threw = false;
        try {
            BudgetFileManager.readCsvFile(file);
        } catch (IOException e) {
            threw = true;
        }
        check("rejects " + label, threw);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    private static void cleanup() {
        try {
            AccountStorage.deleteUser(USER);
            BudgetStorage.deleteAllBudgets(BUDGET_USER);
        } catch (Exception e) {
            System.out.println("Cleanup problem: " + e);
        }
    }
}
