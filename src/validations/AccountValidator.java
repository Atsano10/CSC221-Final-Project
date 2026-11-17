
/**
 * Provides validation utilities for user account information entered during
 * registration and account management.
 *
 * @author Mohammed Islam
 */
public final class AccountValidator {

	//Minimum username length
	private static final int MIN_USERNAME_LEN = 3;

	//Maximum username length
	private static  final int MAX_USERNAME_LEN = 20;

	//Minimum password length
	private static final int MIN_PASSWORD_LEN = 8;

	//Minimum security answer length
	private static final int MIN_ANSWER_LEN = 2;

	/**
	 * Private constructor that prevents creation of an AccountValidator object.
	 *
	 * @author Mohammed Islam
	 */
	private AccountValidator() {

	}

	/**
	 * Checks that a username meets the application's account requirements.
	 *
	 * @param userName The username entered by the user.
	 * @return True if the username is valid, otherwise false.
	 *
	 * @author Mohammed Islam
	 */
	public static boolean validateUserName(String userName) {

		if (userName == null){
			return false;
		}

		userName = userName.trim();

		if (userName.length()<MIN_USERNAME_LEN || userName.length()>MAX_USERNAME_LEN){
			return false;
		}

		for (int i = 0; i < userName.length(); i++) {
            char character = userName.charAt(i);

            if (!Character.isLetterOrDigit(character)
                    && character != '_') {
                return false;
            }
        }


		return true;
	}

	/**
	 * Checks that a password meets the application's password requirements.
	 *
	 * @param password The password entered by the user.
	 * @return True if the password is valid, otherwise false.
	 *
	 * @author Mohammed Islam
	 */
	public static boolean validatePassword(String password) {
		if (password == null || password.length() < MIN_PASSWORD_LEN) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasNumber = false;

        for (int i = 0; i < password.length(); i++) {
            char character = password.charAt(i);

            if (Character.isLetter(character)) {
                hasLetter = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }
        }

        return hasLetter && hasNumber;
	}

	/**
	 * Checks that the secret answer entered by the user is valid.
	 *
	 * @param secretAnswer The secret answer entered by the user.
	 * @return True if the secret answer is valid, otherwise false.
	 *
	 * @author Mohammed Islam
	 */
	public static boolean validateSecretAnswer(String secretAnswer) {
		if (secretAnswer == null) {
            return false;
        }

        return secretAnswer.trim().length() >= MIN_ANSWER_LEN;

	}


	/**
	 * Checks and sanitizes a general string input.
	 *
	 * @param input The string entered by the user.
	 * @return The trimmed string, or null if the input is null or empty.
	 *
	 * @author Mohammed Islam
	 */
	public static String validateString(String input) {

		if (input == null || input.trim().isEmpty()) {
			return null;
		}

		return input.trim();
	}
}
