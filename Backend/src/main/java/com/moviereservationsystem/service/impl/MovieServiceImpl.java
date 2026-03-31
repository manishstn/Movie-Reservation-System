package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.MovieRequest;
import com.moviereservationsystem.entity.Movie;
import com.moviereservationsystem.repository.MovieRepository;
import com.moviereservationsystem.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public Movie addMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .durationInMins(request.durationInMins())
                .releaseDate(request.releaseDate())
                .language(request.language())
                .posterUrl(request.posterUrl())
                .build();
        return movieRepository.save(movie);
    }

    @Override
    @Transactional
    public Movie updateMovie(Long id, MovieRequest request) {
        Movie movie = getMovieById(id);

        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationInMins(request.durationInMins());
        movie.setReleaseDate(request.releaseDate());
        movie.setLanguage(request.language());
        movie.setPosterUrl(request.posterUrl());

        return movieRepository.save(movie);
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Movie does not exist.");
        }
        movieRepository.deleteById(id);
    }
}