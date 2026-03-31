package com.moviereservationsystem.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ShowtimeRequest(
        @NotNull(message = "Movie ID is required")
        Long movieId,

        @NotNull(message = "Hall ID is required")
        Long hallId,

        @NotNull(message = "Start time is required")
        @Future(message = "Showtime must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "Start time is required")
        @Future(message = "Showtime must be in the future")
        LocalDateTime endTime,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price cannot be negative")
        Double price
)
{
    /**
     * Helper method to validate time range logic in your Service layer
     */
    public boolean isTimeRangeValid() {
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }
}