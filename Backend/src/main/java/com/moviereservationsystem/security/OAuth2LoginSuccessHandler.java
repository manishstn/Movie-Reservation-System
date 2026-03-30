package com.moviereservationsystem.security;

import com.moviereservationsystem.entity.User;
import com.moviereservationsystem.repository.UserRepository;
import com.moviereservationsystem.repository.RoleRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 1. Find or Register user in our DB
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            // Assign default role (Make sure 'ROLE_USER' exists in your roles table)
            newUser.setRole(roleRepository.findByName("ROLE_USER").orElse(null));
            return userRepository.save(newUser);
        });

        // 2. Generate JWT
        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        // 3. Redirect to React Frontend (Change this URL to your React app's URL)
        // We pass the token as a query param so React can save it to localStorage
        String targetUrl = "http://localhost:3000/oauth2/redirect?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}