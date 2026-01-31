package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.FormsApi;
import com.taxdividend.backend.api.dto.FormGenerationRequest;
import com.taxdividend.backend.api.dto.GenerateFormResultDTO; // API DTO
import com.taxdividend.backend.mapper.FormMapper;
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
    private final FormMapper formMapper;
    private final FormService formService;

    /**
     * Generate tax forms (5000, 5001, or BUNDLE).
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.GenerateFormResultDTO> generateForms(
            UUID xUserId,
            FormGenerationRequest request) {

        request.setUserId(xUserId);

        log.info("Generating forms for user {}: type={}, year={}, dividends={}",
                xUserId, request.getFormType(), request.getTaxYear(),
                request.getDividendIds() != null ? request.getDividendIds().size() : 0);

        try {
            com.taxdividend.backend.dto.GenerateFormResultDTO result = pdfGenerationService.generateForms(request);

            if (result.getSuccess()) {
                // Audit log
                auditService.logFormGeneration(xUserId, result.getFormId(),
                        result.getFormType(), result.getDividendCount());

                log.info("Form generated successfully: {} ({})", result.getFormId(), result.getFormType());
            }

            return ResponseEntity.ok(formMapper.toApiResultDto(result));

        } catch (Exception e) {
            log.error("Failed to generate forms", e);

            com.taxdividend.backend.api.dto.GenerateFormResultDTO errorResult = new com.taxdividend.backend.api.dto.GenerateFormResultDTO();
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
    public ResponseEntity<List<com.taxdividend.backend.api.dto.GeneratedForm>> listForms(
            UUID xUserId,
            Integer taxYear,
            String formType) {

        List<com.taxdividend.backend.api.dto.GeneratedForm> forms = formService.listForms(xUserId, taxYear, formType);
        log.info("Retrieved {} forms for user {}", forms.size(), xUserId);

        return ResponseEntity.ok(forms);
    }

    /**
     * Get form metadata by ID.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.GeneratedForm> getForm(
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
     * Get pre-signed download URL (for frontend to download directly from S3).
     */
    @GetMapping("/{id}/download-url")
    @Operation(summary = "Get download URL", description = "Get a temporary pre-signed URL for downloading the form")
    public ResponseEntity<String> getDownloadUrl(
            @Parameter(description = "Form ID") @PathVariable UUID id,
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader,
            @Parameter(description = "URL expiration in hours") @RequestParam(defaultValue = "24") int expirationHours) {

        UUID userId = UUID.fromString(userIdHeader);

        return formService.getDownloadUrl(id, userId, expirationHours)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Regenerate an expired or lost form.
     */
    @PostMapping("/{id}/regenerate")
    @Operation(summary = "Regenerate form", description = "Regenerate a form that has expired or been lost")
    public ResponseEntity<GenerateFormResultDTO> regenerateForm(
            @Parameter(description = "Form ID") @PathVariable UUID id,
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader) {

        UUID userId = UUID.fromString(userIdHeader);

        // Verification of existence logic handled in service ideally, but
        // pdfGenerationService might need ID.
        // Assuming pdfGenerationService handles it.
        // But wait, existing code checked existence first via repo.
        // I should stick to existing behavior if I can, or delegate.
        // Actually, PdfGenerationService.regenerateForm probably handles loading.
        // But current controller code loaded entity to check user ownership!
        /*
         * GeneratedForm existingForm = generatedFormRepository.findById(id)
         * .filter(f -> f.getUser().getId().equals(userId))
         * .orElse(null);
         */

        Optional<com.taxdividend.backend.api.dto.GeneratedForm> formOpt = formService.getForm(id, userId);
        if (formOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            com.taxdividend.backend.dto.GenerateFormResultDTO result = pdfGenerationService.regenerateForm(id);

            if (result.getSuccess()) {
                // Audit log
                auditService.logAction(userId, "FORM_REGENERATED", "FORM", id, null, null, null);

                log.info("Form {} regenerated successfully for user {}", id, userId);
            }

            // Need to map Result DTO (backend) to API DTO.
            // result is backend DTO (com.taxdividend.backend.dto.GenerateFormResultDTO)
            // generic return type is GenerateFormResultDTO (API)
            // Controller signature says ResponseEntity<GenerateFormResultDTO> but imports
            // are inconsistent.
            // Using formMapper.toApiResultDto(result)
            return ResponseEntity.ok(formMapper.toApiResultDto(result));

        } catch (Exception e) {
            log.error("Failed to regenerate form {}", id, e);

            com.taxdividend.backend.api.dto.GenerateFormResultDTO errorRes = new com.taxdividend.backend.api.dto.GenerateFormResultDTO();
            errorRes.setSuccess(false);
            errorRes.setErrors(List.of("Regeneration failed: " + e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorRes);
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

    /**
     * Get forms by status.
     * Fixed: Returns DTOs
     */
    @GetMapping("/by-status")
    @Operation(summary = "Get forms by status", description = "Filter forms by status (GENERATED, SIGNED, SUBMITTED, etc.)")
    public ResponseEntity<List<com.taxdividend.backend.api.dto.GeneratedForm>> getFormsByStatus(
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader,
            @Parameter(description = "Status filter") @RequestParam String status) {

        UUID userId = UUID.fromString(userIdHeader);

        List<com.taxdividend.backend.api.dto.GeneratedForm> forms = formService.getFormsByStatus(userId, status);

        log.info("Found {} forms with status {} for user {}", forms.size(), status, userId);

        return ResponseEntity.ok(forms);
    }

    /**
     * Generate forms for all unsubmitted dividends.
     */
    @PostMapping("/generate-all-unsubmitted")
    @Operation(summary = "Generate for all unsubmitted dividends", description = "Automatically generate forms for all dividends not yet linked to a form")
    public ResponseEntity<com.taxdividend.backend.api.dto.GenerateFormResultDTO> generateForAllUnsubmitted(
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader,
            @Parameter(description = "Tax year") @RequestParam Integer taxYear,
            @Parameter(description = "Bundle type (5000, 5001, BUNDLE)") @RequestParam(defaultValue = "BUNDLE") String bundleType) {

        UUID userId = UUID.fromString(userIdHeader);

        try {
            com.taxdividend.backend.dto.GenerateFormResultDTO result = pdfGenerationService.generateForUserDividends(
                    userId, taxYear, bundleType);

            if (result.getSuccess()) {
                // Audit log
                auditService.logFormGeneration(userId, result.getFormId(),
                        result.getFormType(), result.getDividendCount());

                log.info("Generated {} for all unsubmitted dividends: {} dividends",
                        bundleType, result.getDividendCount());
            }

            return ResponseEntity.ok(formMapper.toApiResultDto(result));

        } catch (Exception e) {
            log.error("Failed to generate forms for unsubmitted dividends", e);

            com.taxdividend.backend.api.dto.GenerateFormResultDTO error = new com.taxdividend.backend.api.dto.GenerateFormResultDTO();
            error.setSuccess(false);
            error.setErrors(List.of("Generation failed: " + e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}
