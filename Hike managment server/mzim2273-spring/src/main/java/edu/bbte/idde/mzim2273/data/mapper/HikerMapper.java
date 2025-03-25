package edu.bbte.idde.mzim2273.data.mapper;

import edu.bbte.idde.mzim2273.data.dto.InHikerDto;
import edu.bbte.idde.mzim2273.data.dto.OutLongHikerDto;
import edu.bbte.idde.mzim2273.data.dto.OutShortHikerDto;
import edu.bbte.idde.mzim2273.data.model.Hiker;

import java.util.stream.Collectors;

public class HikerMapper {

    // Convert InHikerDto to Hiker entity
    public static Hiker mapToHiker(InHikerDto inHikerDto) {
        return new Hiker(inHikerDto.getName(), inHikerDto.getAge(), inHikerDto.getEmail());
    }

    // Convert Hiker entity to OutShortHikerDto
    public static OutShortHikerDto mapToShortDto(Hiker hiker) {
        OutShortHikerDto dto = new OutShortHikerDto();
        dto.setId(hiker.getId());
        dto.setName(hiker.getName());
        return dto;
    }

    // Convert Hiker entity to OutLongHikerDto
    public static OutLongHikerDto mapToLongDto(Hiker hiker) {
        OutLongHikerDto dto = new OutLongHikerDto();
        dto.setId(hiker.getId());
        dto.setName(hiker.getName());
        dto.setAge(hiker.getAge());
        dto.setEmail(hiker.getEmail());

        // Map hikes to their names for simplicity
        dto.setHikes(
                hiker.getHikes().stream()
                        .map(HikeMapper::mapToOutShortDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
