package com.moviereservationsystem.entity;

import com.moviereservationsystem.enums.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private Set<Genre> genres = new HashSet<>();

    @CreationTimestamp @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Fixed from LocalDate [cite: 43]
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
