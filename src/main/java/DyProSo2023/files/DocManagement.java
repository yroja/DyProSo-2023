package DyProSo2023.files;

import DyProSo2023.User.Attendee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service layer responsible for managing Doc entities.
 * 
 * Handles uploading, retrieval, filtering and deletion of documents and maps data from MultipartFile 
 * inputs to persistent Doc entities.
 * Also manages submission state for uploads.
 */
@Service
@Transactional
public class DocManagement {

    private final DocRepository docRepository;

    private boolean abstractSubmissionIsActive;

    public DocManagement(DocRepository docRepository){
        this.docRepository = docRepository;
        this.abstractSubmissionIsActive = true;
    }

    /**
     * Saves an uploaded file as a Doc entity.
     * 
     * The method extracts metadata from the uploaded file and stores both file content and metadata in the database.
     * 
     * @param file uploaded file
     * @param author uploader of the file
     * @param categories assigned categories
     * @param poster indicates whether the file is a poster or abstract submission
     * @return saved Doc entity or null if an error occurs
     */
    public Doc saveFile(MultipartFile file, Attendee author, String[] categories, boolean poster){
        try {
            Doc doc = new Doc(file.getOriginalFilename(), file.getContentType(), author, categories, poster, file.getBytes());
            return docRepository.saveAndFlush(doc);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Doc getFileById(Integer id){
        Optional<Doc> doc = docRepository.findById(id);
        return doc.orElse(null);
    }

    /**
     * Retrieves all documents uploaded by a specific author identified by email.
     * 
     * @param mail email of the author
     * @return list of matching documents
     */
    public List <Doc> getFilesByAuthorMail(String mail){
        List<Doc> result = new ArrayList<>();
        List <Doc> allDocs = docRepository.findAll();
        for(Doc temp: allDocs){
            if(temp.getAuthor() == null){
                continue;
            }
            if (temp.getAuthor().getMail().equals(mail)){
                result.add(temp);
            }
        }
        return result;
    }

    /**
     * Filters documents based on category filters and document type.
     * 
     * Type can be:
     *  - ALL: returns all matching documents
     *  - POSTER: only poster submissions
     *  - ABSTRACT: only abstract submissions
     * 
     * @param filters required categories
     * @param type filter type (ALL, POSTER, ABSTRACT)
     * @return filtered list of documents
     */
    public List<Doc> getDocsByFilter(String[] filters, String type){
        List<Doc> docs = new ArrayList<>();

        if(type.equals("ALL")) {
            for (Doc doc : docRepository.findAll()) {
                if(doc.getAuthor() == null){
                    continue;
                }
                if (Arrays.asList(doc.getCategories()).containsAll(Arrays.asList(filters))) {
                    docs.add(doc);
                }
            }
        }else if(type.equals("POSTER")){
            for(Doc doc : docRepository.findAll()){
                if(doc.getAuthor() == null){
                    continue;
                }
                if(Arrays.asList(doc.getCategories()).containsAll(Arrays.asList(filters)) && doc.getPoster()){
                    docs.add(doc);
                }
            }
        }else{
            for(Doc doc : docRepository.findAll()){
                if(doc.getAuthor() == null){
                    continue;
                }
                if(Arrays.asList(doc.getCategories()).containsAll(Arrays.asList(filters)) && !doc.getPoster()){
                    docs.add(doc);
                }
            }
        }
        return docs;
    }

    public List<Doc> getAllDocs(){
        return docRepository.findAll();
    }

    public void deleteDoc(Doc doc){
        docRepository.delete(doc);
    }

    /**
     * Deletes all documents belonging to a specific author.
     * 
     * @param author the author whose documents should be removed
     */
    public void deleteAllDocsOfAnAuthor(Attendee author){
        for(Doc doc : docRepository.findAll()){
            if(doc.getAuthor() == null){
                continue;
            }
            if(doc.getAuthor().getId() == author.getId()){
                docRepository.delete(doc);
            }
        }
    }

    public boolean getAbstractSubmissionIsActive() {
        return abstractSubmissionIsActive;
    }

    public void setAbstractSubmissionIsActive(boolean value){
        this.abstractSubmissionIsActive = value;
    }
}
