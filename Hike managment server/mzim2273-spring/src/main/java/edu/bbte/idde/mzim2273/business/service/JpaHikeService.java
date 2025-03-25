package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.HikeRepository;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dto.InPartialHikeDto;
import edu.bbte.idde.mzim2273.data.model.Hike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Profile("jpa")
public class JpaHikeService implements HikeService {
    private final HikeRepository hikeRepository;
    private static final Logger logger = LoggerFactory.getLogger(JpaHikeService.class);

    @Autowired
    public JpaHikeService(HikeRepository hikeRepository) {
        this.hikeRepository = hikeRepository;
    }

    @Override
    public Hike addHike(Hike hike) {
        logger.info("Attempting to add hike: {}", hike);
        Hike savedHike = hikeRepository.save(hike);
        logger.info("Hike added successfully: {}", savedHike);
        return savedHike;
    }

    @Override
    public Hike getHike(Long id) throws EntityNotFoundException {
        logger.info("Fetching hike with ID: {}", id);
        return hikeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Hike with ID {} not found.", id);
                    return new EntityNotFoundException("Hike not found");
                });
    }

    @Override
    public void updateHike(Hike hike) {
        logger.info("Attempting to update hike: {}", hike);
        Hike updatedHike = hikeRepository.save(hike);
        logger.info("Hike updated successfully: {}", updatedHike);
    }

    @Override
    public void deleteHike(Long id) throws EntityNotFoundException {
        logger.info("Attempting to delete hike with ID: {}", id);
        if (!hikeRepository.existsById(id)) {
            logger.error("Hike with ID {} not found for deletion.", id);
            throw new EntityNotFoundException("Hike not found");
        }
        hikeRepository.deleteById(id);
        logger.info("Hike with ID {} deleted successfully.", id);
    }

    @Override
    public Collection<Hike> getAllHikes() {
        logger.info("Fetching all hikes.");
        List<Hike> hikeList = hikeRepository.findAll();
        logger.info("Fetched {} hikes.", hikeList.size());
        return hikeList;
    }

    @Override
    public List<Hike> getByName(String name) throws RepositoryException {
        return hikeRepository.findByNameContaining(name).stream().toList();
    }

    @Override
    public List<Hike> filterByPrice(double price) throws RepositoryException {
        return hikeRepository.findByPriceLessThan(price).stream().toList();
    }

    @Override
    public Integer updateHikes(int difficulty, InPartialHikeDto inPartialHikeDto) throws EntityNotFoundException {
        logger.info("Attempting to update hikes with Diff LVL: {}", difficulty);
        Collection<Hike> hikes = hikeRepository.findByDifficultyEquals(difficulty);
        if (hikes.isEmpty()) {
            logger.error("Hikes with Diff LVL {} not found.", difficulty);
            throw new EntityNotFoundException("Hike not found");
        }
        Collection<Hike> updatedHikes = hikes.stream().peek(hike ->
        {
            hike.setStartTime(inPartialHikeDto.getStartTime());
            hike.setStartLocation(inPartialHikeDto.getStartLocation());
            hike.setStartDate(inPartialHikeDto.getStartDate());
        }).toList();

        hikeRepository.saveAll(hikes);
        
        logger.info("Hikes with Diff LVL {} updated successfully.", difficulty);
        logger.info("Updated hikes: {}", updatedHikes);
        return updatedHikes.size();
    }
}
