package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dto.InPartialHikeDto;
import edu.bbte.idde.mzim2273.data.model.Hike;

import java.util.Collection;
import java.util.List;

public interface HikeService {
    Hike addHike(Hike hike) throws MissingArgumentException;

    Hike getHike(Long id) throws EntityNotFoundException, RepositoryException;

    void updateHike(Hike hike) throws EntityNotFoundException;

    void deleteHike(Long id) throws EntityNotFoundException;

    Collection<Hike> getAllHikes() throws RepositoryException;

    List<Hike> getByName(String name) throws RepositoryException;

    List<Hike> filterByPrice(double price) throws RepositoryException;

    Integer updateHikes(int difficulty, InPartialHikeDto inPartialHikeDto) throws EntityNotFoundException;
}
