package com.taxdividend.backend.service;

import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing users in the application database.
 *
 * This service handles:
 * - User metadata storage (tax info, address, etc.)
 * - User profile management
 * - User lookup operations
 *
 * Authentication is handled by Keycloak. This service only stores
 * application-specific metadata that Keycloak doesn't manage.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Creates a new user in the application database.
     *
     * This is called after successfully creating the user in Keycloak.
     * Stores application-specific metadata (tax info, canton, etc.)
     *
     * @param keycloakUserId Keycloak user ID (UUID)
     * @param email User email
     * @param fullName User full name
     * @param canton Swiss canton code (optional)
     * @param taxId Swiss tax ID (optional)
     * @param address User address (optional)
     * @return Created User entity
     */
    @Transactional
    public User createUser(
        UUID keycloakUserId,
        String email,
        String fullName,
        String canton,
        String taxId,
        String address
    ) {
        // Check if user already exists
        if (userRepository.findById(keycloakUserId).isPresent()) {
            throw new UserAlreadyExistsException("User with ID " + keycloakUserId + " already exists");
        }

        User user = User.builder()
            .id(keycloakUserId)  // Use Keycloak user ID as primary key
            .email(email)
            .passwordHash("")  // Not used (Keycloak handles authentication)
            .fullName(fullName)
            .canton(canton)
            .taxId(taxId)
            .address(address)
            .country("CH")  // Default to Switzerland
            .status("ACTIVE")
            .isActive(true)
            .isVerified(false)  // Will be updated when Keycloak email is verified
            .build();

        User savedUser = userRepository.save(user);
        log.info("Created user metadata in database: {} ({})", email, keycloakUserId);

        return savedUser;
    }

    /**
     * Gets user by Keycloak user ID.
     *
     * @param userId Keycloak user ID
     * @return User entity
     */
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Gets user by email.
     *
     * @param email User email
     * @return User entity
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Updates user verification status.
     *
     * Called when Keycloak confirms email verification.
     *
     * @param userId Keycloak user ID
     * @param verified Verification status
     */
    @Transactional
    public void updateVerificationStatus(UUID userId, boolean verified) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsVerified(verified);
            userRepository.save(user);
            log.info("Updated verification status for user {}: {}", userId, verified);
        });
    }

    /**
     * Updates user profile information.
     *
     * @param userId Keycloak user ID
     * @param fullName User full name (optional)
     * @param canton Swiss canton (optional)
     * @param taxId Swiss tax ID (optional)
     * @param address User address (optional)
     * @return Updated User entity
     */
    @Transactional
    public User updateUserProfile(
        UUID userId,
        String fullName,
        String canton,
        String taxId,
        String address
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (fullName != null) user.setFullName(fullName);
        if (canton != null) user.setCanton(canton);
        if (taxId != null) user.setTaxId(taxId);
        if (address != null) user.setAddress(address);

        User updatedUser = userRepository.save(user);
        log.info("Updated profile for user: {}", userId);

        return updatedUser;
    }

    /**
     * Exception thrown when user already exists.
     */
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when user is not found.
     */
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
