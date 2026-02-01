package com.taxdividend.backend.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing users in Keycloak via Admin API.
 *
 * This service handles:
 * - User creation with email verification
 * - Password management
 * - User attribute management
 * - User status updates
 *
 * All operations are performed on the configured Keycloak realm.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final Keycloak keycloakAdminClient;

    @Qualifier("keycloakRealmName")
    private final String realmName;

    /**
     * Creates a new user in Keycloak with email verification enabled.
     *
     * @param email User email (will be username)
     * @param password User password (plain text, will be hashed by Keycloak)
     * @param firstName User first name
     * @param lastName User last name
     * @return Keycloak user ID (UUID)
     * @throws KeycloakUserCreationException if user creation fails
     */
    public String createUser(String email, String password, String firstName, String lastName) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(realmName);
            UsersResource usersResource = realmResource.users();

            // Check if user already exists
            List<UserRepresentation> existingUsers = usersResource.search(email, true);
            if (!existingUsers.isEmpty()) {
                throw new KeycloakUserCreationException("User with email " + email + " already exists in Keycloak");
            }

            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(email);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(false);  // Require email verification

            // Create user
            Response response = usersResource.create(user);

            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                log.error("Failed to create Keycloak user. Status: {}, Error: {}", response.getStatus(), errorMessage);
                throw new KeycloakUserCreationException("Failed to create user in Keycloak: " + errorMessage);
            }

            // Extract user ID from Location header
            String locationHeader = response.getHeaderString("Location");
            String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
            log.info("Created Keycloak user: {} with ID: {}", email, userId);

            // Set password
            setUserPassword(userId, password);

            // Send verification email
            sendVerificationEmail(userId);

            return userId;

        } catch (Exception e) {
            log.error("Error creating Keycloak user: {}", email, e);
            throw new KeycloakUserCreationException("Failed to create user in Keycloak", e);
        }
    }

    /**
     * Sets or updates a user's password in Keycloak.
     *
     * @param userId Keycloak user ID
     * @param password New password (plain text)
     */
    public void setUserPassword(String userId, String password) {
        try {
            UserResource userResource = keycloakAdminClient.realm(realmName)
                .users()
                .get(userId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);  // Password is permanent (not temporary)

            userResource.resetPassword(credential);
            log.debug("Password set for Keycloak user: {}", userId);

        } catch (Exception e) {
            log.error("Error setting password for Keycloak user: {}", userId, e);
            throw new KeycloakUserCreationException("Failed to set user password", e);
        }
    }

    /**
     * Sends email verification link to user.
     *
     * Keycloak will send an email with a verification link.
     * Once the user clicks the link, their emailVerified flag is set to true.
     *
     * @param userId Keycloak user ID
     */
    public void sendVerificationEmail(String userId) {
        try {
            UserResource userResource = keycloakAdminClient.realm(realmName)
                .users()
                .get(userId);

            userResource.sendVerifyEmail();
            log.info("Verification email sent for Keycloak user: {}", userId);

        } catch (Exception e) {
            log.error("Error sending verification email for Keycloak user: {}", userId, e);
            throw new KeycloakUserCreationException("Failed to send verification email", e);
        }
    }

    /**
     * Gets user information from Keycloak.
     *
     * @param userId Keycloak user ID
     * @return UserRepresentation with user details
     */
    public UserRepresentation getUser(String userId) {
        try {
            return keycloakAdminClient.realm(realmName)
                .users()
                .get(userId)
                .toRepresentation();

        } catch (Exception e) {
            log.error("Error fetching Keycloak user: {}", userId, e);
            throw new KeycloakUserCreationException("Failed to fetch user from Keycloak", e);
        }
    }

    /**
     * Checks if user's email is verified in Keycloak.
     *
     * @param userId Keycloak user ID
     * @return true if email is verified
     */
    public boolean isEmailVerified(String userId) {
        try {
            UserRepresentation user = getUser(userId);
            return user.isEmailVerified();

        } catch (Exception e) {
            log.error("Error checking email verification for user: {}", userId, e);
            return false;
        }
    }

    /**
     * Exception thrown when Keycloak user operations fail.
     */
    public static class KeycloakUserCreationException extends RuntimeException {
        public KeycloakUserCreationException(String message) {
            super(message);
        }

        public KeycloakUserCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
