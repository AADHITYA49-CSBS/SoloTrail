package com.solotrail.repository;

import com.solotrail.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserId(Long userId);

    List<Trip> findByDestination(String destination);
}
