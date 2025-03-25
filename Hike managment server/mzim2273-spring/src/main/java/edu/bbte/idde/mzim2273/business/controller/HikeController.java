package edu.bbte.idde.mzim2273.business.controller;

import edu.bbte.idde.mzim2273.business.service.HikeService;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dto.InHikeDto;
import edu.bbte.idde.mzim2273.data.dto.InPartialHikeDto;
import edu.bbte.idde.mzim2273.data.dto.OutLongHikeDto;
import edu.bbte.idde.mzim2273.data.dto.OutShortHikeDto;
import edu.bbte.idde.mzim2273.data.mapper.HikeMapper;
import edu.bbte.idde.mzim2273.data.model.Hike;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hikes")
public class HikeController {

    private static final Logger logger = LoggerFactory.getLogger(HikeController.class);

    private final HikeService hikeService;

    @Autowired
    public HikeController(HikeService hikeService) {
        this.hikeService = hikeService;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OutLongHikeDto updateHike(@PathVariable Long id, @RequestBody @Valid InHikeDto hike)
            throws EntityNotFoundException {
        logger.info("Updating hike with ID: {}", id);
        Hike newHike = HikeMapper.mapToHike(hike);
        newHike.setId(id);
        hikeService.updateHike(newHike);
        logger.info("Successfully updated hike with ID: {}", id);

        return HikeMapper.mapToOutLongDto(newHike);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutLongHikeDto addHike(@RequestBody @Valid InHikeDto hike) throws MissingArgumentException {
        logger.info("Adding new hike: {}", hike);
        Hike newHike = HikeMapper.mapToHike(hike);
        OutLongHikeDto result = HikeMapper.mapToOutLongDto(hikeService.addHike(newHike));
        logger.info("Successfully added new hike with ID: {}", result.getId());

        return result;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OutLongHikeDto getHike(@PathVariable Long id)
            throws EntityNotFoundException, RepositoryException {
        logger.info("Fetching hike with ID: {}", id);
        OutLongHikeDto result = HikeMapper.mapToOutLongDto(hikeService.getHike(id));
        logger.info("Successfully fetched hike with ID: {}", id);

        return result;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<OutShortHikeDto> searchHikes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price) throws RepositoryException {
        logger.info("Searching hikes with criteria - Name: {}, Price: {}", name, price);

        Collection<OutShortHikeDto> result;

        if (name != null && price != null) {
            logger.info("Filtering hikes by both name and price.");
            result = HikeMapper.mapToOutShortDtoList(hikeService.filterByPrice(price))
                    .stream().filter(hike -> hike.getName().contains(name)).toList();
        } else if (name != null) {
            logger.info("Filtering hikes by name.");
            result = HikeMapper.mapToOutShortDtoList(hikeService.getByName(name));
        } else if (price != null) {
            logger.info("Filtering hikes by price.");
            result = HikeMapper.mapToOutShortDtoList(hikeService.filterByPrice(price));
        } else {
            logger.info("No filters applied, returning all hikes.");
            logger.info("Fetching all hikes");
            result = HikeMapper.mapToOutShortDtoList(new ArrayList<>(hikeService.getAllHikes()));
            logger.info("Successfully fetched {} hikes", result.size());
        }

        logger.info("Found {} hikes with the applied filters.", result.size());
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> deleteHike(@PathVariable Long id) throws EntityNotFoundException {
        logger.info("Deleting hike with ID: {}", id);
        hikeService.deleteHike(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Delete successful!");
        logger.info("Successfully deleted hike with ID: {}", id);

        return response;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Integer> updateBasedOnDifficulty(@RequestParam int difficulty, @RequestBody @Valid InPartialHikeDto inPartialHikeDto) throws EntityNotFoundException {

        logger.info("Updating hikes with difficulty LVL: {}", difficulty);
        hikeService.updateHikes(difficulty, inPartialHikeDto);

        Map<String, Integer> response = new HashMap<>();
        response.put("message", hikeService.updateHikes(difficulty, inPartialHikeDto));
        logger.info("Successfully Updated hikes with difficulty LVL: {}", difficulty);

        return response;
    }
}
