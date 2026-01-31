package com.taxdividend.backend.service;

import com.taxdividend.backend.dto.TaxCalculationBatchResultDTO;
import com.taxdividend.backend.dto.TaxCalculationResultDTO;
import com.taxdividend.backend.model.Dividend;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for calculating tax reclaim amounts based on double taxation
 * treaties.
 *
 * This service:
 * - Finds applicable tax rules for dividends
 * - Calculates reclaimable amounts
 * - Updates dividend entities with calculated values
 */
public interface TaxCalculationService {

    /**
     * Calculate tax reclaim for a single dividend.
     *
     * @param dividend         The dividend entity
     * @param residenceCountry User's country of tax residence (e.g., "CH")
     * @return Calculation result with reclaimable amount
     */
    TaxCalculationResultDTO calculateForDividend(Dividend dividend, String residenceCountry);

    /**
     * Calculate tax reclaim for a single dividend by ID.
     *
     * @param dividendId       The dividend ID
     * @param residenceCountry User's country of tax residence
     * @return Calculation result with reclaimable amount
     */
    TaxCalculationResultDTO calculateForDividend(UUID dividendId, String residenceCountry);

    /**
     * Calculate and update a single dividend in database.
     *
     * @param dividendId       The dividend ID
     * @param residenceCountry User's country of tax residence
     * @return Calculation result
     */
    TaxCalculationResultDTO calculateAndUpdate(UUID dividendId, String residenceCountry);

    /**
     * Calculate tax reclaim for multiple dividends by IDs.
     *
     * @param dividendIds List of dividend IDs
     * @param userId      The user ID (for verification)
     * @return Batch calculation result with totals
     */
    TaxCalculationBatchResultDTO calculateBatchByIds(List<UUID> dividendIds, UUID userId);

    /**
     * Calculate tax reclaim for multiple dividends (batch processing).
     *
     * @param dividends        List of dividend entities
     * @param residenceCountry User's country of tax residence
     * @return Batch calculation result with totals
     */
    TaxCalculationBatchResultDTO calculateBatch(List<Dividend> dividends, String residenceCountry);

    /**
     * Calculate tax reclaim for all dividends of a user.
     *
     * @param userId The user ID
     * @return Batch calculation result with totals
     */
    TaxCalculationBatchResultDTO calculateForUser(UUID userId);

    /**
     * Calculate and update all dividends of a user in database.
     *
     * @param userId The user ID
     * @return Batch calculation result with totals
     */
    TaxCalculationBatchResultDTO calculateAndUpdateForUser(UUID userId);

    /**
     * Find applicable tax rule for a dividend.
     *
     * @param sourceCountry    Source country (from ISIN)
     * @param residenceCountry Residence country (from user)
     * @param securityType     Type of security (EQUITY, BOND, etc.)
     * @param paymentDate      Date of dividend payment
     * @return Tax rule ID if found, null otherwise
     */
    UUID findApplicableTaxRule(String sourceCountry, String residenceCountry,
            String securityType, LocalDate paymentDate);

    /**
     * Recalculate all dividends without a form (not yet submitted).
     * Useful after tax rule updates.
     *
     * @return Batch calculation result
     */
    TaxCalculationBatchResultDTO recalculateUnsubmittedDividends();
}
