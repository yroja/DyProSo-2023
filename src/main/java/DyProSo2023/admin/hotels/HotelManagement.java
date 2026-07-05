package DyProSo2023.admin.hotels;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing hotel entities.
 * 
 * This class encapsulates the logic related to hotels and acts as an intermediary
 * between controllers and the repository layer.
 */

@Service
@Transactional
public class HotelManagement {
    private final HotelRepository hotelRepository;


    /**
     * Initializes a new HotelManagement service with the required repository dependency.
     * 
     * @param hotelRepository repository used for database access
     */
    public HotelManagement(HotelRepository hotelRepository){
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getAllHotels(){return hotelRepository.findAll();}

    public void addHotel(Hotel hotel){
        hotelRepository.saveAndFlush(hotel);
    }

    public Hotel getHotelById(Long id){
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.orElse(null);
    }

    /**
     * Updates an existing hotel's name and description.
     * 
     * @param id the ID of the hotel to update
     * @param name new hotel name
     * @param text new description text
     */
    public void editHotel(Long id, String name, String text){
        Hotel hotel = getHotelById(id);
        hotel.setName(name);
        hotel.setText(text);
        hotelRepository.saveAndFlush(hotel);
    }

    
    public void deleteHotel(Hotel hotel){
        hotelRepository.delete(hotel);
    }

}
