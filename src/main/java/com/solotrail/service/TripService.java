package com.solotrail.service;

import com.solotrail.entity.Trip;
import com.solotrail.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public List<Trip> getTripsByUser(Long userId) {
        return tripRepository.findByUserId(userId);
    }

    public List<Trip> getTripsByDestination(String destination) {
        return tripRepository.findByDestination(destination);
    }
}
