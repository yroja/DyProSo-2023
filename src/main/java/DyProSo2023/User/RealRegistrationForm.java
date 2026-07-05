package DyProSo2023.User;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Represents a form used for completing the full registration process of an attendee.
 * 
 * This form contains billing information, optional student status, participation in HZDR excursion, and photo permission.
 * 
 * It acts as a transfer object between the registration frontend and backend and is later mapped into to the Attendee domain model.
 */
public class RealRegistrationForm {
    private String student;

    private String type_of_billing_address;    // true: Business within EU (VAT number), false: personal billing address/Business address non-EU (no VAT number)

    private String billing_address_lastname;

    private String billing_address_firstname;

    private String billing_address_poBox;

    private String billing_address_street;

    private String billing_address_postalCode;

    private String billing_address_city;

    private String billing_address_country;

    private String vat_number;

    private String hzdr;

    private String hzdr_lastname;

    private String hzdr_firstname;

    private String hzdr_cardnumber;

    private String hzdr_birthdate;

    private Boolean photo_permission;

    /**
     * Initializes a new RealRegistrationForm containing all registration-related input data.
     * 
     * The form includes:
     *  - student status
     *  - billing address information (including VAT handling)
     *  - optional HZDR excursion data
     *  - photo permission flag
     */
    public RealRegistrationForm(String student, String type_of_billing_address,String billing_address_lastname, String billing_address_firstname, String billing_address_poBox, String billing_address_street, String billing_address_postalCode, String billing_address_city, String billing_address_country, String vat_number,String hzdr, String hzdr_lastname, String hzdr_firstname, String hzdr_cardnumber, String hzdr_birthdate, Boolean photo_permission) {

        this.student = student;
        this.type_of_billing_address = type_of_billing_address;

        this.billing_address_lastname = billing_address_lastname;
        this.billing_address_firstname = billing_address_firstname;
        this.billing_address_poBox = billing_address_poBox;
        this.billing_address_street = billing_address_street;
        this.billing_address_postalCode = billing_address_postalCode;
        this.billing_address_city = billing_address_city;
        this.billing_address_country = billing_address_country;
        this.vat_number = vat_number;
        this.hzdr = hzdr;
        this.hzdr_lastname = hzdr_lastname;
        this.hzdr_firstname = hzdr_firstname;
        this.hzdr_cardnumber = hzdr_cardnumber;
        this.hzdr_birthdate = hzdr_birthdate;
        
        if(photo_permission == null){
            this.photo_permission = false;
        }else{
            this.photo_permission = photo_permission;
        }

    }

    public String getStudent(){
        return this.student;
    }

    public String getBilling_address_lastname(){
        return this.billing_address_lastname;
    }

    public String getBilling_address_firstname(){
        return this.billing_address_firstname;
    }

    public String getBilling_address_poBox(){
        return this.billing_address_poBox;
    }

    public String getBilling_address_street(){
        return this.billing_address_street;
    }

    public String getBilling_address_postalCode(){
        return this.billing_address_postalCode;
    }

    public String getBilling_address_city(){
        return this.billing_address_city;
    }

    public String getBilling_address_country(){
        return this.billing_address_country;
    }

    public String getHzdr(){
        return this.hzdr;
    }

    public String getHzdr_lastname(){
        return this.hzdr_lastname;
    }

    public String getHzdr_firstname(){
        return this.hzdr_firstname;
    }

    public String getHzdr_cardnumber(){
        return this.hzdr_cardnumber;
    }

    public String getHzdr_birthdate(){
        return this.hzdr_birthdate;
    }

    public Boolean getPhoto_permission(){
        return this.photo_permission;
    }

    public String getType_of_billing_address(){
        return this.type_of_billing_address;
    }

    public String getVat_number(){
        return this.vat_number;
    }






}
