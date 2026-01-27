package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.PdfApi;
import com.taxdividend.backend.model.FormGenerationRequest;
import com.taxdividend.backend.model.PdfGenerationResponse;
import com.taxdividend.backend.service.PdfService;
import com.taxdividend.backend.service.StorageService;
import com.taxdividend.backend.service.ZipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.UUID;

@RestController
public class PdfController implements PdfApi {

    private final PdfService pdfService;
    private final ZipService zipService;
    private final StorageService storageService;

    public PdfController(PdfService pdfService, ZipService zipService, StorageService storageService) {
        this.pdfService = pdfService;
        this.zipService = zipService;
        this.storageService = storageService;
    }

    @Override
    public ResponseEntity<PdfGenerationResponse> generatePdf(FormGenerationRequest request) {
        // 1. Generate PDF content (via Strategy)
        byte[] pdfContent = pdfService.generateForms(request);

        // 2. Wrap in ZIP (even if single file, to be consistent with architecture flow
        // and extensible)
        String country = request.getCountry() != null ? request.getCountry() : "FR";
        String fileName = "tax-forms-" + country + "-" + request.getTaxYear() + ".pdf";

        byte[] zipContent = zipService.createZip(Map.of(fileName, pdfContent));
        String zipFileName = "tax-forms-" + request.getTaxYear() + ".zip";

        // 3. Upload to Storage
        String userId = "anonymous"; // Fallback
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            userId = principal.toString();
        }

        String key = storageService.upload(zipContent, userId);
        String downloadUrl = storageService.generatePresignedUrl(key);

        return ResponseEntity.ok(new PdfGenerationResponse()
                .formId(UUID.randomUUID().toString())
                .fileName(zipFileName)
                .downloadUrl(downloadUrl));
    }

    @Override
    public ResponseEntity<com.taxdividend.backend.model.ParseMakePdf200Response> parseMakePdf(MultipartFile file) {
        // Not implemented yet
        return ResponseEntity.ok(new com.taxdividend.backend.model.ParseMakePdf200Response().rawText("TODO"));
    }
}
