package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.HealthApi;
import com.taxdividend.backend.repository.TaxRuleRepository;
import com.taxdividend.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom health check controller for application-specific health indicators.
 * Complements Spring Boot Actuator with business-logic specific checks.
 *
 * Endpoints:
 * - GET /internal/health/deep - Comprehensive health check (database, storage,
 * tax rules)
 * - GET /internal/health/database - Database connectivity check
 * - GET /internal/health/storage - MinIO storage availability check
 * - GET /internal/health/tax-rules - Tax rules data availability check
 * - GET /internal/health/services - Service layer readiness check
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthController implements HealthApi {

    private final JdbcTemplate jdbcTemplate;
    private final StorageService storageService;
    private final TaxRuleRepository taxRuleRepository;

    /**
     * Deep health check - verifies all critical components.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.HealthCheckResponse> deepHealthCheck() {
        com.taxdividend.backend.api.dto.HealthCheckResponse health = new com.taxdividend.backend.api.dto.HealthCheckResponse();

        health.setApplication("tax-dividend-backend");

        // Check database
        Map<String, Object> dbHealth = checkDatabase();
        health.setDatabase(dbHealth);

        // Check storage
        Map<String, Object> storageHealth = checkStorage();
        health.setStorage(storageHealth);

        // Check tax rules
        Map<String, Object> taxRulesHealth = checkTaxRules();
        health.setTaxRules(taxRulesHealth);

        // Overall status
        boolean overallHealthy = "UP".equals(dbHealth.get("status")) &&
                "UP".equals(storageHealth.get("status")) &&
                "UP".equals(taxRulesHealth.get("status"));

        health.setStatus(overallHealthy ? "UP" : "DOWN");

        log.info("Deep health check completed: status={}", health.getStatus());

        return ResponseEntity.ok(health);
    }

    /**
     * Liveness probe.
     */
    @Override
    public ResponseEntity<Void> liveness() {
        return ResponseEntity.ok().build();
    }

    /**
     * Readiness probe.
     */
    @Override
    public ResponseEntity<Void> readiness() {
        try {
            // Quick database check
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            // Verify tax rules are loaded
            if (taxRuleRepository.count() > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        } catch (Exception e) {
            log.error("Readiness probe failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    // Helper methods (private) remain the same
    private Map<String, Object> checkDatabase() {
        Map<String, Object> health = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            long queryTime = System.currentTimeMillis() - startTime;

            if (result != null && result == 1) {
                health.put("status", "UP");
                health.put("queryTime", queryTime + "ms");
            } else {
                health.put("status", "DOWN");
            }
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }
        return health;
    }

    private Map<String, Object> checkStorage() {
        Map<String, Object> health = new HashMap<>();
        try {
            boolean bucketExists = storageService.fileExists("health-check-probe");
            health.put("status", "UP");
            health.put("accessible", true);
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }
        return health;
    }

    private Map<String, Object> checkTaxRules() {
        Map<String, Object> health = new HashMap<>();
        try {
            long totalRules = taxRuleRepository.count();
            if (totalRules > 0) {
                health.put("status", "UP");
                health.put("totalRules", totalRules);
            } else {
                health.put("status", "DOWN");
            }
        } catch (Exception e) {
            health.put("status", "DOWN");
        }
        return health;
    }
}
