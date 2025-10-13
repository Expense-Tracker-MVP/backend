package com.expensetracker.expensetracker_mvp.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "expense_id")
    private UUID expenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", insertable = false, updatable = false)
    private Expense expense;

    @Column(nullable = false, name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", length = 50)
    private String fileType; // e.g., 'image/jpeg', 'application/pdf'

    @Column(nullable = false, name = "file_path", columnDefinition = "TEXT")
    private String filePath; // storage path

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String purpose = "general"; // 'receipt', 'reminder', 'other'

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}