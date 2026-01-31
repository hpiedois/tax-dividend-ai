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
 * Generated Form entity representing tax forms (5000, 5001, bundles).
 * Corresponds to the 'generated_forms' table in the database.
 */
@Entity
@Table(name = "generated_forms", indexes = {
    @Index(name = "idx_generated_forms_user_id", columnList = "user_id"),
    @Index(name = "idx_generated_forms_tax_year", columnList = "taxYear"),
    @Index(name = "idx_generated_forms_status", columnList = "status"),
    @Index(name = "idx_generated_forms_created_at", columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedForm {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * S3/MinIO object key for the generated PDF/ZIP
     */
    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    /**
     * Form type: 5000 (residence), 5001 (dividends), BUNDLE (both)
     */
    @Column(name = "form_type", nullable = false, length = 50)
    private String formType;

    /**
     * Status: GENERATED, SIGNED, SUBMITTED, APPROVED, REJECTED
     */
    @Column(length = 50)
    @Builder.Default
    private String status = "GENERATED";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Additional metadata (JSON): generation params, validation results, etc.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
