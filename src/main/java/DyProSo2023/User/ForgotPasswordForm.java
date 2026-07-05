package DyProSo2023.User;

/**
 * Represents a form used for requesting a password reset via email.
 */
public class ForgotPasswordForm {

    private String email;

    public ForgotPasswordForm(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }
}
