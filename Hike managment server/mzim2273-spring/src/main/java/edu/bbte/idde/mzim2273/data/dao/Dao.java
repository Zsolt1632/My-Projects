package edu.bbte.idde.mzim2273.data.dao;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.BaseEntity;

public interface Dao<T extends BaseEntity> {
    void create(T hike) throws RepositoryException;

    void update(T hike) throws EntityNotFoundException, RepositoryException;

    T read(Long id) throws EntityNotFoundException, RepositoryException;

    void delete(Long id) throws EntityNotFoundException, RepositoryException;
}
