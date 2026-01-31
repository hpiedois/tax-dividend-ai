package com.taxdividend.backend.controller;

import com.taxdividend.backend.config.TestSecurityConfig;
import com.taxdividend.backend.model.TaxRule;
import com.taxdividend.backend.repository.TaxRuleRepository;
import com.taxdividend.backend.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HealthController.
 */
@WebMvcTest(controllers = HealthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Health Controller Tests")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private StorageService storageService;

    @MockitoBean
    private TaxRuleRepository taxRuleRepository;

    @BeforeEach
    void setUp() {
        // Default healthy state
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenReturn(1);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class))
                .thenReturn(10L);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dividends", Long.class))
                .thenReturn(50L);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM generated_forms", Long.class))
                .thenReturn(5L);

        when(storageService.fileExists(anyString()))
                .thenReturn(true);

        when(taxRuleRepository.count())
                .thenReturn(15L);
        when(taxRuleRepository.findActiveRules(any(LocalDate.class)))
                .thenReturn(Arrays.asList(
                        TaxRule.builder().id(UUID.randomUUID()).build(),
                        TaxRule.builder().id(UUID.randomUUID()).build()
                ));
    }

    @Test
    @DisplayName("Should return UP status for deep health check when all systems healthy")
    void shouldReturnUpForDeepHealthCheck() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/deep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("tax-dividend-backend"))
                .andExpect(jsonPath("$.database.status").value("UP"))
                .andExpect(jsonPath("$.storage.status").value("UP"))
                .andExpect(jsonPath("$.taxRules.status").value("UP"));

        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
        verify(storageService).fileExists(anyString());
        verify(taxRuleRepository).count();
    }

    @Test
    @DisplayName("Should return DOWN status when database is unhealthy")
    void shouldReturnDownWhenDatabaseUnhealthy() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When/Then
        mockMvc.perform(get("/health/deep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database.status").value("DOWN"))
                .andExpect(jsonPath("$.database.error").exists());
    }

    @Test
    @DisplayName("Should return DOWN status when storage is unhealthy")
    void shouldReturnDownWhenStorageUnhealthy() throws Exception {
        // Given
        when(storageService.fileExists(anyString()))
                .thenThrow(new RuntimeException("MinIO unavailable"));

        // When/Then
        mockMvc.perform(get("/health/deep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.storage.status").value("DOWN"))
                .andExpect(jsonPath("$.storage.error").exists());
    }

    @Test
    @DisplayName("Should return DOWN status when no tax rules loaded")
    void shouldReturnDownWhenNoTaxRulesLoaded() throws Exception {
        // Given
        when(taxRuleRepository.count()).thenReturn(0L);

        // When/Then
        mockMvc.perform(get("/health/deep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.taxRules.status").value("DOWN"));
    }

    @Test
    @Disabled("Endpoint /health/database not in OpenAPI spec - contract-first violation")
    @DisplayName("Should check database health with metrics")
    void shouldCheckDatabaseHealth() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.queryTime").exists())
                .andExpect(jsonPath("$.userCount").value(10))
                .andExpect(jsonPath("$.dividendCount").value(50))
                .andExpect(jsonPath("$.formCount").value(5));

        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM users", Long.class);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM dividends", Long.class);
        verify(jdbcTemplate).queryForObject("SELECT COUNT(*) FROM generated_forms", Long.class);
    }

    @Test
    @Disabled("Endpoint /health/storage not in OpenAPI spec - contract-first violation")
    @DisplayName("Should check storage health")
    void shouldCheckStorageHealth() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/storage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.checkTime").exists())
                .andExpect(jsonPath("$.accessible").value(true));

        verify(storageService).fileExists(anyString());
    }

    @Test
    @Disabled("Endpoint /health/tax-rules not in OpenAPI spec - contract-first violation")
    @DisplayName("Should check tax rules health with counts")
    void shouldCheckTaxRulesHealth() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/tax-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.totalRules").value(15))
                .andExpect(jsonPath("$.activeRules").value(2))
                .andExpect(jsonPath("$.checkTime").exists());

        verify(taxRuleRepository).count();
        verify(taxRuleRepository).findActiveRules(any(LocalDate.class));
    }

    @Test
    @Disabled("Endpoint /health/services not in OpenAPI spec - contract-first violation")
    @DisplayName("Should check services health")
    void shouldCheckServicesHealth() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.servicesReady").value(true))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @Disabled("Endpoint /health/info not in OpenAPI spec - contract-first violation")
    @DisplayName("Should return application info")
    void shouldReturnApplicationInfo() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("tax-dividend-backend"))
                .andExpect(jsonPath("$.version").value("0.2.0"))
                .andExpect(jsonPath("$.springBootVersion").value("4.0.2"))
                .andExpect(jsonPath("$.javaVersion").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return UP for liveness probe")
    void shouldReturnUpForLivenessProbe() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/live"))
                .andExpect(status().isOk());
        // No JSON body expected - spec returns ResponseEntity<Void>
    }

    @Test
    @DisplayName("Should return UP for readiness probe when ready")
    void shouldReturnUpForReadinessProbe() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/ready"))
                .andExpect(status().isOk());
        // No JSON body expected - spec returns ResponseEntity<Void>

        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
        verify(taxRuleRepository).count();
    }

    @Test
    @DisplayName("Should return 503 for readiness probe when database down")
    void shouldReturn503ForReadinessWhenDatabaseDown() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new RuntimeException("Database unreachable"));

        // When/Then
        mockMvc.perform(get("/health/ready"))
                .andExpect(status().isServiceUnavailable());
        // No JSON body expected - spec returns ResponseEntity<Void>
    }

    @Test
    @DisplayName("Should return 503 for readiness probe when no tax rules loaded")
    void shouldReturn503ForReadinessWhenNoTaxRules() throws Exception {
        // Given
        when(taxRuleRepository.count()).thenReturn(0L);

        // When/Then
        mockMvc.perform(get("/health/ready"))
                .andExpect(status().isServiceUnavailable());
        // No JSON body expected - spec returns ResponseEntity<Void>
    }

    @Test
    @Disabled("Endpoint /health/database not in OpenAPI spec - contract-first violation")
    @DisplayName("Should measure query time for database check")
    void shouldMeasureQueryTime() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queryTime").exists())
                .andExpect(jsonPath("$.queryTime").isString());
    }

    @Test
    @Disabled("Endpoint /health/database not in OpenAPI spec - contract-first violation")
    @DisplayName("Should handle database metrics retrieval failure gracefully")
    void shouldHandleDatabaseMetricsFailureGracefully() throws Exception {
        // Given - main query succeeds but metrics fail
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class))
                .thenThrow(new RuntimeException("Table not accessible"));

        // When/Then - should still return UP status
        mockMvc.perform(get("/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Should provide comprehensive health data in deep check")
    void shouldProvideComprehensiveHealthData() throws Exception {
        // When/Then
        mockMvc.perform(get("/health/deep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.database").exists())
                .andExpect(jsonPath("$.storage").exists())
                .andExpect(jsonPath("$.taxRules").exists());
        // No timestamp field in HealthCheckResponse DTO
    }
}
