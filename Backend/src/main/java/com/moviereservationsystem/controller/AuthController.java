package com.moviereservationsystem.controller;

import com.moviereservationsystem.dto.AuthRequest;
import com.moviereservationsystem.dto.AuthResponse;
import com.moviereservationsystem.dto.SignupRequest;
import com.moviereservationsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp (@Valid  @RequestBody SignupRequest signupRequest){
    log.info("Received SignUpRequest for email: {} ",signupRequest.email());
    authService.register(signupRequest);
    return ResponseEntity.ok("User registerd Successfully .please Login");
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest){
        log.info("Login attempt for user: {}",authRequest.email());
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

}
