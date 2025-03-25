package edu.bbte.idde.mzim2273.business.service;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.model.Hiker;

import java.util.List;

public interface HikerService {
    Hiker getHiker(Long id) throws EntityNotFoundException;
    Hiker saveHiker(Hiker hiker);
    List<Hiker> getAllHikers();
    void deleteHiker(Long id) throws EntityNotFoundException;
    Hiker updateHiker(Long id, Hiker updatedHiker) throws EntityNotFoundException;
}
