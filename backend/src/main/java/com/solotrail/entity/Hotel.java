package com.solotrail.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hotelName;

    @Column(nullable = false)
    private String city;

    private Double pricePerNight;

    private Double rating;

    private Boolean available;

    private String description;
}
