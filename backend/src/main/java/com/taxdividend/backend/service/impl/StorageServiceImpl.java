package com.taxdividend.backend.service.impl;

import com.taxdividend.backend.dto.FileUploadResultDTO;
import com.taxdividend.backend.exception.StorageException;
import com.taxdividend.backend.repository.GeneratedFormRepository;
import com.taxdividend.backend.service.StorageService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of storage service using MinIO (S3-compatible).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;
    private final GeneratedFormRepository generatedFormRepository;

    @Value("${storage.s3.bucket}")
    private String bucketName;

    @Value("${app.forms.expiry-days:30}")
    private int formExpiryDays;

    private static final Duration DEFAULT_PRESIGNED_URL_EXPIRATION = Duration.ofHours(1);

    @Override
    public FileUploadResultDTO uploadFile(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("File is empty");
            }
            String s3Key = generateS3Key(folder, file.getOriginalFilename());
            return uploadFileWithKey(file, s3Key);
        } catch (StorageException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            return FileUploadResultDTO.builder()
                    .fileName(file.getOriginalFilename())
                    .success(false)
                    .errorMessage("Upload failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FileUploadResultDTO uploadFile(InputStream inputStream, String fileName,
            String contentType, String folder) {
        try {
            String s3Key = generateS3Key(folder, fileName);

            // Need to know stream size - MinIO requires it
            byte[] bytes = inputStream.readAllBytes();

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(s3Key)
                    .stream(new java.io.ByteArrayInputStream(bytes), bytes.length, -1)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build();

            ObjectWriteResponse response = minioClient.putObject(args);

            log.info("File uploaded successfully: {} ({} bytes)", s3Key, bytes.length);

            return FileUploadResultDTO.builder()
                    .s3Key(s3Key)
                    .bucketName(bucketName)
                    .fileName(fileName)
                    .fileSize((long) bytes.length)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .etag(response.etag())
                    .uploadedAt(LocalDateTime.now())
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload file: {}", fileName, e);
            return FileUploadResultDTO.builder()
                    .fileName(fileName)
                    .success(false)
                    .errorMessage("Upload failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FileUploadResultDTO uploadFileWithKey(MultipartFile file, String s3Key) {
        try {
            ensureBucketExists();

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(s3Key)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .build();

            ObjectWriteResponse response = minioClient.putObject(args);

            log.info("File uploaded successfully: {} ({} bytes)", s3Key, file.getSize());

            return FileUploadResultDTO.builder()
                    .s3Key(s3Key)
                    .bucketName(bucketName)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .etag(response.etag())
                    .uploadedAt(LocalDateTime.now())
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload file with key {}: {}", s3Key, e.getMessage(), e);
            return FileUploadResultDTO.builder()
                    .fileName(file.getOriginalFilename())
                    .s3Key(s3Key)
                    .success(false)
                    .errorMessage("Upload failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public InputStream downloadFile(String s3Key) {
        try {
            log.debug("Downloading file: {}", s3Key);

            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(s3Key)
                    .build();

            return minioClient.getObject(args);

        } catch (Exception e) {
            log.error("Failed to download file: {}", s3Key, e);
            throw new StorageException("Failed to download file: " + s3Key, e);
        }
    }

    @Override
    public byte[] downloadFileAsBytes(String s3Key) {
        try (InputStream inputStream = downloadFile(s3Key)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Failed to read file bytes: {}", s3Key, e);
            throw new StorageException("Failed to read file: " + s3Key, e);
        }
    }

    @Override
    public String generatePresignedUrl(String s3Key, Duration expiration) {
        try {
            log.debug("Generating presigned URL for: {} (expires in {})", s3Key, expiration);

            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(s3Key)
                    .expiry((int) expiration.getSeconds(), TimeUnit.SECONDS)
                    .build();

            String url = minioClient.getPresignedObjectUrl(args);
            log.debug("Generated presigned URL: {}", url);

            return url;

        } catch (Exception e) {
            log.error("Failed to generate presigned URL for: {}", s3Key, e);
            throw new StorageException("Failed to generate presigned URL: " + s3Key, e);
        }
    }

    @Override
    public String generatePresignedUrl(String s3Key) {
        return generatePresignedUrl(s3Key, DEFAULT_PRESIGNED_URL_EXPIRATION);
    }

    @Override
    public boolean deleteFile(String s3Key) {
        try {
            log.info("Deleting file: {}", s3Key);

            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(s3Key)
                    .build();

            minioClient.removeObject(args);
            log.info("File deleted successfully: {}", s3Key);

            return true;

        } catch (Exception e) {
            log.error("Failed to delete file: {}", s3Key, e);
            return false;
        }
    }

    @Override
    public int deleteFiles(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return 0;
        }

        try {
            List<DeleteObject> objects = s3Keys.stream()
                    .map(DeleteObject::new)
                    .toList();

            RemoveObjectsArgs args = RemoveObjectsArgs.builder()
                    .bucket(bucketName)
                    .objects(objects)
                    .build();

            Iterable<Result<DeleteError>> results = minioClient.removeObjects(args);

            int errorCount = 0;

            for (Result<DeleteError> result : results) {
                try {
                    DeleteError error = result.get();
                    if (error != null) {
                        log.error("Failed to delete object: {} - {}", error.objectName(), error.message());
                        errorCount++;
                    }
                } catch (Exception e) {
                    log.error("Error retrieving delete result", e);
                    // If we can't retrieve the result, assume it's an error in the process ?
                    // Or ignore? Safer to count as error.
                    errorCount++;
                }
            }

            int deletedCount = s3Keys.size() - errorCount;
            log.info("Batch delete completed: {} deleted, {} errors", deletedCount, errorCount);
            return deletedCount;

        } catch (Exception e) {
            log.error("Failed to delete files batch", e);
            return 0;
        }
    }

    @Override
    public boolean fileExists(String s3Key) {
        try {
            StatObjectArgs args = StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(s3Key)
                    .build();

            minioClient.statObject(args);
            return true;

        } catch (Exception e) {
            log.debug("File does not exist: {}", s3Key);
            return false;
        }
    }

    @Override
    public long getFileSize(String s3Key) {
        try {
            StatObjectArgs args = StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(s3Key)
                    .build();

            StatObjectResponse stat = minioClient.statObject(args);
            return stat.size();

        } catch (Exception e) {
            log.error("Failed to get file size: {}", s3Key, e);
            return -1;
        }
    }

    @Override
    public List<String> listFiles(String folderPrefix) {
        List<String> fileKeys = new ArrayList<>();

        try {
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(folderPrefix)
                    .recursive(true)
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(args);

            for (Result<Item> result : results) {
                try {
                    Item item = result.get();
                    if (!item.isDir()) {
                        fileKeys.add(item.objectName());
                    }
                } catch (Exception e) {
                    log.error("Error processing list result", e);
                }
            }

            log.debug("Found {} files in folder: {}", fileKeys.size(), folderPrefix);

        } catch (Exception e) {
            log.error("Failed to list files in folder: {}", folderPrefix, e);
        }

        return fileKeys;
    }

    @Override
    public int cleanupExpiredFiles() {
        log.info("Starting cleanup of expired files (older than {} days)", formExpiryDays);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(formExpiryDays);

        // Find expired forms from database
        var expiredForms = generatedFormRepository.findExpiredForms(cutoffDate);

        if (expiredForms.isEmpty()) {
            log.info("No expired forms found");
            return 0;
        }

        List<String> s3KeysToDelete = expiredForms.stream()
                .map(form -> form.getS3Key())
                .toList();

        int deletedCount = deleteFiles(s3KeysToDelete);

        // Also delete form records
        generatedFormRepository.deleteAll(expiredForms);

        log.info("Cleanup completed: {} files and {} database records deleted",
                deletedCount, expiredForms.size());

        return deletedCount;
    }

    @Override
    public String generateS3Key(String folder, String fileName) {
        // Generate unique key: folder/YYYY/MM/uuid_filename
        LocalDateTime now = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();

        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        return String.format("%s/%d/%02d/%s_%s",
                folder,
                now.getYear(),
                now.getMonthValue(),
                uuid,
                sanitizedFileName);
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    /**
     * Ensure the bucket exists, create if not.
     */
    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());

            if (!exists) {
                log.info("Bucket {} does not exist, creating it", bucketName);
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket {} created successfully", bucketName);
            }

        } catch (Exception e) {
            log.error("Failed to ensure bucket exists: {}", bucketName, e);
            throw new StorageException("Failed to ensure bucket exists", e);
        }
    }
}
