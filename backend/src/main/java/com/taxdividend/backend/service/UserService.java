package com.taxdividend.backend.service;

import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.UserRepository;
import com.taxdividend.backend.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for user management and auto-provisioning.
 *
 * Handles two user creation flows:
 * 1. Classic registration (/auth/register endpoint) - creates user with verified=false
 * 2. SSO auto-provisioning (first login via Google/GitHub/etc.) - creates user with verified=true
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Find user by ID or auto-provision for SSO users.
     *
     * This method is called by InternalApiKeyFilter on every authenticated request.
     *
     * Flow:
     * - If user exists → return it
     * - If user doesn't exist and is SSO login → auto-provision (create user)
     * - If user doesn't exist and is classic login → throw exception (user must register first)
     *
     * @param userContext User context from BFF Gateway (extracted from Keycloak JWT)
     * @return User entity
     * @throws UserNotRegisteredException if classic login but user not registered
     */
    @Transactional
    public User findOrCreateFromSso(UserContext userContext) {
        return userRepository.findById(userContext.userId())
                .orElseGet(() -> {
                    if (userContext.isSsoLogin()) {
                        log.info("Auto-provisioning SSO user: {} (provider: {})",
                                userContext.email(), userContext.identityProvider());
                        return createSsoUser(userContext);
                    } else {
                        log.warn("User not registered: {} (classic login requires /auth/register)",
                                userContext.userId());
                        throw new UserNotRegisteredException(
                                "User must complete registration via /auth/register endpoint");
                    }
                });
    }

    /**
     * Create user from SSO login (Google, GitHub, etc.)
     *
     * SSO users are automatically created on first login with:
     * - verified=true (email already verified by SSO provider)
     * - passwordHash="SSO" (no password needed)
     * - isActive=true
     * - status=ACTIVE
     *
     * @param userContext User context from SSO provider
     * @return Created user entity
     */
    private User createSsoUser(UserContext userContext) {
        User user = User.builder()
                .id(userContext.userId())
                .email(userContext.email())
                .passwordHash("SSO")  // No password for SSO users
                .isVerified(true)     // Email verified by SSO provider
                .isActive(true)
                .status("ACTIVE")
                .country("CH")        // Default to Switzerland
                .registrationSource(userContext.getRegistrationSource())
                .build();

        return userRepository.save(user);
    }

    /**
     * Create user from classic registration (/auth/register endpoint).
     *
     * Classic users are created with:
     * - verified=false (must verify email)
     * - passwordHash from Keycloak (managed separately)
     * - isActive=true
     * - status=ACTIVE
     *
     * @param userId User ID from Keycloak
     * @param email User email
     * @param fullName Full name
     * @param canton Swiss canton (optional)
     * @param taxId Tax ID (optional)
     * @param address Address (optional)
     * @return Created user entity
     * @throws UserAlreadyExistsException if user with this ID or email already exists
     */
    @Transactional
    public User createUser(UUID userId, String email, String fullName,
                          String canton, String taxId, String address) {
        // Check if user already exists
        if (userRepository.findById(userId).isPresent()) {
            throw new UserAlreadyExistsException("User with ID " + userId + " already exists");
        }

        log.info("Creating classic user: {} (ID: {})", email, userId);

        User user = User.builder()
                .id(userId)
                .email(email)
                .fullName(fullName)
                .canton(canton)
                .taxId(taxId)
                .address(address)
                .passwordHash("KEYCLOAK_MANAGED")  // Password managed by Keycloak
                .isVerified(false)                 // Must verify email
                .isActive(true)
                .status("ACTIVE")
                .country("CH")
                .registrationSource("CLASSIC")
                .build();

        return userRepository.save(user);
    }

    /**
     * Find user by ID.
     *
     * @param userId User ID
     * @return Optional containing user if found
     */
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Exception thrown when user attempts to access API without completing registration.
     */
    public static class UserNotRegisteredException extends RuntimeException {
        public UserNotRegisteredException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when attempting to create a user that already exists.
     */
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}
