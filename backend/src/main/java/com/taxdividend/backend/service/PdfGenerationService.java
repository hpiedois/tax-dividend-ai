package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.FormGenerationRequestDto;
import com.taxdividend.backend.api.dto.GenerateFormResultDto;
import com.taxdividend.backend.dto.FileUploadResultDTO;
import com.taxdividend.backend.exception.PdfGenerationException;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.GeneratedFormRepository;
import com.taxdividend.backend.repository.UserRepository;
import com.taxdividend.backend.service.pdf.Form5000FieldMapper;
import com.taxdividend.backend.service.pdf.Form5001FieldMapper;
import com.taxdividend.backend.service.pdf.PdfFormFiller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of PDF generation service using official French tax form
 * templates.
 *
 * Uses Apache PDFBox to fill official Form 5000 and 5001 templates located in
 * src/main/resources/templates/forms/.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final UserRepository userRepository;
    private final DividendRepository dividendRepository;
    private final GeneratedFormRepository generatedFormRepository;
    private final StorageService storageService;
    private final PdfFormFiller pdfFormFiller;
    private final Form5000FieldMapper form5000Mapper;
    private final Form5001FieldMapper form5001Mapper;

    @Value("${app.forms.expiry-days:30}")
    private int formExpiryDays;

    private static final String FORM_5000_TEMPLATE = "templates/forms/form_5000_template.pdf";
    private static final String FORM_5001_TEMPLATE = "templates/forms/form_5001_template.pdf";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public GenerateFormResultDto generateForms(FormGenerationRequestDto request) {
        long startTime = System.currentTimeMillis();

        log.info("Generating forms for user {} - type: {}, year: {}",
                request.getUserId(), request.getFormType(), request.getTaxYear());

        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new PdfGenerationException("User not found: " + request.getUserId()));

            GenerateFormResultDto result;

            switch (request.getFormType().getValue()) {
                case "5000":
                    result = generateForm5000(user, request.getTaxYear());
                    break;

                case "5001":
                    if (request.getDividendIds() == null || request.getDividendIds().isEmpty()) {
                        throw new PdfGenerationException("Dividend IDs required for Form 5001");
                    }
                    List<Dividend> dividends = dividendRepository.findAllById(request.getDividendIds());
                    result = generateForm5001(user, dividends, request.getTaxYear());
                    break;

                case "BUNDLE":
                    if (request.getDividendIds() == null || request.getDividendIds().isEmpty()) {
                        throw new PdfGenerationException("Dividend IDs required for BUNDLE");
                    }
                    List<Dividend> bundleDividends = dividendRepository.findAllById(request.getDividendIds());
                    result = generateBundle(user, bundleDividends, request.getTaxYear());
                    break;

                default:
                    throw new PdfGenerationException("Invalid form type: " + request.getFormType());
            }

            // result.setProcessingTimeMs(System.currentTimeMillis() - startTime); // Not in
            // API DTO
            log.info("Form generation completed");

            return result;

        } catch (PdfGenerationException e) {
            // Re-throw validation exceptions so tests can catch them
            throw e;
        } catch (Exception e) {
            log.error("Failed to generate forms", e);
            GenerateFormResultDto failure = new GenerateFormResultDto();
            failure.setSuccess(false);
            failure.addErrorsItem("Generation failed: " + e.getMessage());
            return failure;
        }
    }

    @Transactional
    public GenerateFormResultDto generateForm5000(User user, Integer taxYear) {
        log.info("Generating Form 5000 for user {}, year {}", user.getId(), taxYear);

        // Validate user data
        if (user.getFullName() == null || user.getAddress() == null || user.getCountry() == null) {
            throw new PdfGenerationException(
                    "User data incomplete: missing required fields (fullName, address, country)");
        }

        try {
            byte[] pdfBytes = createForm5000Pdf(user, taxYear);

            String fileName = String.format("Form_5000_%s_%d.pdf", user.getEmail(), taxYear);
            FileUploadResultDTO uploadResult = uploadFormPdf(pdfBytes, fileName, "forms");

            if (uploadResult == null || !uploadResult.getSuccess()) {
                String errorMsg = uploadResult != null ? uploadResult.getErrorMessage() : "Upload returned null";
                throw new PdfGenerationException("Failed to upload Form 5000: " + errorMsg);
            }

            // Save to database
            GeneratedForm form = saveGeneratedForm(user, uploadResult, "5000", taxYear, 0);

            // Generate download URL
            String downloadUrl = storageService.generatePresignedUrl(form.getS3Key(), Duration.ofDays(7));

            GenerateFormResultDto result = new GenerateFormResultDto();
            result.setFormId(form.getId());
            // result.setS3Key(form.getS3Key()); // Not in API DTO
            result.setFileName(form.getFileName());
            result.setFormType("5000");
            // result.setTaxYear(taxYear); // Not in API DTO? Checked generated file: it
            // does NOT have taxYear!
            // Checked fields: success, formId, formType, downloadUrl, fileName,
            // dividendCount, errors.
            // NO taxYear, fileSize, downloadUrlExpiresAt, expiresAt, generatedAt.

            // Result construction
            result.setDividendCount(0);
            result.setDownloadUrl(downloadUrl);
            result.setSuccess(true);

            return result;

        } catch (Exception e) {
            log.error("Failed to generate Form 5000", e);
            throw new PdfGenerationException("Form 5000 generation failed", e);
        }
    }

    @Transactional
    public GenerateFormResultDto generateForm5001(User user, List<Dividend> dividends, Integer taxYear) {
        log.info("Generating Form 5001 for user {}, year {}, {} dividends",
                user.getId(), taxYear, dividends.size());

        // Validate dividend list
        if (dividends == null || dividends.isEmpty()) {
            throw new PdfGenerationException("Cannot generate Form 5001: dividend list is empty");
        }

        try {
            byte[] pdfBytes = createForm5001Pdf(user, dividends, taxYear);

            String fileName = String.format("Form_5001_%s_%d.pdf", user.getEmail(), taxYear);
            FileUploadResultDTO uploadResult = uploadFormPdf(pdfBytes, fileName, "forms");

            if (uploadResult == null || !uploadResult.getSuccess()) {
                String errorMsg = uploadResult != null ? uploadResult.getErrorMessage() : "Upload returned null";
                throw new PdfGenerationException("Failed to upload Form 5001: " + errorMsg);
            }

            // Save to database
            GeneratedForm form = saveGeneratedForm(user, uploadResult, "5001", taxYear, dividends.size());

            // Link dividends to this form
            for (Dividend dividend : dividends) {
                dividend.setForm(form);
                dividendRepository.save(dividend);
            }

            // Generate download URL
            String downloadUrl = storageService.generatePresignedUrl(form.getS3Key(), Duration.ofDays(7));

            GenerateFormResultDto result = new GenerateFormResultDto();
            result.setFormId(form.getId());
            result.setFileName(form.getFileName());
            result.setFormType("5001");
            result.setDividendCount(dividends.size());
            result.setDownloadUrl(downloadUrl);
            result.setSuccess(true);

            return result;

        } catch (Exception e) {
            log.error("Failed to generate Form 5001", e);
            throw new PdfGenerationException("Form 5001 generation failed", e);
        }
    }

    @Transactional
    public GenerateFormResultDto generateBundle(User user, List<Dividend> dividends, Integer taxYear) {
        log.info("Generating BUNDLE for user {}, year {}", user.getId(), taxYear);

        try {
            // Generate both PDFs
            byte[] form5000Pdf = createForm5000Pdf(user, taxYear);
            byte[] form5001Pdf = createForm5001Pdf(user, dividends, taxYear);

            // Create ZIP bundle
            byte[] zipBytes = createZipBundle(form5000Pdf, form5001Pdf, user.getEmail(), taxYear);

            String fileName = String.format("Bundle_%s_%d.zip", user.getEmail(), taxYear);
            FileUploadResultDTO uploadResult = uploadFormPdf(zipBytes, fileName, "bundles");

            if (uploadResult == null || !uploadResult.getSuccess()) {
                String errorMsg = uploadResult != null ? uploadResult.getErrorMessage() : "Upload returned null";
                throw new PdfGenerationException("Failed to upload BUNDLE: " + errorMsg);
            }

            // Save to database
            GeneratedForm form = saveGeneratedForm(user, uploadResult, "BUNDLE", taxYear, dividends.size());

            // Link dividends to this form
            for (Dividend dividend : dividends) {
                dividend.setForm(form);
                dividendRepository.save(dividend);
            }

            // Generate download URL
            String downloadUrl = storageService.generatePresignedUrl(form.getS3Key(), Duration.ofDays(7));

            GenerateFormResultDto result = new GenerateFormResultDto();
            result.setFormId(form.getId());
            result.setFileName(form.getFileName());
            result.setFormType("BUNDLE");
            result.setDividendCount(dividends.size());
            result.setDownloadUrl(downloadUrl);
            result.setSuccess(true);

            return result;

        } catch (Exception e) {
            log.error("Failed to generate BUNDLE", e);
            throw new PdfGenerationException("BUNDLE generation failed", e);
        }
    }

    @Transactional
    public GenerateFormResultDto regenerateForm(UUID formId) {
        GeneratedForm existingForm = generatedFormRepository.findById(formId)
                .orElseThrow(() -> new PdfGenerationException("Form not found: " + formId));

        User user = existingForm.getUser();
        Integer taxYear = existingForm.getTaxYear();
        String formType = existingForm.getFormType();

        log.info("Regenerating form {} (type: {}) for user {}", formId, formType, user.getId());

        if ("5000".equals(formType)) {
            if (existingForm.getS3Key() != null) {
                storageService.deleteFile(existingForm.getS3Key());
            }
            return generateForm5000(user, taxYear);
        } else if ("5001".equals(formType)) {
            if (existingForm.getS3Key() != null) {
                storageService.deleteFile(existingForm.getS3Key());
            }
            List<Dividend> dividends = dividendRepository.findByFormId(formId);
            return generateForm5001(user, dividends, taxYear);
        } else if ("BUNDLE".equals(formType)) {
            if (existingForm.getS3Key() != null) {
                storageService.deleteFile(existingForm.getS3Key());
            }
            List<Dividend> dividends = dividendRepository.findByFormId(formId);
            return generateBundle(user, dividends, taxYear);
        } else {
            throw new PdfGenerationException("Unknown form type: " + formType);
        }
    }

    @Transactional
    public GenerateFormResultDto generateForUserDividends(UUID userId, Integer taxYear, String bundleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PdfGenerationException("User not found: " + userId));

        List<Dividend> unsubmittedDividends = dividendRepository.findByUserIdAndFormIsNull(userId);

        if (unsubmittedDividends.isEmpty()) {
            throw new PdfGenerationException("No unsubmitted dividends found for user");
        }

        log.info("Generating {} for user {} with {} unsubmitted dividends",
                bundleType, userId, unsubmittedDividends.size());

        switch (bundleType.toUpperCase()) {
            case "5000":
                return generateForm5000(user, taxYear);
            case "5001":
                return generateForm5001(user, unsubmittedDividends, taxYear);
            case "BUNDLE":
                return generateBundle(user, unsubmittedDividends, taxYear);
            default:
                throw new PdfGenerationException("Invalid bundle type: " + bundleType);
        }
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    /**
     * Create Form 5000 PDF (Attestation de résidence fiscale) using official
     * template.
     */
    private byte[] createForm5000Pdf(User user, Integer taxYear) throws IOException {
        log.debug("Creating Form 5000 PDF from template for user {}, year {}", user.getId(), taxYear);

        try {
            // Map user data to form fields
            Map<String, String> fieldValues = form5000Mapper.mapToFormFields(user, taxYear);

            // Fill template and flatten (make non-editable)
            return pdfFormFiller.fillPdfForm(FORM_5000_TEMPLATE, fieldValues, true);

        } catch (IOException e) {
            log.error("Failed to create Form 5000 PDF", e);
            throw new IOException("Failed to create Form 5000: " + e.getMessage(), e);
        }
    }

    /**
     * Create Form 5001 PDF (Liquidation de dividendes) using official template.
     */
    private byte[] createForm5001Pdf(User user, List<Dividend> dividends, Integer taxYear) throws IOException {
        log.debug("Creating Form 5001 PDF from template for user {}, year {}, {} dividends",
                user.getId(), taxYear, dividends.size());

        try {
            // Map user and dividend data to form fields
            Map<String, String> fieldValues = form5001Mapper.mapToFormFields(user, dividends, taxYear);

            // Fill template and flatten (make non-editable)
            return pdfFormFiller.fillPdfForm(FORM_5001_TEMPLATE, fieldValues, true);

        } catch (IOException e) {
            log.error("Failed to create Form 5001 PDF", e);
            throw new IOException("Failed to create Form 5001: " + e.getMessage(), e);
        }
    }

    /**
     * Create ZIP bundle with both forms.
     */
    private byte[] createZipBundle(byte[] form5000, byte[] form5001, String userEmail, Integer taxYear)
            throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos)) {

            // Add Form 5000
            ZipEntry entry5000 = new ZipEntry(String.format("Form_5000_%s_%d.pdf", userEmail, taxYear));
            zos.putNextEntry(entry5000);
            zos.write(form5000);
            zos.closeEntry();

            // Add Form 5001
            ZipEntry entry5001 = new ZipEntry(String.format("Form_5001_%s_%d.pdf", userEmail, taxYear));
            zos.putNextEntry(entry5001);
            zos.write(form5001);
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * Upload form PDF to storage.
     */
    private FileUploadResultDTO uploadFormPdf(byte[] pdfBytes, String fileName, String folder) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes)) {
            return storageService.uploadFile(bais, fileName, "application/pdf", folder);
        } catch (IOException e) {
            log.error("Failed to upload form PDF", e);
            return FileUploadResultDTO.builder()
                    .success(false)
                    .errorMessage("Failed to upload: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Save generated form to database.
     */
    private GeneratedForm saveGeneratedForm(User user, FileUploadResultDTO uploadResult,
            String formType, Integer taxYear, int dividendCount) {
        GeneratedForm form = GeneratedForm.builder()
                .user(user)
                .s3Key(uploadResult.getS3Key())
                .fileName(uploadResult.getFileName())
                .taxYear(taxYear)
                .formType(formType)
                .status("GENERATED")
                .expiresAt(LocalDateTime.now().plusDays(formExpiryDays))
                .build();

        return generatedFormRepository.save(form);
    }

}
