package com.moviereservationsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketNumber; // e.g., TIX-2026-ABCD-123

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // Snapshot fields: Essential for historical auditing
    @Column(nullable = false)
    private String movieTitleSnapshot;

    @Column(nullable = false)
    private String hallNameSnapshot;

    @Column(nullable = false)
    private String seatDetailsSnapshot; // e.g., "A12, A13"

    @Column(name = "qr_code_data")
    private String qrCodeData;

    @CreationTimestamp
    private LocalDateTime issuedAt;
}
