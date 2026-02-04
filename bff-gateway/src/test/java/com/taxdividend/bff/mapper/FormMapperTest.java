package com.taxdividend.bff.mapper;

import com.taxdividend.bff.client.model.FormDownloadUrlResponse;
import com.taxdividend.bff.client.model.GenerateFormResult;
import com.taxdividend.bff.client.model.GeneratedForm;
import com.taxdividend.bff.model.FormDownloadUrlResponseDto;
import com.taxdividend.bff.model.GenerateTaxFormsResponseDto;
import com.taxdividend.bff.model.GeneratedFormDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FormMapper Tests")
class FormMapperTest {

    private FormMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(FormMapper.class);
    }

    // ==================== toGeneratedForm Tests ====================

    @Test
    @DisplayName("Should map GeneratedForm with basic fields")
    void toGeneratedForm_BasicFields() {
        // Arrange
        GeneratedForm source = new GeneratedForm();
        source.setId(UUID.randomUUID());
        source.setFormType("5000");
        source.setTaxYear(2024);
        source.setGeneratedAt(OffsetDateTime.of(2024, 12, 1, 10, 0, 0, 0, ZoneOffset.UTC));

        // Act
        GeneratedFormDto result = mapper.toGeneratedForm(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(source.getId());
        assertThat(result.getTaxYear()).isEqualTo(2024);
        assertThat(result.getGeneratedAt()).isEqualTo(source.getGeneratedAt());
    }

    @Test
    @DisplayName("Should handle null optional fields in GeneratedForm")
    void toGeneratedForm_NullOptionalFields() {
        // Arrange
        GeneratedForm source = new GeneratedForm();
        source.setId(UUID.randomUUID());
        source.setFormType("5000");
        source.setExpiresAt(null);

        // Act
        GeneratedFormDto result = mapper.toGeneratedForm(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(source.getId());
    }

    // ==================== toGenerateResponse Tests ====================

    @Test
    @DisplayName("Should map GenerateFormResultDto with downloadUrl to formUrl")
    void toGenerateResponse_MapDownloadUrlToFormUrl() {
        // Arrange
        GenerateFormResult source = new GenerateFormResult();
        source.setDownloadUrl("https://example.com/forms/form-456.pdf");

        // Act
        GenerateTaxFormsResponseDto result = mapper.toGenerateResponse(source);

        // Assert
        assertThat(result).isNotNull();
        // Key mapping: downloadUrl -> formUrl
        assertThat(result.getFormUrl()).isEqualTo("https://example.com/forms/form-456.pdf");
    }

    @Test
    @DisplayName("Should handle null downloadUrl in GenerateFormResultDto")
    void toGenerateResponse_NullDownloadUrl() {
        // Arrange
        GenerateFormResult source = new GenerateFormResult();
        source.setDownloadUrl(null);

        // Act
        GenerateTaxFormsResponseDto result = mapper.toGenerateResponse(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFormUrl()).isNull();
    }

    @Test
    @DisplayName("Should handle empty downloadUrl string")
    void toGenerateResponse_EmptyDownloadUrl() {
        // Arrange
        GenerateFormResult source = new GenerateFormResult();
        source.setDownloadUrl("");

        // Act
        GenerateTaxFormsResponseDto result = mapper.toGenerateResponse(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFormUrl()).isEmpty();
    }

    // ==================== toDownloadResponse Tests ====================

    @Test
    @DisplayName("Should map FormDownloadUrlResponse correctly")
    void toDownloadResponse_AllFields() throws Exception {
        // Arrange
        FormDownloadUrlResponse source = new FormDownloadUrlResponse();
        source.setUrl(new URI("https://example.com/forms/download-123.pdf"));
        source.setExpiresAt(OffsetDateTime.of(2025, 6, 15, 10, 0, 0, 0, ZoneOffset.UTC));

        // Act
        FormDownloadUrlResponseDto result = mapper.toDownloadResponse(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo(new URI("https://example.com/forms/download-123.pdf"));
        assertThat(result.getExpiresAt()).isEqualTo(source.getExpiresAt());
    }

    @Test
    @DisplayName("Should handle null expiresAt in FormDownloadUrlResponse")
    void toDownloadResponse_NullExpiresAt() throws Exception {
        // Arrange
        FormDownloadUrlResponse source = new FormDownloadUrlResponse();
        source.setUrl(new URI("https://example.com/forms/download-456.pdf"));
        source.setExpiresAt(null);

        // Act
        FormDownloadUrlResponseDto result = mapper.toDownloadResponse(source);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo(new URI("https://example.com/forms/download-456.pdf"));
        assertThat(result.getExpiresAt()).isNull();
    }

    @Test
    @DisplayName("Should preserve long download URLs")
    void toDownloadResponse_LongUrl() throws Exception {
        // Arrange
        String longUrl = "https://storage.example.com/bucket/tax-forms/2024/user-" +
                UUID.randomUUID() + "/form-" + UUID.randomUUID() + ".pdf";
        
        FormDownloadUrlResponse source = new FormDownloadUrlResponse();
        source.setUrl(new URI(longUrl));

        // Act
        FormDownloadUrlResponseDto result = mapper.toDownloadResponse(source);

        // Assert
        assertThat(result.getUrl()).isEqualTo(new URI(longUrl));
    }
}
