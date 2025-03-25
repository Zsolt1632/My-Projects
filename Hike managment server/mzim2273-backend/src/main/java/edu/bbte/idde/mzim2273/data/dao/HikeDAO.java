package edu.bbte.idde.mzim2273.data.dao;

import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;

import java.util.Collection;

public interface HikeDAO extends Dao<Hike> {


    Collection<Hike> getAllHikes(Boolean isArchived) throws RepositoryException;
}
