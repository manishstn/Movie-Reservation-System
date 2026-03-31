package com.moviereservationsystem.service;

import com.moviereservationsystem.dto.AuthRequest;
import com.moviereservationsystem.dto.AuthResponse;
import com.moviereservationsystem.dto.SignupRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    void register(SignupRequest signupRequest);

     AuthResponse authenticate(AuthRequest authRequest);
}
