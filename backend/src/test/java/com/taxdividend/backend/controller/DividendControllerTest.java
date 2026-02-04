package com.taxdividend.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxdividend.backend.config.TestSecurityConfig;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.DividendService;
import com.taxdividend.backend.service.TaxCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for DividendController.
 */
@WebMvcTest(controllers = DividendController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Dividend Controller Tests")
class DividendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TaxCalculationService taxCalculationService;

    @MockitoBean
    private DividendService dividendService;

    @MockitoBean
    private AuditService auditService;

    private UUID testUserId;
    private User testUser;
    private Dividend testDividend;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .country("CH")
                .build();

        testDividend = Dividend.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .isin("FR0000120271")
                .securityName("Total Energies")
                .grossAmount(new BigDecimal("100.00"))
                .withholdingTax(new BigDecimal("30.00"))
                .reclaimableAmount(new BigDecimal("15.00"))
                .currency("EUR")
                .paymentDate(LocalDate.of(2024, 12, 15))
                .sourceCountry("FR")
                .build();
    }

    @Test
    @DisplayName("Should list user's dividends")
    void shouldListUserDividends() throws Exception {
        // Given
        com.taxdividend.backend.api.dto.PaginatedDividendListDto response =
                new com.taxdividend.backend.api.dto.PaginatedDividendListDto();
        when(dividendService.listDividends(eq(testUserId), any(), isNull(), isNull(), isNull()))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(get("/internal/dividends")
                        .header("X-User-Id", testUserId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(dividendService).listDividends(eq(testUserId), any(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("Should get dividend by ID")
    void shouldGetDividendById() throws Exception {
        // Given
        UUID dividendId = testDividend.getId();
        com.taxdividend.backend.api.dto.DividendDto dividendDto =
                new com.taxdividend.backend.api.dto.DividendDto();
        dividendDto.setId(dividendId);
        dividendDto.setIsin("FR0000120271");
        dividendDto.setSecurityName("Total Energies");

        when(dividendService.getDividend(dividendId, testUserId))
                .thenReturn(Optional.of(dividendDto));

        // When/Then
        mockMvc.perform(get("/internal/dividends/{id}", dividendId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isin").value("FR0000120271"))
                .andExpect(jsonPath("$.securityName").value("Total Energies"));

        verify(dividendService).getDividend(dividendId, testUserId);
    }

    @Test
    @DisplayName("Should return 404 when dividend not found")
    void shouldReturn404WhenDividendNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(dividendService.getDividend(nonExistentId, testUserId))
                .thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/internal/dividends/{id}", nonExistentId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should calculate tax for dividend")
    void shouldCalculateTaxForDividend() throws Exception {
        // Given
        UUID dividendId = testDividend.getId();
        com.taxdividend.backend.api.dto.DividendDto dividendDto =
                new com.taxdividend.backend.api.dto.DividendDto();
        dividendDto.setId(dividendId);

        com.taxdividend.backend.api.dto.TaxCalculationResultDto calculationResult =
                new com.taxdividend.backend.api.dto.TaxCalculationResultDto();
        calculationResult.setDividendId(dividendId);
        calculationResult.setReclaimableAmount(new BigDecimal("15.00"));
        calculationResult.setSuccess(true);

        when(dividendService.getDividend(dividendId, testUserId))
                .thenReturn(Optional.of(dividendDto));
        when(taxCalculationService.calculateAndUpdate(eq(dividendId), anyString()))
                .thenReturn(calculationResult);

        // When/Then
        mockMvc.perform(post("/internal/dividends/{id}/calculate", dividendId)
                        .header("X-User-Id", testUserId.toString())
                        .param("residenceCountry", "CH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reclaimableAmount").value(15.00));

        verify(taxCalculationService).calculateAndUpdate(dividendId, "CH");
    }

    @Test
    @DisplayName("Should calculate batch of dividends")
    void shouldCalculateBatch() throws Exception {
        // Given
        List<UUID> dividendIds = Arrays.asList(
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto batchResult =
                new com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto();
        batchResult.setSuccessCount(2);
        batchResult.setFailureCount(0);
        batchResult.setTotalReclaimableAmount(new BigDecimal("25.00"));

        when(taxCalculationService.calculateBatchByIds(dividendIds, testUserId))
                .thenReturn(batchResult);

        // When/Then
        mockMvc.perform(post("/internal/dividends/calculate-batch")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dividendIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.totalReclaimableAmount").value(25.00));

        verify(taxCalculationService).calculateBatchByIds(dividendIds, testUserId);
        verify(auditService).logTaxCalculation(eq(testUserId), eq(2), anyString());
    }

    @Test
    @Disabled("Endpoint /internal/dividends/calculate-all not in OpenAPI spec - contract-first violation")
    @DisplayName("Should calculate all user's dividends")
    void shouldCalculateAllUserDividends() throws Exception {
        // Given
        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto result =
                new com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto();
        result.setSuccessCount(5);
        result.setFailureCount(0);
        result.setTotalReclaimableAmount(new BigDecimal("100.00"));

        when(taxCalculationService.calculateAndUpdateForUser(testUserId))
                .thenReturn(result);

        // When/Then
        mockMvc.perform(post("/internal/dividends/calculate-all")
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(5))
                .andExpect(jsonPath("$.totalReclaimableAmount").value(100.00));

        verify(taxCalculationService).calculateAndUpdateForUser(testUserId);
        verify(auditService).logTaxCalculation(eq(testUserId), eq(5), anyString());
    }

    @Test
    @Disabled("Endpoint /internal/dividends/by-date-range not in OpenAPI spec - contract-first violation")
    @DisplayName("Should get dividends by date range")
    void shouldGetDividendsByDateRange() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        com.taxdividend.backend.api.dto.DividendDto dividendDto =
                new com.taxdividend.backend.api.dto.DividendDto();
        dividendDto.setId(testDividend.getId());

        when(dividendService.getDividendsByDateRange(testUserId, startDate, endDate))
                .thenReturn(Arrays.asList(dividendDto));

        // When/Then
        mockMvc.perform(get("/internal/dividends/by-date-range")
                        .header("X-User-Id", testUserId.toString())
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31"))
                .andExpect(status().isOk());

        verify(dividendService).getDividendsByDateRange(testUserId, startDate, endDate);
    }

    @Test
    @Disabled("Endpoint /internal/dividends/unsubmitted not in OpenAPI spec - contract-first violation")
    @DisplayName("Should get unsubmitted dividends")
    void shouldGetUnsubmittedDividends() throws Exception {
        // Given
        com.taxdividend.backend.api.dto.DividendDto dividendDto =
                new com.taxdividend.backend.api.dto.DividendDto();
        dividendDto.setId(testDividend.getId());

        when(dividendService.getUnsubmittedDividends(testUserId))
                .thenReturn(Arrays.asList(dividendDto));

        // When/Then
        mockMvc.perform(get("/internal/dividends/unsubmitted")
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk());

        verify(dividendService).getUnsubmittedDividends(testUserId);
    }

    @Test
    @DisplayName("Should delete dividend")
    void shouldDeleteDividend() throws Exception {
        // Given
        UUID dividendId = testDividend.getId();
        doNothing().when(dividendService).deleteDividend(dividendId, testUserId);

        // When/Then
        mockMvc.perform(delete("/internal/dividends/{id}", dividendId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNoContent());

        verify(dividendService).deleteDividend(dividendId, testUserId);
    }

    @Test
    @DisplayName("Should not allow user to access other user's dividend")
    void shouldNotAllowAccessToOtherUserDividend() throws Exception {
        // Given
        UUID otherUserId = UUID.randomUUID();
        UUID otherDividendId = UUID.randomUUID();

        when(dividendService.getDividend(otherDividendId, testUserId))
                .thenReturn(Optional.empty());

        // When/Then - testUserId trying to access otherUserId's dividend
        mockMvc.perform(get("/internal/dividends/{id}", otherDividendId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNotFound());
    }
}
