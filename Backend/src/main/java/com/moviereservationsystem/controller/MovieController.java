package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.MovieRequest;
import com.moviereservationsystem.entity.Movie;
import com.moviereservationsystem.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moviereservationsystem.constants.SecurityConstants.HAS_ANY_ROLE_STAFF;
import static com.moviereservationsystem.constants.SecurityConstants.HAS_ROLE_ADMIN;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // Public: Customers can browse movies
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Movie>> search(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMovies(title));
    }

    // Secured: Only Staff/Admin can manage movies
    @PostMapping
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody MovieRequest request) {
        return new ResponseEntity<>(movieService.addMovie(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize(HAS_ANY_ROLE_STAFF)
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(HAS_ROLE_ADMIN)
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}