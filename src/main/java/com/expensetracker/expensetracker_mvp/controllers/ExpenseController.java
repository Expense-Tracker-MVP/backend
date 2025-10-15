package com.expensetracker.expensetracker_mvp.controllers;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByUser(@PathVariable UUID userId) {
        List<Expense> expenses = expenseRepository.findByUserIdOrderByTransactionDateDesc(userId);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Expense> expenses = expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByCategory(
            @PathVariable UUID userId, 
            @PathVariable Integer categoryId) {
        
        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getTotalAmountByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal total = expenseRepository.sumAmountByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<ExpenseResponseDto>> getRecentExpenses(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        List<Expense> expenses = expenseRepository.findRecentExpensesByUserId(userId, sinceDate);
        List<ExpenseResponseDto> expenseDtos = expenseMapper.toResponseDtoList(expenses);
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable UUID id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        return expense.map(exp -> ResponseEntity.ok(expenseMapper.toResponseDto(exp)))
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(@RequestBody ExpenseRequestDto expenseRequestDto) {
        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        
        // Set user and category relationships from IDs
        User user = userRepository.findById(expenseRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(expenseRequestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        expense.setUser(user);
        expense.setCategory(category);
        
        Expense savedExpense = expenseRepository.save(expense);
        ExpenseResponseDto responseDto = expenseMapper.toResponseDto(savedExpense);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable UUID id, @RequestBody ExpenseRequestDto expenseRequestDto) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            expenseMapper.updateEntityFromDto(expenseRequestDto, expense);
            
            // Update user and category relationships if provided
            if (expenseRequestDto.getUserId() != null) {
                User user = userRepository.findById(expenseRequestDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                expense.setUser(user);
            }
            
            if (expenseRequestDto.getCategoryId() != null) {
                Category category = categoryRepository.findById(expenseRequestDto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                expense.setCategory(category);
            }
            
            Expense updatedExpense = expenseRepository.save(expense);
            ExpenseResponseDto responseDto = expenseMapper.toResponseDto(updatedExpense);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}