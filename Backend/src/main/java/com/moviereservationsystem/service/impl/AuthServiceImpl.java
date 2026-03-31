package com.moviereservationsystem.service.impl;

import com.moviereservationsystem.dto.AuthRequest;
import com.moviereservationsystem.dto.AuthResponse;
import com.moviereservationsystem.dto.SignupRequest;
import com.moviereservationsystem.entity.User;
import com.moviereservationsystem.repository.RoleRepository;
import com.moviereservationsystem.repository.UserRepository;
import com.moviereservationsystem.security.JwtService;
import com.moviereservationsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(SignupRequest signupRequest) {
        if(userRepository.findByEmail(signupRequest.email()).isPresent()){
            throw  new RuntimeException("Email Already Registered");
        }

        if (userRepository.existsByContactNumber(signupRequest.contactNumber())) {
            throw new RuntimeException("Contact number already in use");
        }

        User user = new User();
        user.setEmail(signupRequest.email());
        user.setFullName(signupRequest.fullName());
        user.setContactNumber(signupRequest.contactNumber());
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        user.setRole(roleRepository.findByName("ROLE_CUSTOMER").orElseThrow( () -> new RuntimeException("Invalid Role")));
        userRepository.save(user);
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(),authRequest.password()));
        User user = userRepository.findByEmail(authRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

       String jwtToken = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        return AuthResponse.builder().accessToken(jwtToken).tokenType("Bearer").email(user.getEmail()).role(user.getRole().getName()).build();
    }

}
