package DyProSo2023.User;

/**
 * Represents a form used for updating editable user profile information such as name, affiliation and title.
 */
public class EditForm {

    private String lastname;

    private String firstname;

    private String affiliation;

    private String title;

    /**
     * Creates a new EditForm containing updated user profile data.
     * 
     * @param lastname updated last name of the user
     * @param firstname updated first name of the user
     * @param affiliation updated institutional affiliation
     * @param title updated academic or professional title
     */
    public EditForm(String lastname, String firstname, String affiliation, String title){
        this.lastname = lastname;
        this.firstname = firstname;
        this.affiliation = affiliation;
        this.title = title;

    }

    public String getLastname(){
        return lastname;
    }

    public String getFirstname(){
        return firstname;
    }

    public String getAffiliation(){
        return affiliation;
    }

    public String getTitle(){
        return title;
    }
}
