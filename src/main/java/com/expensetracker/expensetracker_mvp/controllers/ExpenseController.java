package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.ExpenseDto;
import com.expensetracker.expensetracker_mvp.entities.Expense;
import com.expensetracker.expensetracker_mvp.mappers.ExpenseMapper;
import com.expensetracker.expensetracker_mvp.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "${frontend.url}")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseController(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable UUID id) {
        return expenseRepository.findById(id)
                .map(expenseMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDto>> getExpensesByUserId(@PathVariable UUID userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<ExpenseDto>> getExpensesByUserIdAndCategoryId(@PathVariable UUID userId,
            @PathVariable UUID categoryId) {
        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<ExpenseDto>> getExpensesByUserIdAndDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findExpensesByUserAndDateRangeOrdered(userId, startDate, endDate);
        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Double> getTotalExpensesByUserIdAndDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double total = expenseRepository.getTotalExpensesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(total != null ? total : 0.0);
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@Valid @RequestBody ExpenseDto expenseDto) {
        Expense expense = expenseMapper.toEntity(expenseDto);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        Expense savedExpense = expenseRepository.save(expense);
        ExpenseDto savedExpenseDto = expenseMapper.toDto(savedExpense);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpenseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable UUID id, @Valid @RequestBody ExpenseDto expenseDto) {
        return expenseRepository.findById(id)
                .map(existingExpense -> {
                    expenseMapper.updateEntityFromDto(expenseDto, existingExpense);
                    existingExpense.setUpdatedAt(LocalDateTime.now());
                    Expense updatedExpense = expenseRepository.save(existingExpense);
                    ExpenseDto updatedExpenseDto = expenseMapper.toDto(updatedExpense);
                    return ResponseEntity.ok(updatedExpenseDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllExpensesByUserId(@PathVariable UUID userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        expenseRepository.deleteAll(expenses);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<Void> deleteAllExpensesByUserIdAndCategoryId(@PathVariable UUID userId,
            @PathVariable UUID categoryId) {
        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        expenseRepository.deleteAll(expenses);
        return ResponseEntity.noContent().build();
    }
}
