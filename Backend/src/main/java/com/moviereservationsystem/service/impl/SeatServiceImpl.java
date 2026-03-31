package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.enums.SeatTier;
import com.moviereservationsystem.repository.SeatRepository;
import com.moviereservationsystem.repository.ShowTimeSeatRepository;
import com.moviereservationsystem.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ShowTimeSeatRepository showTimeSeatRepository;
    @Override
    public List<Seat> getSeatsByHall(Long hallId) {
        return seatRepository.findByCinemaHallIdOrderByRowIdentifierAscSeatNumberAsc(hallId);
    }

    @Override
    @Transactional
    public Seat updateSeatTier(Long seatId, SeatTier tier) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        seat.setSeatTier(tier);
        return seatRepository.save(seat);
    }

    @Override
    @Transactional
    public void deleteSeat(Long seatId) {
        if (!seatRepository.existsById(seatId)) {
            throw new RuntimeException("Seat not found with ID: " + seatId);
        }
        boolean isBusy = showTimeSeatRepository.isSeatLinkedToActiveShowtime(
                seatId, LocalDateTime.now());

        if (isBusy) {
            throw new IllegalStateException("Cannot delete seat: It is currently part of an active or upcoming showtime schedule.");
        }
        seatRepository.deleteById(seatId);
    }
}