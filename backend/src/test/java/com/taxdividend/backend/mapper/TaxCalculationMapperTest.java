package com.taxdividend.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TaxCalculationMapper.
 */
class TaxCalculationMapperTest {

    private TaxCalculationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TaxCalculationMapper();
    }

    @Test
    void toApiDto_AllFields_MapsCorrectly() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        internal.setDividendId(UUID.randomUUID());
        internal.setTreatyApplied(true);
        internal.setReclaimableAmount(new BigDecimal("75.50"));
        internal.setWithholdingRate(new BigDecimal("30.00"));
        internal.setTreatyRate(new BigDecimal("15.00"));
        internal.setWithholdingTax(new BigDecimal("150.00"));
        internal.setNotes("Treaty FR-CH applied");

        // When
        com.taxdividend.backend.api.dto.TaxCalculationResultDTO dto = mapper.toApiDto(internal);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getDividendId()).isEqualTo(internal.getDividendId());
        assertThat(dto.getSuccess()).isTrue();
        assertThat(dto.getReclaimableAmount()).isEqualByComparingTo(internal.getReclaimableAmount());
        assertThat(dto.getStandardRate()).isEqualByComparingTo(internal.getWithholdingRate());
        assertThat(dto.getTreatyRate()).isEqualByComparingTo(internal.getTreatyRate());
        assertThat(dto.getWithheldAmount()).isEqualByComparingTo(internal.getWithholdingTax());
        assertThat(dto.getErrors()).containsExactly("Treaty FR-CH applied");
    }

    @Test
    void toApiDto_TreatyAppliedNull_SuccessFalse() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        internal.setDividendId(UUID.randomUUID());
        // treatyApplied is null

        // When
        com.taxdividend.backend.api.dto.TaxCalculationResultDTO dto = mapper.toApiDto(internal);

        // Then
        assertThat(dto.getSuccess()).isFalse();
    }

    @Test
    void toApiDto_TreatyAppliedFalse_SuccessFalse() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        internal.setDividendId(UUID.randomUUID());
        internal.setTreatyApplied(false);

        // When
        com.taxdividend.backend.api.dto.TaxCalculationResultDTO dto = mapper.toApiDto(internal);

        // Then
        assertThat(dto.getSuccess()).isFalse();
    }

    @Test
    void toApiDto_EmptyNotes_NoErrors() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        internal.setDividendId(UUID.randomUUID());
        internal.setNotes("");

        // When
        com.taxdividend.backend.api.dto.TaxCalculationResultDTO dto = mapper.toApiDto(internal);

        // Then
        assertThat(dto.getErrors()).isNull();
    }

    @Test
    void toApiDto_NullNotes_NoErrors() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        internal.setDividendId(UUID.randomUUID());
        // notes is null

        // When
        com.taxdividend.backend.api.dto.TaxCalculationResultDTO dto = mapper.toApiDto(internal);

        // Then
        assertThat(dto.getErrors()).isNull();
    }

    @Test
    void toApiDto_NullInternal_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.TaxCalculationResultDTO dto = mapper.toApiDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void toApiBatchDto_AllFields_MapsCorrectly() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationResultDTO result1 =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        result1.setDividendId(UUID.randomUUID());
        result1.setTreatyApplied(true);
        result1.setReclaimableAmount(new BigDecimal("50.00"));

        com.taxdividend.backend.dto.TaxCalculationResultDTO result2 =
                new com.taxdividend.backend.dto.TaxCalculationResultDTO();
        result2.setDividendId(UUID.randomUUID());
        result2.setTreatyApplied(false);

        com.taxdividend.backend.dto.TaxCalculationBatchResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationBatchResultDTO();
        internal.setSuccessCount(1);
        internal.setFailureCount(1);
        internal.setTotalReclaimableAmount(new BigDecimal("50.00"));
        internal.setResults(List.of(result1, result2));

        // When
        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO dto = mapper.toApiBatchDto(internal);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getSuccessCount()).isEqualTo(1);
        assertThat(dto.getFailureCount()).isEqualTo(1);
        assertThat(dto.getTotalReclaimableAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(dto.getResults()).hasSize(2);
        assertThat(dto.getResults().get(0).getSuccess()).isTrue();
        assertThat(dto.getResults().get(1).getSuccess()).isFalse();
    }

    @Test
    void toApiBatchDto_NullResults_NoResultsList() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationBatchResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationBatchResultDTO();
        internal.setSuccessCount(0);
        internal.setFailureCount(0);
        // results is null

        // When
        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO dto = mapper.toApiBatchDto(internal);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getResults()).isNull();
    }

    @Test
    void toApiBatchDto_EmptyResults_EmptyResultsList() {
        // Given
        com.taxdividend.backend.dto.TaxCalculationBatchResultDTO internal =
                new com.taxdividend.backend.dto.TaxCalculationBatchResultDTO();
        internal.setSuccessCount(0);
        internal.setFailureCount(0);
        internal.setResults(List.of());

        // When
        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO dto = mapper.toApiBatchDto(internal);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getResults()).isEmpty();
    }

    @Test
    void toApiBatchDto_NullInternal_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO dto = mapper.toApiBatchDto(null);

        // Then
        assertThat(dto).isNull();
    }
}
