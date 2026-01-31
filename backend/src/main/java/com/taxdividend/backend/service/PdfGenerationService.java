package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.FormGenerationRequest;
import com.taxdividend.backend.dto.GenerateFormResultDTO;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.User;

import java.util.List;
import java.util.UUID;

/**
 * Service for generating PDF tax forms (5000, 5001).
 *
 * Form 5000: Attestation de résidence fiscale (Swiss tax residence certificate)
 * Form 5001: Liquidation de dividendes (Dividend reclaim details)
 */
public interface PdfGenerationService {

    /**
     * Generate tax forms based on request.
     *
     * @param request Form generation request
     * @return Generation result with download URL
     */
    GenerateFormResultDTO generateForms(FormGenerationRequest request);

    /**
     * Generate Form 5000 (attestation de résidence fiscale).
     *
     * @param user User requesting the form
     * @param taxYear Tax year
     * @return Generation result
     */
    GenerateFormResultDTO generateForm5000(User user, Integer taxYear);

    /**
     * Generate Form 5001 (liquidation de dividendes).
     *
     * @param user User requesting the form
     * @param dividends List of dividends to include
     * @param taxYear Tax year
     * @return Generation result
     */
    GenerateFormResultDTO generateForm5001(User user, List<Dividend> dividends, Integer taxYear);

    /**
     * Generate both forms (5000 + 5001) as a ZIP bundle.
     *
     * @param user User requesting the forms
     * @param dividends List of dividends for Form 5001
     * @param taxYear Tax year
     * @return Generation result
     */
    GenerateFormResultDTO generateBundle(User user, List<Dividend> dividends, Integer taxYear);

    /**
     * Regenerate a form by form ID.
     * Useful if original was lost or expired.
     *
     * @param formId Existing form ID
     * @return New generation result
     */
    GenerateFormResultDTO regenerateForm(UUID formId);

    /**
     * Generate forms for all unsubmitted dividends of a user.
     *
     * @param userId User ID
     * @param taxYear Tax year
     * @param bundleType Type: "5000", "5001", or "BUNDLE"
     * @return Generation result
     */
    GenerateFormResultDTO generateForUserDividends(UUID userId, Integer taxYear, String bundleType);
}
