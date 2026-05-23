package com.solotrail.service;

import com.solotrail.entity.Hotel;
import com.solotrail.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> getHotelsByCity(String city) {

        return hotelRepository.findByCityIgnoreCase(city);
    }

    public Hotel addHotel(Hotel hotel) {

        return hotelRepository.save(hotel);
    }
}
