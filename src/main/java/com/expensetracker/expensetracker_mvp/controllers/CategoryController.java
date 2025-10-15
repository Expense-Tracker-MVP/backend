package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.CategoryRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.CategoryResponseDto;
import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.mappers.CategoryMapper;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesByUser(@PathVariable UUID userId) {
        List<Category> categories = categoryRepository.findByUserIdOrderByName(userId);
        List<CategoryResponseDto> categoryDtos = categoryMapper.toResponseDtoList(categories);
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(cat -> ResponseEntity.ok(categoryMapper.toResponseDto(cat)))
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        
        // Set user relationship from ID
        User user = userRepository.findById(categoryRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        category.setUser(user);
        
        Category savedCategory = categoryRepository.save(category);
        CategoryResponseDto responseDto = categoryMapper.toResponseDto(savedCategory);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequestDto categoryRequestDto) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            categoryMapper.updateEntityFromDto(categoryRequestDto, category);
            
            // Update user relationship if provided
            if (categoryRequestDto.getUserId() != null) {
                User user = userRepository.findById(categoryRequestDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                category.setUser(user);
            }
            
            Category updatedCategory = categoryRepository.save(category);
            CategoryResponseDto responseDto = categoryMapper.toResponseDto(updatedCategory);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}