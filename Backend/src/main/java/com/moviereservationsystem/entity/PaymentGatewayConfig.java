package com.moviereservationsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_gateway_config")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class PaymentGatewayConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gateway_name", nullable = false, unique = true)
    private String gatewayName;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "api_secret", nullable = false)
    private String apiSecret;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
