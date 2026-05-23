package com.solotrail.controller;

import com.solotrail.entity.Hotel;
import com.solotrail.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping
    public Hotel addHotel(@RequestBody Hotel hotel) {

        return hotelService.addHotel(hotel);
    }

    @GetMapping("/{city}")
    public List<Hotel> getHotelsByCity(
            @PathVariable String city
    ) {

        return hotelService.getHotelsByCity(city);
    }
}
