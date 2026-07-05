package DyProSo2023.admin.news;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a news article that is displayed within the application.
 * 
 * Each news entry consists of a headline, the article content and the publication date.
 */

@Entity
public class News {

    @Id
    @GeneratedValue
    private Long id;
    @Lob
    @Column(length = 1000)
    private String content;

    private String header;

    private String date;

    public News(){}

    /**
     * Initializes a new news entry.
     * 
     * The publication date is automatically initialized with the current system date.
     * @param header the news headline
     * @param content the news content
     */
    public News(String header, String content){

        this.content = content;
        this.header = header;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH);
        String formattedTime = LocalDateTime.now().format(formatter);
        formattedTime = formattedTime.replace("-", " ");
        date = formattedTime;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getHeader() {
        return header;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDate(String date){
        this.date = date;
    }

}
