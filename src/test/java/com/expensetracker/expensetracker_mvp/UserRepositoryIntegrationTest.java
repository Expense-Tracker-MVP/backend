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

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndReadUser() {
        // Generate unique test data
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        String uniqueEmail = "test_" + System.currentTimeMillis() + "@example.com";
        
        User user = User.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("hashedpass")
                .build();

        user = userRepository.save(user);
        Optional<User> found = userRepository.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals(uniqueUsername, found.get().getUsername());
        assertEquals(uniqueEmail, found.get().getEmail());
    }
}