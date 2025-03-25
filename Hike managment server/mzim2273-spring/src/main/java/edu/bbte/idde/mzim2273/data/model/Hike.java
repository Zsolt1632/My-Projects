package edu.bbte.idde.mzim2273.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
@Table(name = "hikes")
public class Hike extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name="start_location",nullable = false)
    private String startLocation;

    @Column(name="start_date",nullable = false)
    private LocalDate startDate;

    @Column(name="start_time",nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int difficulty;

    @ToString.Exclude
    @ManyToMany(mappedBy = "hikes", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Hiker> hikers = new ArrayList<>();


    public Hike(String startLocation, String name, LocalDate startDate, LocalTime startTime, double price) {
        this.startLocation = startLocation;
        this.name = name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.price = price;
    }
}
