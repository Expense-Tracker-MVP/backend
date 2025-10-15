package com.expensetracker.expensetracker_mvp.mappers;

import com.expensetracker.expensetracker_mvp.dtos.ExpenseRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.ExpenseResponseDto;
import com.expensetracker.expensetracker_mvp.entities.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for converting between Expense entities and DTOs
 * Simplified to only map essential fields without nested objects
 */
@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    /**
     * Converts an Expense entity to ExpenseResponseDto
     * Maps only essential fields, excludes nested objects for cleaner API
     */
    ExpenseResponseDto toResponseDto(Expense expense);

    /**
     * Converts a list of Expense entities to a list of ExpenseResponseDtos
     */
    List<ExpenseResponseDto> toResponseDtoList(List<Expense> expenses);

    /**
     * Converts ExpenseRequestDto to Expense entity
     * Ignores system-managed fields and relationships
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    Expense toEntity(ExpenseRequestDto requestDto);

    /**
     * Updates an existing Expense entity with data from ExpenseRequestDto
     * Only updates non-null values from the request DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(ExpenseRequestDto requestDto, @MappingTarget Expense expense);
}