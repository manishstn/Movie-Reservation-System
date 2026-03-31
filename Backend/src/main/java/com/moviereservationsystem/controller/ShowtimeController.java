package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.ShowtimeRequest;
import com.moviereservationsystem.entity.ShowTimeSeat;
import com.moviereservationsystem.entity.Showtime;
import com.moviereservationsystem.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ANY_ROLE_STAFF;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ResponseEntity<List<Showtime>> getByMovie(@PathVariable Long movieId){
        return ResponseEntity.ok(showtimeService.getShowtimesByMovie(movieId));
    }

    @PostMapping
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Showtime> scheduleShow(@Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(showtimeService.createShowtime(request));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<ShowTimeSeat>> getSeatingPlan(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getSeatingPlan(id));
    }
}
