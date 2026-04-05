//package com.moviereservationsystem.service.impl;
//
//import com.moviereservationsystem.dto.BookingResponse;
//import com.moviereservationsystem.dto.ReservationRequest;
//import com.moviereservationsystem.entity.Seat;
//import com.moviereservationsystem.entity.Showtime;
//import com.moviereservationsystem.entity.User;
//import com.moviereservationsystem.enums.;
//import com.moviereservationsystem.repository.SeatRepository;
//import com.moviereservationsystem.repository.ShowTimeRepository;
//import com.moviereservationsystem.repository.UserRepository;
//import com.moviereservationsystem.service.BookingService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BookingServiceImpl implements BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final ShowTimeRepository showtimeRepository;
//    private final SeatRepository seatRepository;
//    private final UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public BookingResponse processReservation(ReservationRequest request, String username) {
//        log.info("Processing reservation for user: {} | Showtime: {}", username, request.showtimeId());
//
//        // 1. Fetch Showtime and User
//        Showtime showtime = showtimeRepository.findById(request.showtimeId()).orElseThrow(() -> new RuntimeException("Showtime not found"));
//
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User context invalid"));
//
//        // 2. LLD Strategy: Pessimistic Locking
//        // We verify if seats are already booked for THIS specific showtime
//        List<Long> alreadyBookedIds = bookingRepository.findBookedSeatIds(request.showtimeId(), request.seatIds());
//        if (!alreadyBookedIds.isEmpty()) {
//            throw new IllegalStateException("Concurrency Conflict: Some selected seats are no longer available.");
//        }
//
//        // 3. Fetch physical seats to calculate pricing based on Tier (Premium vs Standard)
//        List<Seat> selectedSeats = seatRepository.findAllById(request.seatIds());
//        BigDecimal totalAmount = calculateTotal(showtime.getBasePrice(), selectedSeats);
//
//        // 4. Create Booking Record
//        Booking booking = Booking.builder().user(user).showtime(showtime).seats(selectedSeats).totalPrice(totalAmount).status(BookingStatus.CONFIRMED) // In a real ERP, this might be PENDING until payment
//                .bookingTime(LocalDateTime.now()).paymentMethod(request.paymentMethod()).build();
//
//        Booking savedBooking = bookingRepository.save(booking);
//
//        return mapToResponse(savedBooking);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Page<BookingResponse> getUserBookings(String username, Pageable pageable) {
//        return bookingRepository.findByUserUsername(username, pageable).map(this::mapToResponse);
//    }
//
//    @Override
//    @Transactional
//    @Caching(evict = {@CacheEvict(value = "public_schedule", allEntries = true)}) // Release seats in cache
//    public void cancelBooking(Long bookingId, String username) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
//
//        // Guard: IDOR Protection
//        if (!booking.getUser().getUsername().equals(username)) {
//            throw new RuntimeException("Unauthorized: This is not your booking.");
//        }
//
//        // Guard: Business Logic - Cannot cancel if show already started
//        if (booking.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
//            throw new IllegalStateException("Policy Violation: Cannot cancel a show that has already started.");
//        }
//
//        booking.setStatus(BookingStatus.CANCELLED);
//        bookingRepository.save(booking);
//        log.warn("Booking {} cancelled by user {}", bookingId, username);
//    }
//
//    private BigDecimal calculateTotal(BigDecimal basePrice, List<Seat> seats) {
//        return seats.stream().map(seat -> seat.getSeatTier().getMultiplier().multiply(basePrice)).reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    private BookingResponse mapToResponse(Booking b) {
//        return new BookingResponse(b.getId(), b.getShowtime().getMovie().getTitle(), b.getShowtime().getCinemaHall().getName(), b.getSeats().stream().map(s -> s.getRowIdentifier() + s.getSeatNumber()).collect(Collectors.toList()), b.getTotalPrice(), b.getStatus().name(), b.getShowtime().getStartTime());
//    }
//
//    @Override
//    public BookingResponse getBookingById(Long bookingId, String username) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
//
//        if (!booking.getUser().getUsername().equals(username)) {
//            throw new RuntimeException("Access Denied");
//        }
//        return mapToResponse(booking);
//    }
//}