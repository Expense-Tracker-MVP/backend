package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for file data
 * Provides a clean view of file information without exposing internal file paths
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDto {
    private UUID id;
    private UUID userId;
    private UUID expenseId;
    private String fileName;
    private String fileType;
    private String purpose;
    private LocalDateTime uploadedAt;
    
    // Note: Exclude filePath for security reasons
    // File downloads should be handled through dedicated endpoints
}