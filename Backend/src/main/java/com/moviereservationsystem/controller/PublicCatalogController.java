package com.moviereservationsystem.controller;

import com.moviereservationsystem.entity.Movie;
import com.moviereservationsystem.entity.CinemaHall;
import com.moviereservationsystem.service.MovieService;
import com.moviereservationsystem.service.CinemaHallService;
import com.moviereservationsystem.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final MovieService movieService;
    private final CinemaHallService hallService;
    private final ShowtimeService showtimeService;

    /**
     * HLD: Browsing Catalog
     * Hits Redis 'movies' cache.
     */
    @GetMapping("/movies")
    public ResponseEntity<Page<Movie>> getActiveMovies(
            @PageableDefault(size = 12, sort = "releaseDate") Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    /**
     * LLD: Movie Deep-Dive
     * Fetches metadata + all upcoming showtimes for this specific movie.
     */
    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovieDetails(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    /**
     * HLD: Search Functionality
     * Standardized search for customers looking for specific titles.
     */
    @GetMapping("/movies/search")
    public ResponseEntity<Page<Movie>> searchMovies(
            @RequestParam String title,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(movieService.searchMovies(title, pageable));
    }

    /**
     * Extra Logic: Cinema Infrastructure Overview
     * Customers often want to see which halls are available in a theater.
     */
    @GetMapping("/halls")
    public ResponseEntity<Page<CinemaHall>> getHalls(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(hallService.getAllHalls(pageable));
    }

    /**
     * Critical LLD: Live Schedule
     * Returns showtimes for a specific date (defaults to today in Service logic).
     */
        @GetMapping("/schedule")
        public ResponseEntity<?> getTodaySchedule(
                @RequestParam(required = false) Long movieId,
                @RequestParam(required = false) Long hallId) {
            // Logic: Returns combined view of Hall + Movie + StartTime
            return ResponseEntity.ok(showtimeService.getPublicSchedule(movieId, hallId));
        }
}