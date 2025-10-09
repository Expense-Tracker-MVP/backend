package com.expensetracker.expensetracker_mvp.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ExpenseDto {
    private UUID id;
    
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
    
    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;
    
    @NotBlank(message = "Description cannot be empty or whitespace only")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits before decimal and 2 after")
    private BigDecimal amount;
    
    @NotNull(message = "Expense date cannot be null")
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate expenseDate;
    
    @Size(max = 100, message = "Source cannot exceed 100 characters")
    private String source;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
