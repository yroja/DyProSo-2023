package DyProSo2023.admin.hotels;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a hotel entity that is persisted in the database.
 * The entity stores basic information required to display a hotel within the application,
 * including its name, a textual description and a link to additional information. 
 */

@Entity
public class Hotel {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(length = 300)
    private String text;

    private String link;

    public Hotel() {
    }

    public Hotel(String name, String text, String link) {
        this.name = name;
        this.text = text;
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}