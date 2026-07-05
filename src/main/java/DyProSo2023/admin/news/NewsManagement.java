package DyProSo2023.admin.news;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer responsible for managing news entries.
 * 
 * This service encapsulates the logic for initializing, retrieving, updating and the deleting news articles.
 */

@Service
@Transactional
public class NewsManagement {

    private final NewsRepository newsRepository;

    /**
     * Initializes a new service instance.
     * 
     * @param newsRepository repository used to access news entities.
     */
    public NewsManagement(NewsRepository newsRepository){
        this.newsRepository = newsRepository;
    }

    public void addNews(News text){
        newsRepository.saveAndFlush(text);
    }

    public List<News> getAllNews() {return newsRepository.findAll();}

    public News getNewsById(Long id){
        Optional<News> news = newsRepository.findById(id);
        return news.orElse(null);
    }

    public void deleteNews(News news){newsRepository.delete(news);}

    /**
     * Updates the headline and content of an existing news entry.
     * 
     * @param id unique identifier of the news entry
     * @param newHeader updated headline
     * @param newContent updated article content
     */
    public void editNews(Long id, String newHeader, String newContent){
        News content = getNewsById(id);
            content.setHeader(newHeader);
            content.setContent(newContent);
            newsRepository.saveAndFlush(content);
        }
    }

