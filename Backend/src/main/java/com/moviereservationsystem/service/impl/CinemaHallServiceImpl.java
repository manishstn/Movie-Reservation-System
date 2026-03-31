package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import com.moviereservationsystem.entity.Seat;
import com.moviereservationsystem.enums.SeatTier;
import com.moviereservationsystem.repository.CinemaHallRepository;
import com.moviereservationsystem.repository.SeatRepository;
import com.moviereservationsystem.repository.ShowTimeRepository;
import com.moviereservationsystem.service.CinemaHallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CinemaHallServiceImpl implements CinemaHallService {

    private final CinemaHallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final ShowTimeRepository showtimeRepository;

    @Override
    @Transactional
    public CinemaHall createHall(CinemaHallRequest request) {
        // LLD Rule: Unique Name Check
        if (hallRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Hall configuration error: Name '" + request.name() + "' is already in use.");
        }

        CinemaHall hall = CinemaHall.builder()
                .name(request.name())
                .totalRows(request.totalRows())
                .seatPerRows(request.seatPerRows())
                .build();

        CinemaHall savedHall = hallRepository.save(hall);

        // LLD Algorithm: Bulk Seat Initialization
        generateSeatsForHall(savedHall);

        return savedHall;
    }

    @Override
    @Transactional
    public CinemaHall updateHallName(Long id, String newName) {
        CinemaHall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Update Failed: Cinema Hall with ID " + id + " not found."));
       if (hallRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            throw new RuntimeException("Conflict: A hall with the name '" + newName + "' already exists.");
        }
        hall.setName(newName);
        return hallRepository.save(hall);
    }

    @Override
    @Transactional
    public void deleteHall(Long id) {
        // LLD Guard: Business Dependency Check
        // Prevents deleting a hall if users have already booked tickets for a future show in it.
        boolean isHallInUse = showtimeRepository.existsByCinemaHallIdAndEndTimeAfter(id, LocalDateTime.now());

        if (isHallInUse) {
            throw new IllegalStateException("Deletion Denied: Hall is currently linked to active or upcoming showtimes.");
        }

        CinemaHall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));

        // CascadeType.ALL in Entity handles Seat deletion
        hallRepository.delete(hall);
    }

    @Override
    public Map<String, Object> getHallAnalytics(Long id) {
        CinemaHall hall = getHallById(id);
        long physicalSeatCount = seatRepository.countByCinemaHallId(id);

        return Map.of(
                "hallName", hall.getName(),
                "totalCapacity", (long) hall.getTotalRows() * hall.getSeatPerRows(),
                "actualSeatsInDb", physicalSeatCount,
                "isDataConsistent", physicalSeatCount == (long) hall.getTotalRows() * hall.getSeatPerRows()
        );
    }

    private void generateSeatsForHall(CinemaHall hall) {
        List<Seat> seats = new ArrayList<>();
        for (int row = 0; row < hall.getTotalRows(); row++) {
            String rowLetter = String.valueOf((char) ('A' + row));
            for (int col = 1; col <= hall.getSeatPerRows(); col++) {
                seats.add(Seat.builder()
                        .cinemaHall(hall)
                        .rowIdentifier(rowLetter)
                        .seatNumber(col)
                        // LLD Business Rule: Row A & B are Premium
                        .seatTier(row < 2 ? SeatTier.PREMIUM : SeatTier.STANDARD)
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }

    @Override
    public CinemaHall getHallById(Long id) {
        return hallRepository.findById(id).orElseThrow(() -> new RuntimeException("Hall not found"));
    }

    @Override
    public List<CinemaHall> getAllHalls() {
        return hallRepository.findAll();
    }
}