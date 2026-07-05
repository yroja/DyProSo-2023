package DyProSo2023.User;

/**
 * Represents a form used for creating a new user account.
 * 
 * This form contains personal data, login credentials, and acceptance of terms and conditions.
 */
public class RegistrationForm {
    private String mail;
    private String password;

    private String rpassword;

    private String lastname;

    private String name;

    private String adress;

    private String title;

    private Boolean terms;

    public RegistrationForm(String mail, String password, String rpassword, String lastname, String name,
                            String title, String adress, Boolean terms){
        this.mail = mail;
        this.password = password;
        this.rpassword = rpassword;
        this.lastname = lastname;
        this.name = name;
        this.adress = adress;
        this.title = title;
        this.terms = terms;
    }

    public String getMail(){
        return mail;
    }

    public String getPassword(){
        return password;
    }

    public String getRpassword() {return rpassword;}

    public String getAdress() {
        return adress;
    }

    public String getLastname() {
        return lastname;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getTerms(){ return terms; }

    @Override
    public String toString() {
        return "RegistrationForm {Lastname: "+ lastname+ ", Name: " + name + "e-mail: " + mail + ", password: " + password + "Title: "+ title + "Adress: " + adress  + "}\n";
    }
}
