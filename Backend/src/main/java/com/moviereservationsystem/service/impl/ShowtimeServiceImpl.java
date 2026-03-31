package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.ShowtimeRequest;
import com.moviereservationsystem.entity.*;
import com.moviereservationsystem.enums.SeatStatus;
import com.moviereservationsystem.repository.*;
import com.moviereservationsystem.service.ShowtimeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowTimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CinemaHallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final ShowTimeSeatRepository showTimeSeatRepository;

    @Override
    public List<Showtime> getShowtimesByMovie(Long movieId) {
        return showtimeRepository.findByMovieIdAndIsActiveTrue(movieId);
    }

    @Override
    @Transactional
    public Showtime createShowtime(ShowtimeRequest request) {
        // 1. LLD Validation: Check for time overlaps in the same hall
        if (showtimeRepository.existsOverlappingShowtime(request.hallId(), request.startTime(), request.endTime())) {
            throw new IllegalStateException("Hall is already booked for another movie during this time slot.");
        }

        // 2. Fetch dependencies
        Movie movie = movieRepository.findById(request.movieId()).orElseThrow();
        CinemaHall hall = hallRepository.findById(request.hallId()).orElseThrow();

        // 3. Save Showtime
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .cinemaHall(hall)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .basePrice(BigDecimal.valueOf(request.price()))
                .isActive(true)
                .build();

        Showtime savedShow = showtimeRepository.save(showtime);

        List<Seat> physicalSeats = seatRepository.findByCinemaHallId(request.hallId());

        List<ShowTimeSeat> showTimeSeats = physicalSeats.stream().map(ps ->
                ShowTimeSeat.builder()
                        .showtime(savedShow)
                        .seat(ps)
                        .seatStatus(SeatStatus.AVAILABLE)
                        .version(0) // Initial version for Optimistic Locking
                        .build()
        ).toList();

        showTimeSeatRepository.saveAll(showTimeSeats);
        return savedShow;
    }

    @Override
    public List<ShowTimeSeat> getSeatingPlan(Long id) {
        return showTimeSeatRepository.findByShowtimeIdOrderBySeatRowIdentifierAscSeatSeatNumberAsc(id);
    }
}
