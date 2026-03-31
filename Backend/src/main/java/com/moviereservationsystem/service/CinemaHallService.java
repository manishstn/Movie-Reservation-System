package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import java.util.List;

public interface CinemaHallService {
    CinemaHall createHall(CinemaHallRequest request);
    List<CinemaHall> getAllHalls();
    CinemaHall getHallById(Long id);
    void deleteHall(Long id);
}