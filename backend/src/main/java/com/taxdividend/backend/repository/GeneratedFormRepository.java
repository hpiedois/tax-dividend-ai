package com.taxdividend.backend.repository;

import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedFormRepository extends JpaRepository<GeneratedForm, UUID> {

    /**
     * Find all forms for a specific user
     */
    List<GeneratedForm> findByUser(User user);

    /**
     * Find all forms for a specific user ID
     */
    List<GeneratedForm> findByUserId(UUID userId);

    /**
     * Find forms by user and tax year
     */
    List<GeneratedForm> findByUserAndTaxYear(User user, Integer taxYear);

    /**
     * Find forms by user ID and tax year
     */
    List<GeneratedForm> findByUserIdAndTaxYear(UUID userId, Integer taxYear);

    /**
     * Find forms by user and status
     */
    List<GeneratedForm> findByUserAndStatus(User user, String status);

    /**
     * Find forms by form type
     */
    List<GeneratedForm> findByFormType(String formType);

    /**
     * Find form by S3 key
     */
    Optional<GeneratedForm> findByS3Key(String s3Key);

    /**
     * Find expired forms
     */
    @Query("SELECT f FROM GeneratedForm f WHERE f.expiresAt < :now")
    List<GeneratedForm> findExpiredForms(@Param("now") LocalDateTime now);

    /**
     * Find forms created between dates
     */
    List<GeneratedForm> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Count forms by user
     */
    Long countByUserId(UUID userId);
}
