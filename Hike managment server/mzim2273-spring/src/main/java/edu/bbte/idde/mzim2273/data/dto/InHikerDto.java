package edu.bbte.idde.mzim2273.data.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InHikerDto {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Min(value = 0, message = "Age must not be negative, we do not accept unborn")
    @NotNull(message = "Must have an Age")
    private Integer age;

    @Email(message = "Email should be valid")
    private String email;
}
