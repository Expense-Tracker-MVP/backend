package com.expensetracker.expensetracker_mvp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.dtos.CategoryDto;
import com.expensetracker.expensetracker_mvp.mappers.CategoryMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void testToDto() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String name = "Groceries";
        LocalDateTime createdAt = LocalDateTime.now();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        Category category = Category.builder()
                .id(id)
                .user(user)
                .name(name)
                .createdAt(createdAt)
                .build();

        CategoryDto categoryDto = categoryMapper.toDto(category);

        assertNotNull(categoryDto);
        assertEquals(id, categoryDto.getId());
        assertEquals(userId, categoryDto.getUserId());
        assertEquals(name, categoryDto.getName());
        assertEquals(createdAt, categoryDto.getCreatedAt());
    }

    @Test
    void testToEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String name = "Transportation";
        LocalDateTime createdAt = LocalDateTime.now();

        CategoryDto categoryDto = CategoryDto.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .createdAt(createdAt)
                .build();

        Category category = categoryMapper.toEntity(categoryDto);

        assertNotNull(category);
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(createdAt, category.getCreatedAt());
        assertNull(category.getUser()); // User should be ignored as per mapping
    }

    @Test
    void testUpdateEntityFromDto() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String originalName = "Original Category";
        String updatedName = "Updated Category";
        LocalDateTime originalCreatedAt = LocalDateTime.now();
        LocalDateTime updatedCreatedAt = LocalDateTime.now().plusHours(1);

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        Category existingCategory = Category.builder()
                .id(id)
                .user(user)
                .name(originalName)
                .createdAt(originalCreatedAt)
                .build();

        CategoryDto updateDto = CategoryDto.builder()
                .id(id)
                .userId(userId)
                .name(updatedName)
                .createdAt(updatedCreatedAt)
                .build();

        categoryMapper.updateEntityFromDto(updateDto, existingCategory);

        assertEquals(id, existingCategory.getId());
        assertEquals(updatedName, existingCategory.getName());
        assertEquals(updatedCreatedAt, existingCategory.getCreatedAt());
        assertEquals(user, existingCategory.getUser()); // User should remain unchanged
    }

    @Test
    void testUpdateEntityFromDtoWithNullValues() {
        UUID id = UUID.randomUUID();
        String name = "Test Category";
        LocalDateTime createdAt = LocalDateTime.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();

        Category existingCategory = Category.builder()
                .id(id)
                .user(user)
                .name(name)
                .createdAt(createdAt)
                .build();

        CategoryDto updateDto = CategoryDto.builder()
                .id(id)
                .name(null)
                .build();

        categoryMapper.updateEntityFromDto(updateDto, existingCategory);

        assertEquals(id, existingCategory.getId());
        assertNull(existingCategory.getName()); // MapStruct overwrites with null values
        assertNull(existingCategory.getCreatedAt()); // createdAt is also null in DTO, so it gets overwritten
        assertEquals(user, existingCategory.getUser());
    }

    @Test
    void testToDtoWithNullCategory() {
        CategoryDto categoryDto = categoryMapper.toDto(null);
        assertNull(categoryDto);
    }

    @Test
    void testToEntityWithNullDto() {
        Category category = categoryMapper.toEntity(null);
        assertNull(category);
    }

    @Test
    void testUpdateEntityFromDtoWithNullDto() {
        Category existingCategory = Category.builder()
                .name("Test Category")
                .build();

        String originalName = existingCategory.getName();

        assertDoesNotThrow(() -> categoryMapper.updateEntityFromDto(null, existingCategory));

        assertEquals(originalName, existingCategory.getName());
    }

    @Test
    void testUpdateEntityFromDtoWithNullTarget() {
        CategoryDto updateDto = CategoryDto.builder()
                .name("Test Category")
                .build();

        assertThrows(NullPointerException.class, () -> {
            categoryMapper.updateEntityFromDto(updateDto, null);
        });
    }
}