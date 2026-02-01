package com.taxdividend.backend.repository;

import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.DividendStatus;
import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, UUID> {

    /**
     * Find all dividends for a specific user
     */
    List<Dividend> findByUser(User user);

    /**
     * Find all dividends for a specific user ID
     */
    List<Dividend> findByUserId(UUID userId);

    /**
     * Find dividends by form
     */
    List<Dividend> findByForm(GeneratedForm form);

    /**
     * Find dividends by form ID
     */
    List<Dividend> findByFormId(UUID formId);

    /**
     * Find dividends by ISIN
     */
    List<Dividend> findByIsin(String isin);

    /**
     * Find dividends by user and ISIN
     */
    List<Dividend> findByUserIdAndIsin(UUID userId, String isin);

    /**
     * Find dividends by source country
     */
    List<Dividend> findBySourceCountry(String sourceCountry);

    /**
     * Find dividends by user and source country
     */
    List<Dividend> findByUserIdAndSourceCountry(UUID userId, String sourceCountry);

    /**
     * Find dividends by payment date range
     */
    List<Dividend> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find dividends by user and payment date range
     */
    List<Dividend> findByUserIdAndPaymentDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find dividends not yet associated with a form
     */
    List<Dividend> findByFormIsNull();

    /**
     * Find dividends not yet associated with a form for a specific user
     */
    List<Dividend> findByUserIdAndFormIsNull(UUID userId);

    /**
     * Calculate total reclaimable amount for a user
     */
    @Query("SELECT SUM(d.reclaimableAmount) FROM Dividend d WHERE d.user.id = :userId")
    BigDecimal calculateTotalReclaimableByUser(@Param("userId") UUID userId);

    /**
     * Calculate total reclaimable amount for a form
     */
    @Query("SELECT SUM(d.reclaimableAmount) FROM Dividend d WHERE d.form.id = :formId")
    BigDecimal calculateTotalReclaimableByForm(@Param("formId") UUID formId);

    /**
     * Count total dividends for a user
     */
    long countByUserId(UUID userId);

    /**
     * Count total dividends for a user in a specific tax year
     */
    @Query("SELECT COUNT(d) FROM Dividend d WHERE d.user.id = :userId AND YEAR(d.paymentDate) = :year")
    long countByUserIdAndYear(@Param("userId") UUID userId, @Param("year") int year);

    /**
     * Calculate sum of reclaimable amount by user and status
     */
    @Query("SELECT SUM(d.reclaimableAmount) FROM Dividend d WHERE d.user.id = :userId AND d.status = :status")
    BigDecimal sumReclaimableByStatus(@Param("userId") UUID userId, @Param("status") DividendStatus status);

    /**
     * Calculate sum of reclaimable amount by user, list of statuses and year
     */
    @Query("SELECT SUM(d.reclaimableAmount) FROM Dividend d WHERE d.user.id = :userId AND d.status IN :statuses AND YEAR(d.paymentDate) = :year")
    BigDecimal sumReclaimableByStatusAndYear(@Param("userId") UUID userId,
            @Param("statuses") List<DividendStatus> statuses, @Param("year") int year);

    /**
     * Calculate sum of reclaimable amount by user and list of statuses (All time)
     */
    @Query("SELECT SUM(d.reclaimableAmount) FROM Dividend d WHERE d.user.id = :userId AND d.status IN :statuses")
    BigDecimal sumReclaimableByStatuses(@Param("userId") UUID userId, @Param("statuses") List<DividendStatus> statuses);
}
