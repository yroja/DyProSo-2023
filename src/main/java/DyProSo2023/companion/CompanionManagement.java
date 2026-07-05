package DyProSo2023.companion;

import DyProSo2023.User.Attendee;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer responsible for managing Companion entities.
 * 
 * Handles initialization, retrieval and deletion of companions and maps data from CompanionForm objects to persistent entities.
 */

@Service
@Transactional
public class CompanionManagement {

    private final CompanionRepository companionRepository;

    public CompanionManagement(CompanionRepository companionRepository){
        this.companionRepository = companionRepository;
    }

    /**
     * Creates and persists a new Companion entity based on provided form data.
     * 
     * The method maps data from CompanionForm to a Companion entity and applies logics such as:
     * Inheriting billing address from the attendee if selected.
     * Handling EU vs non-EU billing type and VAT number rules.
     * Converting string-based form values into typed fields.
     * Handling optional HZDR participation data.
     * 
     * @param attendee the attendee to which the companion belongs
     * @param companionForm form data submitted by the user 
     */
    public void createCompanion(Attendee attendee, CompanionForm companionForm){
        Companion companion = new Companion();
        companion.setCompanionOf(attendee);
        if(companionForm.getSame_billing_address()){
            companion.setBilling_address_lastname(attendee.getBilling_address_lastname());
            companion.setBilling_address_firstname(attendee.getBilling_address_firstname());
            companion.setBilling_address_poBox(attendee.getBilling_address_poBox());
            companion.setBilling_address_street(attendee.getBilling_address_street());
            companion.setBilling_address_postalCode(attendee.getBilling_address_postalCode());
            companion.setBilling_address_city(attendee.getBilling_address_city());
            companion.setBilling_address_country(attendee.getBilling_address_country());
            companion.setType_of_billing_address(attendee.getType_of_billing_address());
            companion.setVat_number(attendee.getVat_number());
        }else{
            companion.setBilling_address_lastname(companionForm.getBilling_address_lastname());
            companion.setBilling_address_firstname(companionForm.getBilling_address_firstname());
            companion.setBilling_address_poBox(companionForm.getBilling_address_poBox());
            companion.setBilling_address_street(companionForm.getBilling_address_street());
            companion.setBilling_address_postalCode(companionForm.getBilling_address_postalCode());
            companion.setBilling_address_city(companionForm.getBilling_address_city());
            companion.setBilling_address_country(companionForm.getBilling_address_country());
            boolean type_of_billing_address = false;
            if(companionForm.getType_of_billing_address().equals("EU")){
                type_of_billing_address = true;
            }
            companion.setType_of_billing_address(type_of_billing_address);
            if(type_of_billing_address){
                companion.setVat_number(companionForm.getVat_number());
            }else{
                companion.setVat_number(null);
            }
        }
        if(companionForm.getSocial_programm().equals("TRUE")){
            companion.setSocial_programm(true);
        }else{
            companion.setSocial_programm(false);
        }
        if(companionForm.getHzdr().equals("TRUE")){
            companion.setHzdr(true);
            companion.setHzdr_lastname(companionForm.getHzdr_lastname());
            companion.setHzdr_firstname(companionForm.getHzdr_firstname());
            companion.setHzdr_cardnumber(companionForm.getHzdr_cardnumber());
            companion.setHzdr_birthdate(LocalDate.parse(companionForm.getHzdr_birthdate()));
        }else{
            companion.setHzdr(false);
            companion.setHzdr_lastname(null);
            companion.setHzdr_firstname(null);
            companion.setHzdr_cardnumber(null);
            companion.setHzdr_birthdate(null);
        }
        companionRepository.saveAndFlush(companion);
    }

    /**
     * Returns all companions associated with a specific attendee.
     * 
     * @param attendee the attendee whose companions are requested
     * @return list of companions linked to the attendee
     */
    public List<Companion> getListByAttendee(Attendee attendee){
        List<Companion> result = new ArrayList<>();
        for(Companion companion : companionRepository.findAll()){
            if(attendee.equals(companion.getCompanionOf())){
                result.add(companion);
            }
        }
        return result;
    }

    /**
     * Deletes all companions belonging to the given attendee.
     * 
     * @param attendee the attendee whose companions should be removed
     */
    public void deleteAllCompanionsOfAttendee(Attendee attendee){
        for(Companion companion: companionRepository.findAll()){
            if(attendee.equals(companion.getCompanionOf())){
                companionRepository.delete(companion);
            }
        }
    }

    public List<Companion> getAllCompanions(){
        return companionRepository.findAll();
    }
}
