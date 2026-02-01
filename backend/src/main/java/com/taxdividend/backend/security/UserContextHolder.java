package com.taxdividend.backend.security;

/**
 * Thread-local holder for user context extracted from X-User-Context header.
 *
 * Usage in services:
 * <pre>
 * UserContext context = UserContextHolder.get();
 * UUID userId = context.userId();
 * </pre>
 *
 * The context is automatically set by InternalApiKeyFilter and cleared after request processing.
 */
public class UserContextHolder {
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * Sets the user context for the current thread.
     * Should only be called by security filters.
     */
    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    /**
     * Gets the user context for the current thread.
     *
     * @return UserContext or null if not authenticated
     */
    public static UserContext get() {
        return CONTEXT.get();
    }

    /**
     * Clears the user context for the current thread.
     * Must be called after request processing to prevent memory leaks.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
