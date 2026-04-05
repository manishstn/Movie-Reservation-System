package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.AuthRequest;
import com.moviereservationsystem.dto.AuthResponse;
import com.moviereservationsystem.dto.SignupRequest;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {
    void register(SignupRequest signupRequest);

    AuthResponse authenticate(AuthRequest authRequest);

    @Transactional
    AuthResponse refreshToken(String tokenStr);

    @Transactional
    void logout(String tokenStr);
}
