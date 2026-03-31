package com.moviereservationsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name="movies")
@AllArgsConstructor
@NoArgsConstructor
@Data@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name="duration_in_mins")
    private Integer durationInMins;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private String language;

    @Column(name ="poster_url")
    private String posterUrl;

    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at" ,nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;
}
