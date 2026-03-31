package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.CinemaHallRequest;
import com.moviereservationsystem.entity.CinemaHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;

public interface CinemaHallService {
    // HLD: Use Page instead of List for scalability
    Page<CinemaHall> getAllHalls(Pageable pageable);

    CinemaHall getHallById(Long id);
    CinemaHall createHall(CinemaHallRequest request);
    CinemaHall updateHallName(Long id, String newName);

    // Extra: Bulk Update logic
    void updateRowTier(Long hallId, String rowIdentifier, String tier);

    Map<String, Object> getHallAnalytics(Long id);
    void deleteHall(Long id);
}