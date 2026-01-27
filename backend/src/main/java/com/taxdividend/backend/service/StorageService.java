package com.taxdividend.backend.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class StorageService {

    private final MinioClient minioClient;

    @Value("${storage.s3.bucket}")
    private String bucketName;

    public StorageService(
            @Value("${storage.s3.endpoint}") String endpoint,
            @Value("${storage.s3.access-key}") String accessKey,
            @Value("${storage.s3.secret-key}") String secretKey) {

        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String upload(byte[] content, String userId) {
        String key = String.format("forms/%s/%s/%s.zip",
                userId,
                LocalDate.now().getYear(),
                UUID.randomUUID());

        try {
            // Check if bucket exists, create if not (simplified for now)
            // In prod, bucket creation usually handled by terraform/admin

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .contentType("application/zip")
                    .build());

            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to storage", e);
        }
    }

    public String generatePresignedUrl(String key) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(key)
                            .expiry(1, TimeUnit.HOURS)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}
