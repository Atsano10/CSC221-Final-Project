/**
 * Represents the preset security questions available for user accounts.
 * 
 * @author Ronnie
 */
public enum SecurityQuestion {

	// Question about user's first pet
	FIRST_PET("What was the name of your first pet?"),

	// Question about user's mother's maiden name
	MOTHER_MAIDEN_NAME("What is your mother's maiden name?"),

	// Question about user's birthplace
	BIRTH_CITY("In what city were you born?"),

	// Question about user's elementary school
	ELEMENTARY_SCHOOL("What elementary school did you attend?"),

	// Question about user's first car
	FIRST_CAR("What was the make of your first car?");

	private final String questionText;

	/**
	 * Constructor for SecurityQuestion enum.
	 * 
	 * @param questionText The full question text.
	 * @author Ronnie
	 */
	SecurityQuestion(String questionText) {
		this.questionText = questionText;
	}

	/**
	 * Returns the user-readable question text.
	 * 
	 * @return The question string.
	 * @author Ronnie
	 */
	public String getQuestionText() {
		return questionText;
	}
}
