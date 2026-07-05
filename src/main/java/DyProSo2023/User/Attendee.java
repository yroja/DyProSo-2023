package DyProSo2023.User;

import net.bytebuddy.utility.RandomString;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Represents a participant of the conference. 
 * 
 * Stores personal information, account data, billing details, registration status and additional conference-related information
 * such as uploaded abstracts and permissions.
 */
@Entity
public class Attendee {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

@OneToOne
private UserAccount userAccount;

private String lastname;

private String name;

private String affiliation;

private String resetPasswordToken;

private LocalDateTime expiryDate;

private String verificationCode;

private String title;

private int filesUploaded;


private boolean student;

private boolean type_of_billing_address;

private String billing_address_lastname;

private String billing_address_firstname;

private String billing_address_poBox;

private String billing_address_street;

private String billing_address_postalCode;

private String billing_address_city;

private String billing_address_country;

private String vat_number;

private boolean hzdr;

private String hzdr_lastname;

private String hzdr_firstname;

private String hzdr_cardnumber;

private LocalDate hzdr_birthdate;

private boolean photo_permission;

private boolean terms;

private LocalDate date_of_registration;

public Attendee(){}

/**
 * Initializes a new attendee with the required account information and default registration settings.
 */
public Attendee(UserAccount userAccount, String lastname, String name, String title, String affiliation, boolean terms){
    this.userAccount = userAccount;
    this.affiliation = affiliation;
    this.title = title;
    this.name = name;
    this.lastname = lastname;
    this.resetPasswordToken = null;
    this.expiryDate = null;
    this.verificationCode = RandomString.make(64);
    this.filesUploaded = 0;
    this.student = false;
    this.type_of_billing_address = false;
    this.billing_address_lastname = null;
    this.billing_address_firstname = null;
    this.billing_address_poBox = null;
    this.billing_address_street = null;
    this.billing_address_postalCode = null;
    this.billing_address_city = null;
    this.billing_address_country = null;
    this.vat_number = null;
    this.hzdr = false;
    this.hzdr_lastname = null;
    this.hzdr_firstname = null;
    this.hzdr_cardnumber = null;
    this.hzdr_birthdate = null;
    this.photo_permission = true;
    this.terms = terms;
    this.date_of_registration = null;
}

public Long getId(){
    return id;
}

public UserAccount getUserAccount(){
    return userAccount;
}

public String getMail(){
    return userAccount.getUsername();
}

public Password.EncryptedPassword getPassword(){
    return userAccount.getPassword();
}

    public String getTitle() {
        return title;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getResetPasswordToken(){
    return resetPasswordToken;
}

public void setResetPasswordToken(String resetPasswordToken){
    this.resetPasswordToken = resetPasswordToken;
}


    public String getAffiliation() {
        return affiliation;
    }

    public String affiliationToString(){
        if(affiliation.length() == 0){
            return "/";
        }
        return affiliation;
    }

    public String getLastname() {
        return lastname;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getExpiryDate(){
    return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate){
    this.expiryDate = expiryDate;
    }

    public String getVerificationCode(){
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode){
        this.verificationCode = verificationCode;
    }

    /**
     * Checks whether the attendee has verified the account.
     */
    public boolean isVerified(){
        if(verificationCode == null){
            return true;
        }
        return false;
    }

    public int getFilesUploaded(){
        return this.filesUploaded;
    }

    public void setType_of_billing_address(boolean type_of_billing_address){
        this.type_of_billing_address = type_of_billing_address;
    }

    public boolean getType_of_billing_address(){
        return this.type_of_billing_address;
    }

    public void setVat_number(String vat_number){
        this.vat_number = vat_number;
    }

    public String getVat_number(){
        return this.vat_number;
    }

    public void setFilesUploaded(int filesUploaded){
        this.filesUploaded = filesUploaded;
    }

    public void setStudent(boolean student){
        this.student = student;
    }

    public boolean getStudent(){
        return student;
    }

    public void setBilling_address_lastname(String billing_address_lastname){
        this.billing_address_lastname = billing_address_lastname;
    }

    public String getBilling_address_lastname(){
        return this.billing_address_lastname;
    }

    public void setBilling_address_firstname(String billing_address_firstname){
        this.billing_address_firstname = billing_address_firstname;
    }

    public String getBilling_address_firstname(){
        return this.billing_address_firstname;
    }

    public void setBilling_address_poBox(String billing_address_poBox){
        this.billing_address_poBox = billing_address_poBox;
    }

    public String getBilling_address_poBox(){
        return this.billing_address_poBox;
    }

    public String getBill_po_box() {
        if(billing_address_poBox.isBlank()){
            return "/";
        }
        return billing_address_poBox;
    }

    public void setBilling_address_street(String billing_address_street){
        this.billing_address_street = billing_address_street;
    }

    public String getBilling_address_street(){
        return this.billing_address_street;
    }

    public void setBilling_address_postalCode(String billing_address_postalCode){
        this.billing_address_postalCode = billing_address_postalCode;
    }

    public String getBilling_address_postalCode(){
        return this.billing_address_postalCode;
    }

    public void setBilling_address_city(String billing_address_city){
        this.billing_address_city = billing_address_city;
    }

    public String getBilling_address_city(){
        return this.billing_address_city;
    }

    public void setBilling_address_country(String billing_address_country){
        this.billing_address_country = billing_address_country;
    }

    public String getBilling_address_country(){
        return this.billing_address_country;
    }

    public void setHzdr(boolean hzdr){
        this.hzdr = hzdr;
    }

    public boolean getHzdr(){
        return this.hzdr;
    }

    public void setDate_of_registration(LocalDate date_of_registration){
        this.date_of_registration = date_of_registration;
    }

    public LocalDate getDate_of_registration(){
        return date_of_registration;
    }

    public String date_of_registration_toString(){
        if(date_of_registration == null){
            return "/";
        }
        return date_of_registration.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public String take_part_hzdr(){
        if(hzdr){
            return "Yes";
        }
        return "No";
    }

    public void setHzdr_lastname(String hzdr_lastname){
        this.hzdr_lastname = hzdr_lastname;
    }

    public String getHzdr_lastname(){
        return this.hzdr_lastname;
    }

    public void setHzdr_firstname(String hzdr_firstname){
        this.hzdr_firstname = hzdr_firstname;
    }

    public String getHzdr_firstname(){
        return this.hzdr_firstname;
    }

    public void setHzdr_cardnumber(String hzdr_cardnumber){
        this.hzdr_cardnumber = hzdr_cardnumber;
    }

    public String getHzdr_cardnumber(){
        return this.hzdr_cardnumber;
    }

    public void setHzdr_birthdate(LocalDate hzdr_birthdate){
        this.hzdr_birthdate = hzdr_birthdate;
    }

    public LocalDate getHzdr_birthdate(){
        return this.hzdr_birthdate;
    }

    public String hzdr_birthdate_toString(){
        if(this.hzdr_birthdate == null){
            return "/";
        }
        return this.hzdr_birthdate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public void setPhoto_permission(boolean photo_permission){
        this.photo_permission = photo_permission;
    }

    public boolean getPhoto_permission(){
        return photo_permission;
    }

    public String gives_permission(){
        if(photo_permission){
            return "Yes";
        }
        return "No";
    }

    public void setTerms(boolean terms){
        this.terms = terms;
    }

    public boolean getTerms(){
        return terms;
    }

    public String isStudent(){
        if(student){
            return "Yes";
        }
        return "No";
    }

    /**
     * Checks whether the attendee has completed the registration process.
     */
    public boolean isRegistered(){
        if(billing_address_street == null || billing_address_street.isEmpty() || billing_address_street.isBlank()){
            return false;
        }
        return true;
    }

    /**
     * Returns the formatted billing address for display purposes.
     */
    public String billing_address_toString(){
        if(billing_address_street == null || billing_address_street.length() == 0 || billing_address_street.isBlank()){
            return "/";
        }else if(!(billing_address_poBox.isEmpty() || billing_address_poBox.isBlank())){
            return billing_address_poBox + ", " + billing_address_street + ", " + billing_address_postalCode + " " + billing_address_city + ", " + billing_address_country;
        }

        return billing_address_street + ", " + billing_address_postalCode + " " + billing_address_city + ", " + billing_address_country;
    }

    /**
     * Returns the attendee's full display name including the academic title if available. 
     */
    public String participant_name(){
        if(title.isEmpty() || title.isBlank()){
            return name + " " + lastname;
        }
        return title + " " + name + " " + lastname;
    }

    public String participant_name_for_excel(){
        return name + " " + lastname;
    }

    public String get_vat_string(){
        if(!this.type_of_billing_address){
            return "/";
        }
        return this.vat_number;
    }

    public String get_type_billing_string(){
        if(this.type_of_billing_address){
            return "True";
        }
        return "False";
    }

    @Override
    public String toString(){
        return getLastname().replace(' ', '-') + "_" + getName().replace(' ', '-');

    }
}
