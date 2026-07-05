package DyProSo2023.companion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanionRepository extends JpaRepository<Companion, Integer> {

    @Override
    List<Companion> findAll();
}
