package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Table(name = "LOCATIONS")
@RequiredArgsConstructor
public class Location {

    @Id
    @Column(name = "LOCATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "LAT")
    private Float lat;

    @Column(name = "LON")
    private Float lon;

    @Transient
    Long likes;

}
