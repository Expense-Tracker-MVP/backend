package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for full user information for now
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private UUID id;
    private String email;
    private String provider;
    private String providerId;
    private String providerUserId;
    private String displayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}