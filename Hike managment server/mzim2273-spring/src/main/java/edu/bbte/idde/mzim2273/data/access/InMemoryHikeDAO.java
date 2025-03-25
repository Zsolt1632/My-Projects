package edu.bbte.idde.mzim2273.data.access;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.model.Hike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Profile("in-memory")
@Repository
public class InMemoryHikeDAO implements HikeDAO {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryHikeDAO.class);
    private final Map<Long, Hike> hikes = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    public InMemoryHikeDAO() {
        initializeSampleData();
    }

    @Override
    public void create(Hike hike) {
        hike.setId(currentId.getAndIncrement());
        hikes.put(hike.getId(), hike);
        logger.info("Hike created: {}", hike);
    }

    @Override
    public Hike read(Long id) throws EntityNotFoundException {
        if (!hikes.containsKey(id)) {
            logger.warn("Hike with ID {} not found for read operation.", id);
            throw new EntityNotFoundException("Hike with ID " + id + " not found.");
        }
        logger.info("Hike read: {}", hikes.get(id));
        return hikes.get(id);
    }

    @Override
    public void update(Hike hike) throws EntityNotFoundException {
        if (!hikes.containsKey(hike.getId())) {
            logger.warn("Hike with ID {} not found for update operation.", hike.getId());
            throw new EntityNotFoundException("Hike with ID " + hike.getId() + " not found.");
        }
        hikes.put(hike.getId(), hike);
        logger.info("Hike updated: {}", hike);
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        if (!hikes.containsKey(id)) {
            logger.warn("Hike with ID {} not found for delete operation.", id);
            throw new EntityNotFoundException("Hike with ID " + id + " not found.");
        }
        hikes.remove(id);
        logger.info("Hike deleted with ID: {}", id);
    }

    @Override
    public Collection<Hike> findAll() {
        logger.info("Fetching all hikes. Total count: {}", hikes.size());
        return hikes.values();
    }

    @Override
    public Collection<Hike> getByName(String name) throws RepositoryException {
        return hikes.values().stream().filter(hike -> hike.getName().contains(name)).toList();
    }

    @Override
    public Collection<Hike> filterByPrice(double price) throws RepositoryException {
        return hikes.values().stream().filter(hike -> hike.getPrice() <= price).toList();
    }

    private void initializeSampleData() {
        logger.info("Initializing sample data for hikes.");
        create(new Hike("Mountain Adventure", "Trailhead A", LocalDate.of(2024, 6, 15), LocalTime.of(10, 0), 25.0));
        create(new Hike("Forest Trek", "Trailhead B", LocalDate.of(2024, 7, 20), LocalTime.of(8, 0), 15.0));
        create(new Hike("River Walk", "Trailhead C", LocalDate.of(2024, 8, 10), LocalTime.of(14, 0), 10.0));
        logger.info("Sample data initialization complete. Total hikes: {}", hikes.size());
    }
}
