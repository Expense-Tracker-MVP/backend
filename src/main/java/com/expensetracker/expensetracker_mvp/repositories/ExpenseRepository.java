package com.expensetracker.expensetracker_mvp.repositories;

import com.expensetracker.expensetracker_mvp.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    
    List<Expense> findByUserId(UUID userId);
    
    List<Expense> findByUserIdOrderByTransactionDateDesc(UUID userId);
    
    List<Expense> findByUserIdAndTransactionDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByUserIdAndCategoryId(UUID userId, UUID categoryId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND e.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndDateRange(@Param("userId") UUID userId, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.transactionDate >= :date ORDER BY e.transactionDate DESC")
    List<Expense> findRecentExpensesByUserId(@Param("userId") UUID userId, @Param("date") LocalDate date);
}