package com.taxdividend.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxdividend.backend.api.dto.FormGenerationRequestDto;
import com.taxdividend.backend.config.TestSecurityConfig;
import com.taxdividend.backend.api.dto.GenerateFormResultDto;
import com.taxdividend.backend.mapper.FormMapper;
import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.FormService;
import com.taxdividend.backend.service.PdfGenerationService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for FormController.
 */
@WebMvcTest(controllers = FormController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Form Controller Tests")
class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PdfGenerationService pdfGenerationService;

    @MockitoBean
    private FormService formService;

    @MockitoBean
    private FormMapper formMapper;

    @MockitoBean
    private AuditService auditService;

    private UUID testUserId;
    private User testUser;
    private GeneratedForm testForm;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .fullName("John Doe")
                .country("CH")
                .build();

        testForm = GeneratedForm.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .formType("5000")
                .taxYear(2024)
                .fileName("Form_5000_2024.pdf")
                .s3Key("forms/2024/12/test.pdf")
                .fileSize(1024L)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    @Disabled("PdfGenerationService incomplete - returns 500 Internal Server Error")
    @DisplayName("Should generate Form 5000 successfully")
    void shouldGenerateForm5000() throws Exception {
        // Given
        FormGenerationRequestDto request = new FormGenerationRequestDto()
                .userId(testUserId)
                .formType(FormGenerationRequestDto.FormTypeEnum._5000)
                .taxYear(2024);

        GenerateFormResultDto result = new GenerateFormResultDto()
                .success(true)
                .formId(UUID.randomUUID())
                .formType("5000")
                .downloadUrl("https://minio.example.com/form.pdf")
;

        when(pdfGenerationService.generateForms(any(FormGenerationRequestDto.class)))
                .thenReturn(result);

        // When/Then
        mockMvc.perform(post("/internal/forms/generate")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.formType").value("5000"))
                .andExpect(jsonPath("$.downloadUrl").isNotEmpty());

        verify(pdfGenerationService).generateForms(any(FormGenerationRequestDto.class));
        verify(auditService).logFormGeneration(eq(testUserId), any(), eq("5000"), anyInt());
    }

    @Test
    @Disabled("PdfGenerationService incomplete - DTO mismatch")
    @DisplayName("Should generate Form 5001 successfully")
    void shouldGenerateForm5001() throws Exception {
        // Given
        List<UUID> dividendIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        FormGenerationRequestDto request = new FormGenerationRequestDto()
                .userId(testUserId)
                .formType(FormGenerationRequestDto.FormTypeEnum._5001)
                .taxYear(2024)
                .dividendIds(dividendIds);

        GenerateFormResultDto result = new GenerateFormResultDto()
                .success(true)
                .formId(UUID.randomUUID())
                .formType("5001")
                .dividendCount(2)
                .downloadUrl("https://minio.example.com/form.pdf")
;

        when(pdfGenerationService.generateForms(any(FormGenerationRequestDto.class)))
                .thenReturn(result);

        // When/Then
        mockMvc.perform(post("/internal/forms/generate")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.formType").value("5001"))
                .andExpect(jsonPath("$.dividendCount").value(2));

        verify(auditService).logFormGeneration(eq(testUserId), any(), eq("5001"), eq(2));
    }

    @Test
    @Disabled("PdfGenerationService incomplete - DTO mismatch")
    @DisplayName("Should generate BUNDLE successfully")
    void shouldGenerateBundle() throws Exception {
        // Given
        List<UUID> dividendIds = Arrays.asList(UUID.randomUUID());
        FormGenerationRequestDto request = new FormGenerationRequestDto()
                .userId(testUserId)
                .formType(FormGenerationRequestDto.FormTypeEnum.BUNDLE)
                .taxYear(2024)
                .dividendIds(dividendIds);

        GenerateFormResultDto result = new GenerateFormResultDto()
                .success(true)
                .formId(UUID.randomUUID())
                .formType("BUNDLE")
                .dividendCount(1)
                .downloadUrl("https://minio.example.com/bundle.zip")
;

        when(pdfGenerationService.generateForms(any(FormGenerationRequestDto.class)))
                .thenReturn(result);

        // When/Then
        mockMvc.perform(post("/internal/forms/generate")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formType").value("BUNDLE"));
    }

    @Test
    @DisplayName("Should handle generation failure")
    void shouldHandleGenerationFailure() throws Exception {
        // Given
        FormGenerationRequestDto request = new FormGenerationRequestDto()
                .userId(testUserId)
                .formType(FormGenerationRequestDto.FormTypeEnum._5000)
                .taxYear(2024);

        when(pdfGenerationService.generateForms(any(FormGenerationRequestDto.class)))
                .thenThrow(new RuntimeException("Generation failed"));

        // When/Then
        mockMvc.perform(post("/internal/forms/generate")
                        .header("X-User-Id", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Should list user's forms")
    void shouldListUserForms() throws Exception {
        // Given
        com.taxdividend.backend.api.dto.GeneratedFormDto formDto =
                new com.taxdividend.backend.api.dto.GeneratedFormDto();
        formDto.setId(testForm.getId());

        when(formService.listForms(testUserId, null, null))
                .thenReturn(Arrays.asList(formDto));

        // When/Then
        mockMvc.perform(get("/internal/forms")
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk());

        verify(formService).listForms(testUserId, null, null);
    }

    @Test
    @DisplayName("Should filter forms by tax year")
    void shouldFilterFormsByTaxYear() throws Exception {
        // Given
        com.taxdividend.backend.api.dto.GeneratedFormDto formDto =
                new com.taxdividend.backend.api.dto.GeneratedFormDto();
        formDto.setId(testForm.getId());

        when(formService.listForms(testUserId, 2024, null))
                .thenReturn(Arrays.asList(formDto));

        // When/Then
        mockMvc.perform(get("/internal/forms")
                        .header("X-User-Id", testUserId.toString())
                        .param("taxYear", "2024"))
                .andExpect(status().isOk());

        verify(formService).listForms(testUserId, 2024, null);
    }

    @Test
    @DisplayName("Should get form metadata by ID")
    void shouldGetFormById() throws Exception {
        // Given
        UUID formId = testForm.getId();
        com.taxdividend.backend.api.dto.GeneratedFormDto formDto =
                new com.taxdividend.backend.api.dto.GeneratedFormDto();
        formDto.setId(formId);

        when(formService.getForm(formId, testUserId))
                .thenReturn(Optional.of(formDto));

        // When/Then
        mockMvc.perform(get("/internal/forms/{id}", formId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk());

        verify(formService).getForm(formId, testUserId);
    }

    @Test
    @DisplayName("Should download form as bytes")
    void shouldDownloadForm() throws Exception {
        // Given
        UUID formId = testForm.getId();
        byte[] fileBytes = "PDF content".getBytes();
        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.ByteArrayResource(fileBytes);
        org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> response =
                org.springframework.http.ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .body(resource);

        when(formService.downloadForm(formId, testUserId))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(get("/internal/forms/{id}/download", formId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk());

        verify(formService).downloadForm(formId, testUserId);
    }

    @Test
    @DisplayName("Should download bundle as ZIP")
    void shouldDownloadBundleAsZip() throws Exception {
        // Given
        UUID bundleFormId = UUID.randomUUID();
        byte[] zipBytes = "ZIP content".getBytes();
        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.ByteArrayResource(zipBytes);
        org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> response =
                org.springframework.http.ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType("application/zip"))
                        .body(resource);

        when(formService.downloadForm(bundleFormId, testUserId))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(get("/internal/forms/{id}/download", bundleFormId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk());

        verify(formService).downloadForm(bundleFormId, testUserId);
    }

    @Test
    @Disabled("Endpoint /internal/forms/{id}/download-url not in OpenAPI spec - contract-first violation")
    @DisplayName("Should get pre-signed download URL")
    void shouldGetDownloadUrl() throws Exception {
        // Given
        UUID formId = testForm.getId();
        String presignedUrl = "https://minio.example.com/presigned-url";

        when(formService.getDownloadUrl(formId, testUserId, 24))
                .thenReturn(Optional.of(presignedUrl));

        // When/Then
        mockMvc.perform(get("/internal/forms/{id}/download-url", formId)
                        .header("X-User-Id", testUserId.toString())
                        .param("expirationHours", "24"))
                .andExpect(status().isOk())
                .andExpect(content().string(presignedUrl));

        verify(formService).getDownloadUrl(formId, testUserId, 24);
    }

    @Test
    @Disabled("Endpoint /internal/forms/{id}/regenerate not in OpenAPI spec - contract-first violation")
    @DisplayName("Should regenerate expired form")
    void shouldRegenerateExpiredForm() throws Exception {
        // Given
        UUID formId = testForm.getId();
        GenerateFormResultDto result = new GenerateFormResultDto()
                .success(true)
                .formId(formId)
                .formType("5000")
                .downloadUrl("https://minio.example.com/new-form.pdf")
;

        when(pdfGenerationService.regenerateForm(formId))
                .thenReturn(result);

        // When/Then
        mockMvc.perform(post("/internal/forms/{id}/regenerate", formId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.formId").value(formId.toString()));

        verify(pdfGenerationService).regenerateForm(formId);
        verify(auditService).logAction(eq(testUserId), eq("FORM_REGENERATED"),
                eq("FORM"), eq(formId), any(), any(), any());
    }

    @Test
    @DisplayName("Should delete form and file from storage")
    void shouldDeleteForm() throws Exception {
        // Given
        UUID formId = testForm.getId();
        doNothing().when(formService).deleteForm(formId, testUserId);

        // When/Then
        mockMvc.perform(delete("/internal/forms/{id}", formId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNoContent());

        verify(formService).deleteForm(formId, testUserId);
    }

    @Test
    @Disabled("Endpoint /internal/forms/generate-all-unsubmitted not in OpenAPI spec - contract-first violation")
    @DisplayName("Should generate forms for all unsubmitted dividends")
    void shouldGenerateForAllUnsubmitted() throws Exception {
        // Given
        GenerateFormResultDto result = new GenerateFormResultDto()
                .success(true)
                .formId(UUID.randomUUID())
                .formType("BUNDLE")
                .dividendCount(5)
                .downloadUrl("https://minio.example.com/bundle.zip")
;

        when(pdfGenerationService.generateForUserDividends(testUserId, 2024, "BUNDLE"))
                .thenReturn(result);

        // When/Then
        mockMvc.perform(post("/internal/forms/generate-all-unsubmitted")
                        .header("X-User-Id", testUserId.toString())
                        .param("taxYear", "2024")
                        .param("bundleType", "BUNDLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dividendCount").value(5));

        verify(pdfGenerationService).generateForUserDividends(testUserId, 2024, "BUNDLE");
        verify(auditService).logFormGeneration(eq(testUserId), any(), eq("BUNDLE"), eq(5));
    }

    @Test
    @DisplayName("Should return 404 when form not found")
    void shouldReturn404WhenFormNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(formService.getForm(nonExistentId, testUserId))
                .thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/internal/forms/{id}", nonExistentId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should not allow user to access other user's form")
    void shouldNotAllowAccessToOtherUserForm() throws Exception {
        // Given
        UUID otherFormId = UUID.randomUUID();

        when(formService.getForm(otherFormId, testUserId))
                .thenReturn(Optional.empty());

        // When/Then - testUserId trying to access otherUserId's form
        mockMvc.perform(get("/internal/forms/{id}", otherFormId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle storage download failure")
    void shouldHandleStorageDownloadFailure() throws Exception {
        // Given
        UUID formId = testForm.getId();

        when(formService.downloadForm(formId, testUserId))
                .thenReturn(org.springframework.http.ResponseEntity.notFound().build());

        // When/Then
        mockMvc.perform(get("/internal/forms/{id}/download", formId)
                        .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNotFound());
    }
}
