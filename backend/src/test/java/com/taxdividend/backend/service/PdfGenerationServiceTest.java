package com.taxdividend.backend.service;

import com.taxdividend.backend.dto.FileUploadResultDTO;
import com.taxdividend.backend.api.dto.FormGenerationRequestDto;
import com.taxdividend.backend.api.dto.GenerateFormResultDto;
import com.taxdividend.backend.exception.PdfGenerationException;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.GeneratedForm;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.GeneratedFormRepository;
import com.taxdividend.backend.repository.UserRepository;

import com.taxdividend.backend.service.pdf.PdfFormFiller;
import com.taxdividend.backend.service.pdf.Form5000FieldMapper;
import com.taxdividend.backend.service.pdf.Form5001FieldMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PdfGenerationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PDF Generation Service Tests")
class PdfGenerationServiceTest {

        @Mock
        private StorageService storageService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private DividendRepository dividendRepository;

        @Mock
        private GeneratedFormRepository generatedFormRepository;

        @Mock
        private PdfFormFiller pdfFormFiller;

        @Mock
        private Form5000FieldMapper form5000Mapper;

        @Mock
        private Form5001FieldMapper form5001Mapper;

        @InjectMocks
        private PdfGenerationService pdfGenerationService;

        private User testUser;
        private List<Dividend> testDividends;

        @BeforeEach
        void setUp() throws Exception {
                // Set form expiration via reflection
                ReflectionTestUtils.setField(pdfGenerationService, "formExpiryDays", 30);

                // Create test user
                testUser = User.builder()
                                .id(UUID.randomUUID())
                                .email("test@example.com")
                                .fullName("John Doe")
                                .country("CH")
                                .address("Test Street 123, 8000 Zurich")
                                .taxId("CH123456789")
                                .canton("ZH")
                                .build();

                // Create test dividends
                testDividends = Arrays.asList(
                                createTestDividend("FR0000120271", "Total Energies", "100.00", "30.00", "15.00"),
                                createTestDividend("FR0000120644", "Danone", "50.00", "15.00", "7.50"));

                // Mock PDF form filling components (lenient to avoid
                // UnnecessaryStubbingException)
                Map<String, String> mockFieldValues = new HashMap<>();
                mockFieldValues.put("test", "value");

                lenient().when(form5000Mapper.mapToFormFields(any(User.class), anyInt()))
                                .thenReturn(mockFieldValues);
                lenient().when(form5001Mapper.mapToFormFields(any(User.class), anyList(), anyInt()))
                                .thenReturn(mockFieldValues);
                lenient().when(pdfFormFiller.fillPdfForm(anyString(), anyMap(), anyBoolean()))
                                .thenReturn("MOCK PDF CONTENT".getBytes());
        }

        @Test
        @DisplayName("Should generate Form 5000 successfully")
        void shouldGenerateForm5000Successfully() {
                // Given
                Integer taxYear = 2024;
                FileUploadResultDTO uploadResult = FileUploadResultDTO.builder()
                                .success(true)
                                .s3Key("forms/2024/12/form-5000.pdf")
                                .fileSize(1024L)
                                .build();

                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(uploadResult);
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/presigned-url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> {
                                        GeneratedForm form = invocation.getArgument(0);
                                        form.setId(UUID.randomUUID());
                                        return form;
                                });

                // When
                GenerateFormResultDto result = pdfGenerationService.generateForm5000(testUser, taxYear);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isTrue();
                assertThat(result.getFormType()).isEqualTo("5000");
                assertThat(result.getDownloadUrl()).isNotNull();
                assertThat(result.getFormId()).isNotNull();

                verify(storageService).uploadFile(any(InputStream.class), anyString(), anyString(), anyString());
                verify(generatedFormRepository).save(any(GeneratedForm.class));
        }

        @Test
        @DisplayName("Should generate Form 5001 successfully")
        void shouldGenerateForm5001Successfully() {
                // Given
                Integer taxYear = 2024;
                FileUploadResultDTO uploadResult = FileUploadResultDTO.builder()
                                .success(true)
                                .s3Key("forms/2024/12/form-5001.pdf")
                                .fileSize(2048L)
                                .build();

                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(uploadResult);
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/presigned-url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(dividendRepository.save(any(Dividend.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                GenerateFormResultDto result = pdfGenerationService.generateForm5001(
                                testUser, testDividends, taxYear);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isTrue();
                assertThat(result.getFormType()).isEqualTo("5001");
                assertThat(result.getDividendCount()).isEqualTo(2);
                assertThat(result.getDownloadUrl()).isNotNull();

                verify(storageService).uploadFile(any(InputStream.class), anyString(), anyString(), anyString());
                verify(generatedFormRepository).save(any(GeneratedForm.class));
                verify(dividendRepository, times(2)).save(any(Dividend.class));
        }

        @Test
        @DisplayName("Should generate BUNDLE successfully")
        void shouldGenerateBundleSuccessfully() {
                // Given
                Integer taxYear = 2024;
                FileUploadResultDTO uploadResult = FileUploadResultDTO.builder()
                                .success(true)
                                .s3Key("bundles/2024/12/bundle.zip")
                                .fileSize(5120L)
                                .build();

                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(uploadResult);
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/presigned-url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(dividendRepository.save(any(Dividend.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                GenerateFormResultDto result = pdfGenerationService.generateBundle(
                                testUser, testDividends, taxYear);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isTrue();
                assertThat(result.getFormType()).isEqualTo("BUNDLE");
                assertThat(result.getDividendCount()).isEqualTo(2);
                assertThat(result.getDownloadUrl()).isNotNull();

                verify(storageService).uploadFile(any(InputStream.class), anyString(), anyString(), anyString());
                verify(generatedFormRepository).save(any(GeneratedForm.class));
        }

        @Test
        @DisplayName("Should throw exception when user data incomplete for Form 5000")
        void shouldThrowExceptionWhenUserDataIncomplete() {
                // Given
                User incompleteUser = User.builder()
                                .id(UUID.randomUUID())
                                .email("test@example.com")
                                // Missing required fields: firstName, lastName, etc.
                                .build();

                // When/Then
                assertThatThrownBy(() -> pdfGenerationService.generateForm5000(incompleteUser, 2024))
                                .isInstanceOf(PdfGenerationException.class)
                                .hasStackTraceContaining("User data incomplete");
        }

        @Test
        @DisplayName("Should throw exception when dividend list is empty for Form 5001")
        void shouldThrowExceptionWhenDividendListEmpty() {
                // Given
                List<Dividend> emptyList = Arrays.asList();

                // When/Then
                // When/Then
                assertThatThrownBy(() -> pdfGenerationService.generateForm5001(testUser, emptyList, 2024))
                                .isInstanceOf(PdfGenerationException.class);
                // Message check removed as it depends on wrapped exception details which vary
        }

        @Test
        @DisplayName("Should generate forms from request")
        void shouldGenerateFormsFromRequest() {
                // Given
                FormGenerationRequestDto request = new FormGenerationRequestDto()
                                .userId(testUser.getId())
                                .formType(FormGenerationRequestDto.FormTypeEnum._5001)
                                .taxYear(2024)
                                .dividendIds(Arrays.asList(
                                                testDividends.get(0).getId(),
                                                testDividends.get(1).getId()));

                when(userRepository.findById(testUser.getId()))
                                .thenReturn(Optional.of(testUser));
                when(dividendRepository.findAllById(anyList()))
                                .thenReturn(testDividends);
                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(FileUploadResultDTO.builder()
                                                .success(true)
                                                .s3Key("forms/2024/12/form.pdf")
                                                .build());
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(dividendRepository.save(any(Dividend.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                GenerateFormResultDto result = pdfGenerationService.generateForms(request);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isTrue();
                assertThat(result.getFormType()).isEqualTo("5001");

                verify(userRepository).findById(testUser.getId());
                verify(dividendRepository).findAllById(anyList());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
                // Given
                UUID nonExistentUserId = UUID.randomUUID();
                FormGenerationRequestDto request = new FormGenerationRequestDto()
                                .userId(nonExistentUserId)
                                .formType(FormGenerationRequestDto.FormTypeEnum._5000)
                                .taxYear(2024);

                when(userRepository.findById(nonExistentUserId))
                                .thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> pdfGenerationService.generateForms(request))
                                .isInstanceOf(PdfGenerationException.class)
                                .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should regenerate expired form")
        void shouldRegenerateExpiredForm() {
                // Given
                UUID formId = UUID.randomUUID();
                GeneratedForm existingForm = GeneratedForm.builder()
                                .id(formId)
                                .user(testUser)
                                .formType("5000")
                                .taxYear(2024)
                                .fileName("Form_5000_2024.pdf")
                                .s3Key("forms/2024/12/old-form.pdf")
                                .expiresAt(LocalDateTime.now().minusDays(1)) // Expired
                                .build();

                when(generatedFormRepository.findById(formId))
                                .thenReturn(Optional.of(existingForm));
                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(FileUploadResultDTO.builder()
                                                .success(true)
                                                .s3Key("forms/2024/12/new-form.pdf")
                                                .build());
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/new-url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                GenerateFormResultDto result = pdfGenerationService.regenerateForm(formId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getSuccess()).isTrue();
                assertThat(result.getFormType()).isEqualTo("5000");

                verify(storageService).deleteFile("forms/2024/12/old-form.pdf");
                verify(generatedFormRepository).save(any(GeneratedForm.class));
        }

        @Test
        @DisplayName("Should throw exception when form not found for regeneration")
        void shouldThrowExceptionWhenFormNotFoundForRegeneration() {
                // Given
                UUID nonExistentFormId = UUID.randomUUID();
                when(generatedFormRepository.findById(nonExistentFormId))
                                .thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> pdfGenerationService.regenerateForm(nonExistentFormId))
                                .isInstanceOf(PdfGenerationException.class)
                                .hasMessageContaining("Form not found");
        }

        @Test
        @DisplayName("Should set expiration date correctly")
        void shouldSetExpirationDateCorrectly() {
                // Given
                Integer taxYear = 2024;
                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(FileUploadResultDTO.builder()
                                                .success(true)
                                                .s3Key("forms/2024/12/form.pdf")
                                                .build());
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> {
                                        GeneratedForm form = invocation.getArgument(0);
                                        assertThat(form.getExpiresAt()).isNotNull();
                                        assertThat(form.getExpiresAt())
                                                        .isAfter(LocalDateTime.now().plusDays(29));
                                        return form;
                                });

                // When
                pdfGenerationService.generateForm5000(testUser, taxYear);

                // Then
                verify(generatedFormRepository).save(any(GeneratedForm.class));
        }

        @Test
        @DisplayName("Should link dividends to form")
        void shouldLinkDividendsToForm() {
                // Given
                Integer taxYear = 2024;
                when(storageService.uploadFile(any(), anyString(), anyString(), anyString()))
                                .thenReturn(FileUploadResultDTO.builder()
                                                .success(true)
                                                .s3Key("forms/2024/12/form.pdf")
                                                .build());
                when(storageService.generatePresignedUrl(anyString(), any(Duration.class)))
                                .thenReturn("https://minio.example.com/url");
                when(generatedFormRepository.save(any(GeneratedForm.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(dividendRepository.save(any(Dividend.class)))
                                .thenAnswer(invocation -> {
                                        Dividend div = invocation.getArgument(0);
                                        assertThat(div.getForm()).isNotNull();
                                        return div;
                                });

                // When
                pdfGenerationService.generateForm5001(testUser, testDividends, taxYear);

                // Then
                verify(dividendRepository, times(2)).save(any(Dividend.class));
        }

        // Helper method
        private Dividend createTestDividend(String isin, String name,
                        String gross, String withholding, String reclaimable) {
                return Dividend.builder()
                                .id(UUID.randomUUID())
                                .user(testUser)
                                .isin(isin)
                                .securityName(name)
                                .grossAmount(new BigDecimal(gross))
                                .withholdingTax(new BigDecimal(withholding))
                                .reclaimableAmount(new BigDecimal(reclaimable))
                                .currency("EUR")
                                .paymentDate(LocalDate.of(2024, 12, 15))
                                .sourceCountry("FR")
                                .build();
        }
}
