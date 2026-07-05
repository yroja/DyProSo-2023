package DyProSo2023.admin.news;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Override
    List<News> findAll();
}
