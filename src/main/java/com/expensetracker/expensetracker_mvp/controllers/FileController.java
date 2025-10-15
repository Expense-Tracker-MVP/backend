package com.expensetracker.expensetracker_mvp.controllers;

import com.expensetracker.expensetracker_mvp.dtos.FileRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.FileResponseDto;
import com.expensetracker.expensetracker_mvp.entities.File;
import com.expensetracker.expensetracker_mvp.mappers.FileMapper;
import com.expensetracker.expensetracker_mvp.repositories.FileRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

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
@Tag(name = "Files", description = "API for file upload, download and management")
@RequiredArgsConstructor
public class FileController {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    private final String uploadDir = "uploads/"; // Configure this path as needed

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FileResponseDto>> getFilesByUser(@PathVariable UUID userId) {
        List<File> files = fileRepository.findByUserIdOrderByUploadedAtDesc(userId);
        List<FileResponseDto> fileDtos = fileMapper.toResponseDtoList(files);
        return ResponseEntity.ok(fileDtos);
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<List<FileResponseDto>> getFilesByExpense(@PathVariable UUID expenseId) {
        List<File> files = fileRepository.findByExpenseIdOrderByUploadedAtDesc(expenseId);
        List<FileResponseDto> fileDtos = fileMapper.toResponseDtoList(files);
        return ResponseEntity.ok(fileDtos);
    }

    @GetMapping("/user/{userId}/purpose/{purpose}")
    public ResponseEntity<List<FileResponseDto>> getFilesByPurpose(
            @PathVariable UUID userId, 
            @PathVariable String purpose) {
        
        List<File> files = fileRepository.findByUserIdAndPurpose(userId, purpose);
        List<FileResponseDto> fileDtos = fileMapper.toResponseDtoList(files);
        return ResponseEntity.ok(fileDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponseDto> getFileById(@PathVariable UUID id) {
        Optional<File> file = fileRepository.findById(id);
        return file.map(f -> ResponseEntity.ok(fileMapper.toResponseDto(f)))
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
    public ResponseEntity<FileResponseDto> uploadFile(
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
            FileResponseDto responseDto = fileMapper.toResponseDto(savedFile);
            return ResponseEntity.ok(responseDto);
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileResponseDto> updateFile(@PathVariable UUID id, @RequestBody FileRequestDto fileRequestDto) {
        Optional<File> optionalFile = fileRepository.findById(id);
        
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            fileMapper.updateEntityFromDto(fileRequestDto, file);
            File updatedFile = fileRepository.save(file);
            FileResponseDto responseDto = fileMapper.toResponseDto(updatedFile);
            return ResponseEntity.ok(responseDto);
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