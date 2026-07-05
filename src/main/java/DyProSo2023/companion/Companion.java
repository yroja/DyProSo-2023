package DyProSo2023.companion;

import DyProSo2023.User.Attendee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Represents a companion registration linked to an attendee.
 * 
 * A companion contains billing information, participation flags (e.g. social programs, HZDR), and optional personal data.
 */

@Entity
public class Companion {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Attendee companionOf;

    private boolean same_billing_address;

    private boolean type_of_billing_address;

    private String billing_address_lastname;

    private String billing_address_firstname;

    private String billing_address_poBox;

    private String billing_address_street;

    private String billing_address_postalCode;

    private String billing_address_city;

    private String billing_address_country;

    private String vat_number;

    private boolean social_programm;

    private boolean hzdr;

    private String hzdr_lastname;

    private String hzdr_firstname;

    private String hzdr_cardnumber;

    private LocalDate hzdr_birthdate;

    private LocalDate date_of_registration;

    public Companion(){
        this.same_billing_address = false;
        this.type_of_billing_address = false;
        this.companionOf = null;
        this.billing_address_lastname = null;
        this.billing_address_firstname = null;
        this.billing_address_poBox = null;
        this.billing_address_street = null;
        this.billing_address_postalCode = null;
        this.billing_address_city = null;
        this.billing_address_country = null;
        this.vat_number = null;
        this.social_programm = false;
        this.hzdr = false;
        this.hzdr_lastname = null;
        this.hzdr_firstname = null;
        this.hzdr_cardnumber = null;
        this.hzdr_birthdate = null;
        this.date_of_registration = LocalDate.now();
    }

    public void setSame_billing_address(boolean same_billing_address){
        this.same_billing_address = same_billing_address;
    }

    public boolean getSame_billing_address(){
        return same_billing_address;
    }

    public void setCompanionOf(Attendee attendee){
        this.companionOf = attendee;
    }

    public Attendee getCompanionOf(){
        return this.companionOf;
    }

    public void setBilling_address_lastname(String billing_address_lastname) {
        this.billing_address_lastname = billing_address_lastname;
    }

    public void setBilling_address_firstname(String billing_address_firstname) {
        this.billing_address_firstname = billing_address_firstname;
    }

    public void setBilling_address_poBox(String billing_address_poBox) {
        this.billing_address_poBox = billing_address_poBox;
    }

    public void setBilling_address_street(String billing_address_street) {
        this.billing_address_street = billing_address_street;
    }

    public void setBilling_address_postalCode(String billing_address_postalCode) {
        this.billing_address_postalCode = billing_address_postalCode;
    }

    public void setBilling_address_city(String billing_address_city) {
        this.billing_address_city = billing_address_city;
    }

    public void setBilling_address_country(String billing_address_country) {
        this.billing_address_country = billing_address_country;
    }

    public void setSocial_programm(boolean social_programm) {
        this.social_programm = social_programm;
    }

    public void setHzdr(boolean hzdr) {
        this.hzdr = hzdr;
    }

    public void setHzdr_lastname(String hzdr_lastname) {
        this.hzdr_lastname = hzdr_lastname;
    }

    public void setHzdr_firstname(String hzdr_firstname) {
        this.hzdr_firstname = hzdr_firstname;
    }

    public void setHzdr_cardnumber(String hzdr_cardnumber) {
        this.hzdr_cardnumber = hzdr_cardnumber;
    }

    public void setHzdr_birthdate(LocalDate hzdr_birthdate) {
        this.hzdr_birthdate = hzdr_birthdate;
    }

    public Integer getId() {
        return id;
    }

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

    public boolean isSocial_programm() {
        return social_programm;
    }

    public String date_of_registration_toString(){
        if(date_of_registration == null){
            return "/";
        }
        return date_of_registration.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public String takes_part_social_program(){
        if(social_programm){
            return "Yes";
        }
        return "No";
    }

    public boolean isHzdr() {
        return hzdr;
    }

    public String takes_part_hzdr(){
        if(hzdr){
            return "Yes";
        }
        return "No";
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

    public void setDate_of_registration(LocalDate date_of_registration){
        this.date_of_registration = date_of_registration;
    }

    public LocalDate getDate_of_registration(){
        return date_of_registration;
    }

    public LocalDate getHzdr_birthdate() {
        return hzdr_birthdate;
    }

    public String billing_address_toString(){
        if(billing_address_street == null || billing_address_street.length() == 0 || billing_address_street.isBlank()){
            return "/";
        }else if(!(billing_address_poBox.isEmpty() || billing_address_poBox.isBlank())){
            return billing_address_poBox + ", " + billing_address_street + ", " + billing_address_postalCode + " " + billing_address_city + ", " + billing_address_country;
        }

        return billing_address_street + ", " + billing_address_postalCode + " " + billing_address_city + ", " + billing_address_country;
    }

    public String companion_toString(){
        return billing_address_firstname + " " + billing_address_lastname;
    }

    public String hzdr_birthdate_toString(){
        if(this.hzdr_birthdate == null){
            return "/";
        }
        return this.hzdr_birthdate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public String getBill_po_box() {
        if(billing_address_poBox.isBlank()){
            return "/";
        }
        return billing_address_poBox;
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

    public String get_type_of_billing_string(){
        if(!this.type_of_billing_address){
            return "False";
        }
        return "True";
    }

    public String get_vat_string(){
        if(!this.type_of_billing_address){
            return "/";
        }
        return vat_number;
    }
}
