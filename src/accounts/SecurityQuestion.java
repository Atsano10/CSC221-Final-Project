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

	FAV_POKEMON("What is your favorite pokemon?"),	
	
    HS_MASCOT("What creature was your high school mascot?"),

    SOCCER_PLAYER("Look its as simple as: Messi or Ronaldo?"),

    DONE_DREAMS("In what year did you abandon your dreams?"),

    BEST_FRIEND("What is the name of your best friend?"),

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
