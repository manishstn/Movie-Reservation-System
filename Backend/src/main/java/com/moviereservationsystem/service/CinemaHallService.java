package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

public interface CinemaHallService {
    List<CinemaHall> getAllHalls();
    CinemaHall getHallById(Long id);

    @Transactional
    CinemaHall createHall(CinemaHallRequest request);

    CinemaHall updateHallName(Long id, String newName); // New
    Map<String, Object> getHallAnalytics(Long id);      // New
    void deleteHall(Long id);
}