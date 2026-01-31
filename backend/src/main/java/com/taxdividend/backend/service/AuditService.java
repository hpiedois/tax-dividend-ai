package com.taxdividend.backend.service;

import com.taxdividend.backend.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing audit logs and security trail.
 *
 * Tracks:
 * - User actions (login, logout, form generation, etc.)
 * - Security events (failed logins, permission changes)
 * - Data modifications (CRUD operations)
 */
public interface AuditService {

    /**
     * Log a user action.
     *
     * @param userId User ID (can be null for anonymous actions)
     * @param action Action name (e.g., "LOGIN", "FORM_GENERATED")
     * @param entityType Type of entity affected (e.g., "USER", "FORM", "DIVIDEND")
     * @param entityId ID of the entity affected
     * @param details Additional details as JSON
     * @param ipAddress Client IP address
     * @param userAgent Client user agent string
     * @return Created audit log
     */
    AuditLog logAction(UUID userId, String action, String entityType, UUID entityId,
                       Map<String, Object> details, String ipAddress, String userAgent);

    /**
     * Log a user action without entity.
     *
     * @param userId User ID
     * @param action Action name
     * @param details Additional details
     * @param ipAddress Client IP
     * @param userAgent User agent
     * @return Created audit log
     */
    AuditLog logAction(UUID userId, String action, Map<String, Object> details,
                       String ipAddress, String userAgent);

    /**
     * Log a simple action without details.
     *
     * @param userId User ID
     * @param action Action name
     * @return Created audit log
     */
    AuditLog logAction(UUID userId, String action);

    /**
     * Log a login attempt.
     *
     * @param userId User ID (null if login failed)
     * @param email Email used for login
     * @param success Whether login was successful
     * @param ipAddress Client IP
     * @param userAgent User agent
     * @return Created audit log
     */
    AuditLog logLogin(UUID userId, String email, boolean success, String ipAddress, String userAgent);

    /**
     * Log a logout.
     *
     * @param userId User ID
     * @param ipAddress Client IP
     * @return Created audit log
     */
    AuditLog logLogout(UUID userId, String ipAddress);

    /**
     * Log PDF parsing action.
     *
     * @param userId User ID
     * @param fileName PDF filename
     * @param dividendCount Number of dividends found
     * @param success Whether parsing succeeded
     * @return Created audit log
     */
    AuditLog logPdfParsing(UUID userId, String fileName, int dividendCount, boolean success);

    /**
     * Log form generation.
     *
     * @param userId User ID
     * @param formId Generated form ID
     * @param formType Form type (5000, 5001, BUNDLE)
     * @param dividendCount Number of dividends included
     * @return Created audit log
     */
    AuditLog logFormGeneration(UUID userId, UUID formId, String formType, int dividendCount);

    /**
     * Log tax calculation.
     *
     * @param userId User ID
     * @param dividendCount Number of dividends calculated
     * @param totalReclaimable Total reclaimable amount
     * @return Created audit log
     */
    AuditLog logTaxCalculation(UUID userId, int dividendCount, String totalReclaimable);

    /**
     * Get audit logs for a user.
     *
     * @param userId User ID
     * @param limit Maximum number of logs to return
     * @return List of audit logs
     */
    List<AuditLog> getUserLogs(UUID userId, int limit);

    /**
     * Get audit logs by action type.
     *
     * @param action Action name
     * @param limit Maximum number of logs
     * @return List of audit logs
     */
    List<AuditLog> getLogsByAction(String action, int limit);

    /**
     * Get failed login attempts for an IP address.
     *
     * @param ipAddress IP address
     * @param since Time threshold (e.g., last 15 minutes)
     * @return List of failed login attempts
     */
    List<AuditLog> getFailedLoginAttempts(String ipAddress, LocalDateTime since);

    /**
     * Check if IP address is rate-limited due to failed logins.
     *
     * @param ipAddress IP address
     * @param maxAttempts Maximum allowed attempts
     * @param windowMinutes Time window in minutes
     * @return true if rate-limited
     */
    boolean isRateLimited(String ipAddress, int maxAttempts, int windowMinutes);

    /**
     * Delete old audit logs (data retention policy).
     *
     * @param retentionDays Number of days to retain logs
     * @return Number of logs deleted
     */
    int cleanupOldLogs(int retentionDays);

    /**
     * Get audit log statistics for a user.
     *
     * @param userId User ID
     * @return Map of action counts
     */
    Map<String, Long> getUserActionStatistics(UUID userId);
}
