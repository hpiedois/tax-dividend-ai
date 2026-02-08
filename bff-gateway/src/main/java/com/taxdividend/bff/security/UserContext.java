package com.taxdividend.bff.security;

import java.util.List;

/**
 * User context extracted from Keycloak JWT and sent to Backend.
 *
 * @param userId Keycloak subject (sub claim)
 * @param email User email
 * @param roles User roles/authorities
 * @param identityProvider Identity provider used for authentication (e.g., "google", "github", "keycloak")
 *                         Used by Backend to determine if auto-provisioning is needed for SSO users
 */
public record UserContext(
        String userId,
        String email,
        List<String> roles,
        String identityProvider
) {
}
