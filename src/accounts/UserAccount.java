/**
 * Provides managament pertaining to a singular user account
 * 
 * @author Vic
 */
public class UserAccount {

    private String username;

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
        this.username = username;
    }

    public String getUsername() {
        return username;
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

        // Checks if the arguments are null
        if ((question == null) || (answer == null)) {
            return false;
        }

        // Checks that there is an active user and it matches the accessing user.
        UserAccount currentUser = AccountServer.getCurrentUser();

        if (currentUser == null) {
            return false;
        }

        if (!currentUser.username.equals(this.username)){
            return false;
        }

        /*
        Need a method from storage team to access a given user's information 
        to set the security question and answer.
         */

        return true;
    }

    /**
     * Changes the accounts password.
     * 
     * @param oldPass is the users current password.
     * @param newPass is the users new password.
     * @return True when the password is successfully changed.
     * 
     * @author Ronnie
     */
    public boolean changePass(String oldPass, String newPass){
        return false;
    }

    /**
    *  Returns the Users stored security Question.
    *  @return the security question.
    *
    *  @author Ronnie
    */
    public String securityQuestion(){
        return null;
    }

    /** 
    *  Compares the given answer of the security question to the stored one.
    *  @param answer is the user's answer to the question.
    *  @return True when the answer matches the stored answer.
    *   
    *  @author Ronnie
    */
    public boolean securityAnswer(String answer){
        return false;
    }

}