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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CinemaHallServiceImpl implements CinemaHallService {

    private final CinemaHallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final ShowTimeRepository showtimeRepository;

    @Override
    @Transactional
    @CacheEvict(value = "halls", allEntries = true)
    public CinemaHall createHall(CinemaHallRequest request) {
        if (hallRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Conflict: Hall name '" + request.name() + "' already exists.");
        }

        CinemaHall hall = CinemaHall.builder()
                .name(request.name())
                .totalRows(request.totalRows())
                .seatPerRows(request.seatPerRows())
                .build();

        CinemaHall savedHall = hallRepository.save(hall);
        generateSeatsForHall(savedHall);

        log.info("Hall created with ID: {} and seats generated.", savedHall.getId());
        return savedHall;
    }

    @Override
    @Cacheable(value = "halls", key = "{#pageable.pageNumber, #pageable.pageSize}")
    public Page<CinemaHall> getAllHalls(Pageable pageable) {
        return hallRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "hall_details", key = "#id")
    public CinemaHall getHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema Hall not found with ID: " + id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "halls", allEntries = true),
            @CacheEvict(value = "hall_details", key = "#id")
    })
    public CinemaHall updateHallName(Long id, String newName) {
        CinemaHall hall = getHallById(id);
        if (hallRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            throw new RuntimeException("Conflict: Name '" + newName + "' is already in use.");
        }
        hall.setName(newName);
        return hallRepository.save(hall);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "halls", allEntries = true),
            @CacheEvict(value = "hall_details", key = "#hallId")
    })
    public void updateRowTier(Long hallId, String rowIdentifier, String tier) {
        log.info("Bulk updating tier to {} for Hall {} Row {}", tier, hallId, rowIdentifier);
        List<Seat> seats = seatRepository.findByCinemaHallIdAndRowIdentifier(hallId, rowIdentifier);

        if (seats.isEmpty()) {
            throw new RuntimeException("No seats found for the specified hall and row.");
        }

        SeatTier targetTier = SeatTier.valueOf(tier.toUpperCase());
        seats.forEach(seat -> seat.setSeatTier(targetTier));
        seatRepository.saveAll(seats);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "halls", allEntries = true),
            @CacheEvict(value = "hall_details", key = "#id")
    })
    public void deleteHall(Long id) {
        // LLD Guard: Prevent deletion if active showtimes exist
        boolean isHallInUse = showtimeRepository.existsByCinemaHallIdAndEndTimeAfter(id, LocalDateTime.now());
        if (isHallInUse) {
            throw new IllegalStateException("Integrity Violation: Active or upcoming showtimes are linked to this hall.");
        }

        CinemaHall hall = getHallById(id);
        hallRepository.delete(hall);
        log.warn("Cinema Hall ID {} deleted successfully.", id);
    }

    @Override
    public Map<String, Object> getHallAnalytics(Long id) {
        CinemaHall hall = getHallById(id);
        long actualSeatCount = seatRepository.countByCinemaHallId(id);

        return Map.of(
                "hallName", hall.getName(),
                "totalCapacity", (long) hall.getTotalRows() * hall.getSeatPerRows(),
                "actualSeatsInDb", actualSeatCount,
                "isDataConsistent", actualSeatCount == (long) hall.getTotalRows() * hall.getSeatPerRows(),
                "timestamp", LocalDateTime.now()
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
                        .seatTier(row < 2 ? SeatTier.PREMIUM : SeatTier.STANDARD)
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }
}