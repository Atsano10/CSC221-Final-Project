package storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import validations.CsvValidator;

/**
 * Reads and writes annual budget CSV files.
 *
 * CSV format:
 * Date,Category,Amount
 * MM/DD/YYYY,Category,whole-dollar-amount
 *
 * Income is positive and expenses are negative. "Other" may have either sign.
 *
 * @author Ivan Chukharev
 *
 */
public final class BudgetFileManager {

    private static final String HEADER = "Date,Category,Amount";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.US)
                    .withResolverStyle(ResolverStyle.STRICT);

    private BudgetFileManager() {
    }

    /**
     * Builds the path for one user's annual budget file.
     *
     * @param username account username
     * @param year budget year
     * @return path such as data/budgets/username/2026.csv
     * @author  Ivan Chukharev
     */
    public static Path getFilePath(String username, int year) {
        validateUsername(username);
        if (year < 1) {
            throw new IllegalArgumentException("Year must be positive.");
        }

        Path root = Path.of("data", "budgets").toAbsolutePath().normalize();
        Path result = root.resolve(username).resolve(year + ".csv").normalize();

        if (!result.startsWith(root)) {
            throw new IllegalArgumentException("Invalid username for file path.");
        }
        return result;
    }

    /**
     * Checks whether a file exists.
     *
     * @param filePath file to check
     * @return true if it is a regular file
     * @author  Ivan Chukharev
     */
    public static boolean fileExists(Path filePath) {
        return filePath != null && Files.isRegularFile(filePath);
    }

    /**
     * Creates an empty budget CSV containing its required header.
     *
     * @param filePath destination path
     * @throws IOException if the file cannot be created
     * @author  Ivan Chukharev
     */
    public static void createBudgetFile(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null.");
        }
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(filePath, HEADER + System.lineSeparator(),
                StandardCharsets.UTF_8);
    }

    /**
     * Reads and validates an annual budget CSV file.
     *
     * @param filePath CSV file to read
     * @return parsed budget
     * @throws IOException if the file cannot be read or contains invalid data
     * @author  Ivan Chukharev
     */
    public static Budget readCsvFile(Path filePath) throws IOException {
        if (filePath == null || !Files.isRegularFile(filePath)) {
            throw new IOException("Budget CSV file does not exist.");
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !HEADER.equals(lines.get(0).trim())) {
            throw new IOException("CSV header must be exactly: " + HEADER);
        }

        String fileName = filePath.getFileName().toString();
        if (!fileName.matches("\\d{4}\\.csv")) {
            throw new IOException("Budget filename must use YYYY.csv format.");
        }
        int fileYear = Integer.parseInt(fileName.substring(0, 4));
        List<BudgetTransaction> transactions = new ArrayList<>();

        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber).trim();

            // Use the validation team's row check (blank lines, column count,
            // date, category and amount). Any problem rejects the whole file.
            List<String> problems = CsvValidator.validateRow(line);
            if (!problems.isEmpty()) {
                throw new IOException("Invalid row on line " + (lineNumber + 1)
                        + ": " + String.join("; ", problems) + ".");
            }

            String[] fields = line.split(",", -1);
            if (fields.length != 3) {
                throw new IOException("Invalid number of columns on line "
                        + (lineNumber + 1) + ".");
            }

            try {
                LocalDate date = LocalDate.parse(fields[0].trim(), DATE_FORMAT);
                if (date.getYear() != fileYear) {
                    throw new IOException("Date year does not match filename on line "
                            + (lineNumber + 1) + ".");
                }

                String category = fields[1].trim();
                String amountText = fields[2].trim();

                if (!amountText.matches("-?\\d+")) {
                    throw new IOException("Amount must be a whole number on line "
                            + (lineNumber + 1) + ".");
                }

                int amount;
                try {
                    amount = Integer.parseInt(amountText);
                } catch (NumberFormatException e) {
                    throw new IOException("Amount is outside the supported range on line "
                            + (lineNumber + 1) + ".", e);
                }

                transactions.add(new BudgetTransaction(date, category, amount));
            } catch (DateTimeParseException | IllegalArgumentException e) {
                throw new IOException("Invalid transaction on line "
                        + (lineNumber + 1) + ": " + e.getMessage(), e);
            }
        }

        try {
            return new Budget(fileYear, transactions);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid budget data: " + e.getMessage(), e);
        }
    }

    /**
     * Writes a budget to a CSV file, replacing any existing file.
     *
     * @param budget budget to write
     * @param filePath destination CSV path
     * @throws IOException if the file cannot be written
     * @author  Ivan Chukharev
     */
    public static void writeCsvFile(Budget budget, Path filePath) throws IOException {
        if (budget == null || filePath == null) {
            throw new IllegalArgumentException("Budget and file path are required.");
        }

        String expectedName = budget.year() + ".csv";
        if (!filePath.getFileName().toString().equals(expectedName)) {
            throw new IOException("Budget filename must be " + expectedName + ".");
        }

        Path absolutePath = filePath.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path tempFile = Files.createTempFile(parent, "budget-", ".tmp");
        try {
            List<String> lines = new ArrayList<>();
            lines.add(HEADER);

            for (BudgetTransaction transaction : budget.transactions()) {
                lines.add(DATE_FORMAT.format(transaction.date()) + ","
                        + transaction.category() + ","
                        + transaction.amount());
            }

            Files.write(tempFile, lines, StandardCharsets.UTF_8);

            try {
                Files.move(tempFile, absolutePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tempFile, absolutePath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Deletes a budget CSV file.
     *
     * @param filePath file to delete
     * @return true if a file was deleted
     * @throws IOException if deletion fails
     * @author  Ivan Chukharev
     */
    public static boolean deleteBudgetFile(Path filePath) throws IOException {
        if (filePath == null) {
            return false;
        }
        return Files.deleteIfExists(filePath);
    }

    private static void validateUsername(String username) {
        if (username == null || username.isBlank()
                || username.equals(".") || username.equals("..")
                || username.contains("/") || username.contains("\\")
                || username.contains("\u0000")) {
            throw new IllegalArgumentException("Invalid username.");
        }
    }
}

