package com.moviereservationsystem.controller;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ANY_ROLE_STAFF;
import static com.moviereservationsystem.constants.SecurityConstants.HAS_ROLE_ADMIN;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.service.CinemaHallService;
import com.moviereservationsystem.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/halls")
@RequiredArgsConstructor
public class CinemaHallController {

    private final CinemaHallService hallService;
    private final SeatService seatService;

    @PostMapping
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<CinemaHall> createHall(@Valid @RequestBody CinemaHallRequest request) {
        return new ResponseEntity<>(hallService.createHall(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CinemaHall>> getAllHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Map<String, Object>> getHallStats(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.getHallAnalytics(id));
    }

    @GetMapping("/{id}/seats")
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<List<Seat>> getHallSeats(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getSeatsByHall(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/rename")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<CinemaHall> renameHall(
            @PathVariable Long id,
            @RequestParam String newName) {
        return ResponseEntity.ok(hallService.updateHallName(id, newName));
    }
}