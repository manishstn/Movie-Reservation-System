package com.moviereservationsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^\\d{10}$", message = "Contact number must be exactly 10 digits")
        String contactNumber,

        @NotBlank(message = "Full name is required")
        String fullName
) {}