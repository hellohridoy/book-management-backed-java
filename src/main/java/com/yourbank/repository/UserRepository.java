package com.yourbank.repository;

import com.yourbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email address
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * @param email The email address to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given phone number
     * @param phone The phone number to check
     * @return true if a user exists with this phone number, false otherwise
     */
    boolean existsByPhone(String phone);
}
