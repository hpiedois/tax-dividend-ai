package com.taxdividend.backend.service.impl;

import com.taxdividend.backend.dto.TaxCalculationBatchResultDTO;
import com.taxdividend.backend.dto.TaxCalculationResultDTO;
import com.taxdividend.backend.exception.TaxCalculationException;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.TaxRule;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.TaxRuleRepository;
import com.taxdividend.backend.repository.UserRepository;
import com.taxdividend.backend.service.TaxCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class TaxCalculationServiceImpl implements TaxCalculationService {

    private final DividendRepository dividendRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final UserRepository userRepository;

    private static final int CALCULATION_SCALE = 2; // 2 decimal places
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public TaxCalculationResultDTO calculateForDividend(Dividend dividend, String residenceCountry) {
        log.debug("Calculating tax for dividend {} from {} to {}",
                dividend.getIsin(), dividend.getSourceCountry(), residenceCountry);

        TaxCalculationResultDTO.TaxCalculationResultDTOBuilder builder = TaxCalculationResultDTO.builder()
                .dividendId(dividend.getId())
                .securityName(dividend.getSecurityName())
                .isin(dividend.getIsin())
                .grossAmount(dividend.getGrossAmount())
                .currency(dividend.getCurrency())
                .withholdingTax(dividend.getWithholdingTax())
                .withholdingRate(dividend.getWithholdingRate())
                .sourceCountry(dividend.getSourceCountry())
                .residenceCountry(residenceCountry);

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

            builder.treatyApplied(false)
                    .treatyRate(null)
                    .treatyWithholdingTax(BigDecimal.ZERO)
                    .reclaimableAmount(BigDecimal.ZERO)
                    .notes("No tax treaty available. Cannot reclaim withholding tax.");

            return builder.build();
        }

        // Tax treaty found - calculate reclaimable amount
        TaxRule taxRule = taxRuleOpt.get();

        if (taxRule.getTreatyRate() == null) {
            // Treaty exists but no reduced rate
            log.warn("Tax treaty exists but no treaty rate defined for rule {}", taxRule.getId());

            builder.treatyApplied(false)
                    .treatyRate(null)
                    .treatyWithholdingTax(BigDecimal.ZERO)
                    .reclaimableAmount(BigDecimal.ZERO)
                    .taxRuleId(taxRule.getId())
                    .notes("Tax treaty exists but no reduced rate defined.");

            return builder.build();
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

        builder.treatyApplied(true)
                .treatyRate(taxRule.getTreatyRate())
                .treatyWithholdingTax(treatyWithholdingTax)
                .reclaimableAmount(reclaimableAmount)
                .taxRuleId(taxRule.getId());

        // Add notes based on reclaim options
        if (reclaimableAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (taxRule.getRefundProcedureAvailable()) {
                builder.notes("Refund procedure available. Can reclaim via forms 5000/5001.");
            } else if (taxRule.getReliefAtSourceAvailable()) {
                builder.notes("Only relief at source available. Refund not possible.");
            } else {
                builder.notes("No reclaim procedure available despite treaty.");
            }
        } else {
            builder.notes("Treaty rate already applied. No additional reclaim possible.");
        }

        log.debug("Calculated reclaimable amount: {} {} for dividend {}",
                reclaimableAmount, dividend.getCurrency(), dividend.getId());

        return builder.build();
    }

    @Override
    public TaxCalculationResultDTO calculateForDividend(UUID dividendId, String residenceCountry) {
        Dividend dividend = dividendRepository.findById(dividendId)
                .orElseThrow(() -> new TaxCalculationException("Dividend not found: " + dividendId));

        return calculateForDividend(dividend, residenceCountry);
    }

    @Override
    @Transactional
    public TaxCalculationResultDTO calculateAndUpdate(UUID dividendId, String residenceCountry) {
        Dividend dividend = dividendRepository.findById(dividendId)
                .orElseThrow(() -> new TaxCalculationException("Dividend not found: " + dividendId));

        TaxCalculationResultDTO result = calculateForDividend(dividend, residenceCountry);

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

    @Override
    @Transactional
    public TaxCalculationBatchResultDTO calculateBatchByIds(List<UUID> dividendIds, UUID userId) {
        List<Dividend> dividends = dividendRepository.findAllById(dividendIds);

        // Filter to ensure user owns all dividends
        dividends = dividends.stream()
                .filter(d -> d.getUser().getId().equals(userId))
                .toList();

        if (dividends.isEmpty()) {
            return TaxCalculationBatchResultDTO.builder()
                    .processedCount(0)
                    .successCount(0)
                    .failureCount(0)
                    .totalGrossAmount(BigDecimal.ZERO)
                    .totalWithholdingTax(BigDecimal.ZERO)
                    .totalReclaimableAmount(BigDecimal.ZERO)
                    .calculatedAt(LocalDateTime.now())
                    .build();
        }

        String country = dividends.get(0).getUser().getCountry();
        return calculateBatch(dividends, country);
    }

    @Override
    public TaxCalculationBatchResultDTO calculateBatch(List<Dividend> dividends, String residenceCountry) {
        long startTime = System.currentTimeMillis();

        TaxCalculationBatchResultDTO.TaxCalculationBatchResultDTOBuilder builder = TaxCalculationBatchResultDTO
                .builder()
                .processedCount(dividends.size())
                .calculatedAt(LocalDateTime.now());

        List<TaxCalculationResultDTO> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalWithholding = BigDecimal.ZERO;
        BigDecimal totalReclaimable = BigDecimal.ZERO;

        for (Dividend dividend : dividends) {
            try {
                TaxCalculationResultDTO result = calculateForDividend(dividend, residenceCountry);
                results.add(result);
                successCount++;

                // Accumulate totals
                totalGross = totalGross.add(result.getGrossAmount());
                totalWithholding = totalWithholding.add(result.getWithholdingTax());
                totalReclaimable = totalReclaimable.add(result.getReclaimableAmount());

            } catch (Exception e) {
                log.error("Failed to calculate tax for dividend {}", dividend.getId(), e);
                builder.errors(addToList(builder.build().getErrors(),
                        "Failed for dividend " + dividend.getId() + ": " + e.getMessage()));
                failureCount++;
            }
        }

        builder.results(results)
                .successCount(successCount)
                .failureCount(failureCount)
                .totalGrossAmount(totalGross.setScale(CALCULATION_SCALE, ROUNDING_MODE))
                .totalWithholdingTax(totalWithholding.setScale(CALCULATION_SCALE, ROUNDING_MODE))
                .totalReclaimableAmount(totalReclaimable.setScale(CALCULATION_SCALE, ROUNDING_MODE))
                .processingTimeMs(System.currentTimeMillis() - startTime);

        log.info("Batch calculation completed: {} success, {} failures, total reclaimable: {}",
                successCount, failureCount, totalReclaimable);

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public TaxCalculationBatchResultDTO calculateForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaxCalculationException("User not found: " + userId));

        String residenceCountry = user.getCountry();
        if (residenceCountry == null || residenceCountry.isEmpty()) {
            throw new TaxCalculationException("User has no residence country defined");
        }

        List<Dividend> dividends = dividendRepository.findByUserId(userId);

        if (dividends.isEmpty()) {
            log.info("No dividends found for user {}", userId);
            return TaxCalculationBatchResultDTO.builder()
                    .processedCount(0)
                    .successCount(0)
                    .failureCount(0)
                    .totalGrossAmount(BigDecimal.ZERO)
                    .totalWithholdingTax(BigDecimal.ZERO)
                    .totalReclaimableAmount(BigDecimal.ZERO)
                    .calculatedAt(LocalDateTime.now())
                    .build();
        }

        return calculateBatch(dividends, residenceCountry);
    }

    @Override
    @Transactional
    public TaxCalculationBatchResultDTO calculateAndUpdateForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaxCalculationException("User not found: " + userId));

        String residenceCountry = user.getCountry();
        if (residenceCountry == null || residenceCountry.isEmpty()) {
            throw new TaxCalculationException("User has no residence country defined");
        }

        List<Dividend> dividends = dividendRepository.findByUserId(userId);
        TaxCalculationBatchResultDTO result = calculateBatch(dividends, residenceCountry);

        // Update all dividends with calculated values
        for (TaxCalculationResultDTO calcResult : result.getResults()) {
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

    @Override
    public UUID findApplicableTaxRule(String sourceCountry, String residenceCountry,
            String securityType, LocalDate paymentDate) {
        Optional<TaxRule> taxRule = taxRuleRepository.findApplicableRule(
                sourceCountry, residenceCountry, securityType, paymentDate);

        return taxRule.map(TaxRule::getId).orElse(null);
    }

    @Override
    @Transactional
    public TaxCalculationBatchResultDTO recalculateUnsubmittedDividends() {
        log.info("Recalculating all unsubmitted dividends");

        List<Dividend> unsubmittedDividends = dividendRepository.findByFormIsNull();

        if (unsubmittedDividends.isEmpty()) {
            log.info("No unsubmitted dividends found");
            return TaxCalculationBatchResultDTO.builder()
                    .processedCount(0)
                    .successCount(0)
                    .failureCount(0)
                    .totalGrossAmount(BigDecimal.ZERO)
                    .totalWithholdingTax(BigDecimal.ZERO)
                    .totalReclaimableAmount(BigDecimal.ZERO)
                    .calculatedAt(LocalDateTime.now())
                    .build();
        }

        // Group by user and calculate
        List<TaxCalculationResultDTO> allResults = new ArrayList<>();
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

                TaxCalculationResultDTO result = calculateForDividend(dividend, residenceCountry);

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
        BigDecimal totalGross = allResults.stream()
                .map(TaxCalculationResultDTO::getGrossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithholding = allResults.stream()
                .map(TaxCalculationResultDTO::getWithholdingTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReclaimable = allResults.stream()
                .map(TaxCalculationResultDTO::getReclaimableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Recalculation completed: {} success, {} failures", totalSuccess, totalFailure);

        return TaxCalculationBatchResultDTO.builder()
                .results(allResults)
                .processedCount(unsubmittedDividends.size())
                .successCount(totalSuccess)
                .failureCount(totalFailure)
                .totalGrossAmount(totalGross.setScale(CALCULATION_SCALE, ROUNDING_MODE))
                .totalWithholdingTax(totalWithholding.setScale(CALCULATION_SCALE, ROUNDING_MODE))
                .totalReclaimableAmount(totalReclaimable.setScale(CALCULATION_SCALE, ROUNDING_MODE))
                .calculatedAt(LocalDateTime.now())
                .build();
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
