package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.BookingRequest;
import com.moviereservationsystem.entity.Reservation;
import com.moviereservationsystem.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ROLE_CUSTOMER;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/book")
    @PreAuthorize(HAS_ROLE_CUSTOMER)
    public ResponseEntity<Reservation> bookTickets(@Valid @RequestBody BookingRequest request) {
        try {
            return ResponseEntity.ok(reservationService.createBooking(request));
        } catch (ObjectOptimisticLockingFailureException e) {
            // HLD Concurrency Management: Handle simultaneous clicks
            throw new RuntimeException("Sorry, those seats were just taken by another user. Please try again.");
        }
    }
}

