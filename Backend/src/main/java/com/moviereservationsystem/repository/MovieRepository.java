package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByLanguageIgnoreCase(String language);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.movie.id = :movieId AND s.endTime > :now")
    boolean hasActiveShowtimes(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    Page<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title, Pageable pageable);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.movie.id = :movieId AND s.endTime > :now")
    boolean existsActiveShowtimes(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);

    long countByIsActiveTrue();

    long countByIsActiveFalse();

    @Query("SELECT m.language, COUNT(m) FROM Movie m GROUP BY m.language")
    List<Object[]> countMoviesByLanguage();

    @Query("SELECT COUNT(m) FROM Movie m WHERE m.releaseDate >= :startDate")
    long countRecentReleases(@Param("startDate") LocalDate startDate);
}
