package com.expensetracker.expensetracker_mvp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for uploading or updating files
 * Contains only the fields that clients should be able to modify
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRequestDto {
    private UUID userId;
    private UUID expenseId; // Optional - file can exist without being linked to an expense
    private String fileName;
    private String fileType;
    private String purpose;
    
    // Note: We don't include id, filePath, uploadedAt as these are system-managed
    // filePath is internal and should not be exposed to clients
}