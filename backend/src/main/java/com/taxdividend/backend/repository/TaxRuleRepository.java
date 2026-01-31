package com.taxdividend.backend.repository;

import com.taxdividend.backend.model.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaxRuleRepository extends JpaRepository<TaxRule, UUID> {

    /**
     * Find tax rules by source country
     */
    List<TaxRule> findBySourceCountry(String sourceCountry);

    /**
     * Find tax rules by residence country
     */
    List<TaxRule> findByResidenceCountry(String residenceCountry);

    /**
     * Find tax rules by source and residence countries
     */
    List<TaxRule> findBySourceCountryAndResidenceCountry(String sourceCountry, String residenceCountry);

    /**
     * Find tax rules by source country, residence country, and security type
     */
    List<TaxRule> findBySourceCountryAndResidenceCountryAndSecurityType(
        String sourceCountry,
        String residenceCountry,
        String securityType
    );

    /**
     * Find the applicable tax rule for a specific date
     */
    @Query("SELECT tr FROM TaxRule tr WHERE " +
           "tr.sourceCountry = :sourceCountry AND " +
           "tr.residenceCountry = :residenceCountry AND " +
           "tr.securityType = :securityType AND " +
           "tr.effectiveFrom <= :date AND " +
           "(tr.effectiveTo IS NULL OR tr.effectiveTo >= :date)")
    Optional<TaxRule> findApplicableRule(
        @Param("sourceCountry") String sourceCountry,
        @Param("residenceCountry") String residenceCountry,
        @Param("securityType") String securityType,
        @Param("date") LocalDate date
    );

    /**
     * Find currently active tax rules (no effective_to or effective_to in future)
     */
    @Query("SELECT tr FROM TaxRule tr WHERE tr.effectiveTo IS NULL OR tr.effectiveTo >= :now")
    List<TaxRule> findActiveRules(@Param("now") LocalDate now);

    /**
     * Find expired tax rules
     */
    @Query("SELECT tr FROM TaxRule tr WHERE tr.effectiveTo IS NOT NULL AND tr.effectiveTo < :now")
    List<TaxRule> findExpiredRules(@Param("now") LocalDate now);

    /**
     * Find tax rules with relief at source available
     */
    List<TaxRule> findByReliefAtSourceAvailable(Boolean reliefAtSourceAvailable);

    /**
     * Find tax rules with refund procedure available
     */
    List<TaxRule> findByRefundProcedureAvailable(Boolean refundProcedureAvailable);

    /**
     * Check if a tax treaty exists between two countries
     */
    @Query("SELECT CASE WHEN COUNT(tr) > 0 THEN true ELSE false END FROM TaxRule tr WHERE " +
           "tr.sourceCountry = :sourceCountry AND " +
           "tr.residenceCountry = :residenceCountry AND " +
           "tr.effectiveFrom <= :date AND " +
           "(tr.effectiveTo IS NULL OR tr.effectiveTo >= :date)")
    boolean hasTaxTreaty(
        @Param("sourceCountry") String sourceCountry,
        @Param("residenceCountry") String residenceCountry,
        @Param("date") LocalDate date
    );
}
