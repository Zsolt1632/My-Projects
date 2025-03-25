package edu.bbte.idde.mzim2273.data.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InPartialHikeDto {

    @NotBlank(message = "Start location cannot be blank")
    private String startLocation;

    @NotNull(message = "Start time cannot be null")
    @FutureOrPresent(message = "Start time must be in the present or future")
    private LocalTime startTime;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;
}
