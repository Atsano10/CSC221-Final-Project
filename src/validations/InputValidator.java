import java.util.List;

/**
 * Provides validation utilities for user input entered through the menus.
 *
 * @author Kareem
 */
public final class InputValidator {

	/**
	 * Private constructor that prevents creation of an InputValidator object.
	 *
	 * @author Kareem
	 */
	private InputValidator() {

	}

	/**
	 * Checks that a year is 4 digits and within a sensible range. Used when
	 * loading, analyzing, or deleting a year's data.
	 *
	 * @param year The year the user entered, for example "2025".
	 * @return True if the year is exactly 4 digits and within the allowed range.
	 *
	 * @author Kareem
	 */
	public static boolean validateYear(String year) {
		return false;
	}

	/**
	 * Checks that the file path or YYYY.csv name entered exists, is a file rather
	 * than a folder, ends in .csv, and can be read.
	 *
	 * @param filePath The file path or YYYY.csv file name the user entered.
	 * @return True if the file exists, is not a folder, ends in .csv, and is
	 *         readable.
	 *
	 * @author Kareem
	 */
	public static boolean validateFilePath(String filePath) {
		return false;
	}

	/**
	 * Checks the categories the user wants to exclude from the anomaly check.
	 * Makes sure each one is a valid category and removes duplicates.
	 *
	 * @param categories The list of categories the user wants to exclude.
	 * @return The cleaned list of valid categories with duplicates removed, or
	 *         null if any category is invalid.
	 *
	 * @author Kareem
	 */
	public static List<String> validateCategoryList(List<String> categories) {
		return null;
	}
}
