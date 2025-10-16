package com.expensetracker.expensetracker_mvp.config;

import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;
import com.expensetracker.expensetracker_mvp.services.CategoryService;
import com.expensetracker.expensetracker_mvp.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user information from OAuth2User
        String email = oauth2User.getAttribute("email");
        String providerId = oauth2User.getAttribute("sub");
        String displayName = oauth2User.getAttribute("name");
        String provider = "google"; // we are only handling Google for now

        if (email == null || email.isEmpty()) {
            log.error("Email not found in OAuth2 response");
            response.sendRedirect(frontendUrl + "/login?error=email_missing");
            return;
        }

        // Find or create user
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // Check if user exists with same email but different provider
                    return userRepository.findByEmail(email)
                            .orElseGet(() -> createNewUser(provider, email, providerId, displayName));
                });

        // Update user info if needed
        if (!user.getEmail().equals(email)) {
            user.setEmail(email);
            userRepository.save(user);
        }

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Set refresh token as httpOnly cookie
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Set to true in production with HTTPS
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(refreshCookie);

        // Redirect to frontend with access token as URL parameter
        String redirectUrl = String.format(
                "%s/auth/callback?token=%s&user=%s",
                frontendUrl,
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));

        log.info("OAuth2 login successful for user: {}", user.getEmail());
        response.sendRedirect(redirectUrl);
    }

    private User createNewUser(String provider, String email, String providerId, String displayName) {
        log.info("Creating new user for provider: {}, email: {}, displayName: {}", provider, email, displayName);

        User user = User.builder()
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .providerUserId(providerId) // For now, same as providerId
                .displayName(displayName)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Create default "All" category for the new user
        categoryService.createDefaultCategoryForUser(savedUser);

        return savedUser;
    }
}