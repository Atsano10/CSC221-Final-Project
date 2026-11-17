package accounts;

/**
 * Represents the preset security questions available for user accounts.
 * 
 * @author Ronnie
 */
public enum SecurityQuestion {

    FIRST_PET("What was the name of your first pet?"),

    TASTY_ROCK("In your questionable mind, what is the tastiest looking rock?"),

    BIRTH_CITY("In what city were you born?"),

    ELEMENTARY_SCHOOL("What elementary school did you attend?"),

	FAV_POKEMON("What is your favorite Pokemon?"),	
	
    HS_MASCOT("What creature was your high school mascot?"),

	FAV_ATHLETE("Who is your favorite sports athlete?"),

	CHILDHOOD_TEAM("What was the name of your first youth sports team?"),

    DONE_DREAMS("In what year did you abandon your dreams?"),

    BEST_FRIEND("What is the name of your best friend?"),

	WORST_GROUP_PROJECT("Which historical figure would be the worst group project partner?"),

    FOX_WORDS("You definitely know this one, or you don't, but what does the fox say?");

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
