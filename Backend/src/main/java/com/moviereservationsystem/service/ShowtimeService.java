package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.ShowtimeRequest;
import com.moviereservationsystem.entity.ShowTimeSeat;
import com.moviereservationsystem.entity.Showtime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeService {
    List<Showtime> getShowtimesByMovie(Long movieId);

    Showtime createShowtime( ShowtimeRequest request);

     List<ShowTimeSeat> getSeatingPlan(Long id);
}
