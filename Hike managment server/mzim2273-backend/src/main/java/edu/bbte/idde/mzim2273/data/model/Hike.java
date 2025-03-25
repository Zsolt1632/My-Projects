package edu.bbte.idde.mzim2273.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hike extends BaseEntity {
    private String name;
    private String startLocation;
    private LocalDate startDate;
    private LocalTime startTime;
    private double price;
}
