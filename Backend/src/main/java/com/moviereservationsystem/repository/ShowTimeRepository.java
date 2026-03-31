package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieIdAndIsActiveTrue(Long movieId);

    // SQL Logic: Check if (NewStart < ExistingEnd) AND (NewEnd > ExistingStart)
    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.cinemaHall.id = :hallId " + "AND s.isActive = true " + "AND (:start < s.endTime AND :end > s.startTime)")
    boolean existsOverlappingShowtime(Long hallId, LocalDateTime start, LocalDateTime end);

    boolean existsByCinemaHallIdAndEndTimeAfter(Long hallId, LocalDateTime now);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.movie.id = :movieId AND s.endTime > :now")
    boolean existsByMovieIdAndEndTimeAfter(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);
}
