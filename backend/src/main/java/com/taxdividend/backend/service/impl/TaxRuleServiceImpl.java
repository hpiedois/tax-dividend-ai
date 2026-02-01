package com.taxdividend.backend.service.impl;

import com.taxdividend.backend.api.dto.TaxRule;
import com.taxdividend.backend.api.dto.TreatyRateResponse;
import com.taxdividend.backend.mapper.TaxRuleMapper;
import com.taxdividend.backend.repository.TaxRuleRepository;
import com.taxdividend.backend.service.TaxRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxRuleServiceImpl implements TaxRuleService {

    private final TaxRuleRepository taxRuleRepository;
    private final TaxRuleMapper taxRuleMapper;

    @Override
    public List<TaxRule> getAllTaxRules(String sourceCountry, String residenceCountry, Boolean active, Boolean reliefAtSource, Boolean refundProcedure) {
        // Start with all rules
        List<com.taxdividend.backend.model.TaxRule> rules;

        // Apply filters in order of specificity
        if (sourceCountry != null && residenceCountry != null) {
            // Most specific: both countries
            rules = taxRuleRepository.findBySourceCountryAndResidenceCountry(
                    sourceCountry.toUpperCase(),
                    residenceCountry.toUpperCase());
        } else if (sourceCountry != null) {
            // Filter by source country only
            rules = taxRuleRepository.findBySourceCountry(sourceCountry.toUpperCase());
        } else if (residenceCountry != null) {
            // Filter by residence country only
            rules = taxRuleRepository.findByResidenceCountry(residenceCountry.toUpperCase());
        } else if (Boolean.TRUE.equals(active)) {
            // Active rules filter
            rules = taxRuleRepository.findActiveRules(LocalDate.now());
        } else if (Boolean.FALSE.equals(active)) {
            // Expired rules filter
            rules = taxRuleRepository.findExpiredRules(LocalDate.now());
        } else {
            // No country or active filter, get all
            rules = taxRuleRepository.findAll();
        }

        // Apply additional boolean filters
        if (Boolean.TRUE.equals(reliefAtSource)) {
            rules = rules.stream()
                    .filter(rule -> Boolean.TRUE.equals(rule.getReliefAtSourceAvailable()))
                    .toList();
        }

        if (Boolean.TRUE.equals(refundProcedure)) {
            rules = rules.stream()
                    .filter(rule -> Boolean.TRUE.equals(rule.getRefundProcedureAvailable()))
                    .toList();
        }

        return taxRuleMapper.toApiDtoList(rules);
    }

    @Override
    public Optional<TaxRule> getTaxRule(UUID id) {
        return taxRuleRepository.findById(id).map(taxRuleMapper::toApiDto);
    }

    @Override
    public Optional<TaxRule> findApplicableRule(String sourceCountry, String residenceCountry, String securityType,
            LocalDate date) {
        LocalDate referenceDate = date != null ? date : LocalDate.now();
        return taxRuleRepository.findApplicableRule(
                sourceCountry.toUpperCase(),
                residenceCountry.toUpperCase(),
                securityType.toUpperCase(),
                referenceDate).map(taxRuleMapper::toApiDto);
    }

    @Override
    public List<TaxRule> getRulesBetweenCountries(String sourceCountry, String residenceCountry) {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findBySourceCountryAndResidenceCountry(
                sourceCountry.toUpperCase(),
                residenceCountry.toUpperCase()));
    }

    @Override
    public List<TaxRule> getActiveRules() {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findActiveRules(LocalDate.now()));
    }

    @Override
    public List<TaxRule> getExpiredRules() {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findExpiredRules(LocalDate.now()));
    }

    @Override
    public boolean hasTaxTreaty(String sourceCountry, String residenceCountry, LocalDate date) {
        LocalDate referenceDate = date != null ? date : LocalDate.now();
        return taxRuleRepository.hasTaxTreaty(
                sourceCountry.toUpperCase(),
                residenceCountry.toUpperCase(),
                referenceDate);
    }

    @Override
    public List<TaxRule> getRulesBySourceCountry(String country) {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findBySourceCountry(country.toUpperCase()));
    }

    @Override
    public List<TaxRule> getRulesByResidenceCountry(String country) {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findByResidenceCountry(country.toUpperCase()));
    }

    @Override
    public List<TaxRule> getRulesWithReliefAtSource() {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findByReliefAtSourceAvailable(true));
    }

    @Override
    public List<TaxRule> getRulesWithRefundProcedure() {
        return taxRuleMapper.toApiDtoList(taxRuleRepository.findByRefundProcedureAvailable(true));
    }

    @Override
    public Optional<TreatyRateResponse> getTreatyRate(String sourceCountry, String residenceCountry,
            String securityType, LocalDate date) {
        LocalDate referenceDate = date != null ? date : LocalDate.now();
        return taxRuleRepository.findApplicableRule(
                sourceCountry.toUpperCase(),
                residenceCountry.toUpperCase(),
                securityType.toUpperCase(),
                referenceDate).map(taxRuleMapper::toTreatyRateResponse);
    }
}
