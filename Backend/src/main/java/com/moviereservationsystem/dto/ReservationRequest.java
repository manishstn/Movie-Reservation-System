package com.moviereservationsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * HLD: ReservationRequest
 * Represents the intent to lock specific seats for a showtime.
 * Renamed from BookingRequest to better reflect the 'Pending' state in the lifecycle.
 */
public record ReservationRequest(

        @NotNull(message = "Showtime ID is mandatory")
        Long showtimeId,

        @NotEmpty(message = "At least one seat must be selected")
        List<Long> seatIds,

        @NotBlank(message = "Payment method selection is required")
        String paymentMethod
) {}