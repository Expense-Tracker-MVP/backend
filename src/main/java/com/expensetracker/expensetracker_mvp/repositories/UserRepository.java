package com.expensetracker.expensetracker_mvp.repositories;

import com.expensetracker.expensetracker_mvp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByProviderAndProviderId(String provider, String providerId);

    Optional<User> findByDisplayNameIgnoreCase(String displayName);

    List<User> findByDisplayNameContainingIgnoreCase(String displayName);

    boolean existsByDisplayNameIgnoreCase(String displayName);

    List<User> findByProvider(String provider);

}