package com.moviereservationsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CinemaHallRequest(
        @NotBlank(message = "Hall name is required")
        String name,

        @NotNull(message = "Total rows is required")
        @Min(value = 1, message = "Must have at least 1 row")
        Integer totalRows,

        @NotNull(message = "Seats per row is required")
        @Min(value = 1, message = "Must have at least 1 seat per row")
        Integer seatPerRows
) {}