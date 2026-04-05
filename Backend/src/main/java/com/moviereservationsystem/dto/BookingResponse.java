package com.moviereservationsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String movieTitle,
        String hallName,
        List<String> seatNumbers,
        Double totalAmount,
        String status, // PENDING, CONFIRMED, CANCELLED
        LocalDateTime showTime
) {}