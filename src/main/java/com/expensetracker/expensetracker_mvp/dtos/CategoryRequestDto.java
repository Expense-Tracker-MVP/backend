package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating or updating categories
 * Contains only the fields that clients should be able to modify
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    private UUID userId;
    private String name;
    private String description;
    private String color;
}