package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.enums.SeatTier;
import com.moviereservationsystem.repository.CinemaHallRepository;
import com.moviereservationsystem.repository.SeatRepository;
import com.moviereservationsystem.service.CinemaHallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaHallServiceImpl implements CinemaHallService {

    private final CinemaHallRepository hallRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public CinemaHall createHall(CinemaHallRequest request) {
        if (hallRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Hall with this name already exists.");
        }

        // 1. Persist the Hall record
        CinemaHall hall = CinemaHall.builder()
                .name(request.name())
                .totalRows(request.totalRows())
                .seatPerRows(request.seatPerRows())
                .build();

        CinemaHall savedHall = hallRepository.save(hall);

        // 2. LLD Logic: Automatically generate physical seats
        List<Seat> seats = new ArrayList<>();
        for (int row = 0; row < request.totalRows(); row++) {
            // Convert row index to Letter (0=A, 1=B, etc.)
            String rowIdentifier = String.valueOf((char) ('A' + row));

            for (int col = 1; col <= request.seatPerRows(); col++) {
                seats.add(Seat.builder()
                        .cinemaHall(savedHall)
                        .rowIdentifier(rowIdentifier)
                        .seatNumber(col)
                        // LLD: Business rule - First 2 rows are Premium, rest are Standard
                        .seatTier(row < 2 ? SeatTier.PREMIUM : SeatTier.STANDARD)
                        .build());
            }
        }

        seatRepository.saveAll(seats);
        return savedHall;
    }

    @Override
    public List<CinemaHall> getAllHalls() {
        return hallRepository.findAll();
    }

    @Override
    public CinemaHall getHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteHall(Long id) {
        // Note: In production, check if there are active showtimes before deleting
        hallRepository.deleteById(id);
    }
}