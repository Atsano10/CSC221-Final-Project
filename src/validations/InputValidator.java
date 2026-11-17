import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides validation utilities for user input entered through the menus.
 *
 * @author Kareem
 */
public final class InputValidator {

	/**
	 * Earliest year the PFM accepts.
	 */
	public static final int MIN_YEAR = 1900;

	/**
	 * Every valid income and expense category, spelled exactly as in the spec.
	 * "Other" appears once because it is both an income and an expense category.
	 */
	private static final List<String> VALID_CATEGORIES = List.of(
			"Compensation", "Allowance", "Investments", "Other",
			"Home", "Utilities", "Food", "Appearance", "Work", "Education",
			"Transportation", "Entertainment", "Professional Services");

	/**
	 * Private constructor that prevents creation of an InputValidator object.
	 *
	 * @author Kareem
	 */
	private InputValidator() {

	}

	/**
	 * Checks that a year is 4 digits and between {@link #MIN_YEAR} and the
	 * current year, inclusive. Used when loading, analyzing, or deleting a
	 * year's data.
	 *
	 * @param year The year the user entered, for example "2025".
	 * @return True if the year is exactly 4 digits and within the allowed range.
	 *
	 * @author Kareem
	 */
	public static boolean validateYear(String year) {
		if (year == null) {
			return false;
		}
		String trimmed = year.trim();
		if (!trimmed.matches("\\d{4}")) {
			return false;
		}
		int value = Integer.parseInt(trimmed);
		return value >= MIN_YEAR && value <= Year.now().getValue();
	}

	/**
	 * Checks that the file path or YYYY.csv name entered exists, is a file rather
	 * than a folder, ends in .csv, and can be read. A bare file name is looked up
	 * in the current working folder. Surrounding quotes (added by Windows
	 * "Copy as path") are ignored.
	 *
	 * @param filePath The file path or YYYY.csv file name the user entered.
	 * @return True if the file exists, is not a folder, ends in .csv, and is
	 *         readable.
	 *
	 * @author Kareem
	 */
	public static boolean validateFilePath(String filePath) {
		if (filePath == null) {
			return false;
		}
		String cleaned = filePath.trim();
		if (cleaned.length() >= 2 && cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
			cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
		}
		if (cleaned.isEmpty()) {
			return false;
		}
		File file = new File(cleaned);
		return file.isFile()
				&& file.canRead()
				&& file.getName().toLowerCase().endsWith(".csv");
	}

	/**
	 * Checks the categories the user wants to exclude from the anomaly check.
	 * Matching ignores case and extra spaces; each result uses the official
	 * spelling. Duplicates are removed and the original order is kept.
	 *
	 * @param categories The list of categories the user wants to exclude.
	 * @return The cleaned list of valid categories with duplicates removed (empty
	 *         if nothing is excluded), or null if the list is null or any
	 *         category is invalid.
	 *
	 * @author Kareem
	 */
	public static List<String> validateCategoryList(List<String> categories) {
		if (categories == null) {
			return null;
		}
		Set<String> cleaned = new LinkedHashSet<>();
		for (String category : categories) {
			String match = findCategory(category);
			if (match == null) {
				return null;
			}
			cleaned.add(match);
		}
		return new ArrayList<>(cleaned);
	}

	/**
	 * Finds the official spelling of a category, ignoring case and extra spaces.
	 *
	 * @param category The category text to look up.
	 * @return The official category name, or null if there is no match.
	 *
	 * @author Kareem
	 */
	private static String findCategory(String category) {
		if (category == null) {
			return null;
		}
		String normalized = category.trim().replaceAll("\\s+", " ");
		for (String valid : VALID_CATEGORIES) {
			if (valid.equalsIgnoreCase(normalized)) {
				return valid;
			}
		}
		return null;
	}
}
