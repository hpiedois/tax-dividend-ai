package com.taxdividend.backend.mapper;

import com.taxdividend.backend.model.GeneratedForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for FormMapper.
 */
class FormMapperTest {

    private FormMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FormMapper();
    }

    @Test
    void toApiDto_AllFields_MapsCorrectly() {
        // Given
        GeneratedForm entity = createTestGeneratedForm();

        // When
        com.taxdividend.backend.api.dto.GeneratedForm dto = mapper.toApiDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getFormType()).isEqualTo(entity.getFormType());
        assertThat(dto.getTaxYear()).isEqualTo(entity.getTaxYear());
        assertThat(dto.getFileName()).isEqualTo(entity.getFileName());
        assertThat(dto.getS3Key()).isEqualTo(entity.getS3Key());
        assertThat(dto.getFileSize()).isEqualTo(entity.getFileSize());
    }

    @Test
    void toApiDto_LocalDateTimeToOffsetDateTime_ConvertsCorrectly() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        LocalDateTime expiresAt = LocalDateTime.of(2024, 2, 15, 10, 30, 45);

        GeneratedForm entity = new GeneratedForm();
        entity.setId(UUID.randomUUID());
        entity.setCreatedAt(createdAt);
        entity.setExpiresAt(expiresAt);

        // When
        com.taxdividend.backend.api.dto.GeneratedForm dto = mapper.toApiDto(entity);

        // Then
        assertThat(dto.getGeneratedAt()).isEqualTo(createdAt.atOffset(ZoneOffset.UTC));
        assertThat(dto.getExpiresAt()).isEqualTo(expiresAt.atOffset(ZoneOffset.UTC));
    }

    @Test
    void toApiDto_NullDates_HandlesCorrectly() {
        // Given
        GeneratedForm entity = new GeneratedForm();
        entity.setId(UUID.randomUUID());
        // createdAt and expiresAt are null

        // When
        com.taxdividend.backend.api.dto.GeneratedForm dto = mapper.toApiDto(entity);

        // Then
        assertThat(dto.getGeneratedAt()).isNull();
        assertThat(dto.getExpiresAt()).isNull();
    }

    @Test
    void toApiDto_NullEntity_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.GeneratedForm dto = mapper.toApiDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void toApiDtoList_MultipleEntities_MapsAll() {
        // Given
        List<GeneratedForm> entities = List.of(
                createTestGeneratedForm(),
                createTestGeneratedForm()
        );

        // When
        List<com.taxdividend.backend.api.dto.GeneratedForm> dtos = mapper.toApiDtoList(entities);

        // Then
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0)).isNotNull();
        assertThat(dtos.get(1)).isNotNull();
    }

    @Test
    void toApiDtoList_NullList_ReturnsNull() {
        // When
        List<com.taxdividend.backend.api.dto.GeneratedForm> dtos = mapper.toApiDtoList(null);

        // Then
        assertThat(dtos).isNull();
    }

    @Test
    void toApiDtoList_EmptyList_ReturnsEmptyList() {
        // When
        List<com.taxdividend.backend.api.dto.GeneratedForm> dtos = mapper.toApiDtoList(List.of());

        // Then
        assertThat(dtos).isEmpty();
    }

    @Test
    void toApiResultDto_AllFields_MapsCorrectly() {
        // Given
        com.taxdividend.backend.dto.GenerateFormResultDTO internal =
                new com.taxdividend.backend.dto.GenerateFormResultDTO();
        internal.setSuccess(true);
        internal.setFormId(UUID.randomUUID());
        internal.setFormType("FORM_5000");
        internal.setDownloadUrl("https://storage.example.com/form.pdf");
        internal.setFileName("form-5000-2024.pdf");
        internal.setDividendCount(5);
        internal.setErrors(List.of("Warning: Missing ISIN"));

        // When
        com.taxdividend.backend.api.dto.GenerateFormResultDTO dto = mapper.toApiResultDto(internal);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getSuccess()).isEqualTo(internal.getSuccess());
        assertThat(dto.getFormId()).isEqualTo(internal.getFormId());
        assertThat(dto.getFormType()).isEqualTo(internal.getFormType());
        assertThat(dto.getDownloadUrl()).isEqualTo(internal.getDownloadUrl());
        assertThat(dto.getFileName()).isEqualTo(internal.getFileName());
        assertThat(dto.getDividendCount()).isEqualTo(internal.getDividendCount());
        assertThat(dto.getErrors()).containsExactlyElementsOf(internal.getErrors());
    }

    @Test
    void toApiResultDto_NullInternal_ReturnsNull() {
        // When
        com.taxdividend.backend.api.dto.GenerateFormResultDTO dto = mapper.toApiResultDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    void toApiResultDto_NullOptionalFields_MapsCorrectly() {
        // Given
        com.taxdividend.backend.dto.GenerateFormResultDTO internal =
                new com.taxdividend.backend.dto.GenerateFormResultDTO();
        internal.setSuccess(false);
        // All other fields null

        // When
        com.taxdividend.backend.api.dto.GenerateFormResultDTO dto = mapper.toApiResultDto(internal);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getSuccess()).isFalse();
        assertThat(dto.getFormId()).isNull();
        assertThat(dto.getErrors()).isNull();
    }

    private GeneratedForm createTestGeneratedForm() {
        GeneratedForm form = new GeneratedForm();
        form.setId(UUID.randomUUID());
        form.setFormType("FORM_5000");
        form.setTaxYear(2024);
        form.setFileName("form-5000-2024.pdf");
        form.setS3Key("forms/user123/form-5000-2024.pdf");
        form.setFileSize(125000L);
        form.setCreatedAt(LocalDateTime.now());
        form.setExpiresAt(LocalDateTime.now().plusDays(30));
        return form;
    }
}
