package DyProSo2023.files;

import DyProSo2023.User.Attendee;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an uploaded document in the system.
 * 
 * A document contains metadata such as file name, type, author, upload date, categories, and the binary file content.
 * It can represent different types of submissions such as posters or abstracts.
 */
@Entity
public class Doc {

    @Id
    @GeneratedValue
    private Integer id;

    private String fileName;
    private String fileType;

    private String date;
    private boolean poster;

    @ManyToOne
    private Attendee author;

    private String[] categories;

    @Lob
    private byte[] data;                // @Lob - Large object notation

    public Doc(){}

    /**
     * Initializes a new document and its metadata.
     * 
     * The upload date is automatically set to the current system date.
     * 
     * @param fileName name of the file
     * @param fileType type of the file
     * @param author author of the document
     * @param categories assigned categories
     * @param poster indicates whether the document is a poster or abstract
     * @param data binary content of the file
     */
    public Doc(String fileName, String fileType, Attendee author, String[] categories, boolean poster, byte[] data){
        this.fileName = fileName;
        this.fileType = fileType;
        this.author = author;
        this.categories = categories;
        this.data = data;
        this.poster = poster;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");
        String formattedTime = LocalDateTime.now().format(formatter);
        formattedTime = formattedTime.replace("-", ".");
        this.date = formattedTime;
    }

    public Integer getId(){
        return id;
    }

    public String getFileName(){
        return fileName;
    }

    public String getFileType(){
        return fileType;
    }

    public byte[] getData(){
        return data;
    }

    public Attendee getAuthor(){
        return author;
    }

    public String[] getCategories(){ return categories; }

    /**
     * Converts the categories array into a single readable string.
     * 
     * @return formatted categories string
     */
    public String categoriesToString(){
        String categoriesString = "";
        for(String categorie : categories){
            if(categoriesString.length() == 0){
                categoriesString = categorie;
            }else{
                categoriesString = categoriesString + "; " + categorie;
            }
        }

        return categoriesString;
    }

    public String getDate() {
        return date;
    }

    public boolean getPoster(){
        return poster;
    }

    public String getType(){
        if(poster){
            return "Poster";
        }else{
            return "Talk";
        }
    }

    public String toString(){
        return author.toString() + "_" + getDate() + "_" + getFileName();
    }
}
