package com.taxdividend.bff.mapper;

import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.model.DividendCaseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DividendMapper Tests")
class DividendMapperTest {

    private DividendMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DividendMapper.class);
    }

    @Test
    @DisplayName("Should map Dividend to DividendCase with all fields")
    void toDividendCase_AllFields() {
        // Arrange
        Dividend source = new Dividend();
        source.setId(UUID.randomUUID());
        source.setSecurityName("Total Energies");
        source.setIsin("FR0000120271");
        source.setGrossAmount(new BigDecimal("100.00"));
        source.setCurrency("EUR");
        source.setPaymentDate(LocalDate.of(2024, 6, 15));
        source.setWithholdingTax(new BigDecimal("30.00"));
        source.setReclaimableAmount(new BigDecimal("15.00"));
        source.setStatus(Dividend.StatusEnum.OPEN);

        // Act
        DividendCaseDto result = mapper.toDividendCase(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(source.getId().toString());
        assertThat(result.getSecurity()).isEqualTo("Total Energies");
        assertThat(result.getDate()).isEqualTo("2024-06-15");
        assertThat(result.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(result.getReclaimedAmount()).isEqualTo(new BigDecimal("15.00"));
        assertThat(result.getStatus()).isEqualTo(DividendCaseDto.StatusEnum.OPEN);
    }

    @Test
    @DisplayName("Should handle null status by defaulting to OPEN")
    void toDividendCase_NullStatus() {
        // Arrange
        Dividend source = new Dividend();
        source.setId(UUID.randomUUID());
        source.setSecurityName("Test Security");
        source.setPaymentDate(LocalDate.of(2024, 6, 15));
        source.setStatus(null);

        // Act
        DividendCaseDto result = mapper.toDividendCase(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(DividendCaseDto.StatusEnum.OPEN);
    }

    @Test
    @DisplayName("Should map SENT status correctly")
    void toDividendCase_SentStatus() {
        // Arrange
        Dividend source = new Dividend();
        source.setId(UUID.randomUUID());
        source.setSecurityName("Test Security");
        source.setPaymentDate(LocalDate.of(2024, 6, 15));
        source.setStatus(Dividend.StatusEnum.SENT);

        // Act
        DividendCaseDto result = mapper.toDividendCase(source);

        // Assert
        assertThat(result.getStatus()).isEqualTo(DividendCaseDto.StatusEnum.SENT);
    }

    @Test
    @DisplayName("Should map PAID status correctly")
    void toDividendCase_PaidStatus() {
        // Arrange
        Dividend source = new Dividend();
        source.setId(UUID.randomUUID());
        source.setSecurityName("Test Security");
        source.setPaymentDate(LocalDate.of(2024, 6, 15));
        source.setStatus(Dividend.StatusEnum.PAID);

        // Act
        DividendCaseDto result = mapper.toDividendCase(source);

        // Assert
        assertThat(result.getStatus()).isEqualTo(DividendCaseDto.StatusEnum.PAID);
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void toDividendCase_NullOptionalFields() {
        // Arrange
        Dividend source = new Dividend();
        source.setId(UUID.randomUUID());
        source.setSecurityName("Test Security");
        source.setPaymentDate(LocalDate.of(2024, 6, 15));
        // Optional fields left null
        source.setGrossAmount(null);
        source.setReclaimableAmount(null);

        // Act
        DividendCaseDto result = mapper.toDividendCase(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSecurity()).isEqualTo("Test Security");
        assertThat(result.getGrossAmount()).isNull();
        assertThat(result.getReclaimedAmount()).isNull();
    }

    @Test
    @DisplayName("Should preserve UUID when mapping ID")
    void toDividendCase_PreserveUUID() {
        // Arrange
        UUID testId = UUID.randomUUID();
        Dividend source = new Dividend();
        source.setId(testId);
        source.setSecurityName("Test Security");
        source.setPaymentDate(LocalDate.of(2024, 6, 15));

        // Act
        DividendCaseDto result = mapper.toDividendCase(source);

        // Assert
        assertThat(result.getId()).isEqualTo(testId.toString());
    }
}
