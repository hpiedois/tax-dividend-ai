package com.taxdividend.backend.service.impl;

import com.taxdividend.backend.dto.FileUploadResultDTO;
import com.taxdividend.backend.api.dto.FormGenerationRequest;
import com.taxdividend.backend.dto.GenerateFormResultDTO;
import com.taxdividend.backend.exception.PdfGenerationException;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.GeneratedFormRepository;
import com.taxdividend.backend.repository.UserRepository;
import com.taxdividend.backend.service.PdfGenerationService;
import com.taxdividend.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of PDF generation service using Apache PDFBox.
 *
 * NOTE: This is a simplified implementation that generates basic PDFs.
 * In production, you would use official PDF templates for Forms 5000/5001.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private final UserRepository userRepository;
    private final DividendRepository dividendRepository;
    private final GeneratedFormRepository generatedFormRepository;
    private final StorageService storageService;

    @Value("${app.forms.expiry-days:30}")
    private int formExpiryDays;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Transactional
    public GenerateFormResultDTO generateForms(FormGenerationRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("Generating forms for user {} - type: {}, year: {}",
                 request.getUserId(), request.getFormType(), request.getTaxYear());

        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new PdfGenerationException("User not found: " + request.getUserId()));

            GenerateFormResultDTO result;

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

            result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            log.info("Form generation completed in {}ms", result.getProcessingTimeMs());

            return result;

        } catch (Exception e) {
            log.error("Failed to generate forms", e);
            return GenerateFormResultDTO.builder()
                    .success(false)
                    .errors(List.of("Generation failed: " + e.getMessage()))
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    @Transactional
    public GenerateFormResultDTO generateForm5000(User user, Integer taxYear) {
        log.info("Generating Form 5000 for user {}, year {}", user.getId(), taxYear);

        try {
            byte[] pdfBytes = createForm5000Pdf(user, taxYear);

            String fileName = String.format("Form_5000_%s_%d.pdf", user.getEmail(), taxYear);
            FileUploadResultDTO uploadResult = uploadFormPdf(pdfBytes, fileName, "forms");

            if (!uploadResult.getSuccess()) {
                throw new PdfGenerationException("Failed to upload Form 5000: " + uploadResult.getErrorMessage());
            }

            // Save to database
            GeneratedForm form = saveGeneratedForm(user, uploadResult, "5000", taxYear, 0);

            // Generate download URL
            String downloadUrl = storageService.generatePresignedUrl(form.getS3Key(), Duration.ofDays(7));

            return GenerateFormResultDTO.builder()
                    .formId(form.getId())
                    .s3Key(form.getS3Key())
                    .fileName(form.getFileName())
                    .formType("5000")
                    .taxYear(taxYear)
                    .fileSize(uploadResult.getFileSize())
                    .downloadUrl(downloadUrl)
                    .downloadUrlExpiresAt(LocalDateTime.now().plusDays(7))
                    .expiresAt(form.getExpiresAt())
                    .generatedAt(LocalDateTime.now())
                    .success(true)
                    .dividendCount(0)
                    .build();

        } catch (Exception e) {
            log.error("Failed to generate Form 5000", e);
            throw new PdfGenerationException("Form 5000 generation failed", e);
        }
    }

    @Override
    @Transactional
    public GenerateFormResultDTO generateForm5001(User user, List<Dividend> dividends, Integer taxYear) {
        log.info("Generating Form 5001 for user {}, year {}, {} dividends",
                 user.getId(), taxYear, dividends.size());

        try {
            byte[] pdfBytes = createForm5001Pdf(user, dividends, taxYear);

            String fileName = String.format("Form_5001_%s_%d.pdf", user.getEmail(), taxYear);
            FileUploadResultDTO uploadResult = uploadFormPdf(pdfBytes, fileName, "forms");

            if (!uploadResult.getSuccess()) {
                throw new PdfGenerationException("Failed to upload Form 5001: " + uploadResult.getErrorMessage());
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

            return GenerateFormResultDTO.builder()
                    .formId(form.getId())
                    .s3Key(form.getS3Key())
                    .fileName(form.getFileName())
                    .formType("5001")
                    .taxYear(taxYear)
                    .fileSize(uploadResult.getFileSize())
                    .downloadUrl(downloadUrl)
                    .downloadUrlExpiresAt(LocalDateTime.now().plusDays(7))
                    .expiresAt(form.getExpiresAt())
                    .generatedAt(LocalDateTime.now())
                    .success(true)
                    .dividendCount(dividends.size())
                    .build();

        } catch (Exception e) {
            log.error("Failed to generate Form 5001", e);
            throw new PdfGenerationException("Form 5001 generation failed", e);
        }
    }

    @Override
    @Transactional
    public GenerateFormResultDTO generateBundle(User user, List<Dividend> dividends, Integer taxYear) {
        log.info("Generating BUNDLE for user {}, year {}", user.getId(), taxYear);

        try {
            // Generate both PDFs
            byte[] form5000Pdf = createForm5000Pdf(user, taxYear);
            byte[] form5001Pdf = createForm5001Pdf(user, dividends, taxYear);

            // Create ZIP bundle
            byte[] zipBytes = createZipBundle(form5000Pdf, form5001Pdf, user.getEmail(), taxYear);

            String fileName = String.format("Bundle_%s_%d.zip", user.getEmail(), taxYear);
            FileUploadResultDTO uploadResult = uploadFormPdf(zipBytes, fileName, "bundles");

            if (!uploadResult.getSuccess()) {
                throw new PdfGenerationException("Failed to upload BUNDLE: " + uploadResult.getErrorMessage());
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

            return GenerateFormResultDTO.builder()
                    .formId(form.getId())
                    .s3Key(form.getS3Key())
                    .fileName(form.getFileName())
                    .formType("BUNDLE")
                    .taxYear(taxYear)
                    .fileSize(uploadResult.getFileSize())
                    .downloadUrl(downloadUrl)
                    .downloadUrlExpiresAt(LocalDateTime.now().plusDays(7))
                    .expiresAt(form.getExpiresAt())
                    .generatedAt(LocalDateTime.now())
                    .success(true)
                    .dividendCount(dividends.size())
                    .build();

        } catch (Exception e) {
            log.error("Failed to generate BUNDLE", e);
            throw new PdfGenerationException("BUNDLE generation failed", e);
        }
    }

    @Override
    @Transactional
    public GenerateFormResultDTO regenerateForm(UUID formId) {
        GeneratedForm existingForm = generatedFormRepository.findById(formId)
                .orElseThrow(() -> new PdfGenerationException("Form not found: " + formId));

        User user = existingForm.getUser();
        Integer taxYear = existingForm.getTaxYear();
        String formType = existingForm.getFormType();

        log.info("Regenerating form {} (type: {}) for user {}", formId, formType, user.getId());

        if ("5000".equals(formType)) {
            return generateForm5000(user, taxYear);
        } else if ("5001".equals(formType)) {
            List<Dividend> dividends = dividendRepository.findByFormId(formId);
            return generateForm5001(user, dividends, taxYear);
        } else if ("BUNDLE".equals(formType)) {
            List<Dividend> dividends = dividendRepository.findByFormId(formId);
            return generateBundle(user, dividends, taxYear);
        } else {
            throw new PdfGenerationException("Unknown form type: " + formType);
        }
    }

    @Override
    @Transactional
    public GenerateFormResultDTO generateForUserDividends(UUID userId, Integer taxYear, String bundleType) {
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
     * Create Form 5000 PDF (Attestation de résidence fiscale).
     */
    private byte[] createForm5000Pdf(User user, Integer taxYear) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float fontSize = 12;

                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // Title
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("FORMULAIRE 5000");
                contentStream.endText();
                yPosition -= 20;

                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Attestation de résidence fiscale en Suisse");
                contentStream.endText();
                yPosition -= 40;

                // User details
                contentStream.setFont(fontRegular, fontSize);
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Année fiscale : " + taxYear);
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Nom complet : " + (user.getFullName() != null ? user.getFullName() : "N/A"));
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Email : " + user.getEmail());
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Adresse : " + (user.getAddress() != null ? user.getAddress() : "N/A"));
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Canton : " + (user.getCanton() != null ? user.getCanton() : "N/A"));
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "NIF : " + (user.getTaxId() != null ? user.getTaxId() : "N/A"));
                yPosition -= 20;

                // Attestation text
                contentStream.setFont(fontRegular, fontSize);
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Je soussigné(e), certifie que je suis résident(e) fiscal(e) en Suisse");
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "pour l'année " + taxYear + ".");
                yPosition -= 40;

                // Signature
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Date : " + LocalDateTime.now().format(DATE_FORMATTER));
                yPosition -= 20;
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Signature : _______________________");
            }

            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Create Form 5001 PDF (Liquidation de dividendes).
     */
    private byte[] createForm5001Pdf(User user, List<Dividend> dividends, Integer taxYear) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float fontSize = 10;

                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // Title
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("FORMULAIRE 5001");
                contentStream.endText();
                yPosition -= 20;

                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Liquidation de dividendes");
                contentStream.endText();
                yPosition -= 30;

                // User info
                contentStream.setFont(fontRegular, fontSize);
                yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                        "Année : " + taxYear + " | Nom : " + user.getFullName());
                yPosition -= 20;

                // Dividends table
                BigDecimal totalGross = BigDecimal.ZERO;
                BigDecimal totalWithholding = BigDecimal.ZERO;
                BigDecimal totalReclaimable = BigDecimal.ZERO;

                for (Dividend dividend : dividends) {
                    yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                            String.format("- %s (%s)", dividend.getSecurityName(), dividend.getIsin()));
                    yPosition = writeTextLine(contentStream, fontRegular, fontSize, margin + 10, yPosition,
                            String.format("  Brut: %.2f %s | Retenue: %.2f %s | Récupérable: %.2f %s",
                                    dividend.getGrossAmount(), dividend.getCurrency(),
                                    dividend.getWithholdingTax(), dividend.getCurrency(),
                                    dividend.getReclaimableAmount(), dividend.getCurrency()));
                    yPosition -= 5;

                    totalGross = totalGross.add(dividend.getGrossAmount());
                    totalWithholding = totalWithholding.add(dividend.getWithholdingTax());
                    totalReclaimable = totalReclaimable.add(dividend.getReclaimableAmount());
                }

                yPosition -= 15;
                contentStream.setFont(fontBold, fontSize);
                yPosition = writeTextLine(contentStream, fontBold, fontSize, margin, yPosition,
                        String.format("TOTAL - Brut: %.2f | Retenue: %.2f | RÉCUPÉRABLE: %.2f",
                                totalGross, totalWithholding, totalReclaimable));
            }

            document.save(baos);
            return baos.toByteArray();
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

    /**
     * Helper to write a text line in PDF.
     */
    private float writeTextLine(PDPageContentStream contentStream, PDType1Font font, float fontSize,
                                 float x, float y, String text) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - (fontSize + 5);
    }
}
