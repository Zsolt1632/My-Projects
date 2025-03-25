package edu.bbte.idde.mzim2273.data.mapper;

import edu.bbte.idde.mzim2273.data.dto.InHikeDto;
import edu.bbte.idde.mzim2273.data.dto.OutLongHikeDto;
import edu.bbte.idde.mzim2273.data.dto.OutShortHikeDto;
import edu.bbte.idde.mzim2273.data.model.Hike;

import java.util.List;
import java.util.stream.Collectors;

public class HikeMapper {

    // Map InDto to Hike
    public static Hike mapToHike(InHikeDto inHikeDto) {
        Hike hike = new Hike();
        hike.setName(inHikeDto.getName());
        hike.setDifficulty(inHikeDto.getDifficulty());
        hike.setStartLocation(inHikeDto.getStartLocation());
        hike.setStartTime(inHikeDto.getStartTime());
        hike.setStartDate(inHikeDto.getStartDate());
        hike.setPrice(inHikeDto.getPrice());
        return hike;
    }

    // Map Hike to OutShortDto
    public static OutShortHikeDto mapToOutShortDto(Hike hike) {
        return new OutShortHikeDto(
                hike.getId(),
                hike.getName(),
                hike.getStartLocation()
        );
    }

    // Map Hike to OutLongDto
    public static OutLongHikeDto mapToOutLongDto(Hike hike) {
        return new OutLongHikeDto(
                hike.getId(),
                hike.getName(),
                hike.getStartLocation(),
                hike.getStartTime(),
                hike.getStartDate(),
                hike.getDifficulty(),
                hike.getPrice(),
                hike.getHikers().stream().map(HikerMapper::mapToShortDto).toList()
        );
    }

    // Map a List of Hike to a List of OutShortDto
    public static List<OutShortHikeDto> mapToOutShortDtoList(List<Hike> hikes) {
        return hikes.stream()
                .map(HikeMapper::mapToOutShortDto)
                .collect(Collectors.toList());
    }

    // Map a List of Hike to a List of OutLongDto
    public static List<OutLongHikeDto> mapToOutLongDtoList(List<Hike> hikes) {
        return hikes.stream()
                .map(HikeMapper::mapToOutLongDto)
                .collect(Collectors.toList());
    }
}
