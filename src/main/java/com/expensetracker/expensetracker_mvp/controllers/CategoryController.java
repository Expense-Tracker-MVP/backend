package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.CategoryDto;
import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.mappers.CategoryMapper;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "${frontend.url}")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByUserId(@PathVariable UUID userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        List<CategoryDto> categoryDtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/user/{userId}/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByUserIdAndName(@PathVariable UUID userId,
            @PathVariable String name) {
        return categoryRepository.findByUserIdAndName(userId, name)
                .map(categoryMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        // Check if category with same name already exists for the user
        if (categoryRepository.existsByUserIdAndName(categoryDto.getUserId(), categoryDto.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Category category = categoryMapper.toEntity(categoryDto);
        category.setCreatedAt(LocalDateTime.now());
        Category savedCategory = categoryRepository.save(category);
        CategoryDto savedCategoryDto = categoryMapper.toDto(savedCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable UUID id,
            @Valid @RequestBody CategoryDto categoryDto) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Check if name is being changed and if it conflicts with existing categories
                    // for the same user
                    if (!existingCategory.getName().equals(categoryDto.getName())) {
                        if (categoryRepository.existsByUserIdAndName(categoryDto.getUserId(), categoryDto.getName())) {
                            return ResponseEntity.badRequest().<CategoryDto>build();
                        }
                    }

                    categoryMapper.updateEntityFromDto(categoryDto, existingCategory);
                    Category updatedCategory = categoryRepository.save(existingCategory);
                    CategoryDto updatedCategoryDto = categoryMapper.toDto(updatedCategory);
                    return ResponseEntity.ok(updatedCategoryDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllCategoriesByUserId(@PathVariable UUID userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        categoryRepository.deleteAll(categories);
        return ResponseEntity.noContent().build();
    }
}
