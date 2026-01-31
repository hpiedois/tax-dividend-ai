package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.TaxRule;
import com.taxdividend.backend.api.dto.TreatyRateResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxRuleService {
    List<TaxRule> getAllTaxRules();

    Optional<TaxRule> getTaxRule(UUID id);

    Optional<TaxRule> findApplicableRule(String sourceCountry, String residenceCountry, String securityType,
            LocalDate date);

    List<TaxRule> getRulesBetweenCountries(String sourceCountry, String residenceCountry);

    List<TaxRule> getActiveRules();

    List<TaxRule> getExpiredRules();

    boolean hasTaxTreaty(String sourceCountry, String residenceCountry, LocalDate date);

    List<TaxRule> getRulesBySourceCountry(String country);

    List<TaxRule> getRulesByResidenceCountry(String country);

    List<TaxRule> getRulesWithReliefAtSource();

    List<TaxRule> getRulesWithRefundProcedure();

    Optional<TreatyRateResponse> getTreatyRate(String sourceCountry, String residenceCountry, String securityType,
            LocalDate date);
}
