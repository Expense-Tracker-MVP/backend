package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.ApiResponse;
import com.expensetracker.expensetracker_mvp.dtos.ExpenseRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.ExpenseResponseDto;
import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.Expense;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.mappers.ExpenseMapper;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import com.expensetracker.expensetracker_mvp.repositories.ExpenseRepository;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expenses", description = "API for managing expenses and expense tracking")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    private User getCurrentUser() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExpenseResponseDto>>> getExpensesByUser(@PathVariable UUID userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        List<Expense> expenses = expenseRepository.findByUserIdOrderByTransactionDateDesc(userId);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(ApiResponse.success(expenseDtos));
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<ApiResponse<List<ExpenseResponseDto>>> getExpensesByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        List<Expense> expenses = expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(ApiResponse.success(expenseDtos));
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ExpenseResponseDto>>> getExpensesByCategory(
            @PathVariable UUID userId,
            @PathVariable UUID categoryId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(ApiResponse.success(expenseDtos));
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAmountByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        BigDecimal total = expenseRepository.sumAmountByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total != null ? total : BigDecimal.ZERO));
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<ApiResponse<List<ExpenseResponseDto>>> getRecentExpenses(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "30") int days) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        List<Expense> expenses = expenseRepository.findRecentExpensesByUserId(userId, sinceDate);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(ApiResponse.success(expenseDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> getExpenseById(@PathVariable UUID id) {
        User currentUser = getCurrentUser();
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()) {
            if (!expense.get().getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            return ResponseEntity.ok(ApiResponse.success(expenseMapper.toResponseDto(expense.get())));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Expense not found"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> createExpense(@RequestBody ExpenseRequestDto expenseRequestDto) {
        User currentUser = getCurrentUser();
        if (expenseRequestDto.getUserId() != null && !expenseRequestDto.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }
        try {
            Expense expense = expenseMapper.toEntity(expenseRequestDto);
            // Set user and category relationships from IDs
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Category category = categoryRepository.findById(expenseRequestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setUser(user);
            expense.setCategory(category);
            Expense savedExpense = expenseRepository.save(expense);
            ExpenseResponseDto responseDto = expenseMapper.toResponseDto(savedExpense);
            return ResponseEntity.ok(ApiResponse.success(responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Failed to create expense: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDto>> updateExpense(@PathVariable UUID id,
            @RequestBody ExpenseRequestDto expenseRequestDto) {
        User currentUser = getCurrentUser();
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            if (!expense.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            try {
                expenseMapper.updateEntityFromDto(expenseRequestDto, expense);
                // Update category relationship if provided
                if (expenseRequestDto.getCategoryId() != null) {
                    Category category = categoryRepository.findById(expenseRequestDto.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    expense.setCategory(category);
                }
                Expense updatedExpense = expenseRepository.save(expense);
                ExpenseResponseDto responseDto = expenseMapper.toResponseDto(updatedExpense);
                return ResponseEntity.ok(ApiResponse.success(responseDto));
            } catch (Exception e) {
                return ResponseEntity.status(400).body(ApiResponse.error("Failed to update expense: " + e.getMessage()));
            }
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Expense not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable UUID id) {
        User currentUser = getCurrentUser();
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            if (!expense.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            expenseRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success());
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Expense not found"));
        }
    }
}