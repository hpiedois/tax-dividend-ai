package com.taxdividend.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Form Submission entity representing tracking of form submissions to tax authorities.
 * Corresponds to the 'form_submissions' table in the database.
 */
@Entity
@Table(name = "form_submissions", indexes = {
    @Index(name = "idx_form_submissions_form_id", columnList = "form_id"),
    @Index(name = "idx_form_submissions_status", columnList = "status"),
    @Index(name = "idx_form_submissions_submitted_at", columnList = "submittedAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private GeneratedForm form;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    /**
     * How the form was submitted: EMAIL, POSTAL, API
     */
    @Column(name = "submission_method", length = 50)
    private String submissionMethod;

    /**
     * Official tracking number from tax authority
     */
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    /**
     * Status: PENDING, RECEIVED, PROCESSING, APPROVED, REJECTED
     */
    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "status_updated_at")
    @Builder.Default
    private LocalDateTime statusUpdatedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Additional submission data (JSON): API response, email confirmation, etc.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
