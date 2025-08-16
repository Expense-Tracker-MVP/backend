package com.expensetracker.expensetracker_mvp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.expensetracker.expensetracker_mvp.dtos.ExpenseDto;
import com.expensetracker.expensetracker_mvp.entities.Expense;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    ExpenseDto toDto(Expense expense);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    Expense toEntity(ExpenseDto expenseDto);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(ExpenseDto expenseDto, @MappingTarget Expense expense);
}
