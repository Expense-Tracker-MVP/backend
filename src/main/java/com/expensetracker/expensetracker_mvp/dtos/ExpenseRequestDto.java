package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating or updating expenses
 * Contains only the fields that clients should be able to modify
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequestDto {
    private UUID userId;
    private Integer categoryId;
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDate transactionDate;
    private String source;
}