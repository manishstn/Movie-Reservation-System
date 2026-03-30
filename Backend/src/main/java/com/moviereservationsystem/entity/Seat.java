package com.moviereservationsystem.entity;

import com.moviereservationsystem.enums.SeatTier;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="seat")
@Data @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hall_id",nullable = false)
    private CinemaHall cinemaHall;

    @Column(name="row_identifier" , nullable = false ,length = 5)
    private String rowIdentifier;

    @Column(name="seat_number",nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private SeatTier seatTier;
}
