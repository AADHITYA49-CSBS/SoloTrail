package com.solotrail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
        @NotBlank String source,
        @NotBlank String destination,
        @NotNull LocalDate travelDate,
        @NotBlank String transportMode,
        Long hotelId,
        Long restaurantId,
        Long guideId
) {
}
