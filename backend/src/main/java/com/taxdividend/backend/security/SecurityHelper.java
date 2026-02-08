package com.taxdividend.backend.security;

import java.util.UUID;

/**
 * Helper class for security-related operations.
 */
public class SecurityHelper {

    /**
     * Get the current authenticated user's ID from the UserContext.
     *
     * @return UUID of the current user
     * @throws IllegalStateException if no user context is available
     */
    public static UUID getCurrentUserId() {
        UserContext context = UserContextHolder.get();
        if (context == null) {
            throw new IllegalStateException("No user context available. Request must be authenticated.");
        }
        return context.userId();
    }

    /**
     * Get the current user context.
     *
     * @return UserContext or null if not authenticated
     */
    public static UserContext getCurrentUserContext() {
        return UserContextHolder.get();
    }
}
