package com.taxdividend.backend.service;

import com.taxdividend.backend.dto.FileUploadResultDTO;
import com.taxdividend.backend.exception.StorageException;
import com.taxdividend.backend.service.impl.StorageServiceImpl;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StorageService.
 * Uses Mockito to mock MinIO client.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Storage Service Tests")
class StorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StorageServiceImpl storageService;

    private static final String TEST_BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        // Set bucket name via reflection (normally injected via @Value)
        ReflectionTestUtils.setField(storageService, "bucketName", TEST_BUCKET);
    }

    @Test
    @DisplayName("Should upload file successfully")
    void shouldUploadFileSuccessfully() throws Exception {
        // Given
        byte[] content = "Test PDF content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-form.pdf",
                "application/pdf",
                content
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        // When
        FileUploadResultDTO result = storageService.uploadFile(file, "forms");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getS3Key()).isNotNull();
        assertThat(result.getS3Key()).startsWith("forms/");
        assertThat(result.getS3Key()).contains("test-form.pdf");
        assertThat(result.getFileSize()).isEqualTo(content.length);
        assertThat(result.getContentType()).isEqualTo("application/pdf");

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should create bucket if not exists")
    void shouldCreateBucketIfNotExists() throws Exception {
        // Given
        byte[] content = "Test content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                content
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(false);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        // When
        FileUploadResultDTO result = storageService.uploadFile(file, "forms");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSuccess()).isTrue();

        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void shouldThrowExceptionWhenFileIsEmpty() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        // When/Then
        assertThatThrownBy(() -> storageService.uploadFile(emptyFile, "forms"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("Should upload from InputStream")
    void shouldUploadFromInputStream() throws Exception {
        // Given
        byte[] content = "Test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        String s3Key = "forms/2024/12/test.pdf";

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        // When
        FileUploadResultDTO result = storageService.uploadFile(
                inputStream, s3Key, "application/pdf", "test-folder");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getS3Key()).isEqualTo(s3Key);
        assertThat(result.getFileSize()).isEqualTo(content.length);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should download file as bytes")
    void shouldDownloadFileAsBytes() throws Exception {
        // Given
        String s3Key = "forms/2024/12/test.pdf";
        byte[] expectedContent = "Test PDF content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(expectedContent);

        GetObjectResponse response = mock(GetObjectResponse.class);
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(response);
        when(response.readAllBytes()).thenReturn(expectedContent);

        // When
        byte[] result = storageService.downloadFileAsBytes(s3Key);

        // Then
        assertThat(result).isEqualTo(expectedContent);
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    @DisplayName("Should download file as InputStream")
    void shouldDownloadFileAsInputStream() throws Exception {
        // Given
        String s3Key = "forms/2024/12/test.pdf";
        GetObjectResponse response = mock(GetObjectResponse.class);

        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(response);

        // When
        InputStream result = storageService.downloadFile(s3Key);

        // Then
        assertThat(result).isNotNull();
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    @DisplayName("Should delete file successfully")
    void shouldDeleteFileSuccessfully() throws Exception {
        // Given
        String s3Key = "forms/2024/12/test.pdf";

        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When
        boolean result = storageService.deleteFile(s3Key);

        // Then
        assertThat(result).isTrue();
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should check if file exists")
    void shouldCheckIfFileExists() throws Exception {
        // Given
        String s3Key = "forms/2024/12/test.pdf";

        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenReturn(mock(StatObjectResponse.class));

        // When
        boolean exists = storageService.fileExists(s3Key);

        // Then
        assertThat(exists).isTrue();
        verify(minioClient).statObject(any(StatObjectArgs.class));
    }

    @Test
    @DisplayName("Should return false when file does not exist")
    void shouldReturnFalseWhenFileDoesNotExist() throws Exception {
        // Given
        String s3Key = "forms/2024/12/nonexistent.pdf";

        when(minioClient.statObject(any(StatObjectArgs.class)))
                .thenThrow(new Exception("Object does not exist"));

        // When
        boolean exists = storageService.fileExists(s3Key);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should generate presigned URL")
    void shouldGeneratePresignedUrl() throws Exception {
        // Given
        String s3Key = "forms/2024/12/test.pdf";
        Duration expiration = Duration.ofHours(24);
        String expectedUrl = "https://minio.example.com/presigned-url";

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(expectedUrl);

        // When
        String url = storageService.generatePresignedUrl(s3Key, expiration);

        // Then
        assertThat(url).isEqualTo(expectedUrl);
        verify(minioClient).getPresignedObjectUrl(
                argThat(args -> args != null));
    }

    @Test
    @DisplayName("Should throw exception when generating URL fails")
    void shouldThrowExceptionWhenGeneratingUrlFails() throws Exception {
        // Given
        String s3Key = "forms/2024/12/test.pdf";
        Duration expiration = Duration.ofHours(24);

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new Exception("MinIO error"));

        // When/Then
        assertThatThrownBy(() ->
                storageService.generatePresignedUrl(s3Key, expiration))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Failed to generate presigned URL");
    }

    @Test
    @DisplayName("Should generate unique S3 key")
    void shouldGenerateUniqueS3Key() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file",
                "form-5000.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        // When
        FileUploadResultDTO result1 = storageService.uploadFile(file, "forms");
        FileUploadResultDTO result2 = storageService.uploadFile(file, "forms");

        // Then - S3 keys should be different (UUID makes them unique)
        assertThat(result1.getS3Key()).isNotEqualTo(result2.getS3Key());
        assertThat(result1.getS3Key()).contains("form-5000.pdf");
        assertThat(result2.getS3Key()).contains("form-5000.pdf");
    }

    @Test
    @DisplayName("Should sanitize filename")
    void shouldSanitizeFilename() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file",
                "test file with spaces.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        // When
        FileUploadResultDTO result = storageService.uploadFile(file, "forms");

        // Then - spaces should be replaced
        assertThat(result.getS3Key()).contains("test_file_with_spaces.pdf");
    }

    @Test
    @DisplayName("Should delete multiple files")
    void shouldDeleteMultipleFiles() throws Exception {
        // Given
        java.util.List<String> s3Keys = java.util.Arrays.asList(
                "forms/2024/12/file1.pdf",
                "forms/2024/12/file2.pdf",
                "forms/2024/12/file3.pdf"
        );

        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When
        int deletedCount = storageService.deleteFiles(s3Keys);

        // Then
        assertThat(deletedCount).isEqualTo(3);
        verify(minioClient, times(3)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should handle partial deletion failures")
    void shouldHandlePartialDeletionFailures() throws Exception {
        // Given
        java.util.List<String> s3Keys = java.util.Arrays.asList(
                "forms/2024/12/file1.pdf",
                "forms/2024/12/file2.pdf",
                "forms/2024/12/file3.pdf"
        );

        // First succeeds, second fails, third succeeds
        doNothing()
                .doThrow(new RuntimeException("Delete failed"))
                .doNothing()
                .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When
        int deletedCount = storageService.deleteFiles(s3Keys);

        // Then - 2 out of 3 should succeed
        assertThat(deletedCount).isEqualTo(2);
        verify(minioClient, times(3)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should handle upload with null content type")
    void shouldHandleUploadWithNullContentType() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                null, // null content type
                "content".getBytes()
        );

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        // When
        FileUploadResultDTO result = storageService.uploadFile(file, "forms");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getContentType()).isEqualTo("application/octet-stream");
    }
}
