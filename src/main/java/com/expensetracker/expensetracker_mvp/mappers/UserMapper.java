package com.expensetracker.expensetracker_mvp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.expensetracker.expensetracker_mvp.dtos.UserRequestDto;
import com.expensetracker.expensetracker_mvp.dtos.UserResponseDto;
import com.expensetracker.expensetracker_mvp.entities.User;

import java.util.List;

/**
 * MapStruct mapper for converting between User entities and DTOs
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toResponseDto(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDto userRequestDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UserRequestDto userRequestDto, @MappingTarget User user);
    
    List<UserResponseDto> toResponseDtoList(List<User> users);
}