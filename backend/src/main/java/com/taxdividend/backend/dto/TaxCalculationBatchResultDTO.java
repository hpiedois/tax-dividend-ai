package com.taxdividend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing the result of batch tax calculations for multiple dividends.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationBatchResultDTO {

    /**
     * Individual calculation results
     */
    @Builder.Default
    private List<TaxCalculationResultDTO> results = new ArrayList<>();

    /**
     * Number of dividends processed
     */
    private Integer processedCount;

    /**
     * Number of successful calculations
     */
    private Integer successCount;

    /**
     * Number of failed calculations
     */
    private Integer failureCount;

    /**
     * Total gross amount across all dividends
     */
    private BigDecimal totalGrossAmount;

    /**
     * Total withholding tax across all dividends
     */
    private BigDecimal totalWithholdingTax;

    /**
     * Total reclaimable amount across all dividends
     */
    private BigDecimal totalReclaimableAmount;

    /**
     * Timestamp when calculation was completed
     */
    @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();

    /**
     * List of errors encountered
     */
    @Builder.Default
    private List<String> errors = new ArrayList<>();

    /**
     * List of warnings
     */
    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    /**
     * Processing time in milliseconds
     */
    private Long processingTimeMs;
}
