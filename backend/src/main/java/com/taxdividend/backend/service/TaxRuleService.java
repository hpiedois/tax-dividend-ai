package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.TaxRuleDto;
import com.taxdividend.backend.api.dto.TreatyRateResponseDto;
import com.taxdividend.backend.mapper.TaxRuleMapper;
import com.taxdividend.backend.repository.TaxRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.taxdividend.backend.config.CacheConfig.TAX_RULES_CACHE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxRuleService {

        private final TaxRuleRepository taxRuleRepository;
        private final TaxRuleMapper taxRuleMapper;

        public List<TaxRuleDto> getAllTaxRules(String sourceCountry, String residenceCountry, Boolean active,
                        Boolean reliefAtSource, Boolean refundProcedure) {
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

        public Optional<TaxRuleDto> getTaxRule(UUID id) {
                return taxRuleRepository.findById(id).map(taxRuleMapper::toApiDto);
        }

        @Cacheable(value = TAX_RULES_CACHE,
                   key = "#sourceCountry + '_' + #residenceCountry + '_' + #securityType + '_' + #date",
                   unless = "#result == null || #result.isEmpty()")
        public Optional<TaxRuleDto> findApplicableRule(String sourceCountry, String residenceCountry,
                        String securityType,
                        LocalDate date) {
                log.debug("Cache miss - fetching tax rule: {} -> {}, {}, {}",
                         sourceCountry, residenceCountry, securityType, date);
                LocalDate referenceDate = date != null ? date : LocalDate.now();
                return taxRuleRepository.findApplicableRule(
                                sourceCountry.toUpperCase(),
                                residenceCountry.toUpperCase(),
                                securityType.toUpperCase(),
                                referenceDate).map(taxRuleMapper::toApiDto);
        }

        @Deprecated
        public List<TaxRuleDto> getRulesBetweenCountries(String sourceCountry, String residenceCountry) {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findBySourceCountryAndResidenceCountry(
                                sourceCountry.toUpperCase(),
                                residenceCountry.toUpperCase()));
        }

        @Deprecated
        public List<TaxRuleDto> getActiveRules() {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findActiveRules(LocalDate.now()));
        }

        @Deprecated
        public List<TaxRuleDto> getExpiredRules() {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findExpiredRules(LocalDate.now()));
        }

        @Deprecated
        public boolean hasTaxTreaty(String sourceCountry, String residenceCountry, LocalDate date) {
                LocalDate referenceDate = date != null ? date : LocalDate.now();
                return taxRuleRepository.hasTaxTreaty(
                                sourceCountry.toUpperCase(),
                                residenceCountry.toUpperCase(),
                                referenceDate);
        }

        @Deprecated
        public List<TaxRuleDto> getRulesBySourceCountry(String country) {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findBySourceCountry(country.toUpperCase()));
        }

        @Deprecated
        public List<TaxRuleDto> getRulesByResidenceCountry(String country) {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findByResidenceCountry(country.toUpperCase()));
        }

        @Deprecated
        public List<TaxRuleDto> getRulesWithReliefAtSource() {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findByReliefAtSourceAvailable(true));
        }

        @Deprecated
        public List<TaxRuleDto> getRulesWithRefundProcedure() {
                return taxRuleMapper.toApiDtoList(taxRuleRepository.findByRefundProcedureAvailable(true));
        }

        @Cacheable(value = TAX_RULES_CACHE,
                   key = "'treaty_' + #sourceCountry + '_' + #residenceCountry + '_' + #securityType + '_' + #date",
                   unless = "#result == null || #result.isEmpty()")
        public Optional<TreatyRateResponseDto> getTreatyRate(String sourceCountry, String residenceCountry,
                        String securityType, LocalDate date) {
                log.debug("Cache miss - fetching treaty rate: {} -> {}, {}, {}",
                         sourceCountry, residenceCountry, securityType, date);
                LocalDate referenceDate = date != null ? date : LocalDate.now();
                return taxRuleRepository.findApplicableRule(
                                sourceCountry.toUpperCase(),
                                residenceCountry.toUpperCase(),
                                securityType.toUpperCase(),
                                referenceDate).map(taxRuleMapper::toTreatyRateResponse);
        }
}
