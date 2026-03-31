package com.moviereservationsystem.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank(message = "Title is required")
        String title,

        @Size(max = 1000, message = "Description too long")
        String description,

        @NotNull @Min(1)
        Integer durationInMins,

        @NotNull
        LocalDate releaseDate,

        @NotBlank(message = "Language is required")
        String language,

        @URL(message = "Invalid poster URL")
        String posterUrl
) {}