package com.expensetracker.expensetracker_mvp.services;

import com.expensetracker.expensetracker_mvp.entities.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {

    private final JwtEncoder accessJwtEncoder;
    private final JwtEncoder refreshJwtEncoder;
    private final JwtDecoder accessJwtDecoder;
    private final JwtDecoder refreshJwtDecoder;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtService(
            @Value("${app.jwt.access-token-secret}") String accessSecret,
            @Value("${app.jwt.refresh-token-secret}") String refreshSecret,
            @Value("${app.jwt.access-token-expiration-minutes}") long accessTokenExpirationMinutes,
            @Value("${app.jwt.refresh-token-expiration-days}") long refreshTokenExpirationDays) {

        // Create access token encoder/decoder
        var accessKey = new SecretKeySpec(accessSecret.getBytes(), "HmacSHA256");
        this.accessJwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(accessKey));
        this.accessJwtDecoder = NimbusJwtDecoder.withSecretKey(accessKey).build();

        // Create refresh token encoder/decoder
        var refreshKey = new SecretKeySpec(refreshSecret.getBytes(), "HmacSHA256");
        this.refreshJwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(refreshKey));
        this.refreshJwtDecoder = NimbusJwtDecoder.withSecretKey(refreshKey).build();

        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("expense-tracker")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(user.getId().toString())
                .claim("userId", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("provider", user.getProvider())
                .claim("type", "access")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return accessJwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpirationDays, ChronoUnit.DAYS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("expense-tracker")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(user.getId().toString())
                .claim("userId", user.getId().toString())
                .claim("type", "refresh")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return refreshJwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public Jwt validateAccessToken(String token) {
        try {
            return accessJwtDecoder.decode(token);
        } catch (JwtException e) {
            log.debug("Invalid access token: {}", e.getMessage());
            throw new RuntimeException("Invalid access token", e);
        }
    }

    public Jwt validateRefreshToken(String token) {
        try {
            Jwt jwt = refreshJwtDecoder.decode(token);

            if (!"refresh".equals(jwt.getClaimAsString("type"))) {
                throw new RuntimeException("Invalid token type");
            }

            return jwt;
        } catch (JwtException e) {
            log.debug("Invalid refresh token: {}", e.getMessage());
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

    public UUID getUserIdFromToken(String token) {
        Jwt jwt = validateAccessToken(token);
        return UUID.fromString(jwt.getClaimAsString("userId"));
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwt jwt = validateAccessToken(token);
            return jwt.getExpiresAt().isBefore(Instant.now());
        } catch (Exception e) {
            return true;
        }
    }
}