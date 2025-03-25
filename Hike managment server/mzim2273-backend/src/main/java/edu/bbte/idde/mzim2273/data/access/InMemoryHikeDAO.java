package edu.bbte.idde.mzim2273.data.access;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.model.Hike;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryHikeDAO implements HikeDAO {
    private final Map<Long, Hike> hikes = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public void create(Hike hike) {
        hike.setId(currentId.getAndIncrement());
        hike.setArchived(false);
        hikes.put(hike.getId(), hike);
    }

    @Override
    public Hike read(Long id, Boolean isArchived) throws EntityNotFoundException {
        if (!hikes.containsKey(id) || !hikes.get(id).getArchived()) {
            throw new EntityNotFoundException("Hike with ID " + id + " not found.");
        }
        return hikes.get(id);
    }

    @Override
    public void update(Hike hike, Boolean isArchived) throws EntityNotFoundException {
        if (!hikes.containsKey(hike.getId()) || !hikes.get(hike.getId()).getArchived()) {
            throw new EntityNotFoundException("Hike with ID " + hike.getId() + " not found.");
        }
        hikes.put(hike.getId(), hike);
    }

    @Override
    public void delete(Long id, Boolean isArchived) throws EntityNotFoundException {
        if (!hikes.containsKey(id) || !hikes.get(id).getArchived()) {
            throw new EntityNotFoundException("Hike with ID " + id + " not found.");
        }
        hikes.remove(id);
    }

    @Override
    public Collection<Hike> getAllHikes(Boolean isArchived) {
        return hikes.values().stream().filter(hike -> hike.getArchived() == isArchived).toList();
    }

}
