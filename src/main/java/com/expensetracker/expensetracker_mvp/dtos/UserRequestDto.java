package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating users
 * Contains only the fields that clients should be able to modify
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private String email;
    private String provider;
    private String providerId;
    private String providerUserId;
    private String displayName;
}