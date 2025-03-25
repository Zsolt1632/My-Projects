package edu.bbte.idde.mzim2273.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutLongHikeDto {
    private Long id;
    private String name;
    private String startLocation;
    private LocalTime startTime;
    private LocalDate startDate;
    private int difficulty;
    private double price;
    private List<OutShortHikerDto> hikers;
}
