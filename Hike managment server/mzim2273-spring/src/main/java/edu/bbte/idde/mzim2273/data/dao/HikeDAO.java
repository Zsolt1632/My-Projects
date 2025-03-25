package edu.bbte.idde.mzim2273.data.dao;

import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface HikeDAO extends Dao<Hike> {
    Collection<Hike> findAll() throws RepositoryException;
    Collection<Hike> getByName(String name) throws RepositoryException;
    Collection<Hike> filterByPrice(double price) throws RepositoryException;
}
