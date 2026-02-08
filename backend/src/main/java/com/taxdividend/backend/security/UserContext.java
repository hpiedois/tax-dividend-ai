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
 * Example header value (SSO):
 * {"userId":"123e4567-e89b-12d3-a456-426614174000","email":"user@example.com","roles":["USER"],"identityProvider":"google"}
 *
 * Example header value (classic):
 * {"userId":"123e4567-e89b-12d3-a456-426614174000","email":"user@example.com","roles":["USER"],"identityProvider":"keycloak"}
 *
 * Security model:
 * - Backend trusts the BFF Gateway (internal network only)
 * - BFF Gateway is responsible for authentication (Keycloak JWT validation)
 * - Backend validates that requests come from BFF via X-Internal-Api-Key
 * - Backend uses user context for authorization (ownership checks, etc.)
 * - identityProvider used to determine auto-provisioning for SSO users
 */
@Slf4j
public record UserContext(
    UUID userId,
    String email,
    String[] roles,
    String identityProvider
) {
    @JsonCreator
    public UserContext(
        @JsonProperty("userId") UUID userId,
        @JsonProperty("email") String email,
        @JsonProperty("roles") String[] roles,
        @JsonProperty("identityProvider") String identityProvider
    ) {
        this.userId = userId;
        this.email = email;
        this.roles = roles != null ? roles : new String[0];
        this.identityProvider = identityProvider;
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

    /**
     * Checks if user authenticated via SSO (Google, GitHub, etc.)
     * rather than classic email/password.
     *
     * @return true if SSO login, false for classic login
     */
    public boolean isSsoLogin() {
        return identityProvider != null &&
               !identityProvider.isEmpty() &&
               !identityProvider.equalsIgnoreCase("keycloak");
    }

    /**
     * Gets the registration source based on identity provider.
     *
     * @return RegistrationSource enum value
     */
    public String getRegistrationSource() {
        if (identityProvider == null || identityProvider.isEmpty() || identityProvider.equalsIgnoreCase("keycloak")) {
            return "CLASSIC";
        }
        return identityProvider.toUpperCase() + "_SSO";
    }
}
