package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.MovieRequest;
import com.moviereservationsystem.entity.Movie;
import com.moviereservationsystem.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ANY_ROLE_STAFF;
import static com.moviereservationsystem.constants.SecurityConstants.HAS_ROLE_ADMIN;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    /**
     * HLD: Paginated Retrieval
     * Replaces List<Movie> to support high-scale catalogs.
     * Usage: /api/v1/movies?page=0&size=10&sort=title,asc
     */
    @GetMapping
    public ResponseEntity<Page<Movie>> getAllMovies(
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    /**
     * LLD: Detail Retrieval
     * Leverages Redis 'movie_details' cache.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    /**
     * HLD: Optimized Search
     * Supports pagination for search results to avoid memory spikes.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Movie>> search(
            @RequestParam String title,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(movieService.searchMovies(title, pageable));
    }

    /**
     * System Architect Addition: Catalog Analytics
     * Provides quick stats for the Admin Dashboard.
     */
    @GetMapping("/stats")
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Map<String, Object>> getMovieStats() {
        return ResponseEntity.ok(movieService.getMovieStats());
    }

    @PostMapping
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody MovieRequest request) {
        return new ResponseEntity<>(movieService.addMovie(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    /**
     * LLD Strategy: Archival (Soft Delete)
     * This endpoint now triggers the Soft Delete logic in the service.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}