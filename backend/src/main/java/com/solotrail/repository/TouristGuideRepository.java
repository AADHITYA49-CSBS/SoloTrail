package com.solotrail.repository;

import com.solotrail.entity.TouristGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TouristGuideRepository extends JpaRepository<TouristGuide, Long> {

    List<TouristGuide> findByCityIgnoreCase(String city);
}
