package com.healthtech.repository;

import com.healthtech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository - Data access layer for User entity.
 * 
 * Extends JpaRepository for:
 * - CRUD operations (save, findById, findAll, delete, etc.)
 * - Pagination support
 * - Query method derivation
 * 
 * Spring Data JPA automatically provides implementation at runtime.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     * Used during authentication.
     * 
     * @param email User's email address
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists in the system.
     * Used during registration to prevent duplicates.
     * 
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);
}
