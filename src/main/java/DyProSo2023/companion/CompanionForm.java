package DyProSo2023.companion;

import java.time.LocalDate;

/**
 * Represents a form used for creating or updating a companion.
 * 
 * Used as a data transfer object between frontend and backend.
 */
public class CompanionForm {

    private Boolean same_billing_address;

    private String type_of_billing_address;

    private String billing_address_lastname;

    private String billing_address_firstname;

    private String billing_address_poBox;

    private String billing_address_street;

    private String billing_address_postalCode;

    private String billing_address_city;

    private String billing_address_country;

    private String vat_number;

    private String social_programm;

    private String hzdr;

    private String hzdr_lastname;

    private String hzdr_firstname;

    private String hzdr_cardnumber;

    private String hzdr_birthdate;

    public CompanionForm(Boolean same_billing_address, String type_of_billing_address, String billing_address_lastname, String billing_address_firstname, String billing_address_poBox, String billing_address_street, String billing_address_postalCode, String billing_address_city, String billing_address_country, String vat_number, String social_programm, String hzdr, String hzdr_lastname, String hzdr_firstname, String hzdr_cardnumber, String hzdr_birthdate) {
        if(same_billing_address == null){
            this.same_billing_address = false;
            this.type_of_billing_address = type_of_billing_address;
            this.billing_address_lastname = billing_address_lastname;
            this.billing_address_firstname = billing_address_firstname;
            this.billing_address_poBox = billing_address_poBox;
            this.billing_address_street = billing_address_street;
            this.billing_address_postalCode = billing_address_postalCode;
            this.billing_address_city = billing_address_city;
            this.billing_address_country = billing_address_country;
            this.vat_number = vat_number;
        }else{
            this.same_billing_address = same_billing_address;
            this.type_of_billing_address = type_of_billing_address;
            this.billing_address_lastname = billing_address_lastname;
            this.billing_address_firstname = billing_address_firstname;
            this.billing_address_poBox = billing_address_poBox;
            this.billing_address_street = billing_address_street;
            this.billing_address_postalCode = billing_address_postalCode;
            this.billing_address_city = billing_address_city;
            this.billing_address_country = billing_address_country;
            this.vat_number = vat_number;
        }

        this.social_programm = social_programm;
        this.hzdr = hzdr;
        this.hzdr_lastname = hzdr_lastname;
        this.hzdr_firstname = hzdr_firstname;
        this.hzdr_cardnumber = hzdr_cardnumber;
        this.hzdr_birthdate = hzdr_birthdate;
    }

    public Boolean getSame_billing_address(){ return same_billing_address; }

    public String getBilling_address_lastname() {
        return billing_address_lastname;
    }

    public String getBilling_address_firstname() {
        return billing_address_firstname;
    }

    public String getBilling_address_poBox() {
        return billing_address_poBox;
    }

    public String getBilling_address_street() {
        return billing_address_street;
    }

    public String getBilling_address_postalCode() {
        return billing_address_postalCode;
    }

    public String getBilling_address_city() {
        return billing_address_city;
    }

    public String getBilling_address_country() {
        return billing_address_country;
    }

    public String getSocial_programm() {
        return social_programm;
    }

    public String getHzdr() {
        return hzdr;
    }

    public String getHzdr_lastname() {
        return hzdr_lastname;
    }

    public String getHzdr_firstname() {
        return hzdr_firstname;
    }

    public String getHzdr_cardnumber() {
        return hzdr_cardnumber;
    }

    public String getHzdr_birthdate() {
        return hzdr_birthdate;
    }

    public String getVat_number(){ return vat_number; }

    public String getType_of_billing_address(){
        return type_of_billing_address;
    }
}
