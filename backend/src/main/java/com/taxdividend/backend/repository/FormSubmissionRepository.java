package com.taxdividend.backend.repository;

import com.taxdividend.backend.model.FormSubmission;
import com.taxdividend.backend.model.GeneratedForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmission, UUID> {

    /**
     * Find submissions by form
     */
    List<FormSubmission> findByForm(GeneratedForm form);

    /**
     * Find submissions by form ID
     */
    List<FormSubmission> findByFormId(UUID formId);

    /**
     * Find submission by tracking number
     */
    Optional<FormSubmission> findByTrackingNumber(String trackingNumber);

    /**
     * Find submissions by status
     */
    List<FormSubmission> findByStatus(String status);

    /**
     * Find submissions by submission method
     */
    List<FormSubmission> findBySubmissionMethod(String submissionMethod);

    /**
     * Find submissions submitted between dates
     */
    List<FormSubmission> findBySubmittedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find pending submissions older than a certain date
     */
    @Query("SELECT fs FROM FormSubmission fs WHERE fs.status = 'PENDING' AND fs.submittedAt < :date")
    List<FormSubmission> findPendingOlderThan(@Param("date") LocalDateTime date);

    /**
     * Find all submissions for a specific user (through form relationship)
     */
    @Query("SELECT fs FROM FormSubmission fs WHERE fs.form.user.id = :userId")
    List<FormSubmission> findByUserId(@Param("userId") UUID userId);

    /**
     * Count submissions by status
     */
    Long countByStatus(String status);
}
