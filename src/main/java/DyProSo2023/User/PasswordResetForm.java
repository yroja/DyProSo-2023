package DyProSo2023.User;

/**
 * Represents a form used for resetting a user's password.
 * 
 * The form contains the new password and a repeated password for validation purposes. 
 */
public class PasswordResetForm {
    private String password;
    private String rpassword;

    public PasswordResetForm(String password, String rpassword){
        this.password = password;
        this.rpassword = rpassword;
    }

    public String getPassword() {
        return password;
    }

    public String getRpassword() {
        return rpassword;
    }
}
