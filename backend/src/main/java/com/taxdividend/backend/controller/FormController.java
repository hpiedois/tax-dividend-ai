package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.dto.GeneratedFormDto;

import com.taxdividend.backend.api.FormsApi;
import com.taxdividend.backend.api.dto.FormGenerationRequestDto;
import com.taxdividend.backend.api.dto.FormDownloadUrlResponseDto;
import com.taxdividend.backend.api.dto.GenerateFormResultDto;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.FormService;
import com.taxdividend.backend.service.PdfGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for tax form generation and management.
 *
 * Endpoints:
 * - POST /internal/forms/generate - Generate tax forms (5000, 5001, BUNDLE)
 * - GET /internal/forms - List user's generated forms
 * - GET /internal/forms/{id} - Get form metadata
 * - GET /internal/forms/{id}/download - Download form PDF/ZIP
 * - GET /internal/forms/{id}/download-url - Get pre-signed download URL
 * - POST /internal/forms/{id}/regenerate - Regenerate an expired form
 * - DELETE /internal/forms/{id} - Delete form
 */
@Slf4j
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Tag(name = "Forms", description = "Tax form generation and download API")
public class FormController implements FormsApi {

    private final PdfGenerationService pdfGenerationService;
    // GeneratedFormRepository removed
    // StorageService removed (handled in service)
    private final AuditService auditService; // Used for generation log
    private final FormService formService;

    /**
     * Generate tax forms (5000, 5001, or BUNDLE).
     */
    @Override
    public ResponseEntity<GenerateFormResultDto> generateForms(
            UUID xUserId,
            FormGenerationRequestDto request) {

        request.setUserId(xUserId);

        log.info("Generating forms for user {}: type={}, year={}, dividends={}",
                xUserId, request.getFormType(), request.getTaxYear(),
                request.getDividendIds() != null ? request.getDividendIds().size() : 0);

        try {
            GenerateFormResultDto result = pdfGenerationService.generateForms(request);

            if (result.getSuccess()) {
                // Audit log
                auditService.logFormGeneration(xUserId, result.getFormId(),
                        result.getFormType(), result.getDividendCount());

                log.info("Form generated successfully: {} ({})", result.getFormId(), result.getFormType());
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Failed to generate forms", e);

            GenerateFormResultDto errorResult = new GenerateFormResultDto();
            errorResult.setSuccess(false);
            errorResult.setErrors(List.of("Generation failed: " + e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResult);
        }
    }

    /**
     * List all forms for a user.
     */
    @Override
    public ResponseEntity<List<GeneratedFormDto>> listForms(
            UUID xUserId,
            Integer taxYear,
            String formType) {

        List<GeneratedFormDto> forms = formService.listForms(xUserId, taxYear, formType);
        log.info("Retrieved {} forms for user {}", forms.size(), xUserId);

        return ResponseEntity.ok(forms);
    }

    /**
     * Get form metadata by ID.
     */
    @Override
    public ResponseEntity<GeneratedFormDto> getForm(
            UUID id,
            UUID xUserId) {

        return formService.getForm(id, xUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Download form PDF or ZIP.
     */
    @Override
    public ResponseEntity<Resource> downloadForm(
            UUID id,
            UUID xUserId) {

        return formService.downloadForm(id, xUserId);
    }

    /**
     * Get pre-signed download URL (OpenAPI implementation).
     */
    @Override
    public ResponseEntity<FormDownloadUrlResponseDto> getFormDownloadUrl(
            UUID id,
            UUID xUserId,
            Integer expiresIn) {

        // Verify form exists and user has access
        Optional<GeneratedFormDto> formOpt = formService.getForm(id, xUserId);
        if (formOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Convert seconds to hours for service call (service expects hours)
        int expirationHours = expiresIn != null ? expiresIn / 3600 : 1;

        return formService.getDownloadUrl(id, xUserId, expirationHours)
                .map(url -> {
                    FormDownloadUrlResponseDto response = new FormDownloadUrlResponseDto();
                    try {
                        response.setUrl(new java.net.URI(url));
                        // Calculate expiration timestamp
                        java.time.OffsetDateTime expiresAt = java.time.OffsetDateTime.now()
                                .plusSeconds(expiresIn != null ? expiresIn : 3600);
                        response.setExpiresAt(expiresAt);
                    } catch (java.net.URISyntaxException e) {
                        log.error("Invalid URL generated: {}", url, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .<FormDownloadUrlResponseDto>build()
                                .getBody();
                    }
                    return response;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Regenerate an expired or lost form (OpenAPI implementation).
     */
    @Override
    public ResponseEntity<GeneratedFormDto> regenerateForm(
            UUID id,
            UUID xUserId) {

        // Verify form exists and user has access
        Optional<GeneratedFormDto> formOpt = formService.getForm(id, xUserId);
        if (formOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GeneratedFormDto existingForm = formOpt.get();

        // Check if form is expired (optional business logic - could be in service)
        // For now, allow regeneration regardless of expiration

        try {
            GenerateFormResultDto result = pdfGenerationService.regenerateForm(id);

            if (result.getSuccess()) {
                // Audit log
                auditService.logAction(xUserId, "FORM_REGENERATED", "FORM", id, null, null, null);

                log.info("Form {} regenerated successfully for user {}", id, xUserId);

                // Return the regenerated form metadata
                return formService.getForm(result.getFormId(), xUserId)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            } else {
                log.error("Form regeneration failed: {}", result.getErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

        } catch (Exception e) {
            log.error("Failed to regenerate form {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a form.
     */
    @Override
    public ResponseEntity<Void> deleteForm(
            UUID id,
            UUID xUserId) {

        try {
            formService.deleteForm(id, xUserId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
