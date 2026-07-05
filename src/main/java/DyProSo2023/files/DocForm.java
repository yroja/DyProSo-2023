package DyProSo2023.files;

import org.springframework.web.multipart.MultipartFile;


/**
 * Represents a form used for creating or updating a document.
 * 
 * Used as a data transfer object between frontend and backend.
 */
public class DocForm {

    private String[] categories;
    private Boolean poster;
    private Boolean talk;

    public DocForm(String[] categories, String poster){

        if(categories != null && categories[0].length() != 0){
            String[] splitted_cat = categories[0].split(";,");

            this.categories = categories[0].split(";,");
            if(this.categories.length > 1){
                this.categories[this.categories.length-1] = this.categories[this.categories.length-1].substring(0, this.categories[this.categories.length-1].length()-1);
            }else if(this.categories.length == 1){
                this.categories[0] = this.categories[0].substring(0, this.categories[0].length()-1);
            }

        }else{
            this.categories = null;
        }
        if(poster == null){
            this.poster = null;
        }else if(poster.equals("TRUE")){
            this.poster = true;
        }else{
            this.poster = false;
        }


    }

    public String[] getCategories(){
        return categories;
    }

    public Boolean getPoster(){
        return poster;
    }


}
