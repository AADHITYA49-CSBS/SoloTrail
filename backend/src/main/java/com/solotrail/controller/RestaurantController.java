package com.solotrail.controller;

import com.solotrail.entity.Restaurant;
import com.solotrail.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    @GetMapping("/{city}")
    public List<Restaurant> getRestaurantsByCity(@PathVariable String city) {
        return restaurantService.getRestaurantsByCity(city);
    }
}
