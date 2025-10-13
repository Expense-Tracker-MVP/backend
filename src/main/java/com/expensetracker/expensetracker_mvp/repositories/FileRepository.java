package com.expensetracker.expensetracker_mvp.repositories;

import com.expensetracker.expensetracker_mvp.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    
    List<File> findByUserId(UUID userId);
    
    List<File> findByExpenseId(UUID expenseId);
    
    List<File> findByUserIdAndPurpose(UUID userId, String purpose);
    
    List<File> findByUserIdOrderByUploadedAtDesc(UUID userId);
    
    List<File> findByExpenseIdOrderByUploadedAtDesc(UUID expenseId);
}