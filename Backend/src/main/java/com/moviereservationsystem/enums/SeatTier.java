package com.moviereservationsystem.enums;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public enum SeatTier {
    STANDARD(BigDecimal.ZERO),
    PREMIUM(new BigDecimal("100.00")),
    VIP(new BigDecimal("250.00"));

    private final BigDecimal additionalPrice;

    SeatTier(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
}