package DyProSo2023.files;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocRepository extends JpaRepository<Doc, Integer> {

    @Override
    List<Doc> findAll();
}
