package com.taxdividend.backend.repository;

import com.taxdividend.backend.model.AuditLog;
import com.taxdividend.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * Find audit logs by user
     */
    List<AuditLog> findByUser(User user);

    /**
     * Find audit logs by user ID
     */
    List<AuditLog> findByUserId(UUID userId);

    /**
     * Find audit logs by user with pagination
     */
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find audit logs by action
     */
    List<AuditLog> findByAction(String action);

    /**
     * Find audit logs by entity type
     */
    List<AuditLog> findByEntityType(String entityType);

    /**
     * Find audit logs by entity type and entity ID
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    /**
     * Find audit logs created between dates
     */
    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find audit logs by user and action
     */
    List<AuditLog> findByUserIdAndAction(UUID userId, String action);

    /**
     * Find audit logs by user and date range
     */
    List<AuditLog> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    /**
     * Find recent audit logs for a user (last N logs)
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find audit logs by IP address
     */
    List<AuditLog> findByIpAddress(String ipAddress);

    /**
     * Find failed login attempts by IP address within time range
     */
    @Query("SELECT al FROM AuditLog al WHERE al.action = 'LOGIN_FAILED' AND al.ipAddress = :ipAddress AND al.createdAt > :since")
    List<AuditLog> findFailedLoginAttempts(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Delete old audit logs (data retention policy)
     */
    @Query("DELETE FROM AuditLog al WHERE al.createdAt < :cutoffDate")
    void deleteOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
