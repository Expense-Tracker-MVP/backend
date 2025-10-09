package com.expensetracker.expensetracker_mvp.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private UUID id;
    
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
    
    @NotBlank(message = "Category name cannot be empty or whitespace only")
    @Size(min = 1, max = 50, message = "Category name must be between 1 and 50 characters")
    private String name;
    
    private LocalDateTime createdAt;
}
