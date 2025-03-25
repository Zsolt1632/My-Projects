package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.HikerRepository;
import edu.bbte.idde.mzim2273.data.model.Hiker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("jpa")
public class JpaHikerService implements HikerService {
    private static final Logger logger = LoggerFactory.getLogger(JpaHikerService.class);

    private final HikerRepository hikerRepository;

    @Autowired
    public JpaHikerService(HikerRepository hikerRepository) {
        this.hikerRepository = hikerRepository;
    }

    @Override
    public Hiker getHiker(Long id) throws EntityNotFoundException {
        logger.info("Fetching hiker with ID: {}", id);
        return hikerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Hiker not found with ID: {}", id);
                    return new EntityNotFoundException("Hiker not found with ID: " + id);
                });
    }

    @Override
    public Hiker saveHiker(Hiker hiker) {
        logger.info("Saving hiker: {}", hiker);
        Hiker savedHiker = hikerRepository.save(hiker);
        logger.info("Hiker successfully saved with ID: {}", savedHiker.getId());
        return savedHiker;
    }

    @Override
    public List<Hiker> getAllHikers() {
        logger.info("Fetching all hikers...");
        List<Hiker> hikers = hikerRepository.findAll();
        logger.info("Found {} hikers.", hikers.size());
        return hikers;
    }

    @Override
    public void deleteHiker(Long id) throws EntityNotFoundException {
        logger.info("Deleting hiker with ID: {}", id);
        if (!hikerRepository.existsById(id)) {
            logger.error("Hiker not found with ID: {}", id);
            throw new EntityNotFoundException("Hiker not found with ID: " + id);
        }
        hikerRepository.deleteById(id);
        logger.info("Hiker with ID: {} successfully deleted.", id);
    }

    @Override
    public Hiker updateHiker(Long id, Hiker updatedHiker) throws EntityNotFoundException {
        logger.info("Updating hiker with ID: {}", id);
        Hiker existingHiker = hikerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Hiker not found with ID: {}", id);
                    return new EntityNotFoundException("Hiker not found with ID: " + id);
                });

        existingHiker.setName(updatedHiker.getName());
        existingHiker.setAge(updatedHiker.getAge());
        existingHiker.setEmail(updatedHiker.getEmail());

        Hiker savedHiker = hikerRepository.save(existingHiker);
        logger.info("Hiker successfully updated with ID: {}", savedHiker.getId());
        return savedHiker;
    }
}
