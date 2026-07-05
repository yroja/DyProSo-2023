package DyProSo2023.admin.hotels;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Override
    List<Hotel> findAll();
}
