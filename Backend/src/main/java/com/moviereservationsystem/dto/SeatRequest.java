package com.moviereservationsystem.dto;

import com.moviereservationsystem.enums.SeatTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatRequest(
        @NotNull Long hallId,
        @NotBlank String rowIdentifier,
        @NotNull Integer seatNumber,
        @NotNull SeatTier seatTier
) {}