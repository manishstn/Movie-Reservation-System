package com.moviereservationsystem.controller;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ROLE_ADMIN;

import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.enums.SeatTier;
import com.moviereservationsystem.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    // Get layout for a specific hall (Used by both Admin and Customer)
    @GetMapping("/hall/{hallId}")
    public ResponseEntity<List<Seat>> getHallLayout(@PathVariable Long hallId) {
        return ResponseEntity.ok(seatService.getSeatsByHall(hallId));
    }

    // Admin: Change seat type (e.g., Standard to Premium)
    @PatchMapping("/{seatId}/tier")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Seat> updateTier(
            @PathVariable Long seatId,
            @RequestParam SeatTier tier) {
        return ResponseEntity.ok(seatService.updateSeatTier(seatId, tier));
    }
}