package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.entities.Expense;
import com.expensetracker.expensetracker_mvp.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "${app.frontend.url}")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable UUID userId) {
        List<Expense> expenses = expenseRepository.findByUserIdOrderByTransactionDateDesc(userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Expense> expenses = expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(
            @PathVariable UUID userId, 
            @PathVariable Integer categoryId) {
        
        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        return ResponseEntity.ok(expenses);
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
    public ResponseEntity<List<Expense>> getRecentExpenses(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDate sinceDate = LocalDate.now().minusDays(days);
        List<Expense> expenses = expenseRepository.findRecentExpensesByUserId(userId, sinceDate);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable UUID id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        return expense.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(savedExpense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable UUID id, @RequestBody Expense expenseDetails) {
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            expense.setCategoryId(expenseDetails.getCategoryId());
            expense.setDescription(expenseDetails.getDescription());
            expense.setAmount(expenseDetails.getAmount());
            expense.setCurrency(expenseDetails.getCurrency());
            expense.setTransactionDate(expenseDetails.getTransactionDate());
            expense.setSource(expenseDetails.getSource());
            
            Expense updatedExpense = expenseRepository.save(expense);
            return ResponseEntity.ok(updatedExpense);
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