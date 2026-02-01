package com.taxdividend.backend.mapper;

import com.taxdividend.backend.dto.DividendStatementDTO;
import com.taxdividend.backend.dto.DividendStatementUpdateDTO;
import com.taxdividend.backend.model.DividendStatement;
import com.taxdividend.backend.model.DividendStatementStatus;
import com.taxdividend.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DividendStatementMapper.
 */
class DividendStatementMapperTest {

    private DividendStatementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DividendStatementMapper();
    }

    @Test
    void toDto_AllFields_MapsCorrectly() {
        // Given
        DividendStatement entity = createTestDividendStatement();

        // When
        DividendStatementDTO dto = mapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getUserId()).isEqualTo(entity.getUser().getId());
        assertThat(dto.getSourceFileName()).isEqualTo(entity.getSourceFileName());
        assertThat(dto.getSourceFileS3Key()).isEqualTo(entity.getSourceFileS3Key());
        assertThat(dto.getBroker()).isEqualTo(entity.getBroker());
        assertThat(dto.getPeriodStart()).isEqualTo(entity.getPeriodStart());
        assertThat(dto.getPeriodEnd()).isEqualTo(entity.getPeriodEnd());
        assertThat(dto.getStatus()).isEqualTo(entity.getStatus());
        assertThat(dto.getParsedAt()).isEqualTo(entity.getParsedAt());
        assertThat(dto.getDividendCount()).isEqualTo(entity.getDividendCount());
        assertThat(dto.getTotalGrossAmount()).isEqualByComparingTo(entity.getTotalGrossAmount());
        assertThat(dto.getTotalReclaimable()).isEqualByComparingTo(entity.getTotalReclaimable());
    }

    @Test
    void toDto_NullEntity_ReturnsNull() {
        // When
        DividendStatementDTO dto = mapper.toDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void toDtoList_MultipleEntities_MapsAll() {
        // Given
        List<DividendStatement> entities = List.of(
                createTestDividendStatement(),
                createTestDividendStatement()
        );

        // When
        List<DividendStatementDTO> dtos = mapper.toDtoList(entities);

        // Then
        assertThat(dtos).hasSize(2);
    }

    @Test
    void toDtoList_NullList_ReturnsEmptyList() {
        // When
        List<DividendStatementDTO> dtos = mapper.toDtoList(null);

        // Then
        assertThat(dtos).isEmpty();
    }

    @Test
    void toDtoList_EmptyList_ReturnsEmptyList() {
        // When
        List<DividendStatementDTO> dtos = mapper.toDtoList(List.of());

        // Then
        assertThat(dtos).isEmpty();
    }

    @Test
    void toDtoPage_WithContent_MapsCorrectly() {
        // Given
        List<DividendStatement> content = List.of(createTestDividendStatement());
        Page<DividendStatement> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);

        // When
        Page<DividendStatementDTO> dtoPage = mapper.toDtoPage(page);

        // Then
        assertThat(dtoPage.getContent()).hasSize(1);
        assertThat(dtoPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    void toApiDto_AllFields_MapsCorrectly() {
        // Given
        DividendStatementDTO internal = createTestDividendStatementDTO();

        // When
        com.taxdividend.backend.api.dto.DividendStatement api = mapper.toApiDto(internal);

        // Then
        assertThat(api).isNotNull();
        assertThat(api.getId()).isEqualTo(internal.getId());
        assertThat(api.getUserId()).isEqualTo(internal.getUserId());
        assertThat(api.getSourceFileName()).isEqualTo(internal.getSourceFileName());
        assertThat(api.getSourceFileS3Key()).isEqualTo(internal.getSourceFileS3Key());
        assertThat(api.getBroker()).isEqualTo(internal.getBroker());
        assertThat(api.getPeriodStart()).isEqualTo(internal.getPeriodStart());
        assertThat(api.getPeriodEnd()).isEqualTo(internal.getPeriodEnd());
        assertThat(api.getStatus().name()).isEqualTo(internal.getStatus().name());
        assertThat(api.getDividendCount()).isEqualTo(internal.getDividendCount());
        assertThat(api.getTotalGrossAmount()).isEqualByComparingTo(internal.getTotalGrossAmount());
        assertThat(api.getTotalReclaimable()).isEqualByComparingTo(internal.getTotalReclaimable());
    }

    @Test
    void toApiDto_LocalDateTimeToOffsetDateTime_ConvertsCorrectly() {
        // Given
        LocalDateTime parsedAt = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        DividendStatementDTO internal = createTestDividendStatementDTO();
        internal.setParsedAt(parsedAt);

        // When
        com.taxdividend.backend.api.dto.DividendStatement api = mapper.toApiDto(internal);

        // Then
        assertThat(api.getParsedAt().isPresent()).isTrue();
        assertThat(api.getParsedAt().get()).isEqualTo(parsedAt.atOffset(ZoneOffset.UTC));
    }

    @Test
    void toApiDto_NullParsedAt_ReturnsUndefined() {
        // Given
        DividendStatementDTO internal = createTestDividendStatementDTO();
        internal.setParsedAt(null);

        // When
        com.taxdividend.backend.api.dto.DividendStatement api = mapper.toApiDto(internal);

        // Then
        assertThat(api.getParsedAt().isPresent()).isFalse();
    }

    @Test
    void toApiDto_NullInternal_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.DividendStatement api = mapper.toApiDto(null);

        // Then
        assertThat(api).isNull();
    }

    @Test
    void toInternalUpdateDto_AllFields_MapsCorrectly() {
        // Given
        com.taxdividend.backend.api.dto.DividendStatementUpdateDTO api =
                new com.taxdividend.backend.api.dto.DividendStatementUpdateDTO();
        api.setStatus(com.taxdividend.backend.api.dto.DividendStatementStatus.SENT);
        api.setSentMethod("EMAIL");
        api.setSentNotes("Sent to French tax authority");
        api.setPaidAmount(new BigDecimal("150.00"));
        api.setPaidAt(OffsetDateTime.of(2024, 3, 1, 10, 0, 0, 0, ZoneOffset.UTC));
        api.setParsedBy("AGENT_V1");

        // When
        DividendStatementUpdateDTO internal = mapper.toInternalUpdateDto(api);

        // Then
        assertThat(internal).isNotNull();
        assertThat(internal.getStatus()).isEqualTo(DividendStatementStatus.SENT);
        assertThat(internal.getSentMethod()).isEqualTo("EMAIL");
        assertThat(internal.getSentNotes()).isEqualTo("Sent to French tax authority");
        assertThat(internal.getPaidAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(internal.getPaidAt()).isEqualTo(LocalDateTime.of(2024, 3, 1, 10, 0, 0));
        assertThat(internal.getParsedBy()).isEqualTo("AGENT_V1");
    }

    @Test
    void toInternalUpdateDto_NullPaidAt_HandlesCorrectly() {
        // Given
        com.taxdividend.backend.api.dto.DividendStatementUpdateDTO api =
                new com.taxdividend.backend.api.dto.DividendStatementUpdateDTO();
        api.setStatus(com.taxdividend.backend.api.dto.DividendStatementStatus.PARSED);
        // paidAt is null

        // When
        DividendStatementUpdateDTO internal = mapper.toInternalUpdateDto(api);

        // Then
        assertThat(internal.getPaidAt()).isNull();
    }

    @Test
    void toInternalUpdateDto_NullApi_ReturnsNull() {
        // When
        DividendStatementUpdateDTO internal = mapper.toInternalUpdateDto(null);

        // Then
        assertThat(internal).isNull();
    }

    @Test
    void toApiDto_JsonNullableFields_MapsCorrectly() {
        // Given
        DividendStatementDTO internal = createTestDividendStatementDTO();
        internal.setSentMethod("EMAIL");
        internal.setSentNotes("Sent successfully");
        internal.setPaidAmount(new BigDecimal("100.00"));

        // When
        com.taxdividend.backend.api.dto.DividendStatement api = mapper.toApiDto(internal);

        // Then
        assertThat(api.getSentMethod().isPresent()).isTrue();
        assertThat(api.getSentMethod().get()).isEqualTo("EMAIL");
        assertThat(api.getSentNotes().isPresent()).isTrue();
        assertThat(api.getSentNotes().get()).isEqualTo("Sent successfully");
        assertThat(api.getPaidAmount().isPresent()).isTrue();
        assertThat(api.getPaidAmount().get()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void toApiDto_NullJsonNullableFields_ReturnsOf() {
        // Given
        DividendStatementDTO internal = createTestDividendStatementDTO();
        internal.setSentMethod(null);
        internal.setSentNotes(null);
        internal.setPaidAmount(null);

        // When
        com.taxdividend.backend.api.dto.DividendStatement api = mapper.toApiDto(internal);

        // Then
        // JsonNullable.of(null) is present with null value
        assertThat(api.getSentMethod().isPresent()).isTrue();
        assertThat(api.getSentMethod().get()).isNull();
        assertThat(api.getSentNotes().isPresent()).isTrue();
        assertThat(api.getSentNotes().get()).isNull();
        assertThat(api.getPaidAmount().isPresent()).isTrue();
        assertThat(api.getPaidAmount().get()).isNull();
    }

    private DividendStatement createTestDividendStatement() {
        User user = new User();
        user.setId(UUID.randomUUID());

        DividendStatement statement = new DividendStatement();
        statement.setId(UUID.randomUUID());
        statement.setUser(user);
        statement.setSourceFileName("statement-2024.pdf");
        statement.setSourceFileS3Key("statements/user123/statement-2024.pdf");
        statement.setBroker("Interactive Brokers");
        statement.setPeriodStart(LocalDate.of(2024, 1, 1));
        statement.setPeriodEnd(LocalDate.of(2024, 12, 31));
        statement.setStatus(DividendStatementStatus.PARSED);
        statement.setParsedAt(LocalDateTime.now());
        statement.setDividendCount(5);
        statement.setTotalGrossAmount(new BigDecimal("1000.00"));
        statement.setTotalReclaimable(new BigDecimal("150.00"));
        statement.setCreatedAt(LocalDateTime.now());
        statement.setUpdatedAt(LocalDateTime.now());
        return statement;
    }

    private DividendStatementDTO createTestDividendStatementDTO() {
        return DividendStatementDTO.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .sourceFileName("statement-2024.pdf")
                .sourceFileS3Key("statements/user123/statement-2024.pdf")
                .broker("Interactive Brokers")
                .periodStart(LocalDate.of(2024, 1, 1))
                .periodEnd(LocalDate.of(2024, 12, 31))
                .status(DividendStatementStatus.PARSED)
                .parsedAt(LocalDateTime.now())
                .dividendCount(5)
                .totalGrossAmount(new BigDecimal("1000.00"))
                .totalReclaimable(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
