package com.moviereservationsystem.controller;

import static com.moviereservationsystem.constants.SecurityConstants.*;
import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.service.CinemaHallService;
import com.moviereservationsystem.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/halls")
@RequiredArgsConstructor
@Slf4j
public class CinemaHallController {

    private final CinemaHallService hallService;
    private final SeatService seatService;

    /**
     * HLD: Paginated Retrieval
     * usage: /api/v1/halls?page=0&size=10&sort=name,asc
     */
    @GetMapping
    public ResponseEntity<Page<CinemaHall>> getAllHalls(
            @PageableDefault(size = 15, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(hallService.getAllHalls(pageable));
    }

    @PostMapping
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<CinemaHall> createHall(@Valid @RequestBody CinemaHallRequest request) {
        log.info("Admin initiated creation of hall: {}", request.name());
        return new ResponseEntity<>(hallService.createHall(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/rename")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<CinemaHall> renameHall(
            @PathVariable Long id,
            @RequestParam String newName) {
        return ResponseEntity.ok(hallService.updateHallName(id, newName));
    }

    /**
     * Extra Production Functionality: Bulk Seat Tier Management
     * Allows Admin to re-classify a row (e.g., A to PREMIUM) after creation.
     */
    @PatchMapping("/{id}/seats/tier-update")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Void> bulkUpdateSeatTier(
            @PathVariable Long id,
            @RequestParam String rowIdentifier,
            @RequestParam String tier) {
        log.warn("Bulk Update: Changing Hall {} Row {} to {}", id, rowIdentifier, tier);
        hallService.updateRowTier(id, rowIdentifier, tier);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}/analytics")
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Map<String, Object>> getHallAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.getHallAnalytics(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }
}