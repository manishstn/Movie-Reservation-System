package com.moviereservationsystem.utils;

import com.moviereservationsystem.entity.User;
import com.moviereservationsystem.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private static CustomUserDetails getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails principal) {
            return principal;
        }
        throw new RuntimeException("No authenticated session found");
    }

    public static User getCurrentUser() {
        return getPrincipal().getUser();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUserFullName() {
        return getCurrentUser().getFullName();
    }

    public static String getCurrentUserRole() {
        // Matches your Role entity mapping
        return getCurrentUser().getRole().getName();
    }
}