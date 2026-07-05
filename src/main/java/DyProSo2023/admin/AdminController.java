package DyProSo2023.admin;

import DyProSo2023.User.Attendee;
import DyProSo2023.User.AttendeeManagement;
import DyProSo2023.admin.hotels.Hotel;
import DyProSo2023.admin.hotels.HotelManagement;
import DyProSo2023.admin.news.News;
import DyProSo2023.admin.news.NewsManagement;
import DyProSo2023.companion.CompanionManagement;
import DyProSo2023.files.Doc;
import DyProSo2023.files.DocManagement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller responsible for the administrative functionality.
 * 
 * Provides endpoints for managing participant accounts, uploaded documents, news, hotel information, registration state, 
 * and exporting administrative data.
 */
@Controller
public class AdminController {

    private final DocManagement docManagement;

    private final NewsManagement newsManagement;

    private final AttendeeManagement attendeeManagement;

    private  final HotelManagement hotelManagement;

    private final CompanionManagement companionManagement;


    public AdminController(DocManagement docManagement, NewsManagement newsManagement,
                           AttendeeManagement attendeeManagement, HotelManagement hotelManagement, CompanionManagement companionManagement){
        this.docManagement = docManagement;
        this.newsManagement = newsManagement;
        this.attendeeManagement = attendeeManagement;
        this.hotelManagement = hotelManagement;
        this.companionManagement = companionManagement;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin")
    public String getDownloadPage(Model model, Authentication authentication){
        model.addAttribute("docs", docManagement.getAllDocs());
        model.addAttribute("title", "Admin");
        if (!model.containsAttribute("success")){
            model.addAttribute("success", 0);
        }

        if(!model.containsAttribute("success_registration_state")){
            model.addAttribute("success_registration_state", 4);
        }

        return "admin";
    }

    /**
     * Enables or disables abstract submission.
     * 
     * @param button selected action
     * @param redirectAttributes attributes used after redirect
     * @return redirect to the admin dashboard
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin")
    public String postAdminPage(@RequestParam String button, RedirectAttributes redirectAttributes){
        if (button.equals("stop")){
            docManagement.setAbstractSubmissionIsActive(false);
            redirectAttributes.addFlashAttribute("success", 1);
            return "redirect:/admin";
        }
        else if (button.equals("continue")){
            docManagement.setAbstractSubmissionIsActive(true);
            redirectAttributes.addFlashAttribute("success", 2);
            return "redirect:/admin";
        }
        return "redirect:/admin";
    }

    /**
     * Updates the current participant registration state.
     * 
     * @param register_state_button selected registration state
     * @param redirectAttributes attributes used after redirect
     * @return redirect to the admin dashboard
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/change_register_state")
    public String change_register_state(@RequestParam String register_state_button, RedirectAttributes redirectAttributes){
        if(register_state_button.equals("before_start")){
            attendeeManagement.setRegistrationState(0);
            redirectAttributes.addFlashAttribute("success_registration_state", 0);
        }else if(register_state_button.equals("start")){
            attendeeManagement.setRegistrationState(1);
            redirectAttributes.addFlashAttribute("success_registration_state", 1);
        }else if(register_state_button.equals("end")){
            attendeeManagement.setRegistrationState(2);
            redirectAttributes.addFlashAttribute("success_registration_state", 2);
        }
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/documents")
    public String getListOfDocuments(Model model){
        if(!model.containsAttribute("docs")){
            List<Doc> docs = new ArrayList<>();
            for(Doc doc : docManagement.getAllDocs()){
                if(doc.getAuthor() != null){
                    docs.add(doc);
                }
            }
            model.addAttribute("docs", docs);
        }
        model.addAttribute("title", "Documents");

        return "listOfDocuments";
    }

    /**
     * Filters uploaded documents according to the selected criteria.
     * 
     * @param model Spring MVC model
     * @param filters selected filter categories
     * @param type document type
     * @param redirectAttributes attributes used after redirect
     * @return redirect to the document overview
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/applyFilters")
    public String applyFilters(Model model, @RequestParam("filters") String filters, @RequestParam("type") String type,RedirectAttributes redirectAttributes){
        String[] active_filters = filters.split(";,");
        if(active_filters.length > 1){
            active_filters[active_filters.length-1] = active_filters[active_filters.length-1].substring(0, active_filters[active_filters.length-1].length()-1);
        }else if(active_filters.length == 1){
            active_filters[0] = active_filters[0].substring(0, active_filters[0].length()-1);
        }

        redirectAttributes.addFlashAttribute("docs", docManagement.getDocsByFilter(active_filters, type));
        return "redirect:/admin/documents";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/accounts")
    public String getResendVerification(Model model){
        model.addAttribute("users", attendeeManagement.getAllUsers());
        model.addAttribute("title", "Accounts");
        if(!model.containsAttribute("success")){
            model.addAttribute("success", false);
        }

        if(!model.containsAttribute("successfullyDelete")){
            model.addAttribute("successfullyDelete", false);
            model.addAttribute("deletedUser", null);
        }

        return "resendVerification";
    }

    /**
     * Resends the verification email to a participant.
     * 
     * @param request current HTTP request
     * @param id unique attendee identifier
     * @param redirectAttributes attributes used after redirect
     * @return redirect to the account administration page
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/resendVerification/{id}")
    public String resendVerification(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Attendee attendee = attendeeManagement.getAttendeeById(id);
        String url = getDomain(request.getRequestURL().toString()) + "create_account";
        try {
            attendeeManagement.sendVerificationMail(attendee, url);
            redirectAttributes.addFlashAttribute("success", true);
        }catch(MessagingException messagingException){
            messagingException.printStackTrace();
        }
        return "redirect:/admin/accounts";
    }

    /**
     * Deletes a participant together with all associated documents and companions.
     * 
     * @param id attendee identifier 
     * @param redirectAttributes attributes used after redirect
     * @return redirect to the account administration page
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Attendee attendee = attendeeManagement.getAttendeeById(id);
        docManagement.deleteAllDocsOfAnAuthor(attendee);
        companionManagement.deleteAllCompanionsOfAttendee(attendee);
        attendeeManagement.deleteAttendee(attendee);
        redirectAttributes.addFlashAttribute("successfullyDelete", true);
        redirectAttributes.addFlashAttribute("deletedUser", attendee);
        return "redirect:/admin/accounts";
    }

    /**
     * Deletes a news entry.
     * 
     * @param id unique news identifier
     * @return redirect to the home page
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/deleteNews/{id}")
    public String deleteNews(@PathVariable Long id){
        newsManagement.deleteNews(newsManagement.getNewsById(id));
        return "redirect:/";
    }

    /**
     * Opens an existing news entry for editing.
     * 
     * @param id unique news identifier
     * @param redirectAttributes attributes used after redirect
     * @return redirect to the home page
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/editNews/{id}")
    public String editNews(@PathVariable Long id, RedirectAttributes redirectAttributes){
        News news = newsManagement.getNewsById(id);
        redirectAttributes.addFlashAttribute("newsToEdit", news);
        redirectAttributes.addFlashAttribute("edit", true);
        return "redirect:/";
    }

    /**
     * Updates an existing news entry.
     * 
     * @param id unique news identifier
     * @param content_edit updated news content
     * @param header_edit updated news title
     * @return redirect to the home page
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/news/{id}")
    public String editNewsEntry(@PathVariable Long id, @RequestParam String content_edit,
                                @RequestParam String header_edit){
        newsManagement.editNews(id, header_edit, content_edit);
        return "redirect:/";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/download_list")
    public ResponseEntity<ByteArrayResource> get_list_of_users(){
        try{
            byte[] data = attendeeManagement.create_excel_file(companionManagement.getAllCompanions());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Billing_informations.xlsx")
                    .body(new ByteArrayResource(data));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }


    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/download_hzdr_informations")
    public ResponseEntity<ByteArrayResource> get_hzdr_informations(){
        try{
            byte[] data = attendeeManagement.create_excursion_excel_file(companionManagement.getAllCompanions());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=HZDR_informations.xlsx")
                    .body(new ByteArrayResource(data));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/download_nametags_photopermission")
    public ResponseEntity<ByteArrayResource> get_nametags_photopermission(){
        try{
            byte[] data = attendeeManagement.create_nametag_excel_file();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Name-tags_photo-permission.xlsx")
                    .body(new ByteArrayResource(data));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }


    }

    @GetMapping("/timetable")
    public String getTimetableContent(Model model){
        if(!model.containsAttribute("edit")){
            model.addAttribute("edit", false);
        }
        //Sort attendeelist alphabetically
        List<Attendee> attendees = attendeeManagement.getAllUsers();
        Comparator<Attendee> c = (a1, a2) -> a1.getLastname().compareTo(a2.getLastname());
        attendees.sort(c);
        //Give attributes to Model
        model.addAttribute("attendees", attendees);
        model.addAttribute("docs", docManagement.getAllDocs());
        model.addAttribute("title", "Timetable");
        boolean schedule_uploaded = false;
        Doc schedule_doc = null;
        for(Doc doc : docManagement.getAllDocs()){
            if(doc.getAuthor()==null){
                schedule_uploaded = true;
                schedule_doc = doc;
                break;
            }
        }
        model.addAttribute("schedule_uploaded", schedule_uploaded);
        model.addAttribute("schedule_doc", schedule_doc);

        return "timetable";
    }

    @GetMapping("/travel_accommodation")
    public String getTravelpage(Model model){
        model.addAttribute("title", "Accomodation & Travel");
        model.addAttribute("allHotels", hotelManagement.getAllHotels());
        if (!model.containsAttribute("hotelId")){
            model.addAttribute("hotelId", null);
        }
        if (!model.containsAttribute("currentHotel")){
            model.addAttribute("currentHotel", hotelManagement.getAllHotels().get(0));
        }

        return "travel";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/travel/edit/{id}")
    public String editTravelpage(@PathVariable long id,  RedirectAttributes redirectAttributes, Model model){
        redirectAttributes.addFlashAttribute("hotelId", id);
        redirectAttributes.addFlashAttribute("currentHotel", hotelManagement.getHotelById(id));
        return "redirect:/travel_accommodation";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/travel/save/{id}")
    public String saveTravelpage(@PathVariable long id, @RequestParam String text,
                                 @RequestParam String name, Model model){
        hotelManagement.editHotel(id, name, text);

        return "redirect:/travel_accommodation";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/travel/delete/{id}")
    public String deleteTravelpage(@PathVariable long id, Model model){
        hotelManagement.deleteHotel(hotelManagement.getHotelById(id));
        return "redirect:/travel_accommodation";
    }


    public static String getDomain(String url){
        int position = 0;
        int counter = 0;
        for(int i = 0; i < url.length(); i++){
            if(counter == 3){
                position = i;
                break;
            }
            if(url.charAt(i) == '/' ){
                counter++;
            }
        }

        return url.substring(0, position);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/travel/edit/create_hotel")
    public String createHotelEntry(Model model){
        Hotel hotel = new Hotel("-", "-", "");
        hotelManagement.addHotel(hotel);
        return "redirect:/travel_accommodation";
    }
}
