package com.moviereservationsystem.dto;

import java.time.LocalDateTime;

public record ShowtimeResponse(
        Long showtimeId,
        String movieTitle,
        String posterUrl,
        String hallName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double price
) {}