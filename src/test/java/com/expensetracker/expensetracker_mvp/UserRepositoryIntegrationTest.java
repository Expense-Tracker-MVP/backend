package com.expensetracker.expensetracker_mvp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import com.expensetracker.expensetracker_mvp.entities.User;
import com.expensetracker.expensetracker_mvp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("hashedpass123")
                .build();

        User savedUser = userRepository.save(user);
        userRepository.flush();

        assertNotNull(savedUser.getId());
        assertEquals(uniqueUsername, savedUser.getUsername());
        assertEquals(uniqueEmail, savedUser.getEmail());
        assertEquals("hashedpass123", savedUser.getPassword());
        assertNotNull(savedUser.getCreatedAt());

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(uniqueUsername, foundUser.get().getUsername());
        assertEquals(uniqueEmail, foundUser.get().getEmail());
    }

    @Test
    void testFindUserById() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("password123")
                .build();
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(uniqueUsername, foundUser.get().getUsername());
        assertEquals(uniqueEmail, foundUser.get().getEmail());
        assertEquals("password123", foundUser.get().getPassword());
    }

    @Test
    void testFindUserById_NonExistent() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<User> foundUser = userRepository.findById(nonExistentId);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("originalpassword")
                .build();
        User savedUser = userRepository.save(user);

        savedUser.setUsername("updatedusername");
        savedUser.setEmail("updated@example.com");
        savedUser.setPassword("newpassword123");
        User updatedUser = userRepository.save(savedUser);

        assertEquals("updatedusername", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("newpassword123", updatedUser.getPassword());
        assertEquals(savedUser.getId(), updatedUser.getId());
    }

    @Test
    void testDeleteUser() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("password123")
                .build();
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.findById(savedUser.getId()).isPresent());

        userRepository.delete(savedUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUserWithLongValues() {
        String longUsername = "a".repeat(50);
        String longEmail = "a".repeat(50) + "@example.com";

        User user = User.builder()
                .username(longUsername)
                .email(longEmail)
                .password("password123")
                .build();

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        assertEquals(longUsername, savedUser.getUsername());
        assertEquals(longEmail, savedUser.getEmail());
    }

    @Test
    void testUserCreationTimestamp() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("password123")
                .build();

        User savedUser = userRepository.save(user);
        userRepository.flush();
        assertNotNull(savedUser.getCreatedAt());

        assertTrue(savedUser.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void testSaveUserWithNullUsername() {
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(null)
                .email(uniqueEmail)
                .password("password123")
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithNullEmail() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);

        User user = User.builder()
                .username(uniqueUsername)
                .email(null)
                .password("password123")
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithNullPassword() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password(null)
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithAllNullValues() {
        User user = User.builder()
                .username(null)
                .email(null)
                .password(null)
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testUpdateUserWithNullValues() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("originalpassword")
                .build();
        User savedUser = userRepository.save(user);

        savedUser.setUsername(null);
        savedUser.setEmail(null);
        savedUser.setPassword(null);
        
        assertThrows(Exception.class, () -> {
            userRepository.save(savedUser);
            userRepository.flush();
        });
    }

    @Test
    void testFindUserByIdWithNullId() {
        assertThrows(Exception.class, () -> {
            userRepository.findById(null);
        });
    }

    @Test
    void testDeleteUserWithNullUser() {
        assertThrows(Exception.class, () -> {
            userRepository.delete(null);
        });
    }

    @Test
    void testSaveUserWithEmptyStrings() {
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username("")  // Empty string, not null
                .email(uniqueEmail)
                .password("password123")
                .build();

        // Empty string should not be allowed
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithWhitespaceOnly() {
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username("   ")  // Whitespace only
                .email(uniqueEmail)
                .password("password123")
                .build();

        // Whitespace-only string should not be allowed
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithEmptyEmail() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);

        User user = User.builder()
                .username(uniqueUsername)
                .email("")  // Empty string email
                .password("password123")
                .build();

        // Empty email should not be allowed
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithWhitespaceOnlyEmail() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);

        User user = User.builder()
                .username(uniqueUsername)
                .email("   ")  // Whitespace-only email
                .password("password123")
                .build();

        // Whitespace-only email should not be allowed
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithEmptyPassword() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("")  // Empty string password
                .build();

        // Empty password should not be allowed
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithWhitespaceOnlyPassword() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("   ")  // Whitespace-only password
                .build();

        // Whitespace-only password should not be allowed
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testUserWithNullId() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .id(null)  // Explicitly set ID to null
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("password123")
                .build();

        // ID is @GeneratedValue, so null ID should be fine and will be auto-generated
        User savedUser = userRepository.save(user);
        userRepository.flush();
        
        // Verify ID was generated
        assertNotNull(savedUser.getId());
        assertEquals(uniqueUsername, savedUser.getUsername());
        assertEquals(uniqueEmail, savedUser.getEmail());
    }

    @Test
    void testSaveUserWithDuplicateUsername() {
        String duplicateUsername = "duplicateuser";
        String uniqueEmail1 = "test1_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String uniqueEmail2 = "test2_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        // Save first user
        User user1 = User.builder()
                .username(duplicateUsername)
                .email(uniqueEmail1)
                .password("password123")
                .build();
        userRepository.save(user1);

        // Try to save second user with same username
        User user2 = User.builder()
                .username(duplicateUsername)
                .email(uniqueEmail2)
                .password("password456")
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithDuplicateEmail() {
        String duplicateEmail = "duplicate@example.com";
        String uniqueUsername1 = "user1_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueUsername2 = "user2_" + UUID.randomUUID().toString().substring(0, 8);

        // Save first user
        User user1 = User.builder()
                .username(uniqueUsername1)
                .email(duplicateEmail)
                .password("password123")
                .build();
        userRepository.save(user1);

        // Try to save second user with same email
        User user2 = User.builder()
                .username(uniqueUsername2)
                .email(duplicateEmail)
                .password("password456")
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithVeryLongUsername() {
        String veryLongUsername = "a".repeat(51); // Exceeds @Column(length = 50)
        String uniqueEmail = "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        User user = User.builder()
                .username(veryLongUsername)
                .email(uniqueEmail)
                .password("password123")
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    @Test
    void testSaveUserWithVeryLongEmail() {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        String veryLongEmail = "a".repeat(101) + "@example.com"; // Exceeds @Column(length = 100)

        User user = User.builder()
                .username(uniqueUsername)
                .email(veryLongEmail)
                .password("password123")
                .build();

        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }
}