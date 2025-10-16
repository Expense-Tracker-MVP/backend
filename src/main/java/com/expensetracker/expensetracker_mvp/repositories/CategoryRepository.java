package com.expensetracker.expensetracker_mvp.repositories;

import com.expensetracker.expensetracker_mvp.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByUserId(UUID userId);

    List<Category> findByUserIdOrderByName(UUID userId);
}