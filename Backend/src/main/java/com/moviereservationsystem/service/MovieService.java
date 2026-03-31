package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.MovieRequest;
import com.moviereservationsystem.entity.Movie;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface MovieService {
    @Cacheable(value = "movies", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString()}")
    Page<Movie> getAllMovies(Pageable pageable);

    @Cacheable(value = "movie_details", key = "#id", unless = "#result == null")
    Movie getMovieById(Long id);

    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    Movie addMovie(MovieRequest request);

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true)
    }, put = {
            @CachePut(value = "movie_details", key = "#id")
    })
    Movie updateMovie(Long id, MovieRequest request);

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "movies", allEntries = true),
            @CacheEvict(value = "movie_details", key = "#id")
    })
    void deleteMovie(Long id);

    @Cacheable(value = "movie_search", key = "{#title, #pageable.pageNumber}")
    Page<Movie> searchMovies(String title, Pageable pageable);

    Map<String, Object> getMovieStats(); // New for Admin Dashboard
}