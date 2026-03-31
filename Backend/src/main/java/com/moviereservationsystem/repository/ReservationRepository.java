package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.Reservation;
import com.moviereservationsystem.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Reservation> findByReservationStatusAndExpiresAtBefore(
            ReservationStatus status, LocalDateTime now);
}
