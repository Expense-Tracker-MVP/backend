package com.expensetracker.expensetracker_mvp.services;

import com.expensetracker.expensetracker_mvp.entities.Category;
import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Creates a default "All" category for a new user
     * 
     * @param user The user for whom to create the default category
     * @return The created default category
     */
    public Category createDefaultCategoryForUser(User user) {

        Category defaultCategory = Category.builder()
                .userId(user.getId())
                .user(user)
                .name("All")
                .description("Default category for all expenses")
                .color("#6C757D")
                .build();

        Category savedCategory = categoryRepository.save(defaultCategory);

        return savedCategory;
    }
}