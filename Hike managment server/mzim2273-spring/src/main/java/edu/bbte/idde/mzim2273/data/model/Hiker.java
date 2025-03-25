package edu.bbte.idde.mzim2273.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
@Table(name = "hiker")
public class Hiker extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Hike> hikes = new ArrayList<>();

    public Hiker( String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
}
