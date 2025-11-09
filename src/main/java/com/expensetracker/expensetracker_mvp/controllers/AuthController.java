package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.UserResponseDto;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.mappers.UserMapper;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;
import com.expensetracker.expensetracker_mvp.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "API for user authentication and account management")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        User user = (User) authentication.getPrincipal();
        UserResponseDto userResponse = userMapper.toResponseDto(user);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("user", userResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request);
        log.info("Refresh token received: {}", refreshToken != null ? "present" : "absent");
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No refresh token provided"));
        }

        try {
            Jwt jwt = jwtService.validateRefreshToken(refreshToken);
            UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            // Update refresh token cookie
            Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); // Set to true in production
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(refreshCookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAccessToken);
            responseBody.put("user", userMapper.toResponseDto(user));

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear refresh token cookie
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // Delete cookie
        response.addCookie(refreshCookie);

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteCurrentUser(Authentication authentication, HttpServletResponse response) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        try {
            User user = (User) authentication.getPrincipal();
            
            // Log the deletion attempt
            log.info("User deletion requested for user ID: {}, email: {}", user.getId(), user.getEmail());
            
            // Delete the user from the database
            userRepository.deleteById(user.getId());
            
            // Clear the security context
            SecurityContextHolder.clearContext();
            
            // Clear refresh token cookie
            Cookie refreshCookie = new Cookie("refreshToken", "");
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(0); // Delete cookie
            response.addCookie(refreshCookie);
            
            log.info("User account deleted successfully for user ID: {}", user.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "User account deleted successfully",
                "deletedUserId", user.getId().toString()
            ));
            
        } catch (Exception e) {
            log.error("Error deleting user account: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete user account"));
        }
    }

    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}