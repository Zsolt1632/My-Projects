package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.factory.DaoFactory;
import edu.bbte.idde.mzim2273.data.model.Hike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public final class HikeServiceImpl implements HikeService {
    private static final Logger logger = LoggerFactory.getLogger(HikeServiceImpl.class);
    private final HikeDAO hikeDAO;

    private HikeServiceImpl(HikeDAO hikeDAO) {
        this.hikeDAO = hikeDAO;
        logger.info("HikeServiceImpl initialized with HikeDAO");
    }

    @Override
    public HikeDAO getHikeDAO() {
        return hikeDAO;
    }

    // Bill Pugh Singleton Design
    private static final class SingletonHelper {
        // The static inner class is not loaded until the getInstance() method is called, ensuring lazy initialization
        private static final HikeServiceImpl INSTANCE = createInstance();

        private static HikeServiceImpl createInstance() {
            try {
                DaoFactory daoFactory = DaoFactory.getFactory();
                HikeDAO hikeDAO = daoFactory.getHikeDAO();
                return new HikeServiceImpl(hikeDAO);
            } catch (RepositoryException e) {
                logger.error("Failed to initialize HikeServiceImpl", e);
                throw new IllegalStateException("Can't initialize HikeService.", e);
            }
        }
    }

    public static HikeServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public void addHike(Hike hike) throws MissingArgumentException, RepositoryException {
        logger.debug("Attempting to add hike: {}", hike);

        validateHikeArguments(hike);
        hikeDAO.create(hike);
        logger.info("Hike added successfully: {}", hike);
    }

    @Override
    public Hike getHike(Long id, Boolean isArchived) throws EntityNotFoundException, RepositoryException {
        logger.debug("Fetching hike with ID: {}", id);
        Hike hike = hikeDAO.read(id, isArchived);
        if (hike == null) {
            logger.warn("Hike with ID {} not found", id);
            throw new EntityNotFoundException("Hike not found for ID: " + id);
        }
        logger.info("Fetched hike: {}", hike);
        return hike;
    }

    @Override
    public void updateHike(Hike hike, Boolean isArchived) throws EntityNotFoundException, RepositoryException {
        logger.debug("Updating hike: {}", hike);
        Hike existingHike = hikeDAO.read(hike.getId(), isArchived);
        if (existingHike == null) {
            logger.warn("Hike with ID {} not found", hike.getId());
            throw new EntityNotFoundException("Hike not found for ID: " + hike.getId());
        }
        hikeDAO.update(hike, isArchived);
        logger.info("Hike updated successfully: {}", hike);
    }

    @Override
    public void deleteHike(Long id, Boolean isArchived) throws EntityNotFoundException, RepositoryException {
        logger.debug("Attempting to delete hike with ID: {}", id);
        Hike existingHike = hikeDAO.read(id, isArchived);
        if (existingHike == null) {
            logger.warn("Hike with ID {} not found", id);
            throw new EntityNotFoundException("Hike not found for ID: " + id);
        }
        hikeDAO.delete(id, isArchived);
        logger.info("Hike deleted successfully with ID: {}", id);
    }

    @Override
    public Collection<Hike> getAllHikes(Boolean isArchived) throws RepositoryException {
        logger.debug("Fetching all hikes");
        Collection<Hike> hikes = hikeDAO.getAllHikes(isArchived);
        logger.info("Fetched {} hikes", hikes.size());
        return hikes;
    }

    @Override
    public Hike getHikeById(Long id, Boolean isArchived) throws EntityNotFoundException, RepositoryException {
        return getHike(id, isArchived);  // Reusing getHike for logic consolidation
    }

    private void validateHikeArguments(Hike hike) throws MissingArgumentException {
        if (hike == null) {
            logger.error("Hike object is null");
            throw new MissingArgumentException("Hike object cannot be null.");
        }

        validatePrice(hike.getPrice());
        validateNonEmptyField("Hike name", hike.getName());
        validateNonEmptyField("Hike location", hike.getStartLocation());
        validateNonNullField("Start date", hike.getStartDate());
        validateNonNullField("Start time", hike.getStartTime());
    }

    private void validatePrice(double price) throws MissingArgumentException {
        if (price < 0) {
            logger.error("Price cannot be negative: {}", price);
            throw new MissingArgumentException("Price cannot be negative.");
        }
    }

    private void validateNonEmptyField(String fieldName, String value) throws MissingArgumentException {
        if (isEmpty(value)) {
            logger.warn("{} is null or empty", fieldName);
            throw new MissingArgumentException(fieldName + " cannot be null or empty.");
        }
    }

    private void validateNonNullField(String fieldName, Object value) throws MissingArgumentException {
        if (value == null) {
            logger.warn("{} is null", fieldName);
            throw new MissingArgumentException(fieldName + " cannot be null.");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }
}
