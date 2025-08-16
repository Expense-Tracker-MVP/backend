package com.expensetracker.expensetracker_mvp.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensetracker.expensetracker_mvp.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    Optional<Category> findByUserIdAndName(UUID userId, String name);
    boolean existsByUserIdAndName(UUID userId, String name);
}
