package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.AuthApi;
import com.taxdividend.backend.api.dto.RegisterUser200Response;
import com.taxdividend.backend.api.dto.RegisterUserRequest;
import com.taxdividend.backend.api.dto.VerifyEmailResponseDTO;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.KeycloakAdminService;
import com.taxdividend.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Authentication controller.
 *
 * Handles user registration with Keycloak integration:
 * 1. Creates user in Keycloak (with email verification)
 * 2. Stores application-specific metadata in PostgreSQL
 * 3. Keycloak sends verification email automatically
 *
 * Email verification is handled entirely by Keycloak:
 * - User clicks verification link in email
 * - Keycloak marks account as verified
 * - Backend can check verification status via Keycloak Admin API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;
    private final AuditService auditService;

    /**
     * Register a new user.
     *
     * This endpoint is PUBLIC (no authentication required).
     *
     * Flow:
     * 1. Validate input
     * 2. Create user in Keycloak (email, password, names)
     * 3. Keycloak sends verification email automatically
     * 4. Store user metadata in PostgreSQL (canton, tax_id, address)
     * 5. Return success with user ID
     *
     * @param registerUserRequest Registration data
     * @return User ID and success message
     */
    @Override
    public ResponseEntity<RegisterUser200Response> registerUser(RegisterUserRequest registerUserRequest) {
        log.info("Registration request received for email: {}", registerUserRequest.getEmail());

        try {
            // Extract data from request
            String email = registerUserRequest.getEmail();
            String password = registerUserRequest.getPassword();

            // Parse full name into first/last (simple split on space)
            String fullName = registerUserRequest.getFullName();
            String[] nameParts = fullName != null ? fullName.split(" ", 2) : new String[]{"", ""};
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            // 1. Create user in Keycloak (this will send verification email)
            String keycloakUserId = keycloakAdminService.createUser(
                email,
                password,
                firstName,
                lastName
            );

            UUID userId = UUID.fromString(keycloakUserId);

            // 2. Store application metadata in PostgreSQL
            String country = registerUserRequest.getCountry() != null ? registerUserRequest.getCountry() : "CH";
            User user = userService.createUser(
                userId,
                email,
                fullName,
                null,  // canton - not in RegisterUserRequest, user can update profile later
                null,  // taxId - not in RegisterUserRequest, user can update profile later
                null   // address - not in RegisterUserRequest, user can update profile later
            );

            // 3. Audit log
            auditService.logAction(
                userId,
                "USER_REGISTERED",
                "USER",
                userId,
                null,
                null,
                null
            );

            log.info("Successfully registered user: {} (ID: {})", email, userId);

            // 4. Return success response
            RegisterUser200Response response = new RegisterUser200Response();
            response.setId(userId);
            response.setMessage(
                "Registration successful! " +
                "Please check your email (" + email + ") to verify your account. " +
                "You won't be able to log in until you verify your email."
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (KeycloakAdminService.KeycloakUserCreationException e) {
            log.error("Keycloak user creation failed: {}", e.getMessage());
            RegisterUser200Response errorResponse = new RegisterUser200Response();
            errorResponse.setMessage("Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (UserService.UserAlreadyExistsException e) {
            log.error("User already exists: {}", e.getMessage());
            RegisterUser200Response errorResponse = new RegisterUser200Response();
            errorResponse.setMessage("User with this email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            RegisterUser200Response errorResponse = new RegisterUser200Response();
            errorResponse.setMessage("Registration failed due to server error. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verify email with token.
     *
     * Note: In a Keycloak-managed flow, email verification is handled entirely by Keycloak.
     * Users click the verification link in the email sent by Keycloak, which verifies the account.
     *
     * This endpoint is a placeholder for custom verification logic if needed.
     * In most cases, you won't need to call this endpoint - Keycloak handles it automatically.
     *
     * @param token Verification token (from email link)
     * @return Verification status
     */
    @Override
    public ResponseEntity<VerifyEmailResponseDTO> verifyEmail(String token) {
        log.info("Email verification request with token: {}", token.substring(0, Math.min(10, token.length())) + "...");

        // In Keycloak-managed flow, this endpoint is typically not needed.
        // Keycloak handles email verification natively.
        // This implementation is a placeholder.

        VerifyEmailResponseDTO response = new VerifyEmailResponseDTO();
        response.setVerified(false);
        response.setMessage(
            "Email verification is handled by Keycloak. " +
            "Please click the verification link in the email sent by Keycloak."
        );

        return ResponseEntity.ok(response);
    }
}

