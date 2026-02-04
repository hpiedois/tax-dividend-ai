package com.taxdividend.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates required environment variables on application startup.
 * Fails fast if critical configuration is missing.
 *
 * This prevents the application from starting with insecure default values.
 */
@Component
@Slf4j
public class EnvironmentValidator {

    private final Environment environment;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public EnvironmentValidator(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateEnvironment() {
        log.info("Validating environment configuration for profile: {}", activeProfile);

        // Skip validation in 'dev' profile (uses safe local defaults)
        if ("dev".equals(activeProfile)) {
            log.info("Development profile active - skipping environment validation");
            return;
        }

        List<String> missingVariables = new ArrayList<>();

        // Critical database credentials
        validateRequired("DB_PASSWORD", missingVariables);
        validateRequired("DB_USERNAME", missingVariables);

        // Critical storage credentials
        validateRequired("MINIO_ACCESS_KEY", missingVariables);
        validateRequired("MINIO_SECRET_KEY", missingVariables);

        // Critical security credentials
        validateRequired("INTERNAL_API_KEY", missingVariables);
        validateMinLength("INTERNAL_API_KEY", 32, missingVariables);

        // Actuator security
        validateRequired("ACTUATOR_USERNAME", missingVariables);
        validateRequired("ACTUATOR_PASSWORD", missingVariables);

        // Keycloak admin credentials
        validateRequired("KEYCLOAK_ADMIN_USERNAME", missingVariables);
        validateRequired("KEYCLOAK_ADMIN_PASSWORD", missingVariables);

        // Fail fast if any critical variable is missing
        if (!missingVariables.isEmpty()) {
            String errorMessage = String.format(
                    "CRITICAL: Application startup failed due to missing or invalid environment variables:%n%s%n" +
                            "Please set these environment variables or use profile 'dev' for local development.",
                    String.join("\n", missingVariables)
            );
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        // Warn about weak passwords (for production profile)
        if ("prod".equals(activeProfile) || "production".equals(activeProfile)) {
            validatePasswordStrength();
        }

        log.info("Environment validation completed successfully");
    }

    private void validateRequired(String varName, List<String> missingVariables) {
        String value = environment.getProperty(varName);
        if (value == null || value.trim().isEmpty()) {
            missingVariables.add(String.format("  - %s is not set", varName));
        }
    }

    private void validateMinLength(String varName, int minLength, List<String> missingVariables) {
        String value = environment.getProperty(varName);
        if (value != null && value.length() < minLength) {
            missingVariables.add(String.format("  - %s must be at least %d characters (current: %d)",
                    varName, minLength, value.length()));
        }
    }

    private void validatePasswordStrength() {
        List<String> weakPasswords = Arrays.asList(
                "admin", "password", "changeme", "dev_password", "minioadmin"
        );

        String dbPassword = environment.getProperty("DB_PASSWORD", "");
        String actuatorPassword = environment.getProperty("ACTUATOR_PASSWORD", "");
        String keycloakPassword = environment.getProperty("KEYCLOAK_ADMIN_PASSWORD", "");

        List<String> warnings = new ArrayList<>();

        if (dbPassword.length() < 16) {
            warnings.add("DB_PASSWORD should be at least 16 characters for production");
        }
        if (containsWeakPassword(dbPassword, weakPasswords)) {
            warnings.add("DB_PASSWORD appears to use a common/weak password");
        }

        if (actuatorPassword.length() < 16) {
            warnings.add("ACTUATOR_PASSWORD should be at least 16 characters for production");
        }
        if (containsWeakPassword(actuatorPassword, weakPasswords)) {
            warnings.add("ACTUATOR_PASSWORD appears to use a common/weak password");
        }

        if (keycloakPassword.length() < 16) {
            warnings.add("KEYCLOAK_ADMIN_PASSWORD should be at least 16 characters for production");
        }
        if (containsWeakPassword(keycloakPassword, weakPasswords)) {
            warnings.add("KEYCLOAK_ADMIN_PASSWORD appears to use a common/weak password");
        }

        if (!warnings.isEmpty()) {
            log.warn("SECURITY WARNING: Weak credentials detected in production profile:");
            warnings.forEach(warning -> log.warn("  - {}", warning));
        }
    }

    private boolean containsWeakPassword(String password, List<String> weakPasswords) {
        String lowerPassword = password.toLowerCase();
        return weakPasswords.stream().anyMatch(lowerPassword::contains);
    }
}
