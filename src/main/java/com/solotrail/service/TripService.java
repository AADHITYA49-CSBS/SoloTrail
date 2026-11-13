package com.solotrail.service;

import com.solotrail.entity.Trip;
import com.solotrail.entity.User;
import com.solotrail.repository.TripRepository;
import com.solotrail.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    public Trip createTrip(Trip trip, String email) {
        User user = findUserByEmail(email);
        trip.setUserId(user.getId());
        return tripRepository.save(trip);
    }

    public List<Trip> getTripsByUser(Long userId, String email) {
        User user = findUserByEmail(email);
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "You can only view your own trips");
        }
        return tripRepository.findByUserId(userId);
    }

    public List<Trip> getTripsByDestination(String destination) {
        return tripRepository.findByDestination(destination);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }
}
