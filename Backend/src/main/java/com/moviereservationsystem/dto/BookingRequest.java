package com.moviereservationsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingRequest(
        @NotNull(message = "Showtime ID is required")
        Long showtimeId,

        @NotEmpty(message = "At least one seat must be selected")
        List<Long> seatIds
) {}