package com.taxdividend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing the result of a tax calculation for a single dividend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationResultDTO {

    /**
     * Dividend ID
     */
    private UUID dividendId;

    /**
     * Security name
     */
    private String securityName;

    /**
     * ISIN code
     */
    private String isin;

    /**
     * Gross dividend amount
     */
    private BigDecimal grossAmount;

    /**
     * Currency
     */
    private String currency;

    /**
     * Actual withholding tax applied
     */
    private BigDecimal withholdingTax;

    /**
     * Actual withholding rate (%)
     */
    private BigDecimal withholdingRate;

    /**
     * Tax treaty rate (%) - null if no treaty
     */
    private BigDecimal treatyRate;

    /**
     * Withholding tax according to treaty
     */
    private BigDecimal treatyWithholdingTax;

    /**
     * Amount that can be reclaimed
     * = withholdingTax - treatyWithholdingTax
     */
    private BigDecimal reclaimableAmount;

    /**
     * Source country
     */
    private String sourceCountry;

    /**
     * Residence country (from user profile)
     */
    private String residenceCountry;

    /**
     * Tax rule ID used for calculation
     */
    private UUID taxRuleId;

    /**
     * Whether a tax treaty was found and applied
     */
    @Builder.Default
    private Boolean treatyApplied = false;

    /**
     * Calculation notes or warnings
     */
    private String notes;
}
