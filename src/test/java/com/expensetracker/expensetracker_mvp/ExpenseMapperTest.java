package com.expensetracker.expensetracker_mvp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expensetracker_mvp.entities.Expense;
import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.dtos.ExpenseDto;
import com.expensetracker.expensetracker_mvp.mappers.ExpenseMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ExpenseMapperTest {

    @Autowired
    private ExpenseMapper expenseMapper;

    @Test
    void testToDto() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String description = "Grocery shopping";
        BigDecimal amount = new BigDecimal("45.67");
        LocalDate expenseDate = LocalDate.now();
        String source = "manual";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("Groceries")
                .build();

        Expense expense = Expense.builder()
                .id(id)
                .user(user)
                .category(category)
                .description(description)
                .amount(amount)
                .expenseDate(expenseDate)
                .source(source)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        ExpenseDto expenseDto = expenseMapper.toDto(expense);

        assertNotNull(expenseDto);
        assertEquals(id, expenseDto.getId());
        assertEquals(userId, expenseDto.getUserId());
        assertEquals(categoryId, expenseDto.getCategoryId());
        assertEquals(description, expenseDto.getDescription());
        assertEquals(amount, expenseDto.getAmount());
        assertEquals(expenseDate, expenseDto.getExpenseDate());
        assertEquals(source, expenseDto.getSource());
        assertEquals(createdAt, expenseDto.getCreatedAt());
        assertEquals(updatedAt, expenseDto.getUpdatedAt());
    }

    @Test
    void testToEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String description = "Gas station";
        BigDecimal amount = new BigDecimal("35.50");
        LocalDate expenseDate = LocalDate.now();
        String source = "OCR";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ExpenseDto expenseDto = ExpenseDto.builder()
                .id(id)
                .userId(userId)
                .categoryId(categoryId)
                .description(description)
                .amount(amount)
                .expenseDate(expenseDate)
                .source(source)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        Expense expense = expenseMapper.toEntity(expenseDto);

        assertNotNull(expense);
        assertEquals(id, expense.getId());
        assertEquals(description, expense.getDescription());
        assertEquals(amount, expense.getAmount());
        assertEquals(expenseDate, expense.getExpenseDate());
        assertEquals(source, expense.getSource());
        assertEquals(createdAt, expense.getCreatedAt());
        assertEquals(updatedAt, expense.getUpdatedAt());
        assertNull(expense.getUser()); // User should be ignored as per mapping
        assertNull(expense.getCategory()); // Category should be ignored as per mapping
    }

    @Test
    void testUpdateEntityFromDto() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String originalDescription = "Original description";
        String updatedDescription = "Updated description";
        BigDecimal originalAmount = new BigDecimal("25.00");
        BigDecimal updatedAmount = new BigDecimal("30.00");
        LocalDate originalDate = LocalDate.now();
        LocalDate updatedDate = LocalDate.now().plusDays(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("Transportation")
                .build();

        Expense existingExpense = Expense.builder()
                .id(id)
                .user(user)
                .category(category)
                .description(originalDescription)
                .amount(originalAmount)
                .expenseDate(originalDate)
                .source("manual")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        ExpenseDto updateDto = ExpenseDto.builder()
                .id(id)
                .userId(userId)
                .categoryId(categoryId)
                .description(updatedDescription)
                .amount(updatedAmount)
                .expenseDate(updatedDate)
                .source("OCR")
                .build();

        expenseMapper.updateEntityFromDto(updateDto, existingExpense);

        assertEquals(id, existingExpense.getId());
        assertEquals(updatedDescription, existingExpense.getDescription());
        assertEquals(updatedAmount, existingExpense.getAmount());
        assertEquals(updatedDate, existingExpense.getExpenseDate());
        assertEquals("OCR", existingExpense.getSource());
        assertNull(existingExpense.getCreatedAt()); // createdAt is null in DTO, so it gets overwritten
        assertNull(existingExpense.getUpdatedAt()); // updatedAt is null in DTO, so it gets overwritten
        assertEquals(user, existingExpense.getUser()); // User should remain unchanged
        assertEquals(category, existingExpense.getCategory()); // Category should remain unchanged
    }

    @Test
    void testUpdateEntityFromDtoWithNullValues() {
        UUID id = UUID.randomUUID();
        String description = "Test expense";
        BigDecimal amount = new BigDecimal("50.00");
        LocalDate expenseDate = LocalDate.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();

        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Test Category")
                .build();

        Expense existingExpense = Expense.builder()
                .id(id)
                .user(user)
                .category(category)
                .description(description)
                .amount(amount)
                .expenseDate(expenseDate)
                .source("manual")
                .build();

        ExpenseDto updateDto = ExpenseDto.builder()
                .id(id)
                .description(null)
                .amount(null)
                .build();

        expenseMapper.updateEntityFromDto(updateDto, existingExpense);

        assertEquals(id, existingExpense.getId());
        assertNull(existingExpense.getDescription()); // MapStruct overwrites with null values
        assertNull(existingExpense.getAmount()); // MapStruct overwrites with null values
        assertNull(existingExpense.getExpenseDate()); // expenseDate is null in DTO, so it gets overwritten
        assertEquals(user, existingExpense.getUser());
        assertEquals(category, existingExpense.getCategory());
    }

    @Test
    void testToDtoWithNullExpense() {
        ExpenseDto expenseDto = expenseMapper.toDto(null);
        assertNull(expenseDto);
    }

    @Test
    void testToEntityWithNullDto() {
        Expense expense = expenseMapper.toEntity(null);
        assertNull(expense);
    }

    @Test
    void testUpdateEntityFromDtoWithNullDto() {
        Expense existingExpense = Expense.builder()
                .description("Test expense")
                .amount(new BigDecimal("25.00"))
                .build();

        String originalDescription = existingExpense.getDescription();
        BigDecimal originalAmount = existingExpense.getAmount();

        assertDoesNotThrow(() -> expenseMapper.updateEntityFromDto(null, existingExpense));

        assertEquals(originalDescription, existingExpense.getDescription());
        assertEquals(originalAmount, existingExpense.getAmount());
    }

    @Test
    void testUpdateEntityFromDtoWithNullTarget() {
        ExpenseDto updateDto = ExpenseDto.builder()
                .description("Test expense")
                .amount(new BigDecimal("25.00"))
                .build();

        assertThrows(NullPointerException.class, () -> {
            expenseMapper.updateEntityFromDto(updateDto, null);
        });
    }
}
