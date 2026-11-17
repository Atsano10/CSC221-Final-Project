package validations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Validation utilities for budget CSV files.
 *
 * @author Shadman
 */
public final class CsvValidator {

	private static final List<String> INCOME_CATEGORIES = List.of("Compensation", "Allowance", "Investments", "Other");

	private static final List<String> EXPENSE_CATEGORIES = List.of("Home", "Utilities", "Food", "Appearance", "Work",
			"Education", "Transportation", "Entertainment", "Professional Services", "Other");

	/**
	 * Days in each month, January to December. February is fixed up for leap years.
	 */
	private static final int[] DAYS_IN_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/**
	 * Private constructor that prevents creation of a CsvValidator object.
	 *
	 * @author Shadman
	 */
	private CsvValidator() {

	}

	/**
	 * Checks that the first line of the file is exactly "Date,Category,Amount".
	 *
	 * @param headerLine The first line of the file.
	 * @return True if the line is exactly Date, Category, Amount.
	 *
	 * @author Shadman
	 */
	public static boolean validateHeader(String headerLine) {
		if (headerLine == null) {
			return false;
		}
		// Handling the BOM.
		String line = headerLine;
		if (line.startsWith("\uFEFF")) {
			line = line.substring(1);
		}
		String[] columns = line.split(",", -1);
		if (columns.length != 3) {
			return false;
		}
		return columns[0].trim().equals("Date") && columns[1].trim().equals("Category")
				&& columns[2].trim().equals("Amount");
	}

	/**
	 * Checks that a date is a real calendar date in MM/DD/YYYY format, with leading
	 * zeros required.
	 *
	 * @param date The date string from the file, for example "03/15/2025".
	 * @return True if the date is in MM/DD/YYYY format and is a real calendar date.
	 *
	 * @author Shadman
	 */
	public static boolean validateDate(String date) {
		// Enforcing MM/DD/YYYY format.
		if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}")) {
			return false;
		}

		int month = Integer.parseInt(date.substring(0, 2));
		int day = Integer.parseInt(date.substring(3, 5));
		int year = Integer.parseInt(date.substring(6, 10));

		if (month < 1 || month > 12) {
			return false;
		}

		int maxDay = DAYS_IN_MONTH[month - 1];
		if (month == 2 && leapYear(year)) {
			maxDay = 29;
		}

		return day >= 1 && day <= maxDay;
	}

	/**
	 * Checks whether a year is a leap year.
	 *
	 * @param year The year to check.
	 * @return True if the year is a leap year.
	 *
	 * @author Shadman
	 */
	private static boolean leapYear(int year) {
		boolean divisibleBy4 = year % 4 == 0;
		boolean divisibleBy100 = year % 100 == 0;
		boolean divisibleBy400 = year % 400 == 0;

		return divisibleBy4 && (!divisibleBy100 || divisibleBy400);
	}

	/**
	 * Checks that the category is one of the allowed line items.
	 * <p>
	 * Income: Compensation, Allowance, Investments, Other
	 * <p>
	 * Expenses: Home, Utilities, Food, Appearance, Work, Education, Transportation,
	 * Entertainment, Professional Services, Other
	 *
	 * @param category The category string from the file.
	 * @return True if the category is an allowed income or expense category.
	 *
	 * @author Shadman
	 */
	public static boolean validateCategory(String category) {
		if (category == null) {
			return false;
		}
		return INCOME_CATEGORIES.contains(category) || EXPENSE_CATEGORIES.contains(category);
	}

	/**
	 * Checks that the amount is a whole number whose sign matches the category
	 * (income positive, expense negative). "Other" is checked for format only.
	 * <p>
	 * If the category is not a recognized category, only the format is checked,
	 * because the sign cannot be judged. The bad category is reported separately by
	 * {@link #validateCategory(String)}.
	 *
	 * @param amount   The amount string from the file, for example "-45".
	 * @param category The category from the same row, used to check the sign.
	 * @return True if the amount is a whole number and its sign matches the
	 *         category.
	 *
	 * @author Julio
	 */
	public static boolean validateAmount(String amount, String category) {
		if (!isWholeNumber(amount)) {
			return false;
		}
		String trimmed = amount.trim();

		// Sign can't be checked for "Other" or unknown categories.
		if (category == null || category.equals("Other") || !validateCategory(category)) {
			return true;
		}

		// A zero amount is neither income nor an expense.
		if (isZero(trimmed)) {
			return false;
		}

		boolean negative = trimmed.startsWith("-");
		if (INCOME_CATEGORIES.contains(category)) {
			return !negative;
		}
		return negative;
	}

	/**
	 * Checks that a string is a whole number: digits only, with an optional leading
	 * minus sign. No cents, commas, dollar signs or plus signs.
	 *
	 * @param amount The amount string to check.
	 * @return True if the string is a whole number.
	 *
	 * @author Julio
	 */
	private static boolean isWholeNumber(String amount) {
		return amount != null && amount.trim().matches("-?\\d+");
	}

	/**
	 * Checks whether a whole-number string (already known to be well formed) is
	 * zero, such as "0", "-0" or "000".
	 *
	 * @param amount A well-formed whole-number string.
	 * @return True if every digit is zero.
	 *
	 * @author Julio
	 */
	private static boolean isZero(String amount) {
		return amount.replace("-", "").matches("0+");
	}

	/**
	 * Validates one data line by splitting it into 3 columns and checking each.
	 *
	 * @param line One data line from the file.
	 * @return The list of problems found in the row, or an empty list if the row is
	 *         valid.
	 *
	 * @author Julio
	 */
	public static List<String> validateRow(String line) {
		List<String> problems = new ArrayList<>();

		if (line == null || line.trim().isEmpty()) {
			problems.add("blank line");
			return problems;
		}

		String[] columns = line.split(",", -1);
		if (columns.length != 3) {
			problems.add("expected 3 columns but found " + columns.length);
			return problems;
		}

		String date = columns[0].trim();
		String category = columns[1].trim();
		String amount = columns[2].trim();

		if (!validateDate(date)) {
			problems.add("invalid date");
		}
		if (!validateCategory(category)) {
			problems.add("invalid category");
		}
		if (!isWholeNumber(amount)) {
			problems.add("invalid amount (must be a whole number, no cents)");
		} else if (!validateAmount(amount, category)) {
			if (isZero(amount)) {
				problems.add("invalid amount (must not be zero)");
			} else if (INCOME_CATEGORIES.contains(category) && !EXPENSE_CATEGORIES.contains(category)) {
				problems.add("invalid amount (income must be positive)");
			} else {
				problems.add("invalid amount (expenses must be negative)");
			}
		}

		return problems;
	}

	/**
	 * Validates a whole file: the header, every row, that all dates share one year,
	 * and that the year matches the file name (YYYY.csv).
	 *
	 * @param filePath The path of the CSV file to validate.
	 * @return A report as a list of messages, one per problem, each including the
	 *         line number (for example "Line 5: invalid date"), or an empty list if
	 *         the file is valid. Problems about the file as a whole (unreadable,
	 *         empty, file name) have no line number.
	 *
	 * @author Julio
	 */
	public static List<String> validateFile(String filePath) {
		List<String> report = new ArrayList<>();

		if (filePath == null || filePath.trim().isEmpty()) {
			report.add("No file path was given");
			return report;
		}

		Path path;
		List<String> lines;
		try {
			path = Paths.get(filePath.trim());
			lines = Files.readAllLines(path);
		} catch (IOException | RuntimeException e) {
			report.add("Cannot read file: " + filePath.trim() + " (" + e.getClass().getSimpleName() + ")");
			return report;
		}

		// Ignore blank lines at the end of the file.
		int last = lines.size();
		while (last > 0 && lines.get(last - 1).trim().isEmpty()) {
			last--;
		}
		if (last == 0) {
			report.add("File is empty");
			return report;
		}

		if (!validateHeader(lines.get(0))) {
			report.add("Line 1: invalid header (must be exactly Date,Category,Amount)");
		}

		if (last == 1) {
			report.add("File has a header but no data rows");
		}

		String fileYear = null;
		for (int i = 1; i < last; i++) {
			int lineNumber = i + 1;
			String line = lines.get(i);

			for (String problem : validateRow(line)) {
				report.add("Line " + lineNumber + ": " + problem);
			}

			String date = line.split(",", -1)[0].trim();
			if (validateDate(date)) {
				String year = date.substring(6, 10);
				if (fileYear == null) {
					fileYear = year;
				} else if (!year.equals(fileYear)) {
					report.add("Line " + lineNumber + ": year " + year + " does not match year " + fileYear);
				}
			}
		}

		// The file name must be YYYY.csv and match the year of the dates.
		Path nameOnly = path.getFileName();

		String name;
		if (nameOnly == null) {
			name = "";
		} else {
			name = nameOnly.toString();
		}

		if (!name.matches("\\d{4}\\.[cC][sS][vV]")) {
			report.add("File name \"" + name + "\" must be in the form YYYY.csv");
		} else if (fileYear != null && !name.substring(0, 4).equals(fileYear)) {
			report.add(
					"File name year " + name.substring(0, 4) + " does not match the year " + fileYear + " in the file");
		}

		return report;
	}
}