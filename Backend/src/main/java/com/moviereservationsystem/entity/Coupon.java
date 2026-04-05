package com.moviereservationsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g., "FIRST50"

    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;

    private LocalDateTime expiryDate;
    private Integer usageLimit;
    private Integer currentUsage;

    @Version
    private Integer version;

    private boolean isActive;
}