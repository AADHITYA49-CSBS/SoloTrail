package com.solotrail.controller;

import com.solotrail.dto.BookingRequest;
import com.solotrail.entity.Booking;
import com.solotrail.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/checkout")
    public Booking checkout(@Valid @RequestBody BookingRequest request) {
        return bookingService.createDemoBooking(request, currentUserEmail());
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getBookings(@PathVariable Long userId) {
        return bookingService.getBookings(userId, currentUserEmail());
    }

    private String currentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
