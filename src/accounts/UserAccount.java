/**
 * Provides managament pertaining to a singular user account
 * 
 * @author Vic
 */
public class UserAccount {

    /**
     * Public constructor that allows for the creation of a UserAccount object.
     * 
     * @param username The username that user has chosen.
     * @param password The password that the user has set for their account.
     * @param question The security question the user wants to set for their account.
     * @param answer The answer to the security question the user wants to set.
     * 
     * @author Vic
     */
    public UserAccount(String username, String password, 
            String question, String answer) 
    {
        
    }

    /**
     * Sets both the security question and answer to a user's account.
     * 
     * @param question The question the user wants to set on this account.
     * @param answer The answer to the question that the user created on this account.
     * @return True when the security question and answer are updated.
     * 
     * @author Vic
     */
    public boolean setSecurity(String question, String answer) {
        return false;
    }


}