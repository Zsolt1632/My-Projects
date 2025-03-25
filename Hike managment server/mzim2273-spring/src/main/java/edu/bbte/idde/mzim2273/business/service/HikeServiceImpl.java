package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.dto.InPartialHikeDto;
import edu.bbte.idde.mzim2273.data.model.Hike;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Profile({"in-memory","jdbc"})
public class HikeServiceImpl implements HikeService {

    private static final Logger logger = LoggerFactory.getLogger(HikeServiceImpl.class);
    private final HikeDAO hikeDAO;

    @Autowired
    public HikeServiceImpl(HikeDAO hikeDAO) {
        this.hikeDAO = hikeDAO;
    }

    @Override
    public Hike addHike(@Valid Hike hike) throws MissingArgumentException {
        try{
            logger.debug("Attempting to add hike: {}", hike);

            synchronized (this) { // Ensures thread-safety when creating a new hike
                hikeDAO.create(hike);
            }

            logger.info("Hike added successfully: {}", hike);
            return hike;
        } catch (RepositoryException e) {
            throw new MissingArgumentException(e.getMessage());
        }
    }

    @Override
    public Hike getHike(Long id) throws EntityNotFoundException {
        logger.debug("Fetching hike with ID: {}", id);

        Hike hike;
        try {
            synchronized (this) { // Ensures thread-safety when reading a hike
                hike = hikeDAO.read(id);
            }

            if (hike == null) {
                logger.warn("Hike with ID {} not found", id);
                throw new EntityNotFoundException("Hike not found for ID: " + id);
            }
        } catch (RepositoryException e) {
            logger.error("Error accessing the repository while fetching hike with ID: {}", id, e);
            throw new EntityNotFoundException("Hike not found for ID: " + id); // Re-throwing a specific exception
        }

        logger.info("Fetched hike: {}", hike);
        return hike;
    }

    @Override
    public void updateHike(Hike hike) throws EntityNotFoundException {
        logger.debug("Updating hike: {}", hike);

        Hike existingHike;
        try {
            synchronized (this) { // Ensures thread-safety when updating a hike
                existingHike = hikeDAO.read(hike.getId());
            }

            if (existingHike == null) {
                logger.warn("Hike with ID {} not found", hike.getId());
                throw new EntityNotFoundException("Hike not found for ID: " + hike.getId());
            }

            synchronized (this) {
                hikeDAO.update(hike);
            }
        } catch (RepositoryException e) {
            logger.error("Error accessing the repository while updating hike with ID: {}", hike.getId(), e);
            throw new EntityNotFoundException("Hike not found for ID: " + hike.getId()); // Re-throwing a specific exception
        }

        logger.info("Hike updated successfully: {}", hike);
    }

    @Override
    public void deleteHike(Long id) throws EntityNotFoundException{
        logger.debug("Attempting to delete hike with ID: {}", id);

        Hike existingHike;
        try {
            synchronized (this) { // Ensures thread-safety when deleting a hike
                existingHike = hikeDAO.read(id);
            }

            if (existingHike == null) {
                logger.warn("Hike with ID {} not found", id);
                throw new EntityNotFoundException("Hike not found for ID: " + id);
            }

            synchronized (this) {
                hikeDAO.delete(id);
            }
        } catch (RepositoryException e) {
            logger.error("Error accessing the repository while deleting hike with ID: {}", id, e);
            throw new EntityNotFoundException("Hike not found for ID: " + id); // Re-throwing a specific exception
        }

        logger.info("Hike deleted successfully with ID: {}", id);
    }

    @Override
    public Collection<Hike> getAllHikes() throws RepositoryException {
        logger.debug("Fetching all hikes");

        Collection<Hike> hikes;
        synchronized (this) { // Ensures thread-safety when reading all hikes
            hikes = hikeDAO.findAll();
        }

        logger.info("Fetched {} hikes", hikes);
        return hikes;
    }

    @Override
    public List<Hike> getByName(String name) throws RepositoryException {
        Collection<Hike> hikes;
        synchronized (this) { // Ensures thread-safety when reading all hikes
            hikes = hikeDAO.getByName(name);
        }
        return (List<Hike>) hikes;
    }

    @Override
    public List<Hike> filterByPrice(double price) throws RepositoryException {
        Collection<Hike> hikes;
        synchronized (this) { // Ensures thread-safety when reading all hikes
            hikes = hikeDAO.filterByPrice(price);
        }
        return hikes.stream().toList();
    }

    @Override
    public Integer updateHikes(int difficulty, InPartialHikeDto inPartialHikeDto) throws EntityNotFoundException {
        //throw new RuntimeException("Not implemented");
        return -3;
    }
}
