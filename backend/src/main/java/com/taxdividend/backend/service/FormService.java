package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.GeneratedFormDto;
import com.taxdividend.backend.mapper.FormMapper;
import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.repository.GeneratedFormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormService {

    private final GeneratedFormRepository generatedFormRepository;
    private final StorageService storageService;
    private final AuditService auditService;
    private final FormMapper mapper;

    public List<GeneratedFormDto> listForms(UUID userId, Integer taxYear, String formType) {
        List<GeneratedForm> forms;

        if (taxYear != null) {
            forms = generatedFormRepository.findByUserIdAndTaxYear(userId, taxYear);
        } else {
            forms = generatedFormRepository.findByUserId(userId);
        }

        if (formType != null) {
            forms = forms.stream()
                    .filter(f -> f.getFormType().equalsIgnoreCase(formType))
                    .toList();
        }

        // Assuming mapper.toGeneratedFormDto(List<GeneratedForm>) exists
        return mapper.toGeneratedFormDto(forms);
    }

    public Optional<GeneratedFormDto> getForm(UUID id, UUID userId) {
        return generatedFormRepository.findById(id)
                .filter(form -> form.getUser().getId().equals(userId))
                .map(f -> mapper.toApiDto(f));
    }

    public ResponseEntity<Resource> downloadForm(UUID id, UUID userId) {
        GeneratedForm form = generatedFormRepository.findById(id)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElse(null);

        if (form == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] fileBytes = storageService.downloadFileAsBytes(form.getS3Key());

            String contentType = "BUNDLE".equals(form.getFormType())
                    ? "application/zip"
                    : "application/pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", form.getFileName());
            headers.setContentLength(fileBytes.length);

            log.info("Downloading form {} for user {}: {} ({} bytes)",
                    id, userId, form.getFileName(), fileBytes.length);

            auditService.logAction(userId, "FORM_DOWNLOADED", "FORM", id, null, null, null);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(new ByteArrayInputStream(fileBytes)));

        } catch (Exception e) {
            log.error("Failed to download form {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public Optional<String> getDownloadUrl(UUID id, UUID userId, int expirationHours) {
        GeneratedForm form = generatedFormRepository.findById(id)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElse(null);

        if (form == null) {
            return Optional.empty();
        }

        try {
            String url = storageService.generatePresignedUrl(
                    form.getS3Key(),
                    java.time.Duration.ofHours(expirationHours));
            return Optional.of(url);
        } catch (Exception e) {
            log.error("Failed to generate download URL for form {}", id, e);
            return Optional.empty();
        }
    }

    @Transactional
    public void deleteForm(UUID id, UUID userId) {
        GeneratedForm form = generatedFormRepository.findById(id)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElse(null);

        if (form == null) {
            // Already gone or not owned
            return;
        }

        try {
            boolean deleted = storageService.deleteFile(form.getS3Key());
            if (!deleted) {
                log.warn("Failed to delete file from storage: {}", form.getS3Key());
            }

            generatedFormRepository.delete(form);
            auditService.logAction(userId, "FORM_DELETED", "FORM", id, null, null, null);
            log.info("Deleted form {} for user {}", id, userId);

        } catch (Exception e) {
            log.error("Failed to delete form {}", id, e);
            throw new RuntimeException("Failed to delete form", e);
        }
    }

    public List<GeneratedFormDto> getFormsByStatus(UUID userId, String status) {
        List<GeneratedForm> userForms = generatedFormRepository.findByUserId(userId);
        return userForms.stream()
                .filter(f -> status.equalsIgnoreCase(f.getStatus()))
                .map(f -> mapper.toApiDto(f))
                .toList();
    }
}
