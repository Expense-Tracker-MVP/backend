package com.expensetracker.expensetracker_mvp.mappers;

import com.expensetracker.expensetracker_mvp.dtos.FileRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.FileResponseDto;
import com.expensetracker.expensetracker_mvp.entities.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for converting between File entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface FileMapper {

    /**
     * Converts a File entity to FileResponseDto
     */
    FileResponseDto toResponseDto(File file);

    /**
     * Converts a list of File entities to a list of FileResponseDtos
     */
    List<FileResponseDto> toResponseDtoList(List<File> files);

    /**
     * Converts FileRequestDto to File entity
     * Ignores system-managed fields and relationships
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filePath", ignore = true) // This will be set by the upload logic
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "expense", ignore = true)
    File toEntity(FileRequestDto requestDto);

    /**
     * Updates an existing File entity with data from FileRequestDto
     * Only updates non-null values from the request DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filePath", ignore = true) // File path should not be changed after upload
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "expense", ignore = true)
    void updateEntityFromDto(FileRequestDto requestDto, @MappingTarget File file);
}