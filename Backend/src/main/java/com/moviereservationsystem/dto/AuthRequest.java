package com.moviereservationsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank
        @Email(message = "Invalid Email Format")
        String email,
        String password) {

}
