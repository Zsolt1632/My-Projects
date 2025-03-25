package edu.bbte.idde.mzim2273.business.controller;

import edu.bbte.idde.mzim2273.business.service.HikerService;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.dto.InHikerDto;
import edu.bbte.idde.mzim2273.data.dto.OutLongHikerDto;
import edu.bbte.idde.mzim2273.data.dto.OutShortHikerDto;
import edu.bbte.idde.mzim2273.data.mapper.HikerMapper;
import edu.bbte.idde.mzim2273.data.model.Hiker;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Profile("jpa")
@RestController
@RequestMapping("/hikers")
public class HikerController {

    private static final Logger logger = LoggerFactory.getLogger(HikerController.class);
    private final HikerService hikerService;

    @Autowired
    public HikerController(HikerService hikerService) {
        this.hikerService = hikerService;
    }

    // List all hikers
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OutShortHikerDto> getAllHikers() {
        logger.info("Fetching all hikers...");
        List<OutShortHikerDto> hikers = hikerService.getAllHikers().stream()
                .map(HikerMapper::mapToShortDto)
                .collect(Collectors.toList());
        logger.info("Found {} hikers.", hikers.size());
        return hikers;
    }

    // Get a specific hiker by ID
    @GetMapping("/{hikerId}")
    @ResponseStatus(HttpStatus.OK)
    public OutLongHikerDto getHiker(@PathVariable Long hikerId) throws EntityNotFoundException {
        logger.info("Fetching hiker with ID: {}", hikerId);
        Hiker hiker = hikerService.getHiker(hikerId);
        logger.info("Hiker found: {}", hiker);
        return HikerMapper.mapToLongDto(hiker);
    }

    // Create a new hiker
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutLongHikerDto createHiker(@RequestBody @Valid InHikerDto inHikerDto) {
        logger.info("Creating new hiker: {}", inHikerDto);
        Hiker hiker = hikerService.saveHiker(HikerMapper.mapToHiker(inHikerDto));
        logger.info("Hiker successfully created with ID: {}", hiker.getId());
        return HikerMapper.mapToLongDto(hiker);
    }

    // Update an existing hiker
    @PutMapping("/{hikerId}")
    @ResponseStatus(HttpStatus.OK)
    public OutLongHikerDto updateHiker(@PathVariable Long hikerId, @RequestBody @Valid InHikerDto inHikerDto)
            throws EntityNotFoundException {
        logger.info("Updating hiker with ID: {}", hikerId);
        Hiker existingHiker = hikerService.getHiker(hikerId);
        logger.info("Existing hiker details: {}", existingHiker);

        existingHiker.setName(inHikerDto.getName());
        existingHiker.setAge(inHikerDto.getAge());
        existingHiker.setEmail(inHikerDto.getEmail());

        Hiker updatedHiker = hikerService.saveHiker(existingHiker);
        logger.info("Hiker successfully updated: {}", updatedHiker);
        return HikerMapper.mapToLongDto(updatedHiker);
    }

    // Delete a hiker
    @DeleteMapping("/{hikerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHiker(@PathVariable Long hikerId) throws EntityNotFoundException {
        logger.info("Deleting hiker with ID: {}", hikerId);
        hikerService.deleteHiker(hikerId);
        logger.info("Hiker with ID: {} successfully deleted.", hikerId);
    }
}
