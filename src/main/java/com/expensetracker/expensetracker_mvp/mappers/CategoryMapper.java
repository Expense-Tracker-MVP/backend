package com.expensetracker.expensetracker_mvp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.expensetracker.expensetracker_mvp.dtos.CategoryDto;
import com.expensetracker.expensetracker_mvp.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "user.id", target = "userId")
    CategoryDto toDto(Category category);
    
    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryDto categoryDto);
    
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(CategoryDto categoryDto, @MappingTarget Category category);
}
