package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.SeatRequest;
import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.enums.SeatTier;
import java.util.List;

public interface SeatService {
    List<Seat> getSeatsByHall(Long hallId);
    Seat updateSeatTier(Long seatId, SeatTier tier);
    void deleteSeat(Long seatId);
}