package edu.bbte.idde.mzim2273.data.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@ToString(callSuper = true)
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
