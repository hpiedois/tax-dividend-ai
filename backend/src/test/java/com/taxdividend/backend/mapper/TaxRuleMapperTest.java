package com.taxdividend.backend.mapper;

import com.taxdividend.backend.model.TaxRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TaxRuleMapper.
 */
class TaxRuleMapperTest {

    private TaxRuleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TaxRuleMapper.class);
    }

    @Test
    void toApiDto_AllFields_MapsCorrectly() {
        // Given
        TaxRule entity = createTestTaxRule();

        // When
        com.taxdividend.backend.api.dto.TaxRuleDto dto = mapper.toApiDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getSourceCountry()).isEqualTo(entity.getSourceCountry());
        assertThat(dto.getResidenceCountry()).isEqualTo(entity.getResidenceCountry());
        assertThat(dto.getSecurityType()).isEqualTo(entity.getSecurityType());
        assertThat(dto.getStandardWithholdingRate()).isEqualByComparingTo(entity.getStandardWithholdingRate());
        assertThat(dto.getTreatyRate()).isEqualByComparingTo(entity.getTreatyRate());
        assertThat(dto.getReliefAtSourceAvailable()).isEqualTo(entity.getReliefAtSourceAvailable());
        assertThat(dto.getRefundProcedureAvailable()).isEqualTo(entity.getRefundProcedureAvailable());
        assertThat(dto.getEffectiveFrom()).isEqualTo(entity.getEffectiveFrom());
        assertThat(dto.getEffectiveTo()).isEqualTo(entity.getEffectiveTo());
        assertThat(dto.getNotes()).isEqualTo(entity.getNotes());
    }

    @Test
    void toApiDto_NullEntity_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.TaxRuleDto dto = mapper.toApiDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void toApiDto_NullOptionalFields_MapsCorrectly() {
        // Given
        TaxRule entity = new TaxRule();
        entity.setId(UUID.randomUUID());
        entity.setSourceCountry("FR");
        entity.setResidenceCountry("CH");
        entity.setSecurityType("EQUITY");
        entity.setStandardWithholdingRate(new BigDecimal("30.00"));
        entity.setTreatyRate(new BigDecimal("15.00"));
        // effectiveTo and notes are null

        // When
        com.taxdividend.backend.api.dto.TaxRuleDto dto = mapper.toApiDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getEffectiveTo()).isNull();
        assertThat(dto.getNotes()).isNull();
    }

    @Test
    void toApiDtoList_MultipleEntities_MapsAll() {
        // Given
        List<TaxRule> entities = List.of(
                createTestTaxRule(),
                createTestTaxRule()
        );

        // When
        List<com.taxdividend.backend.api.dto.TaxRuleDto> dtos = mapper.toApiDtoList(entities);

        // Then
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isNotNull();
        assertThat(dtos.get(1)).isNotNull();
    }

    @Test
    void toApiDtoList_NullList_ReturnsNull() {
        // When
        List<com.taxdividend.backend.api.dto.TaxRuleDto> dtos = mapper.toApiDtoList(null);

        // Then
        assertThat(dtos).isNull();
    }

    @Test
    void toApiDtoList_EmptyList_ReturnsEmptyList() {
        // When
        List<com.taxdividend.backend.api.dto.TaxRuleDto> dtos = mapper.toApiDtoList(List.of());

        // Then
        assertThat(dtos).isEmpty();
    }

    @Test
    void toTreatyRateResponse_AllFields_MapsCorrectly() {
        // Given
        TaxRule entity = createTestTaxRule();

        // When
        com.taxdividend.backend.api.dto.TreatyRateResponseDto response = mapper.toTreatyRateResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStandardRate()).isEqualByComparingTo(entity.getStandardWithholdingRate());
        assertThat(response.getTreatyRate()).isEqualByComparingTo(entity.getTreatyRate());
        assertThat(response.getReliefAtSourceAvailable()).isEqualTo(entity.getReliefAtSourceAvailable());
        assertThat(response.getRefundProcedureAvailable()).isEqualTo(entity.getRefundProcedureAvailable());
        assertThat(response.getNotes()).isEqualTo(entity.getNotes());
    }

    @Test
    void toTreatyRateResponse_NullEntity_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.TreatyRateResponseDto response = mapper.toTreatyRateResponse(null);

        // Then
        assertThat(response).isNull();
    }

    private TaxRule createTestTaxRule() {
        TaxRule rule = new TaxRule();
        rule.setId(UUID.randomUUID());
        rule.setSourceCountry("FR");
        rule.setResidenceCountry("CH");
        rule.setSecurityType("EQUITY");
        rule.setStandardWithholdingRate(new BigDecimal("30.00"));
        rule.setTreatyRate(new BigDecimal("15.00"));
        rule.setReliefAtSourceAvailable(true);
        rule.setRefundProcedureAvailable(true);
        rule.setEffectiveFrom(LocalDate.of(2020, 1, 1));
        rule.setEffectiveTo(LocalDate.of(2025, 12, 31));
        rule.setNotes("France-Switzerland treaty");
        return rule;
    }
}
