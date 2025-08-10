package com.expensetracker.expensetracker_mvp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.expensetracker.expensetracker_mvp.dtos.UserDto;
import com.expensetracker.expensetracker_mvp.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
}
