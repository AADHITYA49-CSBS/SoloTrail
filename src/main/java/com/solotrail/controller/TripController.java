package com.solotrail.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TripController {

    @GetMapping("/api/trips")
    public String getTrips() {
        return "Protected Trip API Accessed!";
    }
}

