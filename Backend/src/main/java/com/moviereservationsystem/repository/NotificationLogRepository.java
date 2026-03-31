package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog,Long> {
    List<NotificationLog> findByUserIdOrderBySentAtDesc(Long userId);
}
