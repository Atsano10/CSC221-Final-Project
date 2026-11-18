package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
* Provides CRUD operations for users' annual budgets.
 *
 * Each user has a directory under data/budgets, and each calendar year is
 * stored in one CSV file named YYYY.csv.
 *
 * @author 
 *	Nishat Salsabeel, Team Lead                        
 *	Ivan Chukharev
 *	Fahim Taleb
 *	Nusru Zaman
 */
public final class BudgetStorage {

    private BudgetStorage() {
    }

    /**
     * Loads a user's budget for a specified year.
     *
     * @param username account username
     * @param year budget year
     * @return loaded budget
     * @throws IOException if the file is missing or invalid
     */
    public static Budget loadBudget(String username, int year) throws IOException {
        Path file = BudgetFileManager.getFilePath(username, year);
        return BudgetFileManager.readCsvFile(file);
    }

    /**
     * Saves a budget. Existing data is not overwritten unless overwrite is true.
     *
     * @param username account username
     * @param budget budget to save
     * @param overwrite whether an existing year's file may be replaced
     * @return true if saved; false if the file already exists and overwrite is false
     * @throws IOException if writing fails
     */
    public static boolean saveBudget(String username, Budget budget,
            boolean overwrite) throws IOException {

        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null.");
        }

        Path file = BudgetFileManager.getFilePath(username, budget.year());
        if (BudgetFileManager.fileExists(file) && !overwrite) {
            return false;
        }

        BudgetFileManager.writeCsvFile(budget, file);
        return true;
    }

    /**
     * Replaces an existing annual budget.
     *
     * @param username account username
     * @param budget updated budget
     * @return true if an existing budget was updated
     * @throws IOException if writing fails
     */
    public static boolean updateBudget(String username, Budget budget)
            throws IOException {

        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null.");
        }

        Path file = BudgetFileManager.getFilePath(username, budget.year());
        if (!BudgetFileManager.fileExists(file)) {
            return false;
        }

        BudgetFileManager.writeCsvFile(budget, file);
        return true;
    }

    /**
     * Deletes one year's budget.
     *
     * @param username account username
     * @param year budget year
     * @return true if the budget file was deleted
     * @throws IOException if deletion fails
     */
    public static boolean deleteBudget(String username, int year) throws IOException {
        Path file = BudgetFileManager.getFilePath(username, year);
        return BudgetFileManager.deleteBudgetFile(file);
    }

    /**
     * Returns all years for which a user has budget files.
     *
     * @param username account username
     * @return ascending list of available years
     * @throws IOException if the user's budget directory cannot be read
     */
    public static List<Integer> getAvailableYears(String username) throws IOException {
        // Validate username and get the user's directory through the path helper.
        Path samplePath = BudgetFileManager.getFilePath(username, 1);
        Path directory = samplePath.getParent();

        if (!Files.isDirectory(directory)) {
            return List.of();
        }

        List<Integer> years = new ArrayList<>();
        try (Stream<Path> paths = Files.list(directory)) {
            paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.matches("\\d{4}\\.csv"))
                    .map(name -> Integer.parseInt(name.substring(0, 4)))
                    .sorted()
                    .forEach(years::add);
        }
        return List.copyOf(years);
    }

    /**
     * Checks whether a user's budget exists for a year.
     *
     * @param username account username
     * @param year budget year
     * @return true if the annual CSV exists
     */
    public static boolean budgetExists(String username, int year) {
        try {
            return BudgetFileManager.fileExists(
                    BudgetFileManager.getFilePath(username, year));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets a budget if it exists.
     *
     * @param username account username
     * @param year budget year
     * @return budget, or null if it does not exist or cannot be read
     */
    public static Budget getBudget(String username, int year) {
        try {
            if (!budgetExists(username, year)) {
                return null;
            }
            return loadBudget(username, year);
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Deletes all annual budget files belonging to a user.
     * Intended to be called by account deletion after the account is verified.
     *
     * @param username account username
     * @return true if the user's budget directory was removed or did not exist
     * @throws IOException if a file cannot be deleted
     */
    public static boolean deleteAllBudgets(String username) throws IOException {
        Path directory = BudgetFileManager.getFilePath(username, 1).getParent();

        if (!Files.exists(directory)) {
            return true;
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            List<Path> allPaths = paths.sorted(Comparator.reverseOrder()).toList();
            for (Path path : allPaths) {
                Files.delete(path);
            }
        }
        return true;
    }
}

