package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByCinemaHallId(Long hallId);
    List<Seat> findByCinemaHallIdOrderByRowIdentifierAscSeatNumberAsc(Long hallId);
    void deleteByCinemaHallId(Long hallId);
    long countByCinemaHallId(Long hallId);
    List<Seat> findByCinemaHallIdAndRowIdentifier(Long hallId, String rowIdentifier);
}
