package com.taxdividend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing the result of a file upload to S3/MinIO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResultDTO {

    /**
     * S3 object key (full path in bucket)
     */
    private String s3Key;

    /**
     * Bucket name
     */
    private String bucketName;

    /**
     * Original filename
     */
    private String fileName;

    /**
     * File size in bytes
     */
    private Long fileSize;

    /**
     * Content type (MIME type)
     */
    private String contentType;

    /**
     * ETag from S3 (used for versioning/validation)
     */
    private String etag;

    /**
     * Upload timestamp
     */
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    /**
     * Pre-signed URL for download (optional, expires after configured time)
     */
    private String downloadUrl;

    /**
     * Expiration time for the download URL
     */
    private LocalDateTime downloadUrlExpiresAt;

    /**
     * Whether upload was successful
     */
    @Builder.Default
    private Boolean success = true;

    /**
     * Error message if upload failed
     */
    private String errorMessage;
}
