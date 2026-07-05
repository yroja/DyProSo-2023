package DyProSo2023.homepage;

import DyProSo2023.User.Attendee;
import DyProSo2023.User.AttendeeManagement;
import DyProSo2023.admin.news.News;
import DyProSo2023.admin.news.NewsManagement;
import DyProSo2023.files.DocManagement;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller responsible for serving the application's homepage and public information pages. 
 */
@Controller
public class HomepageController {
    private final AttendeeManagement attendeeManagement;

    private final DocManagement docManagement;

    private final NewsManagement newsManagement;

    public HomepageController(AttendeeManagement attendeeManagement, DocManagement docManagement, NewsManagement newsManagement){
        this.attendeeManagement = attendeeManagement;
        this.docManagement = docManagement;
        this.newsManagement = newsManagement;
    }


    @RequestMapping("/")
    public String getHomepage(Model model){
        if(!model.containsAttribute("newsToEdit")){
            model.addAttribute("newsToEdit", new News("", ""));
        }
        if(!model.containsAttribute("edit")){
            model.addAttribute("edit", false);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Attendee user = this.attendeeManagement.getAttendeeByName(auth.getName());
        if (user == null){
            model.addAttribute("name", "");
        }
        else {
            model.addAttribute("name", user.getName());
        }

        model.addAttribute("title", "DyProSo-2023");
        List<News> news = newsManagement.getAllNews();
        Collections.reverse(news);

        model.addAttribute("allnews", news);
        return "index";
    }

    @PostMapping("/news")
    public String createNews(@RequestParam String content, @RequestParam String header, Model model){
        News news = new News(header, content);
        newsManagement.addNews(news);
        return "redirect:/";
    }

    @GetMapping("/venue")
    public String getVenuepage(Model model){
        model.addAttribute("title", "Venue");
        return "venue";
    }

    @GetMapping("/terms")
    public String getTerms(Model model){
        model.addAttribute("title", "Terms & Conditions");
        return "terms";
    }

    @GetMapping("/invited_speakers")
    public String getSpeakerspage(Model model){
        model.addAttribute("title", "Speakers");
        return "speakers";
    }

    @GetMapping("/committee")
    public String getCommitteepage(Model model){
        model.addAttribute("title", "Committee");
        return "committee";
    }

    @GetMapping("/prev")
    public String getPrevpage(Model model){
        model.addAttribute("title", "Previous DyProSo-Editions");
        return "prev";
    }

    @GetMapping("/sponsors")
    public String getSponsors(Model model){
        model.addAttribute("title", "Sponsors");
        return "sponsor";
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/abstract")
    public String getAbstract(Model model){
        model.addAttribute("active", docManagement.getAbstractSubmissionIsActive());
        model.addAttribute("title", "Abstract");
        return "abstract";
    }
    @GetMapping("/tourism")
    public String getTourism(Model model){
        model.addAttribute("title", "Tourism");
        return "tourism";
    }

    @GetMapping("/success/{message}/{link}")
    public String getSuccessPage(@PathVariable String message, @PathVariable String link, Model model){
        model.addAttribute("message", message);
        model.addAttribute("link", "/"+link);
        model.addAttribute("title", "Success");
        return "success";
    }

    @GetMapping("/important_dates")
    public String getImportDatesPage(Model model){
        model.addAttribute("title", "Important Dates");
        return "important_dates";
    }

    @GetMapping("/travel/train")
    public String getTrainPage(Model model){
        model.addAttribute("title", "Train Arrival");
        return "train";
    }

    @GetMapping("/travel/plane")
    public String getPlanePage(Model model){
        model.addAttribute("title", "Airplane Arrival");
        return "plane";
    }


    @GetMapping("/topics")
    public String getTopics(Model model){
        model.addAttribute("title", "Topics");
        return "topics";
    }

    @GetMapping("/contact")
    public String getContact(Model model) {
        model.addAttribute("title", "Contact");
        return "contact";
    }

    @GetMapping("/privacy")
    public String getPrivacy(Model model){
        model.addAttribute("title", "Privacy");
        return "privacypolicy";

    }

    @GetMapping("/excursion")
    public String getExcursion(Model model){
        model.addAttribute("title", "Excursion");
        return "excursion";
    }
}
