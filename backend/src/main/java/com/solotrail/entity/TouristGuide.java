package com.solotrail.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TouristGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String guideName;

    @Column(nullable = false)
    private String city;

    private String languages;

    private Integer experienceYears;

    private Double pricePerDay;

    private String specialization;

    private Boolean available;

    private String contactNumber;
}
