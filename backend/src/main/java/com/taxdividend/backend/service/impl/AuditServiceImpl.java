package com.taxdividend.backend.service.impl;

import com.taxdividend.backend.model.AuditLog;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.AuditLogRepository;
import com.taxdividend.backend.repository.UserRepository;
import com.taxdividend.backend.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of audit service for logging user actions and security events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuditLog logAction(UUID userId, String action, String entityType, UUID entityId,
                              Map<String, Object> details, String ipAddress, String userAgent) {

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        AuditLog saved = auditLogRepository.save(auditLog);

        log.info("Audit log created: action={}, user={}, entity={}:{}",
                 action, userId, entityType, entityId);

        return saved;
    }

    @Override
    @Transactional
    public AuditLog logAction(UUID userId, String action, Map<String, Object> details,
                              String ipAddress, String userAgent) {
        return logAction(userId, action, null, null, details, ipAddress, userAgent);
    }

    @Override
    @Transactional
    public AuditLog logAction(UUID userId, String action) {
        return logAction(userId, action, null, null, null, null, null);
    }

    @Override
    @Transactional
    public AuditLog logLogin(UUID userId, String email, boolean success, String ipAddress, String userAgent) {
        String action = success ? "LOGIN" : "LOGIN_FAILED";

        Map<String, Object> details = new HashMap<>();
        details.put("email", email);
        details.put("success", success);

        if (!success) {
            log.warn("Failed login attempt for email: {} from IP: {}", email, ipAddress);
        } else {
            log.info("Successful login for user: {} from IP: {}", userId, ipAddress);
        }

        return logAction(userId, action, details, ipAddress, userAgent);
    }

    @Override
    @Transactional
    public AuditLog logLogout(UUID userId, String ipAddress) {
        log.info("User logout: {} from IP: {}", userId, ipAddress);

        return logAction(userId, "LOGOUT", null, ipAddress, null);
    }

    @Override
    @Transactional
    public AuditLog logPdfParsing(UUID userId, String fileName, int dividendCount, boolean success) {
        Map<String, Object> details = new HashMap<>();
        details.put("fileName", fileName);
        details.put("dividendCount", dividendCount);
        details.put("success", success);

        String action = success ? "PDF_PARSED" : "PDF_PARSE_FAILED";

        log.info("PDF parsing logged: user={}, file={}, dividends={}, success={}",
                 userId, fileName, dividendCount, success);

        return logAction(userId, action, "DIVIDEND", null, details, null, null);
    }

    @Override
    @Transactional
    public AuditLog logFormGeneration(UUID userId, UUID formId, String formType, int dividendCount) {
        Map<String, Object> details = new HashMap<>();
        details.put("formType", formType);
        details.put("dividendCount", dividendCount);

        log.info("Form generation logged: user={}, form={}, type={}, dividends={}",
                 userId, formId, formType, dividendCount);

        return logAction(userId, "FORM_GENERATED", "FORM", formId, details, null, null);
    }

    @Override
    @Transactional
    public AuditLog logTaxCalculation(UUID userId, int dividendCount, String totalReclaimable) {
        Map<String, Object> details = new HashMap<>();
        details.put("dividendCount", dividendCount);
        details.put("totalReclaimable", totalReclaimable);

        log.info("Tax calculation logged: user={}, dividends={}, total={}",
                 userId, dividendCount, totalReclaimable);

        return logAction(userId, "TAX_CALCULATED", "DIVIDEND", null, details, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getUserLogs(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return auditLogRepository.findRecentByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByAction(String action, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return auditLogRepository.findByAction(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getFailedLoginAttempts(String ipAddress, LocalDateTime since) {
        return auditLogRepository.findFailedLoginAttempts(ipAddress, since);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRateLimited(String ipAddress, int maxAttempts, int windowMinutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        List<AuditLog> failedAttempts = getFailedLoginAttempts(ipAddress, since);

        boolean isLimited = failedAttempts.size() >= maxAttempts;

        if (isLimited) {
            log.warn("IP address {} is rate-limited: {} failed attempts in {} minutes",
                     ipAddress, failedAttempts.size(), windowMinutes);
        }

        return isLimited;
    }

    @Override
    @Transactional
    public int cleanupOldLogs(int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);

        log.info("Cleaning up audit logs older than {} days (before {})", retentionDays, cutoffDate);

        // Note: This should be done in batches for large datasets
        long countBefore = auditLogRepository.count();
        auditLogRepository.deleteOlderThan(cutoffDate);
        long countAfter = auditLogRepository.count();

        int deletedCount = (int) (countBefore - countAfter);

        log.info("Deleted {} old audit logs", deletedCount);

        return deletedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getUserActionStatistics(UUID userId) {
        List<AuditLog> allLogs = auditLogRepository.findByUserId(userId);

        Map<String, Long> statistics = allLogs.stream()
                .collect(Collectors.groupingBy(
                        AuditLog::getAction,
                        Collectors.counting()
                ));

        log.debug("Action statistics for user {}: {}", userId, statistics);

        return statistics;
    }
}
