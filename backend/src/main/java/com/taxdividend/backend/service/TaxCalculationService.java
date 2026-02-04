package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto;
import com.taxdividend.backend.api.dto.TaxCalculationResultDto;
import com.taxdividend.backend.exception.TaxCalculationException;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.TaxRule;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.TaxRuleRepository;
import com.taxdividend.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of tax calculation service.
 *
 * Calculates reclaimable amounts based on double taxation treaties.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaxCalculationService {

    private final DividendRepository dividendRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final UserRepository userRepository;

    private static final int CALCULATION_SCALE = 2; // 2 decimal places
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public TaxCalculationResultDto calculateForDividend(Dividend dividend, String residenceCountry) {
        log.debug("Calculating tax for dividend {} from {} to {}",
                dividend.getIsin(), dividend.getSourceCountry(), residenceCountry);

        TaxCalculationResultDto result = new TaxCalculationResultDto();

        result.setDividendId(dividend.getId());
        result.setWithheldAmount(dividend.getWithholdingTax().setScale(CALCULATION_SCALE, ROUNDING_MODE));
        result.setStandardRate(dividend.getWithholdingRate());

        // Determine security type (default to EQUITY)
        String securityType = determineSecurityType(dividend);

        // Find applicable tax rule
        Optional<TaxRule> taxRuleOpt = taxRuleRepository.findApplicableRule(
                dividend.getSourceCountry(),
                residenceCountry,
                securityType,
                dividend.getPaymentDate());

        if (taxRuleOpt.isEmpty()) {
            // No tax treaty found
            log.warn("No tax treaty found for {} -> {} on {}",
                    dividend.getSourceCountry(), residenceCountry, dividend.getPaymentDate());

            result.setSuccess(false);
            result.setTreatyRate(null);
            result.setReclaimableAmount(BigDecimal.ZERO);
            result.addErrorsItem("No tax treaty available. Cannot reclaim withholding tax.");

            return result;
        }

        // Tax treaty found - calculate reclaimable amount
        TaxRule taxRule = taxRuleOpt.get();

        if (taxRule.getTreatyRate() == null) {
            // Treaty exists but no reduced rate
            log.warn("Tax treaty exists but no treaty defined for rule {}", taxRule.getId());

            result.setSuccess(false);
            result.setTreatyRate(null);
            result.setReclaimableAmount(BigDecimal.ZERO);
            result.addErrorsItem("Tax treaty exists but no reduced rate defined.");

            return result;
        }

        // Calculate treaty withholding tax (what should have been withheld)
        BigDecimal treatyWithholdingTax = dividend.getGrossAmount()
                .multiply(taxRule.getTreatyRate())
                .divide(BigDecimal.valueOf(100), CALCULATION_SCALE, ROUNDING_MODE);

        // Calculate reclaimable amount (actual withholding - treaty withholding)
        BigDecimal reclaimableAmount = dividend.getWithholdingTax()
                .subtract(treatyWithholdingTax)
                .setScale(CALCULATION_SCALE, ROUNDING_MODE);

        // Ensure non-negative
        if (reclaimableAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Calculated negative reclaimable amount for dividend {}. Setting to zero.", dividend.getId());
            reclaimableAmount = BigDecimal.ZERO;
        }

        result.setSuccess(true);
        result.setTreatyRate(taxRule.getTreatyRate());
        result.setReclaimableAmount(reclaimableAmount);

        // Add notes based on reclaim options
        if (reclaimableAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (taxRule.getRefundProcedureAvailable()) {
                result.addErrorsItem("Refund procedure available. Can reclaim via forms 5000/5001.");
            } else if (taxRule.getReliefAtSourceAvailable()) {
                result.addErrorsItem("Only relief at source available. Refund not possible.");
            } else {
                result.addErrorsItem("No reclaim procedure available despite treaty.");
            }
        } else {
            result.addErrorsItem("Treaty rate already applied. No additional reclaim possible.");
        }

        log.debug("Calculated reclaimable amount: {} {} for dividend {}",
                reclaimableAmount, dividend.getCurrency(), dividend.getId());

        return result;
    }

    public TaxCalculationResultDto calculateForDividend(UUID dividendId, String residenceCountry) {
        Dividend dividend = dividendRepository.findById(dividendId)
                .orElseThrow(() -> new TaxCalculationException("Dividend not found: " + dividendId));

        return calculateForDividend(dividend, residenceCountry);
    }

    @Transactional
    public TaxCalculationResultDto calculateAndUpdate(UUID dividendId, String residenceCountry) {
        Dividend dividend = dividendRepository.findById(dividendId)
                .orElseThrow(() -> new TaxCalculationException("Dividend not found: " + dividendId));

        TaxCalculationResultDto result = calculateForDividend(dividend, residenceCountry);

        // Update dividend entity
        dividend.setReclaimableAmount(result.getReclaimableAmount());
        if (result.getTreatyRate() != null) {
            dividend.setTreatyRate(result.getTreatyRate());
        }
        dividendRepository.save(dividend);

        log.info("Updated dividend {} with reclaimable amount: {}",
                dividendId, result.getReclaimableAmount());

        return result;
    }

    @Transactional
    public TaxCalculationBatchResultDto calculateBatchByIds(List<UUID> dividendIds, UUID userId) {
        List<Dividend> dividends = dividendRepository.findAllById(dividendIds);

        // Filter to ensure user owns all dividends
        dividends = dividends.stream()
                .filter(d -> d.getUser().getId().equals(userId))
                .toList();

        if (dividends.isEmpty()) {
            TaxCalculationBatchResultDto emptyResult = new TaxCalculationBatchResultDto();
            emptyResult.setSuccessCount(0);
            emptyResult.setFailureCount(0);
            emptyResult.setTotalReclaimableAmount(BigDecimal.ZERO);
            emptyResult.setResults(new ArrayList<>());
            return emptyResult;
        }

        String country = dividends.get(0).getUser().getCountry();
        return calculateBatch(dividends, country);
    }

    public TaxCalculationBatchResultDto calculateBatch(List<Dividend> dividends, String residenceCountry) {
        TaxCalculationBatchResultDto batchResult = new TaxCalculationBatchResultDto();
        batchResult.setSuccessCount(0);
        batchResult.setFailureCount(0);
        batchResult.setTotalReclaimableAmount(BigDecimal.ZERO);
        batchResult.setResults(new ArrayList<>());

        List<TaxCalculationResultDto> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalWithholding = BigDecimal.ZERO;
        BigDecimal totalReclaimable = BigDecimal.ZERO;

        for (Dividend dividend : dividends) {
            try {
                TaxCalculationResultDto result = calculateForDividend(dividend, residenceCountry);
                results.add(result);
                successCount++;

                // Accumulate totals
                totalGross = totalGross.add(dividend.getGrossAmount());
                totalWithholding = totalWithholding.add(dividend.getWithholdingTax());
                if (result.getReclaimableAmount() != null) {
                    totalReclaimable = totalReclaimable.add(result.getReclaimableAmount());
                }

            } catch (Exception e) {
                log.error("Failed to calculate tax for dividend {}", dividend.getId(), e);

                // Add failure result to maintain result list size
                TaxCalculationResultDto failureResult = new TaxCalculationResultDto();
                failureResult.setDividendId(dividend.getId());
                failureResult
                        .setWithheldAmount(dividend.getWithholdingTax().setScale(CALCULATION_SCALE, ROUNDING_MODE));
                failureResult.setStandardRate(dividend.getWithholdingRate());
                failureResult.setReclaimableAmount(BigDecimal.ZERO);
                failureResult.setSuccess(false);
                failureResult.addErrorsItem("CALCULATION FAILED: " + e.getMessage());

                results.add(failureResult);
                failureCount++;
            }
        }

        batchResult.setResults(results);
        batchResult.setSuccessCount(successCount);
        batchResult.setFailureCount(failureCount);
        batchResult.setTotalReclaimableAmount(totalReclaimable.setScale(CALCULATION_SCALE, ROUNDING_MODE));

        log.info("Batch calculation completed: {} success, {} failures, total reclaimable: {}",
                successCount, failureCount, totalReclaimable);

        return batchResult;
    }

    @Transactional(readOnly = true)
    public TaxCalculationBatchResultDto calculateForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaxCalculationException("User not found: " + userId));

        String residenceCountry = user.getCountry();
        if (residenceCountry == null || residenceCountry.isEmpty()) {
            throw new TaxCalculationException("User has no residence country defined");
        }

        List<Dividend> dividends = dividendRepository.findByUserId(userId);

        if (dividends.isEmpty()) {
            log.info("No dividends found for user {}", userId);
            TaxCalculationBatchResultDto emptyResult = new TaxCalculationBatchResultDto();
            emptyResult.setSuccessCount(0);
            emptyResult.setFailureCount(0);
            emptyResult.setTotalReclaimableAmount(BigDecimal.ZERO);
            emptyResult.setResults(new ArrayList<>());
            return emptyResult;
        }

        return calculateBatch(dividends, residenceCountry);
    }

    @Transactional
    public TaxCalculationBatchResultDto calculateAndUpdateForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaxCalculationException("User not found: " + userId));

        String residenceCountry = user.getCountry();
        if (residenceCountry == null || residenceCountry.isEmpty()) {
            throw new TaxCalculationException("User has no residence country defined");
        }

        List<Dividend> dividends = dividendRepository.findByUserId(userId);
        TaxCalculationBatchResultDto result = calculateBatch(dividends, residenceCountry);

        // Update all dividends with calculated values
        for (TaxCalculationResultDto calcResult : result.getResults()) {
            Dividend dividend = dividendRepository.findById(calcResult.getDividendId())
                    .orElseThrow(
                            () -> new TaxCalculationException("Dividend not found: " + calcResult.getDividendId()));

            dividend.setReclaimableAmount(calcResult.getReclaimableAmount());
            if (calcResult.getTreatyRate() != null) {
                dividend.setTreatyRate(calcResult.getTreatyRate());
            }
            dividendRepository.save(dividend);
        }

        log.info("Updated {} dividends for user {} with calculated values",
                result.getSuccessCount(), userId);

        return result;
    }

    public UUID findApplicableTaxRule(String sourceCountry, String residenceCountry,
            String securityType, LocalDate paymentDate) {
        Optional<TaxRule> taxRule = taxRuleRepository.findApplicableRule(
                sourceCountry, residenceCountry, securityType, paymentDate);

        return taxRule.map(TaxRule::getId).orElse(null);
    }

    @Transactional
    public TaxCalculationBatchResultDto recalculateUnsubmittedDividends() {
        log.info("Recalculating all unsubmitted dividends");

        List<Dividend> unsubmittedDividends = dividendRepository.findByFormIsNull();

        if (unsubmittedDividends.isEmpty()) {
            log.info("No unsubmitted dividends found");
            TaxCalculationBatchResultDto emptyResult = new TaxCalculationBatchResultDto();
            emptyResult.setSuccessCount(0);
            emptyResult.setFailureCount(0);
            emptyResult.setTotalReclaimableAmount(BigDecimal.ZERO);
            emptyResult.setResults(new ArrayList<>());
            return emptyResult;
        }

        // Group by user and calculate
        List<TaxCalculationResultDto> allResults = new ArrayList<>();
        int totalSuccess = 0;
        int totalFailure = 0;

        for (Dividend dividend : unsubmittedDividends) {
            try {
                User user = dividend.getUser();
                String residenceCountry = user.getCountry();

                if (residenceCountry == null || residenceCountry.isEmpty()) {
                    log.warn("Skipping dividend {} - user has no residence country", dividend.getId());
                    totalFailure++;
                    continue;
                }

                TaxCalculationResultDto result = calculateForDividend(dividend, residenceCountry);

                // Update dividend
                dividend.setReclaimableAmount(result.getReclaimableAmount());
                if (result.getTreatyRate() != null) {
                    dividend.setTreatyRate(result.getTreatyRate());
                }
                dividendRepository.save(dividend);

                allResults.add(result);
                totalSuccess++;

            } catch (Exception e) {
                log.error("Failed to recalculate dividend {}", dividend.getId(), e);
                totalFailure++;
            }
        }

        // Calculate totals
        // totalGross, totalWithholding not in API DTO?
        // Only totalReclaimable
        BigDecimal totalReclaimable = allResults.stream()
                .map(TaxCalculationResultDto::getReclaimableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Recalculation completed: {} success, {} failures", totalSuccess, totalFailure);

        TaxCalculationBatchResultDto batchResult = new TaxCalculationBatchResultDto();
        batchResult.setResults(allResults);
        batchResult.setSuccessCount(totalSuccess);
        batchResult.setFailureCount(totalFailure);
        batchResult.setTotalReclaimableAmount(totalReclaimable.setScale(CALCULATION_SCALE, ROUNDING_MODE));

        return batchResult;
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    /**
     * Determine security type from ISIN or other heuristics.
     * Default to EQUITY if cannot be determined.
     */
    private String determineSecurityType(Dividend dividend) {
        // Future enhancement: Parse ISIN to determine type
        // For now, default to EQUITY
        return "EQUITY";
    }

    /**
     * Helper to add item to list (handles null lists).
     */
    private <T> List<T> addToList(List<T> list, T item) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(item);
        return list;
    }
}
