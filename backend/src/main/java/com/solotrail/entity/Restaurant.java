package com.solotrail.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantName;

    @Column(nullable = false)
    private String city;

    private String cuisine;

    private String priceRange;

    private Double rating;

    private Boolean openNow;

    @Column(length = 1000)
    private String description;
}
