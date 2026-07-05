package DyProSo2023.User;

import DyProSo2023.companion.CompanionForm;
import DyProSo2023.companion.CompanionManagement;
import DyProSo2023.files.Doc;
import DyProSo2023.files.DocManagement;

import net.bytebuddy.utility.RandomString;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import DyProSo2023.admin.AdminController;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller responsible for handling attendee-related web requests.
 * 
 * This includes:
 *  - user authentication (login/logout)
 *  - account registration and verification
 *  - profile management
 *  - password reset flow
 *  - companion registration
 */
@Controller
public class AttendeeController {
    private final AttendeeManagement attendeeManagement;

    private final DocManagement docManagement;

    private final CompanionManagement companionManagement;

    public AttendeeController(AttendeeManagement attendeeManagement, DocManagement docManagement, CompanionManagement companionManagement){
        this.attendeeManagement = attendeeManagement;
        this.docManagement = docManagement;
        this.companionManagement = companionManagement;
    }


    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("title", "Login");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = attendeeManagement.getAttendeeByName(auth.getName());
        if(user != null){
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(){

        return "login";
    }

    @GetMapping("/logout")
    public String logout(){
        return "login";
    }



    @GetMapping("/create_account")
    public String getRegistration(@ModelAttribute("form") RegistrationForm form, Errors e,Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = attendeeManagement.getAttendeeByName(auth.getName());
        if(user != null){
            return "redirect:/";
        }
        if(!model.containsAttribute("success")){
            model.addAttribute("success", false);
        }
        model.addAttribute("title", "Create Account");
        return "create_account";
    }

    /**
     * Handles user registration by validating input, creating an account, and sending a verification email.
     * 
     * @param request HTTP request used to build verification URL
     * @param form registration form containing user input data
     * @param bindingResult hold validation errors
     * @param model Spring MVC model
     * @param redirectAttributes flash attributes for redirect handling
     * @return redirect to verification page or registration form on error
     */
    @PostMapping("/create_account")
    public String register(HttpServletRequest request, @ModelAttribute("form") RegistrationForm form, Errors e,BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        String pattern_password = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.+[@$!%*?&-_#~+^])(?=\\S+$).{8,}";
        String pattern_email = "^.+@.+\\..+$";
        model.addAttribute("title", "Create Account");

        if(form.getLastname().length() == 0 || form.getLastname().isBlank()){
            bindingResult.addError(new FieldError("form", "lastname", "Missing last name"));
        }
        if(form.getName().length() == 0 || form.getName().isBlank()){
            bindingResult.addError(new FieldError("form", "name", "Missing first name(s)"));
        }

        if(form.getTerms() == null || !form.getTerms()){
            bindingResult.addError(new FieldError("form", "terms", "You have to agree the terms & conditions"));
        }

        if (!form.getMail().matches(pattern_email)) {
            bindingResult.addError(new FieldError("form", "mail", "no valid Email."));
        } else if (attendeeManagement.checkIfAccountWithEmailExists(form.getMail())) {
            bindingResult.addError(new FieldError("form", "mail",
                    "This Email is already registered."));
        }
        if (!form.getPassword().matches(pattern_password)) {
            bindingResult.addError(new FieldError("form",
                    "password","The password needs following requirements: " +
                    "One number, one lowercase, one uppercase, one specialsign, 8 letters long"));
        } else if (!form.getPassword().equals(form.getRpassword())) {
            bindingResult.addError(new FieldError("form", "rpassword",
                    "Please make sure your passwords match."));
        }
        if (e.hasErrors()) {
            model.addAttribute("success", false);
            return "create_account";
        }

        Attendee attendee = attendeeManagement.createAttendee(form);
        try {
            attendeeManagement.sendVerificationMail(attendee, request.getRequestURL().toString());
        }catch(MessagingException messagingException){
            bindingResult.addError(new FieldError("form", "mail", "Error while sending email. Please try again later."));
        }
        if(e.hasErrors()){
            model.addAttribute("success", false);
            attendeeManagement.deleteAttendee(attendee);
            return "create_account";
        }
        redirectAttributes.addFlashAttribute("user", attendee);
        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String getVerifyRegistration(Model model){
        Attendee user = null;

        if(model.containsAttribute("user")){
             user = (Attendee) model.getAttribute("user");
        }
        if(user == null || user.isVerified()){
            return "redirect:/";
        }


        model.addAttribute("attendee", user);
        model.addAttribute("title", "Verify account");
        if(!model.containsAttribute("success")){
            model.addAttribute("success", true);
        }
        return "verifyRegistration";
    }

    /**
     * Resends the verification email to the specified user.
     * 
     * @param request HTTP request used to build the verification URL
     * @param userId ID of the user who should receive the verification email
     * @param model Spring MVC model
     * @param redirectAttributes flash attributes for redirect handling
     * @return redirects back to verification page with success or failure state 
     */
    @PostMapping("/verify")
    public String resendVerificationMail(HttpServletRequest request, @RequestParam Long userId, Model model, RedirectAttributes redirectAttributes){
        Attendee user = attendeeManagement.getAttendeeById(userId);
        String url = AdminController.getDomain(request.getRequestURL().toString()) + "create_account";
        redirectAttributes.addFlashAttribute("user", user);
        try {
            attendeeManagement.sendVerificationMail(user, url);
            redirectAttributes.addFlashAttribute("success", true);
        }catch (MessagingException exception){
            exception.printStackTrace();
            redirectAttributes.addFlashAttribute("success", false);
        }

        return "redirect:/verify";
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/profile")
    public String getProfile(@ModelAttribute("realRegistrationForm") RealRegistrationForm realRegistrationForm, Errors e, Model model) {
        /*Get the Abstracts the logged-in user has Uploaded*/
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usermail = ((UserDetails)principal).getUsername();
        List<Doc> docs = this.docManagement.getFilesByAuthorMail(usermail);
        model.addAttribute("docs", docs);
        model.addAttribute("title", "Profile");
        model.addAttribute("registration_state", attendeeManagement.getRegistrationState());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Profile");
        model.addAttribute("isAbstractSubmissionActive", docManagement.getAbstractSubmissionIsActive());
        model.addAttribute("companions", companionManagement.getListByAttendee(user));
        model.addAttribute("billing_type", null);
        return "profile";}

    /**
     * Completes the registration process by validating and saving billing data.
     * 
     * @param realRegistrationForm form containing billing and optional data
     * @param bindingResult holds validation errors for form binding
     * @param model Spring MVC model
     * @return redirect to success page if valid, otherwise reloads profile page
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/profile")
    public String register_participant(@ModelAttribute("realRegistrationForm") RealRegistrationForm realRegistrationForm, Errors e, BindingResult bindingResult, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Profile");
        model.addAttribute("docs", docManagement.getFilesByAuthorMail(user.getMail()));
        model.addAttribute("isAbstractSubmissionActive", docManagement.getAbstractSubmissionIsActive());
        model.addAttribute("registration_state", attendeeManagement.getRegistrationState());


        if (realRegistrationForm.getBilling_address_lastname().length() == 0 || realRegistrationForm.getBilling_address_lastname().isBlank()) {
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "billing_address_lastname", "Missing last name"));
        }
        if(realRegistrationForm.getBilling_address_firstname().length() == 0 || realRegistrationForm.getBilling_address_firstname().isBlank()){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "billing_address_firstname", "Missing first name(s)"));
        }
        if(realRegistrationForm.getBilling_address_street().length() == 0 || realRegistrationForm.getBilling_address_street().isBlank()){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "billing_address_street", "Missing street"));
        }
        if(realRegistrationForm.getBilling_address_postalCode().length() == 0 || realRegistrationForm.getBilling_address_postalCode().isBlank()){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "billing_address_postalCode", "Missing postal code"));
        }
        if(realRegistrationForm.getBilling_address_city().length() == 0 || realRegistrationForm.getBilling_address_city().isBlank()){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "billing_address_city", "Missing city"));
        }
        if(realRegistrationForm.getBilling_address_country().length() == 0 || realRegistrationForm.getBilling_address_country().isBlank()){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "billing_address_country", "Missing country"));
        }
        if(realRegistrationForm.getHzdr() != null && realRegistrationForm.getHzdr().equals("TRUE") && (realRegistrationForm.getHzdr_lastname().length() == 0 || realRegistrationForm.getHzdr_lastname().isBlank())){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "hzdr_lastname", "Missing last name"));
        }
        if(realRegistrationForm.getHzdr() != null && realRegistrationForm.getHzdr().equals("TRUE") && (realRegistrationForm.getHzdr_firstname().length() == 0 || realRegistrationForm.getHzdr_firstname().isBlank())){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "hzdr_firstname", "Missing first name(s)"));
        }
        if(realRegistrationForm.getHzdr() != null && realRegistrationForm.getHzdr().equals("TRUE") && (realRegistrationForm.getHzdr_cardnumber().length() == 0 || realRegistrationForm.getHzdr_cardnumber().isBlank())){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "hzdr_cardnumber", "Missing card number"));
        }
        if(realRegistrationForm.getHzdr() != null && realRegistrationForm.getHzdr().equals("TRUE") && (realRegistrationForm.getHzdr_birthdate() == null || realRegistrationForm.getHzdr_birthdate().length() == 0 || realRegistrationForm.getHzdr_birthdate().isBlank())){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "hzdr_birthdate", "Missing date of birth"));
        }

        if(realRegistrationForm.getType_of_billing_address() != null && realRegistrationForm.getType_of_billing_address().equals("EU") && (realRegistrationForm.getVat_number().length() == 0 || realRegistrationForm.getVat_number().isBlank())){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "vat_number", "Missing VAT number"));
        }

        if(realRegistrationForm.getType_of_billing_address() == null){
            bindingResult.addError(new FieldError("realRegistrationForm",
                    "type_of_billing_address", "Please select a type"));
        }



        if(e.hasErrors()){
            return "profile";
        }
        attendeeManagement.register_participant(user, realRegistrationForm);
        try{
            attendeeManagement.send_registration_complete_mail(user);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return "redirect:/registration_successful";
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/registration_successful")
    public String registration_complete(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        if(!user.isRegistered() || attendeeManagement.getRegistrationState() == 0 || attendeeManagement.getRegistrationState() == 2){
            return "redirect:/profile";
        }
        model.addAttribute("title", "Registration complete");
        return "registration_successful";
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/additional_tickets")
    public String getAdditional_ticket_page(@ModelAttribute("companionForm") CompanionForm companionForm, Errors e, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        if(!user.isRegistered() || attendeeManagement.getRegistrationState() == 0 || attendeeManagement.getRegistrationState() == 2){
            return "redirect:/profile";
        }
        model.addAttribute("title", "Additional tickets");
        model.addAttribute("user", user);
        return "additional_tickets";
    }

    /**
     * Creates and additional companion ticket for the currently logged-in user.
     * 
     * Validates billing information, optional HZDR data, and ticket selection before creating the companion entry.
     * 
     * @param companionForm form containing companion ticket data
     * @param bindingResult holds validation errors
     * @param model Spring MVC model
     * @return redirect to success page if valid, otherwise reloads form
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/additional_tickets")
    public String create_companion(@ModelAttribute("companionForm") CompanionForm companionForm, Errors e, BindingResult bindingResult, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        model.addAttribute("title", "Additional tickets");
        model.addAttribute("user", user);

        if (!companionForm.getSame_billing_address() && (companionForm.getBilling_address_lastname().length() == 0 || companionForm.getBilling_address_lastname().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "billing_address_lastname", "Missing last name"));
        }
        if (!companionForm.getSame_billing_address() && (companionForm.getBilling_address_firstname().length() == 0 || companionForm.getBilling_address_firstname().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "billing_address_firstname", "Missing first name(s)"));
        }
        if (!companionForm.getSame_billing_address() && (companionForm.getBilling_address_street().length() == 0 || companionForm.getBilling_address_street().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "billing_address_street", "Missing street"));
        }
        if (!companionForm.getSame_billing_address() && (companionForm.getBilling_address_postalCode().length() == 0 || companionForm.getBilling_address_postalCode().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "billing_address_postalCode", "Missing postal code"));
        }
        if (!companionForm.getSame_billing_address() && (companionForm.getBilling_address_city().length() == 0 || companionForm.getBilling_address_city().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "billing_address_city", "Missing city"));
        }
        if (!companionForm.getSame_billing_address() && (companionForm.getBilling_address_country().length() == 0 || companionForm.getBilling_address_country().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "billing_address_country", "Missing country"));
        }
        if (!((companionForm.getSocial_programm() != null && companionForm.getSocial_programm().equals("TRUE")) || (companionForm.getHzdr() != null && companionForm.getHzdr().equals("TRUE")))) {
            bindingResult.addError(new FieldError("companionForm",
                    "hzdr", "You have to choose at least one ticket\n"));
        }
        if (companionForm.getHzdr() != null && companionForm.getHzdr().equals("TRUE") && (companionForm.getHzdr_lastname().length() == 0 || companionForm.getHzdr_lastname().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "hzdr_lastname", "Missing last name"));
        }
        if (companionForm.getHzdr() != null && companionForm.getHzdr().equals("TRUE") && (companionForm.getHzdr_firstname().length() == 0 || companionForm.getHzdr_firstname().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "hzdr_firstname", "Missing first name(s)"));
        }
        if (companionForm.getHzdr() != null && companionForm.getHzdr().equals("TRUE") && (companionForm.getHzdr_cardnumber().length() == 0 || companionForm.getHzdr_cardnumber().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "hzdr_cardnumber", "Missing card number"));
        }
        if (companionForm.getHzdr() != null && companionForm.getHzdr().equals("TRUE") && (companionForm.getHzdr_birthdate().length() == 0 || companionForm.getHzdr_birthdate().isBlank())) {
            bindingResult.addError(new FieldError("companionForm",
                    "hzdr_birthdate", "Missing date of birth"));
        }
        if(!companionForm.getSame_billing_address() && companionForm.getType_of_billing_address() == null){
            bindingResult.addError(new FieldError("companionForm",
                    "type_of_billing_address", "You have to select a type"));

        }

        boolean type_of_bill_addr = false;
        if(companionForm.getType_of_billing_address() != null && companionForm.getType_of_billing_address().equals("EU")){
            type_of_bill_addr = true;
        }
        if(!companionForm.getSame_billing_address() && type_of_bill_addr && (companionForm.getVat_number().length() == 0 || companionForm.getVat_number().isBlank())){
            bindingResult.addError(new FieldError("companionForm",
                    "vat_number", "Missing VAT number"));
        }


        if(e.hasErrors()){
            return "additional_tickets";
        }

        companionManagement.createCompanion(user, companionForm);
        try{
            attendeeManagement.send_additional_ticket_complete_mail(user);
        }catch(Exception exception){
            exception.printStackTrace();
        }

        return "redirect:/registration_successful";
    }



    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/profile/edit")
    public String getEditProfilePage(@ModelAttribute("editForm") EditForm editForm, Errors e, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        model.addAttribute("title", "Edit Profile");
        model.addAttribute("user", user);
        return "edit_profile";
    }

    /**
     * Updates the profile information of the currently logged-in user.
     * 
     * @param editForm form containing updated user profile data
     * @param bindingResult holds validation errors
     * @param model Spring MVC model
     * @return redirects to profile page if successful, otherwise reloads edit form
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("editForm") EditForm editForm, Errors e, BindingResult bindingResult, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());

        if(editForm.getLastname().length() == 0 || editForm.getLastname().isBlank()){
                bindingResult.addError(new FieldError("editForm",
                        "lastname", "Missing last name"));
        }

        if(editForm.getFirstname().length() == 0 || editForm.getFirstname().isBlank()){
            bindingResult.addError(new FieldError("editForm",
                    "firstname", "Missing first name(s)"));
        }

        if(e.hasErrors()){
            model.addAttribute("user", user);
            model.addAttribute("title", "Edit Profile");
            return "edit_profile";
        }

        attendeeManagement.editUser(editForm, user);

        return "redirect:/profile";
    }



    @GetMapping("/forgot_password")
    public String getForgotPassword(@ModelAttribute("emailForm") ForgotPasswordForm emailForm, Model model){

        if(!model.containsAttribute("success")){
            model.addAttribute("success", false);
        }
        model.addAttribute("title", "Forgot password");

        return "forgotPassword"; }

    /**
     * Initiates the password reset process by generating a reset token, sending a reset email, and handling possible errors.
     * 
     * @param request HTTP request used to build the reset URL
     * @param emailForm containing the user's email address
     * @param bindingResult holds validation and processing errors
     * @param redirectAttributes flash attributes used after redirect
     * @param model Spring MVC model
     * @return redirect back to forgot password page with success or error state
     */
    @PostMapping("/forgot_password")
    public String forgotPassword(HttpServletRequest request, @ModelAttribute("emailForm") ForgotPasswordForm emailForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model){
        String token = RandomString.make(60);
        String email = emailForm.getEmail();
        model.addAttribute("title", "Forgot password");
        try{
            attendeeManagement.updateResetPasswordToken(token, email);
            String resetPasswordLink = request.getRequestURL().toString() + "/reset_password?token=" + token;
            attendeeManagement.sendMail(email, resetPasswordLink);
        } catch (MessagingException mailException){
            bindingResult.addError(new FieldError("emailForm", "email", "Error while sending email. Please try again later."));
            attendeeManagement.setResetPasswordTokenNull(email);
        } catch(IllegalStateException illegalStateException){
            bindingResult.addError(new FieldError("emailForm", "email", "Password reset request has already been sent. Please check your email."));
        } catch (NullPointerException exception){
            bindingResult.addError(new FieldError("emailForm", "email", "There is no user registered with that email address"));

        }
        if(bindingResult.hasErrors()) {
            return "forgotPassword";
        }

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("title", "Forgot password");
        return "redirect:/forgot_password";
    }

    @GetMapping("/forgot_password/reset_password")
    public String showResetPasswordForm(@Param(value="token") String token, Model model,
                                        PasswordResetForm form){

        Attendee attendee = attendeeManagement.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if(attendee == null){
            model.addAttribute("message", "Invalid Token");

        }

        if(!model.containsAttribute("PasswordResetForm")){
            model.addAttribute("PasswordResetForm", new PasswordResetForm("",""));
        }
        model.addAttribute("title", "Reset password");
        return "resetPasswordForm";
    }

    /**
     * Processes password reset using a reset token.
     * 
     * @param request HTTP request containing reset token
     * @param form password reset form with new credentials
     * @param bindingResult validation errors
     * @param redirectAttributes flash attributes for redirect handling
     * @param model Spring MVC model
     * @return redirect to homepage if successful, otherwise back to form
     */
    @PostMapping("/forgot_password/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model,
                                       @ModelAttribute("PasswordResetForm") PasswordResetForm form,
                                       BindingResult bindingResult, RedirectAttributes redirectAttributes){
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String pattern_password = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.+[@$!%*?&-_#~+^])(?=\\S+$).{8,}";

        Attendee attendee = attendeeManagement.getByResetPasswordToken(token);

        if(attendee == null){
            bindingResult.addError(new FieldError("form", "rpassword", "Invalid Token"));
        }else if (!form.getPassword().matches(pattern_password)) {
            bindingResult.addError(new FieldError("form",
                    "rpassword","The password needs following requirements: " +
                    "One number, one lowercase, one uppercase, one specialsign, 8 letters long"));
        }else if (!form.getPassword().equals(form.getRpassword())){
            bindingResult.addError(new FieldError("form", "rpassword", "Please make sure your passwords match."));
        }


        model.addAttribute("title", "Reset your password");

        if (bindingResult.hasErrors()){
            //return "resetPasswordForm";
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.PasswordResetForm", bindingResult);
            redirectAttributes.addFlashAttribute("PasswordResetForm", form);
            redirectAttributes.addFlashAttribute("title", "Reset your password");
            return "redirect:/forgot_password/reset_password?token=" + token;
        }

        if(attendee == null){
            model.addAttribute("message", "Invalid Token");

            System.out.println("invalid token");


        } else{
            attendeeManagement.updatePassword(attendee, password);

            System.out.println("successfully changed password");

            model.addAttribute("message", "You have successfully changed your password.");
        }



        return "redirect:/";
    }

    @GetMapping("/create_account/verify")
    public String getVerificationPage(@Param(value = "code") String code, Model model){
        Attendee attendee = attendeeManagement.getByVerificationCode(code);
        if(attendee == null){
            return "redirect:/";
        }
        attendeeManagement.verify(attendee);
        try{
            attendeeManagement.send_verification_complete_mail(attendee);
        }catch (Exception e){
            e.printStackTrace();
        }
        model.addAttribute("title", "Verification");
        return "verification";
    }
}
