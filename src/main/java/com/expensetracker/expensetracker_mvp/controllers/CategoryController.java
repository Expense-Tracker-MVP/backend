package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.ApiResponse;
import com.expensetracker.expensetracker_mvp.dtos.CategoryRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.CategoryResponseDto;
import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.mappers.CategoryMapper;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "API for managing expense categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getCategoriesByUser(@PathVariable UUID userId) {
        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<Category> categories = categoryRepository.findByUserIdOrderByName(userId);
        List<CategoryResponseDto> categoryDtos = categoryMapper.toResponseDtoList(categories);
        return ResponseEntity.ok(ApiResponse.success(categoryDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getCategoryById(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            if (!category.get().getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            return ResponseEntity.ok(ApiResponse.success(categoryMapper.toResponseDto(category.get())));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Category not found"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        User currentUser = getCurrentUser();

        if (categoryRequestDto.getUserId() != null && !categoryRequestDto.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Category category = categoryMapper.toEntity(categoryRequestDto);

        category.setUser(currentUser);
        category.setUserId(currentUser.getId());

        Category savedCategory = categoryRepository.save(category);
        CategoryResponseDto responseDto = categoryMapper.toResponseDto(savedCategory);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(@PathVariable UUID id,
            @RequestBody CategoryRequestDto categoryRequestDto) {
        User currentUser = getCurrentUser();

        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();

            if (!category.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            if (categoryRequestDto.getUserId() != null && !categoryRequestDto.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            categoryMapper.updateEntityFromDto(categoryRequestDto, category);

            Category updatedCategory = categoryRepository.save(category);
            CategoryResponseDto responseDto = categoryMapper.toResponseDto(updatedCategory);
            return ResponseEntity.ok(ApiResponse.success(responseDto));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Category not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();

            if (!category.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }
            if (category.isUndeletable()) {
                return ResponseEntity.status(403).body(ApiResponse.error("Category cannot be deleted"));
            }
            categoryRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success());
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Category not found"));
        }
    }
}