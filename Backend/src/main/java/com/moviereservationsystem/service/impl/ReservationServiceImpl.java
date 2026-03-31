package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.BookingRequest;
import com.moviereservationsystem.entity.*;
import com.moviereservationsystem.enums.ReservationStatus;
import com.moviereservationsystem.enums.SeatStatus;
import com.moviereservationsystem.enums.SeatTier;
import com.moviereservationsystem.repository.*;
import com.moviereservationsystem.service.ReservationService;
import com.moviereservationsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowTimeSeatRepository showTimeSeatRepository;
    private final ShowTimeRepository showtimeRepository;

    @Transactional
    @Override
    public Reservation createBooking(BookingRequest request) {
        // 1. Context Identity (HLD: Security Context)
        User user = SecurityUtils.getCurrentUser();

        // 2. Fetch Showtime
        Showtime showtime = showtimeRepository.findById(request.showtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // 3. Fetch specific seats for this show (LLD: Data mapping)
        List<ShowTimeSeat> requestedSeats = showTimeSeatRepository.findSelectedSeats(
                request.showtimeId(), request.seatIds());

        if (requestedSeats.size() != request.seatIds().size()) {
            throw new RuntimeException("One or more seats are invalid for this show.");
        }

        // 4. Validate Availability
        requestedSeats.forEach(seat -> {
            if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat " + seat.getSeat().getSeatNumber() + " is already taken.");
            }
        });

        // 5. Create Reservation Object
        Reservation reservation = Reservation.builder()
                .user(user)
                .showtime(showtime)
                .totalAmount(calculateTotal(showtime.getBasePrice(), requestedSeats))
                .reservationStatus(ReservationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // 15-min TTL
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // 6. UPDATE SEAT STATUS (Crucial LLD: Triggers @Version increment)
        requestedSeats.forEach(stSeat -> {
            stSeat.setSeatStatus(SeatStatus.BOOKED);
            stSeat.setReservation(savedReservation);
        });

        // If another thread modified 'version' before this save, Hibernate throws Exception here
        showTimeSeatRepository.saveAll(requestedSeats);

        return savedReservation;
    }

    private BigDecimal calculateTotal(BigDecimal base, List<ShowTimeSeat> seats) {
        // Example LLD Logic: Premium seats cost 1.5x
        return seats.stream().map(s -> {
            if (s.getSeat().getSeatTier() == SeatTier.PREMIUM) return base.multiply(new BigDecimal("1.5"));
            return base;
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}