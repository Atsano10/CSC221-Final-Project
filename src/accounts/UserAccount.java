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
        /* 
         * Needs a method to pull the hashed password from storage.   
         */
       String hashedpassword;

       AccountServer.verifyPassword(oldPass, hashedpassword); //Verify that the password is the oldPassword is the correct one 
       AccountServer.hashPassword(newPass); //Hashes the new password given.
       return true;
    }

    /**
    *  Returns the Users stored security Question.
    *  @return the security question.
    *
    *  @author Ronnie
    */
    public String securityQuestion(){
        /*
        *   Need method from storage to pull the question, or if hashed.
        */
        String storedHashQuestion;
        return storedHashQuestion; //Same reasoning as "password".
    }

    /** 
    *  Compares the given answer of the security question to the stored one.
    *  @param answer is the user's answer to the question.
    *  @return True when the answer matches the stored answer.
    *   
    *  @author Ronnie
    */
    public boolean securityAnswer(String answer){
        /*
        Need a method from storage to get the stored hashed security answer
         */
        
        String storedHashAnswer;
        return AccountServer.verifyPassword(answer, storedHashAnswer); //Compares the parameter with the storedHashAnswer.
    }

}