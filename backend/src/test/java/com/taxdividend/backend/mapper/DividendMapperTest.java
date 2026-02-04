package com.taxdividend.backend.mapper;

import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.DividendStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DividendMapper.
 */
class DividendMapperTest {

    private DividendMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DividendMapper.class);
    }

    @Test
    void toDto_AllFields_MapsCorrectly() {
        // Given
        Dividend entity = createTestDividend();

        // When
        com.taxdividend.backend.api.dto.DividendDto dto = mapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getSecurityName()).isEqualTo(entity.getSecurityName());
        assertThat(dto.getIsin()).isEqualTo(entity.getIsin());
        assertThat(dto.getGrossAmount()).isEqualByComparingTo(entity.getGrossAmount());
        assertThat(dto.getCurrency()).isEqualTo(entity.getCurrency());
        assertThat(dto.getPaymentDate()).isEqualTo(entity.getPaymentDate());
        assertThat(dto.getWithholdingTax()).isEqualByComparingTo(entity.getWithholdingTax());
        assertThat(dto.getReclaimableAmount()).isEqualByComparingTo(entity.getReclaimableAmount());
        assertThat(dto.getStatus().name()).isEqualTo(entity.getStatus().name());
    }

    @Test
    void toDto_NullStatus_DefaultsToOpen() {
        // Given
        Dividend entity = createTestDividend();
        entity.setStatus(null);

        // When
        com.taxdividend.backend.api.dto.DividendDto dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getStatus()).isEqualTo(com.taxdividend.backend.api.dto.DividendDto.StatusEnum.OPEN);
    }

    @Test
    void toDto_SentStatus_MapsCorrectly() {
        // Given
        Dividend entity = createTestDividend();
        entity.setStatus(DividendStatus.SENT);

        // When
        com.taxdividend.backend.api.dto.DividendDto dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getStatus()).isEqualTo(com.taxdividend.backend.api.dto.DividendDto.StatusEnum.SENT);
    }

    @Test
    void toDto_PaidStatus_MapsCorrectly() {
        // Given
        Dividend entity = createTestDividend();
        entity.setStatus(DividendStatus.PAID);

        // When
        com.taxdividend.backend.api.dto.DividendDto dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getStatus()).isEqualTo(com.taxdividend.backend.api.dto.DividendDto.StatusEnum.PAID);
    }

    @Test
    void toDto_NullEntity_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.DividendDto dto = mapper.toDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void toDtoList_MultipleEntities_MapsAll() {
        // Given
        List<Dividend> entities = List.of(
                createTestDividend(),
                createTestDividend());

        // When
        List<com.taxdividend.backend.api.dto.DividendDto> dtos = mapper.toDtoList(entities);

        // Then
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isNotNull();
        assertThat(dtos.get(1)).isNotNull();
    }

    @Test
    void toDtoList_NullList_ReturnsNull() {
        // When
        List<com.taxdividend.backend.api.dto.DividendDto> dtos = mapper.toDtoList(null);

        // Then
        assertThat(dtos).isNull();
    }

    @Test
    void toDtoList_EmptyList_ReturnsEmptyList() {
        // When
        List<com.taxdividend.backend.api.dto.DividendDto> dtos = mapper.toDtoList(List.of());

        // Then
        assertThat(dtos).isEmpty();
    }

    @Test
    void toPageResponse_WithContent_MapsCorrectly() {
        // Given
        List<Dividend> content = List.of(createTestDividend(), createTestDividend());
        Page<Dividend> page = new PageImpl<>(content, PageRequest.of(0, 10), 2);

        // When
        com.taxdividend.backend.api.dto.PaginatedDividendListDto response = mapper.toPageResponse(page);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
    }

    @Test
    void toPageResponse_NullPage_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.PaginatedDividendListDto response = mapper.toPageResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void toEntity_AllFields_MapsCorrectly() {
        // Given
        com.taxdividend.backend.api.dto.DividendDto dto = new com.taxdividend.backend.api.dto.DividendDto();
        dto.setId(UUID.randomUUID());
        dto.setSecurityName("Apple Inc.");
        dto.setIsin("US0378331005");
        dto.setGrossAmount(new BigDecimal("150.00"));
        dto.setCurrency("USD");
        dto.setPaymentDate(LocalDate.of(2024, 3, 15));
        dto.setWithholdingTax(new BigDecimal("45.00"));
        dto.setReclaimableAmount(new BigDecimal("22.50"));
        dto.setStatus(com.taxdividend.backend.api.dto.DividendDto.StatusEnum.OPEN);

        // When
        Dividend entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getSecurityName()).isEqualTo(dto.getSecurityName());
        assertThat(entity.getIsin()).isEqualTo(dto.getIsin());
        assertThat(entity.getGrossAmount()).isEqualByComparingTo(dto.getGrossAmount());
        assertThat(entity.getCurrency()).isEqualTo(dto.getCurrency());
        assertThat(entity.getPaymentDate()).isEqualTo(dto.getPaymentDate());
        assertThat(entity.getWithholdingTax()).isEqualByComparingTo(dto.getWithholdingTax());
        assertThat(entity.getReclaimableAmount()).isEqualByComparingTo(dto.getReclaimableAmount());
        assertThat(entity.getStatus()).isEqualTo(DividendStatus.OPEN);
    }

    @Test
    void toEntity_NullStatus_DefaultsToOpen() {
        // Given
        com.taxdividend.backend.api.dto.DividendDto dto = new com.taxdividend.backend.api.dto.DividendDto();
        dto.setId(UUID.randomUUID());
        // status is null

        // When
        Dividend entity = mapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(DividendStatus.OPEN);
    }

    @Test
    void toEntity_NullDto_ReturnsNull() {
        // When
        Dividend entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    private Dividend createTestDividend() {
        Dividend dividend = new Dividend();
        dividend.setId(UUID.randomUUID());
        dividend.setSecurityName("Apple Inc.");
        dividend.setIsin("US0378331005");
        dividend.setGrossAmount(new BigDecimal("150.00"));
        dividend.setCurrency("USD");
        dividend.setPaymentDate(LocalDate.of(2024, 3, 15));
        dividend.setWithholdingTax(new BigDecimal("45.00"));
        dividend.setReclaimableAmount(new BigDecimal("22.50"));
        dividend.setStatus(DividendStatus.OPEN);
        return dividend;
    }
}
