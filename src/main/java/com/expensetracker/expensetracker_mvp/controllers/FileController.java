package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.entities.File;
import com.expensetracker.expensetracker_mvp.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin(origins = "${app.frontend.url}")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    private final String uploadDir = "uploads/"; // Configure this path as needed

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<File>> getFilesByUser(@PathVariable UUID userId) {
        List<File> files = fileRepository.findByUserIdOrderByUploadedAtDesc(userId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<List<File>> getFilesByExpense(@PathVariable UUID expenseId) {
        List<File> files = fileRepository.findByExpenseIdOrderByUploadedAtDesc(expenseId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/user/{userId}/purpose/{purpose}")
    public ResponseEntity<List<File>> getFilesByPurpose(
            @PathVariable UUID userId, 
            @PathVariable String purpose) {
        
        List<File> files = fileRepository.findByUserIdAndPurpose(userId, purpose);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable UUID id) {
        Optional<File> file = fileRepository.findById(id);
        return file.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            try {
                Path filePath = Paths.get(file.getFilePath());
                Resource resource = new UrlResource(filePath.toUri());
                
                if (resource.exists() && resource.isReadable()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(file.getFileType()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, 
                                   "attachment; filename=\"" + file.getFileName() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "expenseId", required = false) UUID expenseId,
            @RequestParam(value = "purpose", defaultValue = "general") String purpose) {
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFileName = multipartFile.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = uploadPath.resolve(uniqueFileName);
            
            // Save file to disk
            Files.copy(multipartFile.getInputStream(), filePath);
            
            // Create file entity
            File fileEntity = File.builder()
                    .userId(userId)
                    .expenseId(expenseId)
                    .fileName(originalFileName)
                    .fileType(multipartFile.getContentType())
                    .filePath(filePath.toString())
                    .purpose(purpose)
                    .build();
            
            File savedFile = fileRepository.save(fileEntity);
            return ResponseEntity.ok(savedFile);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<File> updateFile(@PathVariable UUID id, @RequestBody File fileDetails) {
        Optional<File> optionalFile = fileRepository.findById(id);
        
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            file.setExpenseId(fileDetails.getExpenseId());
            file.setPurpose(fileDetails.getPurpose());
            
            File updatedFile = fileRepository.save(file);
            return ResponseEntity.ok(updatedFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            
            try {
                // Delete physical file
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
                
                // Delete database record
                fileRepository.deleteById(id);
                return ResponseEntity.noContent().build();
                
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}