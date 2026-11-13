package com.solotrail.controller;

import com.solotrail.entity.Trip;
import com.solotrail.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripService.createTrip(trip, currentUserEmail());
    }

    @GetMapping("/user/{userId}")
    public List<Trip> getTripsByUser(@PathVariable Long userId) {
        return tripService.getTripsByUser(userId, currentUserEmail());
    }

    @GetMapping("/destination/{destination}")
    public List<Trip> getTripsByDestination(@PathVariable String destination) {
        return tripService.getTripsByDestination(destination);
    }

    private String currentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}

