package com.expensetracker.expensetracker_mvp.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
    private UUID userId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
