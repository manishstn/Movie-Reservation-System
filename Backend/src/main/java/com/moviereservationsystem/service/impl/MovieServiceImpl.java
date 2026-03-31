package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.MovieRequest;
import com.moviereservationsystem.entity.Movie;
import com.moviereservationsystem.repository.MovieRepository;
import com.moviereservationsystem.repository.ShowTimeRepository;
import com.moviereservationsystem.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // For production audit logs
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ShowTimeRepository showtimeRepository;

    /**
     * LLD Fix: Key must include pageSize and Sort to avoid cache collisions.
     */
    @Cacheable(value = "movies", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString()}")
    @Override
    public Page<Movie> getAllMovies(Pageable pageable) {
        log.info("Fetching movies from DB for page: {}", pageable.getPageNumber());
        return movieRepository.findAll(pageable);
    }

    @Cacheable(value = "movie_details", key = "#id", unless = "#result == null")
    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
    }

    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public Movie addMovie(MovieRequest request) {
        log.info("Adding new movie to catalog: {}", request.title());
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .durationInMins(request.durationInMins())
                .releaseDate(request.releaseDate())
                .language(request.language())
                .posterUrl(request.posterUrl())
                .isActive(true) // LLD: Default to active
                .build();
        return movieRepository.save(movie);
    }

    /**
     * LLD Strategy: Use @CachePut to update the specific detail cache
     * while Evicting the list cache to prevent stale search results.
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true)
    }, put = {
            @CachePut(value = "movie_details", key = "#id")
    })
    @Override
    public Movie updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot update: Movie not found"));

        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationInMins(request.durationInMins());
        movie.setReleaseDate(request.releaseDate());
        movie.setLanguage(request.language());
        movie.setPosterUrl(request.posterUrl());

        return movieRepository.save(movie);
    }

    /**
     * HLD Logic: Soft Delete
     * Instead of deleting, we set isActive = false. This keeps the data
     * for historical booking records while removing it from the Storefront.
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true),
            @CacheEvict(value = "movie_details", key = "#id")
    })
    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // LLD Guard: Critical Business Rule
        if (showtimeRepository.existsByMovieIdAndEndTimeAfter(id, LocalDateTime.now())) {
            throw new IllegalStateException("Integrity Violation: Active showtimes exist for this movie.");
        }

        // Soft Delete implementation
        movie.setActive(false);
        movieRepository.save(movie);
        log.warn("Movie with ID {} has been archived (Soft Deleted)", id);
    }

    /**
     * Extra Logic: Search should only return ACTIVE movies.
     */
    @Cacheable(value = "movie_search", key = "{#title, #pageable.pageNumber}")
    @Override
    public Page<Movie> searchMovies(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(title, pageable);
    }

    @Override
    @Cacheable(value = "movie_stats", key = "'dashboard_summary'")
    public Map<String, Object> getMovieStats() {
        log.info("Generating high-level movie catalog analytics...");

        // 1. Fetch raw data from Repository
        long activeCount = movieRepository.countByIsActiveTrue();
        long archivedCount = movieRepository.countByIsActiveFalse();
        long totalCount = activeCount + archivedCount;

        // 2. Process Language Distribution
        List<Object[]> languageData = movieRepository.countMoviesByLanguage();
        Map<String, Long> languageMap = languageData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        // 3. Logic for "Recent Releases" (Last 12 months)
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        long recentReleases = movieRepository.countRecentReleases(oneYearAgo);

        // 4. Construct Architect-Grade Response Map
        return Map.of(
                "summary", Map.of(
                        "totalMovies", totalCount,
                        "activeMovies", activeCount,
                        "archivedMovies", archivedCount,
                        "recentReleases", recentReleases
                ),
                "distribution", Map.of(
                        "byLanguage", languageMap
                ),
                "generatedAt", LocalDateTime.now().toString()
        );
    }
}