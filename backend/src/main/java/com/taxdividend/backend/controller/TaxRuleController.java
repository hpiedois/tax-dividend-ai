package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.TaxRulesApi;
import com.taxdividend.backend.service.TaxRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for tax rule consultation.
 *
 * Endpoints:
 * - GET /internal/tax-rules - List all active tax rules
 * - GET /internal/tax-rules/{id} - Get tax rule by ID
 * - GET /internal/tax-rules/applicable - Find applicable rule for specific
 * criteria
 * - GET /internal/tax-rules/by-countries - Get rules between two countries
 * - GET /internal/tax-rules/active - Get all currently active rules
 * - GET /internal/tax-rules/check-treaty - Check if tax treaty exists
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Tax Rules", description = "Tax treaty rules consultation API")
public class TaxRuleController implements TaxRulesApi {

    private final TaxRuleService taxRuleService;

    /**
     * Get all tax rules with optional filters.
     */
    @Override
    public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>> getAllTaxRules(
            String sourceCountry,
            String residenceCountry,
            Boolean active,
            Boolean reliefAtSource,
            Boolean refundProcedure) {

        List<com.taxdividend.backend.api.dto.TaxRule> rules = taxRuleService.getAllTaxRules(
                sourceCountry,
                residenceCountry,
                active,
                reliefAtSource,
                refundProcedure
        );

        log.info("Retrieved {} tax rules (filters: sourceCountry={}, residenceCountry={}, active={}, reliefAtSource={}, refundProcedure={})",
                rules.size(), sourceCountry, residenceCountry, active, reliefAtSource, refundProcedure);

        return ResponseEntity.ok(rules);
    }

    /**
     * Get tax rule by ID.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.TaxRule> getTaxRule(UUID id) {

        return taxRuleService.getTaxRule(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Find applicable tax rule for specific criteria.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.TaxRule> findApplicableRule(
            String sourceCountry,
            String residenceCountry,
            String securityType,
            LocalDate date) {

        return taxRuleService.findApplicableRule(sourceCountry, residenceCountry, securityType, date)
                .map(rule -> {
                    log.info("Found applicable tax rule: {} -> {} ({})",
                            sourceCountry, residenceCountry, securityType);
                    return ResponseEntity.ok(rule);
                })
                .orElseGet(() -> {
                    log.info("No tax rule found for {} -> {} ({})",
                            sourceCountry, residenceCountry, securityType);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Get tax rules between two countries.
     */
    // @GetMapping("/by-countries")
    // @Operation(summary = "Get rules between countries", description = "Get all
    // tax rules between two specific countries")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getRulesBetweenCountries(
    // @Parameter(description = "Source country", example = "FR") @RequestParam
    // String sourceCountry,
    // @Parameter(description = "Residence country", example = "CH") @RequestParam
    // String residenceCountry) {

    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getRulesBetweenCountries(sourceCountry,
    // residenceCountry);

    // log.info("Found {} tax rules between {} and {}",
    // rules.size(), sourceCountry, residenceCountry);

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Get all currently active tax rules.
     */
    // @GetMapping("/active")
    // @Operation(summary = "Get active tax rules", description = "Get all tax rules
    // that are currently active (not expired)")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getActiveRules() {
    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getActiveRules();

    // log.info("Retrieved {} active tax rules", rules.size());

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Get expired tax rules.
     */
    // @GetMapping("/expired")
    // @Operation(summary = "Get expired tax rules", description = "Get all tax
    // rules that have expired")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getExpiredRules() {
    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getExpiredRules();

    // log.info("Retrieved {} expired tax rules", rules.size());

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Check if a tax treaty exists between two countries.
     */
    // @GetMapping("/check-treaty")
    // @Operation(summary = "Check if treaty exists", description = "Check if a tax
    // treaty exists between two countries")
    // public ResponseEntity<Boolean> hasTaxTreaty(
    // @Parameter(description = "Source country", example = "FR") @RequestParam
    // String sourceCountry,
    // @Parameter(description = "Residence country", example = "CH") @RequestParam
    // String residenceCountry,
    // @Parameter(description = "Reference date", example = "2024-12-15")
    // @RequestParam(required = false) LocalDate date) {

    // boolean hasTreaty = taxRuleService.hasTaxTreaty(sourceCountry,
    // residenceCountry, date);

    // log.info("Tax treaty check: {} -> {} = {}",
    // sourceCountry, residenceCountry, hasTreaty);

    // return ResponseEntity.ok(hasTreaty);
    // }

    /**
     * Get tax rules by source country.
     */
    // @GetMapping("/by-source-country/{country}")
    // @Operation(summary = "Get rules by source country", description = "Get all
    // tax rules for a specific source country")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getRulesBySourceCountry(
    // @Parameter(description = "Source country code", example = "FR") @PathVariable
    // String country) {

    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getRulesBySourceCountry(country);

    // log.info("Found {} tax rules for source country {}", rules.size(), country);

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Get tax rules by residence country.
     */
    // @GetMapping("/by-residence-country/{country}")
    // @Operation(summary = "Get rules by residence country", description = "Get all
    // tax rules for a specific residence country")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getRulesByResidenceCountry(
    // @Parameter(description = "Residence country code", example = "CH")
    // @PathVariable String country) {

    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getRulesByResidenceCountry(country);

    // log.info("Found {} tax rules for residence country {}", rules.size(),
    // country);

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Get rules with relief at source available.
     */
    // @GetMapping("/with-relief-at-source")
    // @Operation(summary = "Get rules with relief at source", description = "Get
    // all tax rules where relief at source is available")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getRulesWithReliefAtSource() {
    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getRulesWithReliefAtSource();

    // log.info("Found {} tax rules with relief at source", rules.size());

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Get rules with refund procedure available.
     */
    // @GetMapping("/with-refund-procedure")
    // @Operation(summary = "Get rules with refund procedure", description = "Get
    // all tax rules where refund procedure is available")
    // public ResponseEntity<List<com.taxdividend.backend.api.dto.TaxRule>>
    // getRulesWithRefundProcedure() {
    // List<com.taxdividend.backend.api.dto.TaxRule> rules =
    // taxRuleService.getRulesWithRefundProcedure();

    // log.info("Found {} tax rules with refund procedure", rules.size());

    // return ResponseEntity.ok(rules);
    // }

    /**
     * Get treaty rate for specific criteria.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.TreatyRateResponse> getTreatyRate(
            String sourceCountry,
            String residenceCountry,
            String securityType,
            LocalDate date) {

        return taxRuleService.getTreatyRate(sourceCountry, residenceCountry, securityType, date)
                .map(response -> {
                    log.info("Treaty rate for {} -> {}: standard={}%, treaty={}%",
                            sourceCountry, residenceCountry,
                            response.getStandardRate(), response.getTreatyRate());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
