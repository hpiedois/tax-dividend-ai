package com.taxdividend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing the result of form generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateFormResultDTO {

    /**
     * Generated form ID (database record)
     */
    private UUID formId;

    /**
     * S3 key where the form is stored
     */
    private String s3Key;

    /**
     * Generated filename
     */
    private String fileName;

    /**
     * Form type: "5000", "5001", or "BUNDLE"
     */
    private String formType;

    /**
     * Tax year
     */
    private Integer taxYear;

    /**
     * File size in bytes
     */
    private Long fileSize;

    /**
     * Download URL (pre-signed)
     */
    private String downloadUrl;

    /**
     * Download URL expiration
     */
    private LocalDateTime downloadUrlExpiresAt;

    /**
     * Form expiration date
     */
    private LocalDateTime expiresAt;

    /**
     * Generation timestamp
     */
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    /**
     * Whether generation was successful
     */
    @Builder.Default
    private Boolean success = true;

    /**
     * List of errors if generation failed
     */
    @Builder.Default
    private List<String> errors = new ArrayList<>();

    /**
     * Processing time in milliseconds
     */
    private Long processingTimeMs;

    /**
     * Number of dividends included (for Form 5001)
     */
    private Integer dividendCount;
}
