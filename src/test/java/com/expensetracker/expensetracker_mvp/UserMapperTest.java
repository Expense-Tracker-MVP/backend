package com.expensetracker.expensetracker_mvp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.dtos.UserDto;
import com.expensetracker.expensetracker_mvp.mappers.UserMapper;


import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testToDto() {
        UUID id = UUID.randomUUID();
        String username = "testuser";
        String email = "test@example.com";
        String password = "hashedpassword";
        LocalDateTime createdAt = LocalDateTime.now();

        User user = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .createdAt(createdAt)
                .build();

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(id, userDto.getId());
        assertEquals(username, userDto.getUsername());
        assertEquals(email, userDto.getEmail());
        // Password and createdAt are not included in UserDto for security
    }

    @Test
    void testToEntity() {
        UUID id = UUID.randomUUID();
        String username = "newuser";
        String email = "newuser@example.com";

        UserDto userDto = UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();

        User user = userMapper.toEntity(userDto);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getCreatedAt());
    }

    @Test
    void testUpdateEntityFromDto() {
        UUID id = UUID.randomUUID();
        String originalUsername = "originaluser";
        String originalEmail = "original@example.com";
        String originalPassword = "originalpassword";
        LocalDateTime originalCreatedAt = LocalDateTime.now();

        User existingUser = User.builder()
                .id(id)
                .username(originalUsername)
                .email(originalEmail)
                .password(originalPassword)
                .createdAt(originalCreatedAt)
                .build();

        String updatedEmail = "updated@example.com";
        UserDto updateDto = UserDto.builder()
                .id(id)
                .username(originalUsername)
                .email(updatedEmail)
                .build();

        userMapper.updateEntityFromDto(updateDto, existingUser);

        assertEquals(id, existingUser.getId());
        assertEquals(originalUsername, existingUser.getUsername());
        assertEquals(updatedEmail, existingUser.getEmail());
        assertEquals(originalPassword, existingUser.getPassword());
        assertEquals(originalCreatedAt, existingUser.getCreatedAt());
    }

    @Test
    void testUpdateEntityFromDtoWithNullValues() {
        UUID id = UUID.randomUUID();
        String username = "testuser";
        String email = "test@example.com";
        String password = "password";
        LocalDateTime createdAt = LocalDateTime.now();

        User existingUser = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .createdAt(createdAt)
                .build();

        UserDto updateDto = UserDto.builder()
                .id(id)
                .username(null)
                .email(null)
                .build();

        userMapper.updateEntityFromDto(updateDto, existingUser);

        assertEquals(id, existingUser.getId());
        // MapStruct overwrites with null values by default
        assertNull(existingUser.getUsername());
        assertNull(existingUser.getEmail());
        assertEquals(password, existingUser.getPassword());
        assertEquals(createdAt, existingUser.getCreatedAt());
    }

    @Test
    void testToDtoWithNullUser() {
        UserDto userDto = userMapper.toDto(null);
        assertNull(userDto);
    }

    @Test
    void testToEntityWithNullDto() {
        User user = userMapper.toEntity(null);
        assertNull(user);
    }

    @Test
    void testUpdateEntityFromDtoWithNullDto() {
        User existingUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .build();
    
        String originalUsername = existingUser.getUsername();
        String originalEmail = existingUser.getEmail();
    
        assertDoesNotThrow(() -> userMapper.updateEntityFromDto(null, existingUser));
    
        assertEquals(originalUsername, existingUser.getUsername());
        assertEquals(originalEmail, existingUser.getEmail());
    }

    @Test
    void testUpdateEntityFromDtoWithNullTarget() {
        UserDto updateDto = UserDto.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        assertThrows(NullPointerException.class, () -> {
            userMapper.updateEntityFromDto(updateDto, null);
        });
    }
}
