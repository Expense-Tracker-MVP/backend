package com.expensetracker.expensetracker_mvp.mappers;

import com.expensetracker.expensetracker_mvp.dtos.CategoryRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.CategoryResponseDto;
import com.expensetracker.expensetracker_mvp.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for converting between Category entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Converts a Category entity to CategoryResponseDto
     * MapStruct automatically maps matching field names
     */
    CategoryResponseDto toResponseDto(Category category);

    /**
     * Converts a list of Category entities to a list of CategoryResponseDtos
     */
    List<CategoryResponseDto> toResponseDtoList(List<Category> categories);

    /**
     * Converts CategoryRequestDto to Category entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryRequestDto requestDto);

    /**
     * Updates an existing Category entity with data from CategoryRequestDto
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(CategoryRequestDto requestDto, @MappingTarget Category category);
}