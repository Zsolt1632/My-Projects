package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;

import java.util.Collection;

public interface HikeService {
    void addHike(Hike hike) throws MissingArgumentException, RepositoryException;

    Hike getHike(Long id, Boolean isArchived) throws EntityNotFoundException, RepositoryException;

    void updateHike(Hike hike, Boolean isArchived) throws EntityNotFoundException, RepositoryException;

    void deleteHike(Long id, Boolean isArchived) throws EntityNotFoundException, RepositoryException;

    Collection<Hike> getAllHikes(Boolean isArchived) throws RepositoryException;

    Hike getHikeById(Long id, Boolean isArchived) throws EntityNotFoundException, RepositoryException;

    Object getHikeDAO();
}
