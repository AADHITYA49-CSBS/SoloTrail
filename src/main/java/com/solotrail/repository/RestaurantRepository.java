package com.solotrail.repository;

import com.solotrail.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCityIgnoreCase(String city);
}
