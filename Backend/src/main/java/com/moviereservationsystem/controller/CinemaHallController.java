package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import com.moviereservationsystem.service.CinemaHallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ROLE_ADMIN;

@RestController
@RequestMapping("/api/v1/halls")
@RequiredArgsConstructor
public class CinemaHallController {

    private final CinemaHallService hallService;

    @PostMapping
    @PreAuthorize(HAS_ROLE_ADMIN) // Only Admin can create physical infrastructure
    public ResponseEntity<CinemaHall> createHall(@Valid @RequestBody CinemaHallRequest request) {
        return new ResponseEntity<>(hallService.createHall(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CinemaHall>> getAllHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaHall> getHall(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.getHallById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }
}