package com.solotrail.service;

import com.solotrail.entity.Restaurant;
import com.solotrail.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCityIgnoreCase(city);
    }

    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }
}
