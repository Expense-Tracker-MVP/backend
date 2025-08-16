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
import com.expensetracker.expensetracker_mvp.repositories.ExpenseRepository;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ExpenseRepositoryIntegrationTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveExpense() {
        // Create and save a user and category first
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("Groceries")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Create and save an expense
        Expense expense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Weekly grocery shopping")
                .amount(new BigDecimal("125.50"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        expenseRepository.flush();

        // Verify the expense was saved
        assertNotNull(savedExpense.getId());
        assertEquals("Weekly grocery shopping", savedExpense.getDescription());
        assertEquals(new BigDecimal("125.50"), savedExpense.getAmount());
        assertEquals(savedUser.getId(), savedExpense.getUser().getId());
        assertEquals(savedCategory.getId(), savedExpense.getCategory().getId());
        assertEquals("manual", savedExpense.getSource());
        assertNotNull(savedExpense.getCreatedAt());
        assertNotNull(savedExpense.getUpdatedAt());
    }

    @Test
    void testFindExpenseById() {
        // Create and save a user, category, and expense
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("Transportation")
                .build();
        Category savedCategory = categoryRepository.save(category);

        Expense expense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Gas station")
                .amount(new BigDecimal("45.00"))
                .expenseDate(LocalDate.now())
                .source("OCR")
                .build();
        Expense savedExpense = expenseRepository.save(expense);

        // Find the expense by ID
        Optional<Expense> foundExpense = expenseRepository.findById(savedExpense.getId());

        assertTrue(foundExpense.isPresent());
        assertEquals("Gas station", foundExpense.get().getDescription());
        assertEquals(new BigDecimal("45.00"), foundExpense.get().getAmount());
        assertEquals("OCR", foundExpense.get().getSource());
    }

    @Test
    void testFindByUserId() {
        // Create and save a user
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("Food")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Create and save multiple expenses for the same user
        Expense expense1 = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Lunch")
                .amount(new BigDecimal("15.00"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();

        Expense expense2 = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Dinner")
                .amount(new BigDecimal("25.00"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);

        // Find all expenses for the user
        List<Expense> userExpenses = expenseRepository.findByUserId(savedUser.getId());

        assertEquals(2, userExpenses.size());
        assertTrue(userExpenses.stream().anyMatch(e -> e.getDescription().equals("Lunch")));
        assertTrue(userExpenses.stream().anyMatch(e -> e.getDescription().equals("Dinner")));
    }

    @Test
    void testFindByUserIdAndCategoryId() {
        // Create and save a user and categories
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category foodCategory = Category.builder()
                .user(savedUser)
                .name("Food")
                .build();
        Category savedFoodCategory = categoryRepository.save(foodCategory);

        Category transportCategory = Category.builder()
                .user(savedUser)
                .name("Transportation")
                .build();
        Category savedTransportCategory = categoryRepository.save(transportCategory);

        // Create expenses in different categories
        Expense foodExpense = Expense.builder()
                .user(savedUser)
                .category(savedFoodCategory)
                .description("Groceries")
                .amount(new BigDecimal("50.00"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();

        Expense transportExpense = Expense.builder()
                .user(savedUser)
                .category(savedTransportCategory)
                .description("Bus fare")
                .amount(new BigDecimal("2.50"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();

        expenseRepository.save(foodExpense);
        expenseRepository.save(transportExpense);

        // Find expenses by user ID and food category ID
        List<Expense> foodExpenses = expenseRepository.findByUserIdAndCategoryId(savedUser.getId(), savedFoodCategory.getId());

        assertEquals(1, foodExpenses.size());
        assertEquals("Groceries", foodExpenses.get(0).getDescription());
        assertEquals(savedFoodCategory.getId(), foodExpenses.get(0).getCategory().getId());
    }

    @Test
    void testFindByUserIdAndExpenseDateBetween() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Create expenses on different dates
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusDays(7);

        Expense todayExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Today's expense")
                .amount(new BigDecimal("10.00"))
                .expenseDate(today)
                .source("manual")
                .build();

        Expense yesterdayExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Yesterday's expense")
                .amount(new BigDecimal("20.00"))
                .expenseDate(yesterday)
                .source("manual")
                .build();

        Expense lastWeekExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Last week's expense")
                .amount(new BigDecimal("30.00"))
                .expenseDate(lastWeek)
                .source("manual")
                .build();

        expenseRepository.save(todayExpense);
        expenseRepository.save(yesterdayExpense);
        expenseRepository.save(lastWeekExpense);

        // Find expenses between yesterday and today
        List<Expense> recentExpenses = expenseRepository.findByUserIdAndExpenseDateBetween(
                savedUser.getId(), yesterday, today);

        assertEquals(2, recentExpenses.size());
        assertTrue(recentExpenses.stream().anyMatch(e -> e.getDescription().equals("Today's expense")));
        assertTrue(recentExpenses.stream().anyMatch(e -> e.getDescription().equals("Yesterday's expense")));
    }

    @Test
    void testFindExpensesByUserAndDateRangeOrdered() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Create expenses on different dates
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Expense todayExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Today's expense")
                .amount(new BigDecimal("10.00"))
                .expenseDate(today)
                .source("manual")
                .build();

        Expense yesterdayExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Yesterday's expense")
                .amount(new BigDecimal("20.00"))
                .expenseDate(yesterday)
                .source("manual")
                .build();

        expenseRepository.save(todayExpense);
        expenseRepository.save(yesterdayExpense);

        // Find expenses ordered by date (DESC)
        List<Expense> orderedExpenses = expenseRepository.findExpensesByUserAndDateRangeOrdered(
                savedUser.getId(), yesterday, today);

        assertEquals(2, orderedExpenses.size());
        // First expense should be today's (most recent due to DESC ordering)
        assertEquals("Today's expense", orderedExpenses.get(0).getDescription());
        assertEquals("Yesterday's expense", orderedExpenses.get(1).getDescription());
    }

    @Test
    void testGetTotalExpensesByUserAndDateRange() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Create expenses on different dates
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Expense todayExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Today's expense")
                .amount(new BigDecimal("15.50"))
                .expenseDate(today)
                .source("manual")
                .build();

        Expense yesterdayExpense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Yesterday's expense")
                .amount(new BigDecimal("25.75"))
                .expenseDate(yesterday)
                .source("manual")
                .build();

        expenseRepository.save(todayExpense);
        expenseRepository.save(yesterdayExpense);

        // Get total expenses between yesterday and today
        Double totalExpenses = expenseRepository.getTotalExpensesByUserAndDateRange(
                savedUser.getId(), yesterday, today);

        assertNotNull(totalExpenses);
        assertEquals(41.25, totalExpenses, 0.01); // 15.50 + 25.75 = 41.25
    }

    @Test
    void testUpdateExpense() {
        // Create and save a user, category, and expense
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        Expense expense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Original description")
                .amount(new BigDecimal("100.00"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();
        Expense savedExpense = expenseRepository.save(expense);

        // Update the expense
        savedExpense.setDescription("Updated description");
        savedExpense.setAmount(new BigDecimal("150.00"));
        savedExpense.setSource("OCR");
        Expense updatedExpense = expenseRepository.save(savedExpense);

        // Verify the update
        assertEquals("Updated description", updatedExpense.getDescription());
        assertEquals(new BigDecimal("150.00"), updatedExpense.getAmount());
        assertEquals("OCR", updatedExpense.getSource());
        assertEquals(savedExpense.getId(), updatedExpense.getId());
    }

    @Test
    void testDeleteExpense() {
        // Create and save a user, category, and expense
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        Expense expense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("To delete")
                .amount(new BigDecimal("50.00"))
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();
        Expense savedExpense = expenseRepository.save(expense);

        // Delete the expense
        expenseRepository.delete(savedExpense);

        // Verify it's deleted
        Optional<Expense> foundExpense = expenseRepository.findById(savedExpense.getId());
        assertFalse(foundExpense.isPresent());
    }

    @Test
    void testExpenseWithNullAmount() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Try to create expense with null amount (should fail due to @Column(nullable = false))
        Expense expense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Test expense")
                .amount(null)
                .expenseDate(LocalDate.now())
                .source("manual")
                .build();

        // This should throw an exception due to database constraint
        assertThrows(Exception.class, () -> expenseRepository.save(expense));
    }

    @Test
    void testExpenseWithNullExpenseDate() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("General")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Try to create expense with null expense date (should fail due to @Column(nullable = false))
        Expense expense = Expense.builder()
                .user(savedUser)
                .category(savedCategory)
                .description("Test expense")
                .amount(new BigDecimal("100.00"))
                .expenseDate(null)
                .source("manual")
                .build();

        // This should throw an exception due to database constraint
        assertThrows(Exception.class, () -> expenseRepository.save(expense));
    }
}
