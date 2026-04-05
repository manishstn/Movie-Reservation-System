//package com.moviereservationsystem.controller;
//
//import com.moviereservationsystem.dto.BookingResponse;
//import com.moviereservationsystem.dto.ReservationRequest;
//import com.moviereservationsystem.service.BookingService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/bookings")
//@RequiredArgsConstructor
//@Slf4j
//public class UserBookingController {
//
//    private final BookingService bookingService;
//
//    /**
//     * HLD: Seat Reservation Process
//     * This is a POST because it creates a new "Booking" resource.
//     * Logic: Validates seat availability and creates a pending reservation.
//     */
//    @PostMapping("/reserve")
//    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody ReservationRequest request, // Updated Name
//                                                         @AuthenticationPrincipal UserDetails currentUser) {
//
//        log.info("System Action: User {} initiated reservation for Showtime {}", currentUser.getUsername(), request.showtimeId());
//
//        return new ResponseEntity<>(bookingService.processReservation(request, currentUser.getUsername()), HttpStatus.CREATED);
//    }
//
//    /**
//     * LLD: Personal Booking History
//     * Requirement: Users should only see their own bookings.
//     */
//    @GetMapping("/my-history")
//    public ResponseEntity<Page<BookingResponse>> getMyBookings(@AuthenticationPrincipal UserDetails currentUser, @PageableDefault(size = 10, sort = "bookingTime") Pageable pageable) {
//
//        return ResponseEntity.ok(bookingService.getUserBookings(currentUser.getUsername(), pageable));
//    }
//
//    /**
//     * LLD: Ticket Cancellation
//     * Logic: Only allowed if the showtime hasn't started yet (checked in Service).
//     */
//    @DeleteMapping("/{bookingId}/cancel")
//    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long bookingId, @AuthenticationPrincipal UserDetails currentUser) {
//
//        log.warn("Cancellation Request: Booking ID {} by User {}", bookingId, currentUser.getUsername());
//        bookingService.cancelBooking(bookingId, currentUser.getUsername());
//
//        return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully and seats released."));
//    }
//
//    /**
//     * Extra Logic: Ticket Details (QR Code/Receipt Data)
//     */
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable Long bookingId, @AuthenticationPrincipal UserDetails currentUser) {
//
//        return ResponseEntity.ok(bookingService.getBookingById(bookingId, currentUser.getUsername()));
//    }
//}