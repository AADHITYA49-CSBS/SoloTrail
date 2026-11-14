package com.solotrail.service;

import com.solotrail.dto.BookingRequest;
import com.solotrail.entity.*;
import com.solotrail.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BookingService {

    private static final double BUS_FARE = 800;
    private static final double TRAIN_FARE = 1200;
    private static final double CAR_FARE = 2500;
    private static final double FLIGHT_FARE = 4500;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TouristGuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking createDemoBooking(BookingRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Booking booking = new Booking();
        booking.setBookingReference("ST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        booking.setUserId(user.getId());
        booking.setSource(request.source().trim());
        booking.setDestination(request.destination().trim());
        booking.setTravelDate(request.travelDate());
        booking.setTransportMode(request.transportMode().trim().toUpperCase(Locale.ROOT));
        booking.setTransportFare(transportFare(request.transportMode()));

        if (request.hotelId() != null) {
            Hotel hotel = hotelRepository.findById(request.hotelId())
                    .orElseThrow(() -> invalidSelection("hotel"));
            booking.setHotelName(hotel.getHotelName());
            booking.setHotelCost(safe(hotel.getPricePerNight()));
        }

        if (request.restaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                    .orElseThrow(() -> invalidSelection("restaurant"));
            booking.setRestaurantName(restaurant.getRestaurantName());
            booking.setRestaurantCost(restaurantCost(restaurant.getPriceRange()));
        }

        if (request.guideId() != null) {
            TouristGuide guide = guideRepository.findById(request.guideId())
                    .orElseThrow(() -> invalidSelection("guide"));
            booking.setGuideName(guide.getGuideName());
            booking.setGuideCost(safe(guide.getPricePerDay()));
        }

        booking.setTotalAmount(round(booking.getTransportFare()
                + safe(booking.getHotelCost())
                + safe(booking.getRestaurantCost())
                + safe(booking.getGuideCost())));
        booking.setPaymentStatus("DEMO_PAID");
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookings(Long userId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own bookings");
        }
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private double transportFare(String mode) {
        return switch (mode.trim().toUpperCase(Locale.ROOT)) {
            case "BUS" -> BUS_FARE;
            case "TRAIN" -> TRAIN_FARE;
            case "CAR" -> CAR_FARE;
            case "FLIGHT" -> FLIGHT_FARE;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transport must be Bus, Train, Car, or Flight");
        };
    }

    private double restaurantCost(String priceRange) {
        if (priceRange == null) {
            return 500;
        }
        int priceLevel = (int) priceRange.codePoints()
                .filter(codePoint -> codePoint == '₹')
                .count();
        return switch (priceLevel) {
            case 1 -> 300;
            case 2 -> 600;
            default -> 1000;
        };
    }

    private ResponseStatusException invalidSelection(String type) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected " + type + " was not found");
    }

    private double safe(Double value) {
        return value == null ? 0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
