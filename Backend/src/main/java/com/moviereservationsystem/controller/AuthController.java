package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.AuthRequest;
import com.moviereservationsystem.dto.AuthResponse;
import com.moviereservationsystem.dto.SignupRequest;
import com.moviereservationsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Received SignUpRequest for email: {}", signupRequest.email());
        authService.register(signupRequest);
        return ResponseEntity.ok("User registered successfully. Please login.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login attempt for user: {}", authRequest.email());
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    /**
     * SDE-2 Feature: Token Rotation
     * Exchange an expired access token for a new one using a valid Refresh Token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam("token") String refreshToken) {
        log.info("Token refresh requested");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    /**
     * SDE-2 Feature: Logout Revocation
     * Explicitly invalidates the refresh token in the database.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("token") String refreshToken) {
        log.info("Logout requested for refresh token");
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}