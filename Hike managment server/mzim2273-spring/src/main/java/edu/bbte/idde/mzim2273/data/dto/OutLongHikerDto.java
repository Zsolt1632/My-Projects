package edu.bbte.idde.mzim2273.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutLongHikerDto {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private List<OutShortHikeDto> hikes = new ArrayList<>();
}
