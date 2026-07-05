package DyProSo2023.User;

import DyProSo2023.companion.Companion;
import com.mysema.commons.lang.Assert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer responsible for managing Attendee entities.
 * 
 * Handles account creation, registration, authentication-related operations, email notifications,
 * and export of attendee information.
 */
@Service
@Transactional
public class AttendeeManagement {

    private final UserAccountManagement userAccountManagement;

    private final AttendeeRepository attendeeRepository;

    private int registrationState;

    private static final Role USER_ROLE = Role.of("USER");


    @Autowired
    private JavaMailSender mailSender;

    /**
     * Initializes the service with the required dependencies for user account management and attendee persistence.
     * 
     * @param userAccountManagement service used to create, update, and delete user accounts
     * @param attendeeRepository repository used to access and persist attendee entities
     */
    public AttendeeManagement(@Qualifier("persistentUserAccountManagement") UserAccountManagement userAccountManagement, AttendeeRepository attendeeRepository){
        Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
        Assert.notNull(attendeeRepository, "AttendeeRepository must not be null!");
        this.registrationState = 0;
        this.userAccountManagement = userAccountManagement;
        this.attendeeRepository = attendeeRepository;
    }

    /**
     * Initializes a new attendee and corresponding UserAccount.
     * 
     * The created account is disabled by default and must be activated via email verification.
     * 
     * @param form registration form containing user input data (email, password, personal details)
     * @return the persisted Attendee entity
     */
    public Attendee createAttendee(RegistrationForm form){
        Password.UnencryptedPassword password = Password.UnencryptedPassword.of(form.getPassword());
        UserAccount useracc = userAccountManagement.create(form.getMail(), password, Role.of("USER"));
        useracc.setEnabled(false);
        return attendeeRepository.saveAndFlush(new Attendee(useracc, form.getLastname(), form.getName(),
                form.getTitle(), form.getAdress(), form.getTerms()));
    }

    public int getRegistrationState(){
        return registrationState;
    }

    public void setRegistrationState(int registrationState){
        this.registrationState = registrationState;
    }

    /**
     * Sends an verification mail to the attendee.
     * 
     * The email contains a unique verification link that activates the account.
     * The account remains disabled until verification is completed.
     * 
     * @param attendee the recipient of the verification mail
     * @param url base URL of the application used to construct the verification link
     */
    public void sendVerificationMail(Attendee attendee, String url) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dyproso-2023@tu-dresden.de");
        helper.setTo(attendee.getMail());
        helper.addBcc("dyproso-2023@tu-dresden.de"); // BCC E-Mail-Adresse hinzufügen
        helper.setSubject("DyProSo - Verify your email");
        String verifyURL = url + "/verify?code=" + attendee.getVerificationCode();

        String content = "<body><div style=\"margin-left: auto;margin-right: auto;width:80%;\"><br><img style=\"display:block;display : inline;\" src=\"https://dyproso-2023.com/images/TUD.png\" height=\"40\" width=\"144\"><h1 style=\"display : inline; float : right;font-size: calc(12px + 2vw);\">DyProSo-2023</h1><br><br><br><img src=\"https://dyproso-2023.com/images/output-onlinepngtools.png\" style=\"margin-left: 0;margin-right: 0;display: block;max-width: 100%;height: auto;margin-bottom: 20px;\"><h2 style=\"font-family: 'Verdana', sans-serif;font-weight:normal;\">Verify your email address</h2>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Dear " + attendee.getName() + ",</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Welcome to DyProSo-2023!</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Please click the link below to verify your email address.</p>"
                + "<p><a href=\"" + verifyURL + "\"><button style=\"width: 200px;height: 35px;background-color: #4CAF50;font-size: 16px;font-family: 'Verdana', sans-serif;text-align: center;color: white;cursor:pointer;border-radius: 8px;\">Verify email address</button></a></p><br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Thank you</p><br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;color:gray;\">If you did not make this request or if you are having any issues with your account, please contact us at dyproso-2023@tu-dresden.de</p></div></body>";
        helper.setText(content, true);
        mailSender.send(message);

    }

    /**
     * Sends a confirmation email after successful email verification.
     * 
     * This informs the user that the account is now active and usable.
     * 
     * @param attendee recipient of the email
     */
    public void send_verification_complete_mail(Attendee attendee) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dyproso-2023@tu-dresden.de");
        helper.setTo(attendee.getMail());
        helper.addBcc("dyproso-2023@tu-dresden.de"); // BCC E-Mail-Adresse hinzufügen
        helper.setSubject("DyProSo - Verification complete");

        String content = "<body><div style=\"margin-left: auto;margin-right: auto;width:80%;\"><br><img style=\"display:block;display : inline;\" src=\"https://dyproso-2023.com/images/TUD.png\" height=\"40\" width=\"144\"><h1 style=\"display : inline; float : right;font-size: calc(12px + 2vw);\">DyProSo-2023</h1><br><br><br><img src=\"https://dyproso-2023.com/images/output-onlinepngtools.png\" style=\"margin-left: 0;margin-right: 0;display: block;max-width: 100%;height: auto;margin-bottom: 20px;\"><h2 style=\"font-family: 'Verdana', sans-serif;font-weight:normal;\">Verification complete</h2>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Dear " + attendee.getName() + ",</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Thank you for verifying your e-mail address.</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">You can login to your DyProSo-2023 account now in order to </p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">(a) register and </p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">(b) upload abstracts. </p><br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;color:gray;\">If you did not make this request or if you are having any issues with your account, please contact us at dyproso-2023@tu-dresden.de</p></div></body>";
        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * Sends a confirmation email after successful registration.
     * Informs the attendee that the registration process is complete.
     * 
     * @param attendee recipient of the email
     */
    public void send_registration_complete_mail(Attendee attendee) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dyproso-2023@tu-dresden.de");
        helper.setTo(attendee.getMail());
        helper.addBcc("dyproso-2023@tu-dresden.de"); // BCC E-Mail-Adresse hinzufügen
        helper.setSubject("DyProSo - Registration complete");

        String content = "<body><div style=\"margin-left: auto;margin-right: auto;width:80%;\"><br><img style=\"display:block;display : inline;\" src=\"https://dyproso-2023.com/images/TUD.png\" height=\"40\" width=\"144\"><h1 style=\"display : inline; float : right;font-size: calc(12px + 2vw);\">DyProSo-2023</h1><br><br><br><img src=\"https://dyproso-2023.com/images/output-onlinepngtools.png\" style=\"margin-left: 0;margin-right: 0;display: block;max-width: 100%;height: auto;margin-bottom: 20px;\"><h2 style=\"font-family: 'Verdana', sans-serif;font-weight:normal;\">Registration complete</h2>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Dear " + attendee.getName() + ",</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Thank you for registering to DyProSo-2023.</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">An invoice will be sent as pdf document via e-mail within approximately the next two weeks.</p><br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;color:gray;\">If you did not make this request or if you are having any issues with your account, please contact us at dyproso-2023@tu-dresden.de</p></div></body>";
        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * Sends confirmation email for an additional ticket purchase.
     * 
     * @param attendee recipient of the email
     */
    public void send_additional_ticket_complete_mail(Attendee attendee) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dyproso-2023@tu-dresden.de");
        helper.setTo(attendee.getMail());
        helper.addBcc("dyproso-2023@tu-dresden.de"); // BCC E-Mail-Adresse hinzufügen
        helper.setSubject("DyProSo - Confirmation of additional ticket purchase");

        String content = "<body><div style=\"margin-left: auto;margin-right: auto;width:80%;\"><br><img style=\"display:block;display : inline;\" src=\"https://dyproso-2023.com/images/TUD.png\" height=\"40\" width=\"144\"><h1 style=\"display : inline; float : right;font-size: calc(12px + 2vw);\">DyProSo-2023</h1><br><br><br><img src=\"https://dyproso-2023.com/images/output-onlinepngtools.png\" style=\"margin-left: 0;margin-right: 0;display: block;max-width: 100%;height: auto;margin-bottom: 20px;\"><h2 style=\"font-family: 'Verdana', sans-serif;font-weight:normal;\">Confirmation of additional ticket purchase</h2>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Thank you for purchasing an additional ticket for DyProSo-2023.</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">An invoice will be sent as pdf document via e-mail within approximately the next two weeks.</p><br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;color:gray;\">If you did not make this request or if you are having any issues with your account, please contact us at dyproso-2023@tu-dresden.de</p></div></body>";
        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * Sends confirmation email after successful abstract upload.
     * @param attendee recipient of the email
     */
    public void send_upload_complete_mail(Attendee attendee) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dyproso-2023@tu-dresden.de");
        helper.setTo(attendee.getMail());
        helper.addBcc("dyproso-2023@tu-dresden.de"); // BCC E-Mail-Adresse hinzufügen
        helper.setSubject("DyProSo - Successful Abstract Upload");

        String content = "<body><div style=\"margin-left: auto;margin-right: auto;width:80%;\"><br><img style=\"display:block;display : inline;\" src=\"https://dyproso-2023.com/images/TUD.png\" height=\"40\" width=\"144\"><h1 style=\"display : inline; float : right;font-size: calc(12px + 2vw);\">DyProSo-2023</h1><br><br><br><img src=\"https://dyproso-2023.com/images/output-onlinepngtools.png\" style=\"margin-left: 0;margin-right: 0;display: block;max-width: 100%;height: auto;margin-bottom: 20px;\"><h2 style=\"font-family: 'Verdana', sans-serif;font-weight:normal;\">Successful Abstract Upload</h2>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">Your abstract upload has been successful. Thank you!</p>"
                + "<p style=\"font-family: 'Verdana', sans-serif;\">You will be informed on the time slot of your contribution as soon as possible.</p><br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;color:gray;\">If you did not make this request or if you are having any issues with your account, please contact us at dyproso-2023@tu-dresden.de</p></div></body>";
        helper.setText(content, true);
        mailSender.send(message);
    }

    public List<Attendee> getAllUsers(){
        return attendeeRepository.findAll(Sort.by(Sort.Direction.ASC, "lastname"));
    }


    public Attendee getAttendeeById(Long id){
        Optional<Attendee> attendee = attendeeRepository.findById(id);
        if(attendee.isPresent()){
            return attendee.get();
        }
        return null;
    }

    public Attendee getAttendeeByName(String name){
        for(Attendee attendee : attendeeRepository.findAll()){
            if(attendee.getMail().equalsIgnoreCase(name)){
                return attendee;
            }
        }
        return null;
    }

    public boolean checkIfAccountWithEmailExists(String mail){
        for(Attendee attendee : attendeeRepository.findAll()){
            if(attendee.getMail().equalsIgnoreCase(mail)){
                return true;
            }
        }
        return false;
    }


    /**
     * Sends a password reset email containing a reset link.
     * 
     * @param to recipient email address
     * @param link password reset URL 
     */
    public void sendMail(String to, String link) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dyproso-2023@tu-dresden.de");
        helper.setTo(to);
        helper.addBcc("dyproso-2023@tu-dresden.de"); // BCC E-Mail-Adresse hinzufügen
        helper.setSubject("DyProSo - Password Reset Request");
        String content = "<body><div style=\"margin-left: auto;margin-right: auto;width:80%;\"><br><img style=\"display:block;display : inline;\" src=\"https://dyproso-2023.com/images/TUD.png\" height=\"40\" width=\"144\"><h1 style=\"display : inline; float : right;font-size: calc(12px + 2vw);\">DyProSo-2023</h1><br><br><br><img src=\"https://dyproso-2023.com/images/output-onlinepngtools.png\" style=\"margin-left: 0;margin-right: 0;display: block;max-width: 100%;height: auto;margin-bottom: 20px;\"><h2 style=\"font-family: 'Verdana', sans-serif;font-weight:normal;\">Password Reset Request</h2>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\"><button style=\"width: 200px;height: 35px;background-color: #4CAF50;font-size: 16px;text-align: center;color: white;cursor:pointer;border-radius: 8px;\" >Change my password</button></a></p>"
                + "<br>"
                + "<p style=\"font-family: 'Verdana', sans-serif;color:gray;\">If you did not make this request or if you are having any issues with your account, please contact us at dyproso-2023@tu-dresden.de</p></div></body>";
        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * Creates or updates a password reset token for the given email address.
     * 
     * A new token is only issued if no valid (non-expired) token exists.
     * Otherwise an exception is thrown to prevent multiple active reset links.
     * 
     * @param token generated reset token
     * @param email email address of the attendee
     */
    public void updateResetPasswordToken(String token, String email) throws IllegalStateException, NullPointerException {
        Attendee attendee = this.getAttendeeByName(email);

        if(attendee != null && attendee.getExpiryDate() != null && attendee.getExpiryDate().isAfter(LocalDateTime.now())){
            throw new IllegalStateException("Reset token has not yet expired");
        }else if(attendee != null){
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
            attendee.setResetPasswordToken(token);
            attendee.setExpiryDate(expiryDate);
            attendeeRepository.saveAndFlush(attendee);
        }
        else{
            throw new NullPointerException("Could not find any attendee with the email " + email);
        }
    }
    public void setResetPasswordTokenNull(String email){
        Attendee attendee = this.getAttendeeByName(email);
        attendee.setResetPasswordToken(null);
        attendee.setExpiryDate(null);
        attendeeRepository.saveAndFlush(attendee);
    }

    /**
     * Deletes an attendee and the associated user account.
     * 
     * This operation is permanent and removes all related authentication data.
     * 
     * @param attendee attendee to delete
     */
    public void deleteAttendee(Attendee attendee){
        userAccountManagement.delete(attendee.getUserAccount());
        attendeeRepository.delete(attendee);
    }

    public Attendee getByResetPasswordToken(String token){
        Attendee attendee = attendeeRepository.findByResetPasswordToken(token);

        if(attendee != null && LocalDateTime.now().isAfter(attendee.getExpiryDate())){
            attendee.setResetPasswordToken(null);
            attendee.setExpiryDate(null);
            attendeeRepository.saveAndFlush(attendee);
            return null;
        }

        return attendee;
    }

    public Attendee getByVerificationCode(String verificationCode){
        return attendeeRepository.findByVerificationCode(verificationCode);
    }

    /**
     * Activates the attendee account after successful verification.
     * 
     * Removes the verification code to prevent reuse.
     * 
     * @param attendee attendee to verify
     */
    public void verify(Attendee attendee){
        //userAccountManagement.enable(attendee.getUserAccount().getId());
        attendee.getUserAccount().setEnabled(true);
        attendee.setVerificationCode(null);
        attendeeRepository.saveAndFlush(attendee);
    }

    /**
     * Updates the password of an attendee and clears any reset token.
     * 
     * @param attendee user whose password should be updated
     * @param newPassword new password
     */
    public void updatePassword(Attendee attendee, String newPassword){
        this.userAccountManagement.changePassword(attendee.getUserAccount(), Password.UnencryptedPassword.of(newPassword));
        attendee.setResetPasswordToken(null);
        attendee.setExpiryDate(null);
        attendeeRepository.saveAndFlush(attendee);
    }

    public void uploadFile(Attendee author){
        author.setFilesUploaded(author.getFilesUploaded() + 1);
        attendeeRepository.saveAndFlush(author);
    }

    public void deleteFile(Attendee author){
        author.setFilesUploaded(author.getFilesUploaded() - 1);
        attendeeRepository.saveAndFlush(author);
    }

    public void editUser(EditForm editForm, Attendee attendee){
        attendee.setLastname(editForm.getLastname());
        attendee.setName(editForm.getFirstname());
        attendee.setAffiliation(editForm.getAffiliation());
        attendee.setTitle(editForm.getTitle());
        attendeeRepository.saveAndFlush(attendee);
    }

    /**
     * Completes the attendee registration process by mapping form data to the domain model.
     * This includes:
     *  - student status
     *  - billing address configuration (EU / non-EU VAT rules)
     *  - optional HZDR excursion participation
     *  - photo permission flag
     * 
     * This method finalizes the registration step before persistence.
     * 
     * @param attendee the attendee to update
     * @param registrationForm submitted registration data
     */
    public void register_participant(Attendee attendee, RealRegistrationForm registrationForm){
        boolean student = false;
        if(registrationForm.getStudent().equals("TRUE")){
            student = true;
        }
        attendee.setStudent(student);
        boolean type_of_billing_address = false;
        if(registrationForm.getType_of_billing_address().equals("EU")){
            type_of_billing_address = true;
        }
        attendee.setType_of_billing_address(type_of_billing_address);
        attendee.setBilling_address_lastname(registrationForm.getBilling_address_lastname());
        attendee.setBilling_address_firstname(registrationForm.getBilling_address_firstname());
        attendee.setBilling_address_poBox(registrationForm.getBilling_address_poBox());
        attendee.setBilling_address_street(registrationForm.getBilling_address_street());
        attendee.setBilling_address_postalCode(registrationForm.getBilling_address_postalCode());
        attendee.setBilling_address_city(registrationForm.getBilling_address_city());
        attendee.setBilling_address_country(registrationForm.getBilling_address_country());
        if(type_of_billing_address){
            attendee.setVat_number(registrationForm.getVat_number());
        }else{
            attendee.setVat_number(null);
        }
        if(registrationForm.getHzdr().equals("TRUE")){
            attendee.setHzdr(true);
            attendee.setHzdr_lastname(registrationForm.getHzdr_lastname());
            attendee.setHzdr_firstname(registrationForm.getHzdr_firstname());
            attendee.setHzdr_cardnumber(registrationForm.getHzdr_cardnumber());
            attendee.setHzdr_birthdate(LocalDate.parse(registrationForm.getHzdr_birthdate()));
        }else{
            attendee.setHzdr(false);
            attendee.setHzdr_lastname(null);
            attendee.setHzdr_firstname(null);
            attendee.setHzdr_cardnumber(null);
            attendee.setHzdr_birthdate(null);
        }

        attendee.setPhoto_permission(registrationForm.getPhoto_permission());
        attendee.setDate_of_registration(LocalDate.now());
    }


    /**
     * Creates an Excel file containing billing information for attendees and companions.
     * 
     * Used for administrative processing and invoicing.
     * Includes both attendee and companion billing data.
     * 
     * @param companionsList list of companions to include in export
     * @return generated Excel file as byte array
     */
    public byte[] create_excel_file(List<Companion> companionsList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Billing_information");
        //Create top row with column headings
        String[] column_headings = {"E-Mail", "Date of registration", "Last name", "First name", "Has Business Address within EU", "VAT number","PO Box", "Street", "Postal code", "City", "Country", "Student", "Conference dinner", "HZDR","Is the companion of"};
        Font header_font = workbook.createFont();
        header_font.setBold(true);
        header_font.setFontHeightInPoints((short)12);
        header_font.setColor(IndexedColors.BLACK.index);
        //Create a CellStyle with font
        CellStyle header_style = workbook.createCellStyle();
        header_style.setFont(header_font);
        header_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        //Create header row
        Row header_row = sheet.createRow(0);
        for(int i = 0; i < column_headings.length; i++){
            Cell cell = header_row.createCell(i);
            cell.setCellValue(column_headings[i]);
            cell.setCellStyle(header_style);
        }
        //Fill data
        int row_num = 1;
        for(Attendee attendee : attendeeRepository.findAll(Sort.by(Sort.Direction.ASC, "lastname"))){
            if(attendee.isRegistered()){
                Row row = sheet.createRow(row_num++);
                row.createCell(0).setCellValue(attendee.getMail());
                row.createCell(1).setCellValue(attendee.date_of_registration_toString());
                row.createCell(2).setCellValue(attendee.getBilling_address_lastname());
                row.createCell(3).setCellValue(attendee.getBilling_address_firstname());
                row.createCell(4).setCellValue(attendee.get_type_billing_string());
                row.createCell(5).setCellValue(attendee.get_vat_string());
                row.createCell(6).setCellValue(attendee.getBill_po_box());
                row.createCell(7).setCellValue(attendee.getBilling_address_street());
                row.createCell(8).setCellValue(attendee.getBilling_address_postalCode());
                row.createCell(9).setCellValue(attendee.getBilling_address_city());
                row.createCell(10).setCellValue(attendee.getBilling_address_country());
                row.createCell(11).setCellValue(attendee.isStudent());
                row.createCell(12).setCellValue("Yes");
                row.createCell(13).setCellValue(attendee.take_part_hzdr());
                row.createCell(14).setCellValue("/");
            }


            List<Companion> companions = new ArrayList<>();
            for(Companion companion : companionsList){
                if(attendee.equals(companion.getCompanionOf())){
                    companions.add(companion);
                }
            }

            for(Companion companion : companions){
                Row companion_row = sheet.createRow(row_num++);
                companion_row.createCell(0).setCellValue(companion.getCompanionOf().getMail());
                companion_row.createCell(1).setCellValue(companion.date_of_registration_toString());
                companion_row.createCell(2).setCellValue(companion.getBilling_address_lastname());
                companion_row.createCell(3).setCellValue(companion.getBilling_address_firstname());
                companion_row.createCell(4).setCellValue(companion.get_type_of_billing_string());
                companion_row.createCell(5).setCellValue(companion.get_vat_string());
                companion_row.createCell(6).setCellValue(companion.getBill_po_box());
                companion_row.createCell(7).setCellValue(companion.getBilling_address_street());
                companion_row.createCell(8).setCellValue(companion.getBilling_address_postalCode());
                companion_row.createCell(9).setCellValue(companion.getBilling_address_city());
                companion_row.createCell(10).setCellValue(companion.getBilling_address_country());
                companion_row.createCell(11).setCellValue("/");
                companion_row.createCell(12).setCellValue(companion.takes_part_social_program());
                companion_row.createCell(13).setCellValue(companion.takes_part_hzdr());
                companion_row.createCell(14).setCellValue(companion.getCompanionOf().participant_name_for_excel());
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
        workbook.write(fileOutputStream);
        return fileOutputStream.toByteArray();

    }

    /**
     * Creates an Excel file containing HZDR excursion participant information.
     * 
     * Includes attendees and companions registered for HZDR participation.
     * 
     * @param companionsList list of companions
     * @return generated Excel file as byte array
     */
    public byte[] create_excursion_excel_file(List<Companion> companionsList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("HZDR_informations");
        //Create top row with column headings
        String[] column_headings = {"Last name", "First name", "Card number", "Date of birth"};
        Font header_font = workbook.createFont();
        header_font.setBold(true);
        header_font.setFontHeightInPoints((short)12);
        header_font.setColor(IndexedColors.BLACK.index);
        //Create a CellStyle with font
        CellStyle header_style = workbook.createCellStyle();
        header_style.setFont(header_font);
        header_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        //Create header row
        Row header_row = sheet.createRow(0);
        for(int i = 0; i < column_headings.length; i++){
            Cell cell = header_row.createCell(i);
            cell.setCellValue(column_headings[i]);
            cell.setCellStyle(header_style);
        }
        //Fill data
        int row_num = 1;
        for(Attendee attendee : attendeeRepository.findAll(Sort.by(Sort.Direction.ASC, "lastname"))){
            if(attendee.getHzdr()){
                Row row = sheet.createRow(row_num++);
                row.createCell(0).setCellValue(attendee.getHzdr_lastname());
                row.createCell(1).setCellValue(attendee.getHzdr_firstname());
                row.createCell(2).setCellValue(attendee.getHzdr_cardnumber());
                row.createCell(3).setCellValue(attendee.hzdr_birthdate_toString());
            }



            List<Companion> companions = new ArrayList<>();
            for(Companion companion : companionsList){
                if(attendee.equals(companion.getCompanionOf()) && companion.isHzdr()){
                    companions.add(companion);
                }
            }

            for(Companion companion : companions){
                Row companion_row = sheet.createRow(row_num++);
                companion_row.createCell(0).setCellValue(companion.getHzdr_lastname());
                companion_row.createCell(1).setCellValue(companion.getHzdr_firstname());
                companion_row.createCell(2).setCellValue(companion.getHzdr_cardnumber());
                companion_row.createCell(3).setCellValue(companion.hzdr_birthdate_toString());
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
        workbook.write(fileOutputStream);
        return fileOutputStream.toByteArray();

    }

    /**
     * Creates an Excel file for printing name tags and photo permissions.
     * 
     * @return generated Excel file as byte array
     */
    public byte[] create_nametag_excel_file() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Names-tags_photo-permission");
        //Create top row with column headings
        String[] column_headings = {"Name", "Affiliation", "Photo permission"};
        Font header_font = workbook.createFont();
        header_font.setBold(true);
        header_font.setFontHeightInPoints((short)12);
        header_font.setColor(IndexedColors.BLACK.index);
        //Create a CellStyle with font
        CellStyle header_style = workbook.createCellStyle();
        header_style.setFont(header_font);
        header_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        //Create header row
        Row header_row = sheet.createRow(0);
        for(int i = 0; i < column_headings.length; i++){
            Cell cell = header_row.createCell(i);
            cell.setCellValue(column_headings[i]);
            cell.setCellStyle(header_style);
        }
        //Fill data
        int row_num = 1;
        for(Attendee attendee : attendeeRepository.findAll(Sort.by(Sort.Direction.ASC, "lastname"))){
            if(attendee.isRegistered()){
                Row row = sheet.createRow(row_num++);
                row.createCell(0).setCellValue(attendee.participant_name());
                row.createCell(1).setCellValue(attendee.affiliationToString());
                row.createCell(2).setCellValue(attendee.gives_permission());
            }

        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
        workbook.write(fileOutputStream);
        return fileOutputStream.toByteArray();
    }

}
