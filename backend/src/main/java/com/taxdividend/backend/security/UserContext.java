package com.taxdividend.backend.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * User context extracted from X-User-Context header sent by BFF Gateway.
 *
 * The BFF Gateway validates Keycloak JWT tokens and sends user information
 * to the backend via the X-User-Context header as JSON.
 *
 * Example header value:
 * {"userId":"123e4567-e89b-12d3-a456-426614174000","email":"user@example.com","roles":["USER"]}
 *
 * Security model:
 * - Backend trusts the BFF Gateway (internal network only)
 * - BFF Gateway is responsible for authentication (Keycloak JWT validation)
 * - Backend validates that requests come from BFF via X-Internal-Api-Key
 * - Backend uses user context for authorization (ownership checks, etc.)
 */
@Slf4j
public record UserContext(
    UUID userId,
    String email,
    String[] roles
) {
    @JsonCreator
    public UserContext(
        @JsonProperty("userId") UUID userId,
        @JsonProperty("email") String email,
        @JsonProperty("roles") String[] roles
    ) {
        this.userId = userId;
        this.email = email;
        this.roles = roles != null ? roles : new String[0];
    }

    /**
     * Checks if user has a specific role.
     */
    public boolean hasRole(String role) {
        if (roles == null) return false;
        for (String r : roles) {
            if (role.equalsIgnoreCase(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if user has admin role.
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
