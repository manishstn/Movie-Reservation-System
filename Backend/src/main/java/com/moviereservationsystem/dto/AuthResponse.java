package com.moviereservationsystem.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken ,String refreshToken,String tokenType ,String email , String fullName,String role) {
    public AuthResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}
