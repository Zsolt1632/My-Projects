package edu.bbte.idde.mzim2273.business.controller;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.HikerService;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dto.OutLongHikerDto;
import edu.bbte.idde.mzim2273.data.dto.OutShortHikeDto;
import edu.bbte.idde.mzim2273.data.mapper.HikeMapper;
import edu.bbte.idde.mzim2273.data.mapper.HikerMapper;
import edu.bbte.idde.mzim2273.data.model.Hike;
import edu.bbte.idde.mzim2273.data.model.Hiker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("jpa")
@RestController
@RequestMapping("/hiker/{hikerId}/hike")
public class HikerHikeController {
    private static final Logger logger = LoggerFactory.getLogger(HikerHikeController.class);
    private final HikerService hikerService;
    private final HikeService hikeService;

    @Autowired
    public HikerHikeController(HikerService hikerService, HikeService hikeService) {
        this.hikerService = hikerService;
        this.hikeService = hikeService;
    }

    // Add a hike to a specific hiker if it's not already added
    @PostMapping("/{hikeId}")
    @ResponseStatus(HttpStatus.OK)
    public OutLongHikerDto addHikeToHiker(@PathVariable Long hikerId, @PathVariable Long hikeId)
            throws EntityNotFoundException, RepositoryException {
        logger.info("Adding hike with ID: {} to hiker with ID: {}", hikeId, hikerId);

        // Check if the hiker exists
        Hiker hiker = hikerService.getHiker(hikerId);
        if (hiker == null) {
            logger.error("Hiker with ID: {} not found.", hikerId);
            throw new EntityNotFoundException("Hiker not found");
        }

        // Check if the hike exists
        Hike hike = hikeService.getHike(hikeId);
        if (hike == null) {
            logger.error("Hike with ID: {} not found.", hikeId);
            throw new EntityNotFoundException("Hike not found");
        }

        // Add the hike to the hiker if it's not already added
        if (!hiker.getHikes().contains(hike)) {
            hiker.getHikes().add(hike);
            hike.getHikers().add(hiker);
            logger.info("Hike was not present in hiker's list, adding it.");
        } else {
            logger.info("Hike already present in hiker's list, no action taken.");
        }

        Hiker updatedHiker = hikerService.saveHiker(hiker);
        logger.info("Successfully added hike if not already present. Updated hiker: {}", updatedHiker);
        return HikerMapper.mapToLongDto(updatedHiker);
    }

    // Remove a hike from a specific hiker
    @DeleteMapping("/{hikeId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> removeHikeFromHiker(@PathVariable Long hikerId, @PathVariable Long hikeId)
            throws EntityNotFoundException, RepositoryException {
        logger.info("Removing hike with ID: {} from hiker with ID: {}", hikeId, hikerId);

        // Check if the hiker exists
        Hiker hiker = hikerService.getHiker(hikerId);
        if (hiker == null) {
            logger.error("Hiker with ID: {} not found.", hikerId);
            throw new EntityNotFoundException("Hiker not found");
        }

        // Check if the hike exists
        Hike hike = hikeService.getHike(hikeId);
        if (hike == null) {
            logger.error("Hike with ID: {} not found.", hikeId);
            throw new EntityNotFoundException("Hike not found");
        }

        // Remove the hike from the hiker's list
        hiker.getHikes().remove(hike);
        hike.getHikers().remove(hiker);

        hikerService.saveHiker(hiker);
        logger.info("Successfully removed hike with ID: {} from hiker with ID: {}", hikeId, hikerId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Delete successful!");
        return response;
    }

    // List all hikes for a specific hiker
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OutShortHikeDto> getHikesByHiker(@PathVariable Long hikerId) throws EntityNotFoundException {
        logger.info("Fetching hikes for hiker with ID: {}", hikerId);
        Hiker hiker = hikerService.getHiker(hikerId);
        List<OutShortHikeDto> hikes = hiker.getHikes().stream()
                .map(HikeMapper::mapToOutShortDto)
                .collect(Collectors.toList());
        logger.info("Found {} hikes for hiker with ID: {}", hikes.size(), hikerId);
        return hikes;
    }
}
