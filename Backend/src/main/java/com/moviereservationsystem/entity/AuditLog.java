package com.moviereservationsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., "UPDATE_PRICE", "DELETE_MOVIE"

    @Column(nullable = false)
    private String entityName; // e.g., "Showtime"

    @Column(nullable = false)
    private String entityId;

    @Column(length = 2000)
    private String oldValues; // JSON format

    @Column(length = 2000)
    private String newValues; // JSON format

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User adminUser;

    @CreationTimestamp
    private LocalDateTime timestamp;
}