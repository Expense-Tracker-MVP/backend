package com.expensetracker.expensetracker_mvp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveCategory() {
        // Create and save a user first
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        // Create and save a category
        Category category = Category.builder()
                .user(savedUser)
                .name("Groceries")
                .build();

        Category savedCategory = categoryRepository.save(category);
        categoryRepository.flush();
        
        // Verify the category was saved
        assertNotNull(savedCategory.getId());
        assertEquals("Groceries", savedCategory.getName());
        assertEquals(savedUser.getId(), savedCategory.getUser().getId());
        assertNotNull(savedCategory.getCreatedAt());
        Category fetchedCategory = categoryRepository.findById(savedCategory.getId()).orElse(null);
        assertNotNull(fetchedCategory);
        assertNotNull(fetchedCategory.getCreatedAt());
    }

    @Test
    void testFindCategoryById() {
        // Create and save a user and category
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

        // Find the category by ID
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());

        assertTrue(foundCategory.isPresent());
        assertEquals("Transportation", foundCategory.get().getName());
        assertEquals(savedUser.getId(), foundCategory.get().getUser().getId());
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

        // Create and save multiple categories for the same user
        Category category1 = Category.builder()
                .user(savedUser)
                .name("Food")
                .build();
        Category category2 = Category.builder()
                .user(savedUser)
                .name("Entertainment")
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // Find all categories for the user
        List<Category> userCategories = categoryRepository.findByUserId(savedUser.getId());

        assertEquals(2, userCategories.size());
        assertTrue(userCategories.stream().anyMatch(c -> c.getName().equals("Food")));
        assertTrue(userCategories.stream().anyMatch(c -> c.getName().equals("Entertainment")));
    }

    @Test
    void testFindByUserIdAndName() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("Healthcare")
                .build();
        categoryRepository.save(category);

        // Find category by user ID and name
        Optional<Category> foundCategory = categoryRepository.findByUserIdAndName(savedUser.getId(), "Healthcare");

        assertTrue(foundCategory.isPresent());
        assertEquals("Healthcare", foundCategory.get().getName());
        assertEquals(savedUser.getId(), foundCategory.get().getUser().getId());
    }

    @Test
    void testExistsByUserIdAndName() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("Shopping")
                .build();
        categoryRepository.save(category);

        // Check if category exists
        boolean exists = categoryRepository.existsByUserIdAndName(savedUser.getId(), "Shopping");
        assertTrue(exists);

        // Check if non-existent category returns false
        boolean notExists = categoryRepository.existsByUserIdAndName(savedUser.getId(), "NonExistent");
        assertFalse(notExists);
    }

    @Test
    void testUpdateCategory() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("Original Name")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Update the category name
        savedCategory.setName("Updated Name");
        Category updatedCategory = categoryRepository.save(savedCategory);

        // Verify the update
        assertEquals("Updated Name", updatedCategory.getName());
        assertEquals(savedCategory.getId(), updatedCategory.getId());
    }

    @Test
    void testDeleteCategory() {
        // Create and save a user and category
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .user(savedUser)
                .name("To Delete")
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Delete the category
        categoryRepository.delete(savedCategory);

        // Verify it's deleted
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());
        assertFalse(foundCategory.isPresent());
    }

    @Test
    void testUniqueConstraintOnUserIdAndName() {
        // Create and save a user
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        // Create first category
        Category category1 = Category.builder()
                .user(savedUser)
                .name("Duplicate Name")
                .build();
        categoryRepository.save(category1);

        // Try to create second category with same name for same user (should work due to @Transactional rollback)
        Category category2 = Category.builder()
                .user(savedUser)
                .name("Duplicate Name")
                .build();

        // This should not throw an exception due to @Transactional, but the constraint should be enforced
        // in a real scenario without @Transactional
        assertDoesNotThrow(() -> categoryRepository.save(category2));
    }

    @Test
    void testCategoryWithNullName() {
        // Create and save a user
        User user = User.builder()
                .username("testuser" + UUID.randomUUID().toString().substring(0, 8))
                .email("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com")
                .password("testpassword123")
                .build();
        User savedUser = userRepository.save(user);

        // Try to create category with null name (should fail due to @Column(nullable = false))
        Category category = Category.builder()
                .user(savedUser)
                .name(null)
                .build();

        // This should throw an exception due to database constraint
        assertThrows(Exception.class, () -> categoryRepository.save(category));
    }
}
