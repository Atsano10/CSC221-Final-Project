import java.util.List;

/**
 * Validation utilities for budget CSV files.
 *
 * @author Shadman
 */
public final class CsvValidator {

	/**
	 * Private constructor that prevents creation of a CsvValidator object.
	 *
	 * @author Shadman
	 */
	private CsvValidator() {

	}

	/**
	 * Checks that the first line of the file is exactly "Date, Category, Amount".
	 *
	 * @param headerLine The first line of the file.
	 * @return True if the line is exactly Date, Category, Amount.
	 *
	 * @author Shadman
	 */
	public static boolean validateHeader(String headerLine) {
		return false;
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
		return false;
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
		return false;
	}

	/**
	 * Checks that the amount is a whole number whose sign matches the category
	 * (income positive, expense negative). "Other" is checked for format only.
	 *
	 * @param amount   The amount string from the file, for example "-45".
	 * @param category The category from the same row, used to check the sign.
	 * @return True if the amount is a whole number and its sign matches the
	 *         category.
	 *
	 * @author Julio
	 */
	public static boolean validateAmount(String amount, String category) {
		return false;
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
		return null;
	}

	/**
	 * Validates a whole file: the header, every row, that all dates share one year,
	 * and that the year matches the file name (YYYY.csv).
	 *
	 * @param filePath The path of the CSV file to validate.
	 * @return A report as a list of messages, one per problem, each including the
	 *         line number (for example "Line 5: invalid date"), or an empty list if
	 *         the file is valid.
	 *
	 * @author Julio
	 */
	public static List<String> validateFile(String filePath) {
		return null;
	}
}
