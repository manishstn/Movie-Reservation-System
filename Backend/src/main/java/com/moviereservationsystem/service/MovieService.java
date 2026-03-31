package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.MovieRequest;
import com.moviereservationsystem.entity.Movie;
import java.util.List;

public interface MovieService {
    Movie addMovie(MovieRequest request);
    Movie updateMovie(Long id, MovieRequest request);
    Movie getMovieById(Long id);
    List<Movie> getAllMovies();
    List<Movie> searchMovies(String title);
    void deleteMovie(Long id);
}