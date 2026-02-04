package com.taxdividend.backend.service;

import com.taxdividend.backend.model.AuditLog;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.AuditLogRepository;
import com.taxdividend.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Audit Service Tests")
class AuditServiceTest {

        @Mock
        private AuditLogRepository auditLogRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private AuditService auditService;

        private User testUser;
        private UUID testUserId;

        @BeforeEach
        void setUp() {
                testUserId = UUID.randomUUID();
                testUser = User.builder()
                                .id(testUserId)
                                .email("test@example.com")
                                .build();
        }

        @Test
        @DisplayName("Should log action successfully")
        void shouldLogActionSuccessfully() {
                // Given
                String action = "LOGIN";
                String entityType = "USER";
                UUID entityId = testUserId;
                Map<String, Object> details = new HashMap<>();
                details.put("email", "test@example.com");
                String ipAddress = "192.168.1.1";
                String userAgent = "Mozilla/5.0";

                when(userRepository.findById(testUserId))
                                .thenReturn(Optional.of(testUser));
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logAction(
                                testUserId, action, entityType, entityId, details, ipAddress, userAgent);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo(action);
                assertThat(result.getEntityType()).isEqualTo(entityType);
                assertThat(result.getEntityId()).isEqualTo(entityId);
                assertThat(result.getDetails()).isEqualTo(details);
                assertThat(result.getIpAddress()).isEqualTo(ipAddress);
                assertThat(result.getUserAgent()).isEqualTo(userAgent);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should log successful login")
        void shouldLogSuccessfulLogin() {
                // Given
                String email = "test@example.com";
                String ipAddress = "192.168.1.1";
                String userAgent = "Mozilla/5.0";

                when(userRepository.findById(testUserId))
                                .thenReturn(Optional.of(testUser));
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logLogin(
                                testUserId, email, true, ipAddress, userAgent);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo("LOGIN");
                assertThat(result.getDetails()).containsEntry("email", email);
                assertThat(result.getDetails()).containsEntry("success", true);
                assertThat(result.getIpAddress()).isEqualTo(ipAddress);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should log failed login")
        void shouldLogFailedLogin() {
                // Given
                String email = "test@example.com";
                String ipAddress = "192.168.1.1";
                String userAgent = "Mozilla/5.0";

                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logLogin(
                                null, email, false, ipAddress, userAgent);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo("LOGIN_FAILED");
                assertThat(result.getDetails()).containsEntry("success", false);
                assertThat(result.getIpAddress()).isEqualTo(ipAddress);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should log logout")
        void shouldLogLogout() {
                // Given
                String ipAddress = "192.168.1.1";

                when(userRepository.findById(testUserId))
                                .thenReturn(Optional.of(testUser));
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logLogout(testUserId, ipAddress);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo("LOGOUT");
                assertThat(result.getIpAddress()).isEqualTo(ipAddress);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should log PDF parsing")
        void shouldLogPdfParsing() {
                // Given
                String fileName = "bank_statement.pdf";
                int dividendCount = 5;
                boolean success = true;

                when(userRepository.findById(testUserId))
                                .thenReturn(Optional.of(testUser));
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logPdfParsing(
                                testUserId, fileName, dividendCount, success);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo("PDF_PARSED");
                assertThat(result.getEntityType()).isEqualTo("DIVIDEND");
                assertThat(result.getDetails()).containsEntry("fileName", fileName);
                assertThat(result.getDetails()).containsEntry("dividendCount", dividendCount);
                assertThat(result.getDetails()).containsEntry("success", success);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should log form generation")
        void shouldLogFormGeneration() {
                // Given
                UUID formId = UUID.randomUUID();
                String formType = "5000";
                int dividendCount = 3;

                when(userRepository.findById(testUserId))
                                .thenReturn(Optional.of(testUser));
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logFormGeneration(
                                testUserId, formId, formType, dividendCount);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo("FORM_GENERATED");
                assertThat(result.getEntityType()).isEqualTo("FORM");
                assertThat(result.getEntityId()).isEqualTo(formId);
                assertThat(result.getDetails()).containsEntry("formType", formType);
                assertThat(result.getDetails()).containsEntry("dividendCount", dividendCount);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should log tax calculation")
        void shouldLogTaxCalculation() {
                // Given
                int dividendCount = 10;
                String totalReclaimable = "500.00";

                when(userRepository.findById(testUserId))
                                .thenReturn(Optional.of(testUser));
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logTaxCalculation(
                                testUserId, dividendCount, totalReclaimable);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getAction()).isEqualTo("TAX_CALCULATED");
                assertThat(result.getEntityType()).isEqualTo("DIVIDEND");
                assertThat(result.getDetails()).containsEntry("dividendCount", dividendCount);
                assertThat(result.getDetails()).containsEntry("totalReclaimable", totalReclaimable);

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("Should get user logs with limit")
        void shouldGetUserLogsWithLimit() {
                // Given
                int limit = 10;
                List<AuditLog> expectedLogs = Arrays.asList(
                                createMockAuditLog("LOGIN"),
                                createMockAuditLog("FORM_GENERATED"));

                when(auditLogRepository.findRecentByUserId(eq(testUserId), any(Pageable.class)))
                                .thenReturn(expectedLogs);

                // When
                List<AuditLog> result = auditService.getUserLogs(testUserId, limit);

                // Then
                assertThat(result).hasSize(2);
                assertThat(result).isEqualTo(expectedLogs);

                verify(auditLogRepository).findRecentByUserId(
                                eq(testUserId),
                                argThat(pageable -> pageable.getPageSize() == limit &&
                                                pageable.getPageNumber() == 0));
        }

        @Test
        @DisplayName("Should get failed login attempts")
        void shouldGetFailedLoginAttempts() {
                // Given
                String ipAddress = "192.168.1.1";
                LocalDateTime since = LocalDateTime.now().minusMinutes(30);
                List<AuditLog> failedAttempts = Arrays.asList(
                                createMockAuditLog("LOGIN_FAILED"),
                                createMockAuditLog("LOGIN_FAILED"));

                when(auditLogRepository.findFailedLoginAttempts(ipAddress, since))
                                .thenReturn(failedAttempts);

                // When
                List<AuditLog> result = auditService.getFailedLoginAttempts(ipAddress, since);

                // Then
                assertThat(result).hasSize(2);
                assertThat(result).isEqualTo(failedAttempts);

                verify(auditLogRepository).findFailedLoginAttempts(ipAddress, since);
        }

        @Test
        @DisplayName("Should detect rate limiting")
        void shouldDetectRateLimiting() {
                // Given
                String ipAddress = "192.168.1.1";
                int maxAttempts = 3;
                int windowMinutes = 15;

                List<AuditLog> failedAttempts = Arrays.asList(
                                createMockAuditLog("LOGIN_FAILED"),
                                createMockAuditLog("LOGIN_FAILED"),
                                createMockAuditLog("LOGIN_FAILED"));

                when(auditLogRepository.findFailedLoginAttempts(
                                eq(ipAddress), any(LocalDateTime.class)))
                                .thenReturn(failedAttempts);

                // When
                boolean isLimited = auditService.isRateLimited(ipAddress, maxAttempts, windowMinutes);

                // Then
                assertThat(isLimited).isTrue();
                verify(auditLogRepository).findFailedLoginAttempts(
                                eq(ipAddress), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Should not rate limit with few attempts")
        void shouldNotRateLimitWithFewAttempts() {
                // Given
                String ipAddress = "192.168.1.1";
                int maxAttempts = 5;
                int windowMinutes = 15;

                List<AuditLog> failedAttempts = Arrays.asList(
                                createMockAuditLog("LOGIN_FAILED"),
                                createMockAuditLog("LOGIN_FAILED"));

                when(auditLogRepository.findFailedLoginAttempts(
                                eq(ipAddress), any(LocalDateTime.class)))
                                .thenReturn(failedAttempts);

                // When
                boolean isLimited = auditService.isRateLimited(ipAddress, maxAttempts, windowMinutes);

                // Then
                assertThat(isLimited).isFalse();
        }

        @Test
        @DisplayName("Should cleanup old logs")
        void shouldCleanupOldLogs() {
                // Given
                int retentionDays = 90;
                long countBefore = 1000;
                long countAfter = 500;

                when(auditLogRepository.count())
                                .thenReturn(countBefore)
                                .thenReturn(countAfter);

                // When
                int deletedCount = auditService.cleanupOldLogs(retentionDays);

                // Then
                assertThat(deletedCount).isEqualTo(500);
                verify(auditLogRepository, times(2)).count();
                verify(auditLogRepository).deleteOlderThan(any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Should get user action statistics")
        void shouldGetUserActionStatistics() {
                // Given
                List<AuditLog> logs = Arrays.asList(
                                createMockAuditLog("LOGIN"),
                                createMockAuditLog("LOGIN"),
                                createMockAuditLog("FORM_GENERATED"),
                                createMockAuditLog("TAX_CALCULATED"));

                when(auditLogRepository.findByUserId(testUserId))
                                .thenReturn(logs);

                // When
                Map<String, Long> statistics = auditService.getUserActionStatistics(testUserId);

                // Then
                assertThat(statistics).containsEntry("LOGIN", 2L);
                assertThat(statistics).containsEntry("FORM_GENERATED", 1L);
                assertThat(statistics).containsEntry("TAX_CALCULATED", 1L);

                verify(auditLogRepository).findByUserId(testUserId);
        }

        @Test
        @DisplayName("Should handle null user ID gracefully")
        void shouldHandleNullUserIdGracefully() {
                // Given
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logAction(
                                null, "ACTION", "TYPE", null, null, "192.168.1.1", "Mozilla");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getUser()).isNull();

                verify(auditLogRepository).save(any(AuditLog.class));
                verify(userRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should handle user not found")
        void shouldHandleUserNotFound() {
                // Given
                UUID nonExistentUserId = UUID.randomUUID();
                when(userRepository.findById(nonExistentUserId))
                                .thenReturn(Optional.empty());
                when(auditLogRepository.save(any(AuditLog.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                AuditLog result = auditService.logAction(
                                nonExistentUserId, "ACTION", null, null, null, null, null);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getUser()).isNull();

                verify(auditLogRepository).save(any(AuditLog.class));
        }

        // Helper method
        private AuditLog createMockAuditLog(String action) {
                return AuditLog.builder()
                                .id(UUID.randomUUID())
                                .action(action)
                                .user(testUser)
                                .createdAt(LocalDateTime.now())
                                .build();
        }
}
