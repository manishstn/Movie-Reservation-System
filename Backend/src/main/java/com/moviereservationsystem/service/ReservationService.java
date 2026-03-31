package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.BookingRequest;
import com.moviereservationsystem.entity.Reservation;
import org.springframework.transaction.annotation.Transactional;

public interface  ReservationService {

    @Transactional
    Reservation createBooking(BookingRequest request);
}