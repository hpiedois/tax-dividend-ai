package com.taxdividend.backend.service;

import com.taxdividend.backend.dto.FileUploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

/**
 * Service for managing file storage in S3/MinIO.
 *
 * Handles:
 * - PDF uploads (bank statements, generated forms)
 * - File downloads
 * - Pre-signed URLs for secure access
 * - File deletion and cleanup
 */
public interface StorageService {

    /**
     * Upload a file to S3/MinIO.
     *
     * @param file The file to upload
     * @param folder Folder/prefix in bucket (e.g., "statements", "forms")
     * @return Upload result with S3 key
     */
    FileUploadResultDTO uploadFile(MultipartFile file, String folder);

    /**
     * Upload a file from InputStream.
     *
     * @param inputStream File input stream
     * @param fileName Original filename
     * @param contentType MIME type
     * @param folder Folder/prefix in bucket
     * @return Upload result with S3 key
     */
    FileUploadResultDTO uploadFile(InputStream inputStream, String fileName,
                                    String contentType, String folder);

    /**
     * Upload file with custom S3 key.
     *
     * @param file The file to upload
     * @param s3Key Custom S3 key (full path)
     * @return Upload result
     */
    FileUploadResultDTO uploadFileWithKey(MultipartFile file, String s3Key);

    /**
     * Download a file from S3/MinIO.
     *
     * @param s3Key The S3 object key
     * @return File input stream
     */
    InputStream downloadFile(String s3Key);

    /**
     * Download file as byte array.
     *
     * @param s3Key The S3 object key
     * @return File bytes
     */
    byte[] downloadFileAsBytes(String s3Key);

    /**
     * Generate a pre-signed URL for temporary download access.
     *
     * @param s3Key The S3 object key
     * @param expiration Duration until URL expires (e.g., Duration.ofHours(1))
     * @return Pre-signed URL
     */
    String generatePresignedUrl(String s3Key, Duration expiration);

    /**
     * Generate a pre-signed URL with default expiration (1 hour).
     *
     * @param s3Key The S3 object key
     * @return Pre-signed URL
     */
    String generatePresignedUrl(String s3Key);

    /**
     * Delete a file from S3/MinIO.
     *
     * @param s3Key The S3 object key
     * @return true if deleted successfully
     */
    boolean deleteFile(String s3Key);

    /**
     * Delete multiple files.
     *
     * @param s3Keys List of S3 object keys
     * @return Number of files successfully deleted
     */
    int deleteFiles(List<String> s3Keys);

    /**
     * Check if a file exists in S3/MinIO.
     *
     * @param s3Key The S3 object key
     * @return true if file exists
     */
    boolean fileExists(String s3Key);

    /**
     * Get file size in bytes.
     *
     * @param s3Key The S3 object key
     * @return File size, or -1 if not found
     */
    long getFileSize(String s3Key);

    /**
     * List all files in a folder.
     *
     * @param folderPrefix Folder prefix (e.g., "forms/2024/")
     * @return List of S3 keys
     */
    List<String> listFiles(String folderPrefix);

    /**
     * Delete expired files (older than configured retention period).
     *
     * @return Number of files deleted
     */
    int cleanupExpiredFiles();

    /**
     * Generate a unique S3 key for a file.
     *
     * @param folder Folder/prefix
     * @param fileName Original filename
     * @return Generated S3 key (e.g., "forms/2024/uuid_filename.pdf")
     */
    String generateS3Key(String folder, String fileName);
}
