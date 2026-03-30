package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeRepository extends JpaRepository<Showtime,Long> {
}
