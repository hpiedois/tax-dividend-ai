package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto;
import com.taxdividend.backend.api.dto.TaxCalculationResultDto;
import com.taxdividend.backend.exception.TaxCalculationException;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.TaxRule;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.TaxRuleRepository;
import com.taxdividend.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaxCalculationService.
 * Uses Mockito to mock dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tax Calculation Service Tests")
class TaxCalculationServiceTest {

        @Mock
        private DividendRepository dividendRepository;

        @Mock
        private TaxRuleRepository taxRuleRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private TaxCalculationService taxCalculationService;

        private User testUser;
        private TaxRule testTaxRule;
        private Dividend testDividend;

        @BeforeEach
        void setUp() {
                // Create test user
                testUser = User.builder()
                                .id(UUID.randomUUID())
                                .email("test@example.com")
                                .country("CH")
                                .build();

                // Create test tax rule: France -> Switzerland, 15% treaty rate (standard 30%)
                testTaxRule = TaxRule.builder()
                                .id(UUID.randomUUID())
                                .sourceCountry("FR")
                                .residenceCountry("CH")
                                .securityType("EQUITY")
                                .standardWithholdingRate(new BigDecimal("30.00"))
                                .treatyRate(new BigDecimal("15.00"))
                                .reliefAtSourceAvailable(true)
                                .refundProcedureAvailable(true)
                                .effectiveFrom(LocalDate.of(2020, 1, 1))
                                .build();

                // Create test dividend: 100 EUR gross, 30 EUR withholding (30%)
                testDividend = Dividend.builder()
                                .id(UUID.randomUUID())
                                .user(testUser)
                                .isin("FR0000120271") // Total Energies
                                .securityName("Total Energies")
                                .grossAmount(new BigDecimal("100.00"))
                                .withholdingTax(new BigDecimal("30.00"))
                                .currency("EUR")
                                .paymentDate(LocalDate.of(2024, 12, 15))
                                .sourceCountry("FR")
                                .build();
        }

        @Test
        @DisplayName("Should calculate reclaimable amount correctly")
        void shouldCalculateReclaimableAmountCorrectly() {
                // Given
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                TaxCalculationResultDto result = taxCalculationService.calculateForDividend(
                                testDividend, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isTrue();

                // Expected calculation:
                // Withheld amount = 30.00 (actual withholding)
                // Treaty withholding = 100 * 15% = 15.00
                // Reclaimable = 30.00 - 15.00 = 15.00
                assertThat(result.getWithheldAmount())
                                .isEqualByComparingTo(new BigDecimal("30.00"));
                assertThat(result.getReclaimableAmount())
                                .isEqualByComparingTo(new BigDecimal("15.00"));

                verify(taxRuleRepository).findApplicableRule(
                                "FR", "CH", "EQUITY", testDividend.getPaymentDate());
        }

        @Test
        @DisplayName("Should handle no treaty found")
        void shouldHandleNoTreatyFound() {
                // Given
                when(taxRuleRepository.findApplicableRule(
                                anyString(), anyString(), anyString(), any(LocalDate.class)))
                                .thenReturn(Optional.empty());

                // When
                TaxCalculationResultDto result = taxCalculationService.calculateForDividend(
                                testDividend, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isFalse();
                assertThat(result.getReclaimableAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should calculate and update dividend")
        void shouldCalculateAndUpdateDividend() {
                // Given
                UUID dividendId = testDividend.getId();
                when(dividendRepository.findById(dividendId))
                                .thenReturn(Optional.of(testDividend));
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));
                when(dividendRepository.save(any(Dividend.class)))
                                .thenReturn(testDividend);

                // When
                TaxCalculationResultDto result = taxCalculationService.calculateAndUpdate(
                                dividendId, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getReclaimableAmount())
                                .isEqualByComparingTo(new BigDecimal("15.00"));

                // Verify dividend was updated
                verify(dividendRepository).save(argThat(
                                dividend -> dividend.getReclaimableAmount().compareTo(new BigDecimal("15.00")) == 0));
        }

        @Test
        @DisplayName("Should throw exception when dividend not found")
        void shouldThrowExceptionWhenDividendNotFound() {
                // Given
                UUID nonExistentId = UUID.randomUUID();
                when(dividendRepository.findById(nonExistentId))
                                .thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> taxCalculationService.calculateAndUpdate(nonExistentId, "CH"))
                                .isInstanceOf(TaxCalculationException.class)
                                .hasMessageContaining("Dividend not found");
        }

        @Test
        @DisplayName("Should calculate batch correctly")
        void shouldCalculateBatchCorrectly() {
                // Given
                Dividend dividend2 = Dividend.builder()
                                .id(UUID.randomUUID())
                                .user(testUser)
                                .isin("FR0000120644") // Danone
                                .securityName("Danone")
                                .grossAmount(new BigDecimal("50.00"))
                                .withholdingTax(new BigDecimal("15.00"))
                                .currency("EUR")
                                .paymentDate(LocalDate.of(2024, 12, 20))
                                .sourceCountry("FR")
                                .build();

                List<Dividend> dividends = Arrays.asList(testDividend, dividend2);

                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                TaxCalculationBatchResultDto result = taxCalculationService.calculateBatch(
                                dividends, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccessCount()).isEqualTo(2);
                assertThat(result.getFailureCount()).isEqualTo(0);

                // Expected totals:
                // Dividend 1: gross=100, withholding=30, reclaimable=15
                // Dividend 2: gross=50, withholding=15, reclaimable=7.50
                // Total: reclaimable=22.50
                assertThat(result.getTotalReclaimableAmount())
                                .isEqualByComparingTo(new BigDecimal("22.50"));

                assertThat(result.getResults()).hasSize(2);
        }

        @Test
        @DisplayName("Should handle partial batch failures")
        void shouldHandlePartialBatchFailures() {
                // Given
                Dividend dividend2 = Dividend.builder()
                                .id(UUID.randomUUID())
                                .user(testUser)
                                .isin("US0378331005") // Apple (US security, no treaty rule)
                                .securityName("Apple Inc")
                                .grossAmount(new BigDecimal("50.00"))
                                .withholdingTax(new BigDecimal("15.00"))
                                .currency("USD")
                                .paymentDate(LocalDate.of(2024, 12, 20))
                                .sourceCountry("US")
                                .build();

                List<Dividend> dividends = Arrays.asList(testDividend, dividend2);

                // FR-CH treaty exists, but US-CH doesn't
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));
                when(taxRuleRepository.findApplicableRule(
                                eq("US"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenThrow(new RuntimeException("DB Error"));

                // When
                TaxCalculationBatchResultDto result = taxCalculationService.calculateBatch(
                                dividends, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccessCount()).isEqualTo(1);
                assertThat(result.getFailureCount()).isEqualTo(1);
                assertThat(result.getResults()).hasSize(2);

                // Only first dividend should contribute to totals
                assertThat(result.getTotalReclaimableAmount())
                                .isEqualByComparingTo(new BigDecimal("15.00"));
        }

        @Test
        @DisplayName("Should calculate for user")
        void shouldCalculateForUser() {
                // Given
                UUID userId = testUser.getId();
                when(userRepository.findById(userId))
                                .thenReturn(Optional.of(testUser));
                when(dividendRepository.findByUserId(userId))
                                .thenReturn(Arrays.asList(testDividend));
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                TaxCalculationBatchResultDto result = taxCalculationService.calculateForUser(userId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccessCount()).isEqualTo(1);
                assertThat(result.getTotalReclaimableAmount())
                                .isEqualByComparingTo(new BigDecimal("15.00"));

                verify(userRepository).findById(userId);
                verify(dividendRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
                // Given
                UUID nonExistentUserId = UUID.randomUUID();
                when(userRepository.findById(nonExistentUserId))
                                .thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> taxCalculationService.calculateForUser(nonExistentUserId))
                                .isInstanceOf(TaxCalculationException.class)
                                .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should handle empty dividend list")
        void shouldHandleEmptyDividendList() {
                // Given
                List<Dividend> emptyList = Arrays.asList();

                // When
                TaxCalculationBatchResultDto result = taxCalculationService.calculateBatch(
                                emptyList, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccessCount()).isEqualTo(0);
                assertThat(result.getFailureCount()).isEqualTo(0);
                assertThat(result.getTotalReclaimableAmount())
                                .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should find applicable tax rule")
        void shouldFindApplicableTaxRule() {
                // Given
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                UUID result = taxCalculationService.findApplicableTaxRule(
                                "FR", "CH", "EQUITY", LocalDate.now());

                // Then
                assertThat(result).isNotNull();
                assertThat(result).isEqualTo(testTaxRule.getId());
        }

        @Test
        @DisplayName("Should handle zero withholding tax")
        void shouldHandleZeroWithholdingTax() {
                // Given
                testDividend.setWithholdingTax(BigDecimal.ZERO);
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                TaxCalculationResultDto result = taxCalculationService.calculateForDividend(
                                testDividend, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getReclaimableAmount())
                                .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should ensure reclaimable amount is never negative")
        void shouldEnsureReclaimableAmountNeverNegative() {
                // Given: treaty rate higher than actual withholding
                testDividend.setWithholdingTax(new BigDecimal("10.00")); // Less than 15%
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                TaxCalculationResultDto result = taxCalculationService.calculateForDividend(
                                testDividend, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getReclaimableAmount())
                                .isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should use correct precision (2 decimals)")
        void shouldUseCorrectPrecision() {
                // Given
                testDividend.setGrossAmount(new BigDecimal("100.123")); // 3 decimals
                testDividend.setWithholdingTax(new BigDecimal("30.456")); // 3 decimals
                when(taxRuleRepository.findApplicableRule(
                                eq("FR"), eq("CH"), eq("EQUITY"), any(LocalDate.class)))
                                .thenReturn(Optional.of(testTaxRule));

                // When
                TaxCalculationResultDto result = taxCalculationService.calculateForDividend(
                                testDividend, "CH");

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getWithheldAmount().scale()).isEqualTo(2);
                assertThat(result.getReclaimableAmount().scale()).isEqualTo(2);
        }
}
