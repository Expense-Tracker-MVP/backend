package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for category data
 * Used within other DTOs like ExpenseResponseDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String color;
}