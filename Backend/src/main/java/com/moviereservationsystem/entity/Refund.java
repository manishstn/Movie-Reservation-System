package com.moviereservationsystem.entity;

import com.moviereservationsystem.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private BigDecimal refundAmount;

    @Column(name = "refund_transaction_id", unique = true)
    private String refundTransactionId;

    @Enumerated(EnumType.STRING)
    private RefundStatus status; // PENDING, COMPLETED, FAILED

    private String reason;

    @CreationTimestamp
    private LocalDateTime initiatedAt;
}