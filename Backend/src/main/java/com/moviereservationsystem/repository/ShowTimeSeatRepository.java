package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.ShowTimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowTimeSeatRepository extends JpaRepository<ShowTimeSeat, Long> {
    List<ShowTimeSeat> findByShowtimeId(Long showtimeId);

    List<ShowTimeSeat> findByShowtimeIdOrderBySeatRowIdentifierAscSeatSeatNumberAsc(Long showtimeId);

    @Query("SELECT COUNT(sts) > 0 FROM ShowTimeSeat sts " +
            "WHERE sts.seat.id = :seatId " +
            "AND sts.showtime.isActive = true " +
            "AND sts.showtime.endTime > :now")
    boolean isSeatLinkedToActiveShowtime(@Param("seatId") Long seatId,
                                         @Param("now") LocalDateTime now);

    @Query("SELECT sts FROM ShowTimeSeat sts WHERE sts.showtime.id = :showId AND sts.seat.id IN :seatIds")
    List<ShowTimeSeat> findSelectedSeats(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds);
}