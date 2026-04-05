package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.AuthRequest;
import com.moviereservationsystem.dto.AuthResponse;
import com.moviereservationsystem.dto.SignupRequest;
import com.moviereservationsystem.entity.RefreshToken;
import com.moviereservationsystem.entity.User;
import com.moviereservationsystem.repository.RefreshTokenRepository;
import com.moviereservationsystem.repository.RoleRepository;
import com.moviereservationsystem.repository.UserRepository;
import com.moviereservationsystem.security.JwtService;
import com.moviereservationsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void register(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.email()).isPresent()) {
            throw new RuntimeException("Email Already Registered");
        }

        if (userRepository.existsByContactNumber(signupRequest.contactNumber())) {
            throw new RuntimeException("Contact number already in use");
        }

        User user = new User();
        user.setEmail(signupRequest.email());
        user.setFullName(signupRequest.fullName());
        user.setContactNumber(signupRequest.contactNumber());
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        user.setRole(roleRepository.findByName("ROLE_CUSTOMER").orElseThrow(() -> new RuntimeException("Invalid Role")));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
        );

        User user = userRepository.findByEmail(authRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Generate Access Token (Short-lived)
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        // 2. Generate and Persist Refresh Token (Long-lived) [cite: 25, 50]
        String refreshTokenStr = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7)) // Requirement: 7 days
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr) // Now returning both
                .tokenType("Bearer")
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }

    @Transactional
    @Override
    public AuthResponse refreshToken(String tokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // Validation: Check expiry and revocation
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            throw new RuntimeException("Refresh token expired or revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(tokenStr)
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    @Override
    public void logout(String tokenStr) {
        // Requirement 6: Logout endpoint revokes the refresh token
        refreshTokenRepository.findByToken(tokenStr).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

}
