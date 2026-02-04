package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.DividendStatementDto;
import com.taxdividend.backend.api.dto.DividendStatementUpdateDto;
import com.taxdividend.backend.dto.FileUploadResultDTO;
import com.taxdividend.backend.exception.StorageException;
import com.taxdividend.backend.mapper.DividendStatementMapper;
import com.taxdividend.backend.model.DividendStatement;
import com.taxdividend.backend.model.DividendStatementStatus;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendStatementRepository;
import com.taxdividend.backend.repository.UserRepository;
import com.taxdividend.backend.api.dto.DividendStatementStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.taxdividend.backend.api.dto.DividendStatementStatusDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DividendStatementService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DividendStatementService Tests")
class DividendStatementServiceTest {

    @Mock
    private DividendStatementRepository statementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private DividendStatementMapper mapper;

    @InjectMocks
    private DividendStatementService service;

    @Mock
    private MultipartFile mockFile;

    private UUID userId;
    private UUID statementId;

    private User user;
    private DividendStatement statement;

    private DividendStatementDto statementDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        statementId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .country("CH")
                .build();

        statement = DividendStatement.builder()
                .id(statementId)
                .user(user)
                .sourceFileName("statement.pdf")
                .sourceFileS3Key("statements/statement.pdf")
                .broker("InteractiveBrokers")
                .periodStart(LocalDate.of(2024, 1, 1))
                .periodEnd(LocalDate.of(2024, 12, 31))
                .status(DividendStatementStatus.UPLOADED)
                .dividendCount(0)
                .totalGrossAmount(BigDecimal.ZERO)
                .totalReclaimable(BigDecimal.ZERO)
                .build();

        statementDTO = new DividendStatementDto();
        statementDTO.setId(statementId);
        statementDTO.setUserId(userId);
        statementDTO.setSourceFileName("statement.pdf");
        statementDTO.setBroker("InteractiveBrokers");
        statementDTO.setStatus(DividendStatementStatusDto.UPLOADED);
        // So `statementDTO.setStatus(DividendStatementStatusDto.UPLOADED);`
        // I need to add import for DividendStatementStatusDto.
        // I will add it in import chunk.

    }

    @Nested
    @DisplayName("Upload Statement Tests")
    class UploadStatementTests {

        @Test
        @DisplayName("Should upload statement successfully")
        void shouldUploadStatementSuccessfully() {
            // Given
            String broker = "InteractiveBrokers";
            LocalDate periodStart = LocalDate.of(2024, 1, 1);
            LocalDate periodEnd = LocalDate.of(2024, 12, 31);

            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn("statement.pdf");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            FileUploadResultDTO uploadResult = FileUploadResultDTO.builder()
                    .success(true)
                    .s3Key("statements/statement.pdf")
                    .build();
            when(storageService.uploadFile(mockFile, "statements")).thenReturn(uploadResult);
            when(statementRepository.save(any(DividendStatement.class))).thenReturn(statement);
            when(mapper.toDto(statement)).thenReturn(statementDTO);

            // When
            DividendStatementDto result = service.uploadStatement(mockFile, userId, broker, periodStart, periodEnd);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(statementId);
            assertThat(result.getBroker()).isEqualTo(broker);

            verify(userRepository).findById(userId);
            verify(storageService).uploadFile(mockFile, "statements");
            verify(statementRepository).save(any(DividendStatement.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.uploadStatement(
                    mockFile, userId, "IB", LocalDate.now(), LocalDate.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should throw exception when file is empty")
        void shouldThrowExceptionWhenFileIsEmpty() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(mockFile.isEmpty()).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> service.uploadStatement(
                    mockFile, userId, "IB", LocalDate.now(), LocalDate.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("File is empty");
        }

        @Test
        @DisplayName("Should throw exception when period dates are invalid")
        void shouldThrowExceptionWhenPeriodDatesInvalid() {
            // Given
            LocalDate start = LocalDate.of(2024, 12, 31);
            LocalDate end = LocalDate.of(2024, 1, 1);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(mockFile.isEmpty()).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> service.uploadStatement(mockFile, userId, "IB", start, end))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Period end must be after period start");
        }

        @Test
        @DisplayName("Should throw exception when storage upload fails")
        void shouldThrowExceptionWhenStorageUploadFails() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(mockFile.isEmpty()).thenReturn(false);

            FileUploadResultDTO uploadResult = FileUploadResultDTO.builder()
                    .success(false)
                    .errorMessage("Storage error")
                    .build();
            when(storageService.uploadFile(mockFile, "statements")).thenReturn(uploadResult);

            // When/Then
            assertThatThrownBy(() -> service.uploadStatement(
                    mockFile, userId, "IB", LocalDate.now(), LocalDate.now()))
                    .isInstanceOf(StorageException.class)
                    .hasMessageContaining("Failed to upload statement file");
        }
    }

    @Nested
    @DisplayName("Get Statement Tests")
    class GetStatementTests {

        @Test
        @DisplayName("Should get statement when found and owned by user")
        void shouldGetStatementWhenFoundAndOwned() {
            // Given
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.of(statement));
            when(mapper.toDto(statement)).thenReturn(statementDTO);

            // When
            Optional<DividendStatementDto> result = service.getStatement(statementId, userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(statementId);
            verify(statementRepository).findByIdAndUserId(statementId, userId);
        }

        @Test
        @DisplayName("Should return empty when statement not found")
        void shouldReturnEmptyWhenStatementNotFound() {
            // Given
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.empty());

            // When
            Optional<DividendStatementDto> result = service.getStatement(statementId, userId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("List Statements Tests")
    class ListStatementsTests {

        @Test
        @DisplayName("Should list all statements when no status filter")
        void shouldListAllStatementsWhenNoStatusFilter() {
            // Given
            Pageable pageable = PageRequest.of(0, 20);
            Page<DividendStatement> page = new PageImpl<>(List.of(statement));

            when(statementRepository.findByUserId(userId, pageable)).thenReturn(page);
            when(mapper.toDtoPage(page)).thenReturn(page.map(s -> statementDTO));

            // When
            Page<DividendStatementDto> result = service.listStatements(userId, null, pageable);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            verify(statementRepository).findByUserId(userId, pageable);
        }

        @Test
        @DisplayName("Should list statements filtered by status")
        void shouldListStatementsFilteredByStatus() {
            // Given
            Pageable pageable = PageRequest.of(0, 20);
            Page<DividendStatement> page = new PageImpl<>(List.of(statement));

            when(statementRepository.findByUserIdAndStatus(userId, DividendStatementStatus.UPLOADED, pageable))
                    .thenReturn(page);
            when(mapper.toDtoPage(page)).thenReturn(page.map(s -> statementDTO));

            // When
            Page<DividendStatementDto> result = service.listStatements(
                    userId, DividendStatementStatus.UPLOADED, pageable);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            verify(statementRepository).findByUserIdAndStatus(userId, DividendStatementStatus.UPLOADED, pageable);
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status successfully with valid transition")
        void shouldUpdateStatusWithValidTransition() {
            // Given
            DividendStatementUpdateDto updateDTO = new DividendStatementUpdateDto();
            updateDTO.setStatus(DividendStatementStatusDto.PARSING);
            updateDTO.setParsedBy("AI_AGENT");
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.of(statement));
            when(statementRepository.save(any(DividendStatement.class))).thenReturn(statement);
            when(mapper.toDto(statement)).thenReturn(statementDTO);

            // When
            DividendStatementDto result = service.updateStatus(statementId, userId, updateDTO);

            // Then
            assertThat(result).isNotNull();
            verify(statementRepository).findByIdAndUserId(statementId, userId);
            verify(statementRepository).save(any(DividendStatement.class));
        }

        @Test
        @DisplayName("Should throw exception when statement not found")
        void shouldThrowExceptionWhenStatementNotFound() {
            // Given
            DividendStatementUpdateDto updateDTO = new DividendStatementUpdateDto();
            updateDTO.setStatus(DividendStatementStatusDto.PARSING);

            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.updateStatus(statementId, userId, updateDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Statement not found or does not belong to user");
        }

        @Test
        @DisplayName("Should update SENT status with method and notes")
        void shouldUpdateSentStatusWithMethodAndNotes() {
            // Given
            statement.setStatus(DividendStatementStatus.VALIDATED);
            DividendStatementUpdateDto updateDTO = new DividendStatementUpdateDto();
            updateDTO.setStatus(DividendStatementStatusDto.SENT);
            updateDTO.setSentMethod("EMAIL");
            updateDTO.setSentNotes("Submitted via email");
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.of(statement));
            when(statementRepository.save(any(DividendStatement.class))).thenReturn(statement);
            when(mapper.toDto(statement)).thenReturn(statementDTO);

            // When
            DividendStatementDto result = service.updateStatus(statementId, userId, updateDTO);

            // Then
            assertThat(result).isNotNull();
            ArgumentCaptor<DividendStatement> captor = ArgumentCaptor.forClass(DividendStatement.class);
            verify(statementRepository).save(captor.capture());
            DividendStatement saved = captor.getValue();
            assertThat(saved.getSentMethod()).isEqualTo("EMAIL");
            assertThat(saved.getSentNotes()).isEqualTo("Submitted via email");
        }

        @Test
        @DisplayName("Should update PAID status with amount")
        void shouldUpdatePaidStatusWithAmount() {
            // Given
            statement.setStatus(DividendStatementStatus.SENT);
            DividendStatementUpdateDto updateDTO = new DividendStatementUpdateDto();
            updateDTO.setStatus(DividendStatementStatusDto.PAID);
            updateDTO.setPaidAmount(new BigDecimal("150.50"));
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.of(statement));
            when(statementRepository.save(any(DividendStatement.class))).thenReturn(statement);
            when(mapper.toDto(statement)).thenReturn(statementDTO);

            // When
            DividendStatementDto result = service.updateStatus(statementId, userId, updateDTO);

            // Then
            assertThat(result).isNotNull();
            ArgumentCaptor<DividendStatement> captor = ArgumentCaptor.forClass(DividendStatement.class);
            verify(statementRepository).save(captor.capture());
            DividendStatement saved = captor.getValue();
            assertThat(saved.getPaidAmount()).isEqualByComparingTo(new BigDecimal("150.50"));
        }
    }

    @Nested
    @DisplayName("Update After Parsing Tests")
    class UpdateAfterParsingTests {

        @Test
        @DisplayName("Should update statement metadata after parsing")
        void shouldUpdateStatementMetadataAfterParsing() {
            // Given
            int dividendCount = 5;
            BigDecimal totalGross = new BigDecimal("1000.00");
            BigDecimal totalReclaimable = new BigDecimal("150.00");

            when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));
            when(statementRepository.save(any(DividendStatement.class))).thenReturn(statement);

            // When
            service.updateAfterParsing(statementId, dividendCount, totalGross, totalReclaimable);

            // Then
            ArgumentCaptor<DividendStatement> captor = ArgumentCaptor.forClass(DividendStatement.class);
            verify(statementRepository).save(captor.capture());
            DividendStatement saved = captor.getValue();
            assertThat(saved.getDividendCount()).isEqualTo(5);
            assertThat(saved.getTotalGrossAmount()).isEqualByComparingTo(totalGross);
            assertThat(saved.getTotalReclaimable()).isEqualByComparingTo(totalReclaimable);
        }

        @Test
        @DisplayName("Should throw exception when statement not found")
        void shouldThrowExceptionWhenStatementNotFound() {
            // Given
            when(statementRepository.findById(statementId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.updateAfterParsing(
                    statementId, 5, BigDecimal.TEN, BigDecimal.ONE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Statement not found");
        }
    }

    @Nested
    @DisplayName("Delete Statement Tests")
    class DeleteStatementTests {

        @Test
        @DisplayName("Should delete statement and file successfully")
        void shouldDeleteStatementAndFileSuccessfully() {
            // Given
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.of(statement));
            // No need to stub void methods - they do nothing by default in Mockito

            // When
            service.deleteStatement(statementId, userId);

            // Then
            verify(statementRepository).findByIdAndUserId(statementId, userId);
            verify(storageService).deleteFile(statement.getSourceFileS3Key());
            verify(statementRepository).delete(statement);
        }

        @Test
        @DisplayName("Should throw exception when statement not found")
        void shouldThrowExceptionWhenStatementNotFound() {
            // Given
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> service.deleteStatement(statementId, userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Statement not found or does not belong to user");

            verify(storageService, never()).deleteFile(any());
            verify(statementRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should continue deletion even if file deletion fails")
        void shouldContinueDeletionEvenIfFileDeletionFails() {
            // Given
            when(statementRepository.findByIdAndUserId(statementId, userId))
                    .thenReturn(Optional.of(statement));
            doThrow(new RuntimeException("Storage error"))
                    .when(storageService).deleteFile(statement.getSourceFileS3Key());
            doNothing().when(statementRepository).delete(statement);

            // When
            service.deleteStatement(statementId, userId);

            // Then
            verify(storageService).deleteFile(statement.getSourceFileS3Key());
            verify(statementRepository).delete(statement); // Still called despite file deletion error
        }
    }

    @Nested
    @DisplayName("Find By Date Range Tests")
    class FindByDateRangeTests {

        @Test
        @DisplayName("Should find statements by date range")
        void shouldFindStatementsByDateRange() {
            // Given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            when(statementRepository.findByUserIdAndPeriodBetween(userId, startDate, endDate))
                    .thenReturn(List.of(statement));
            when(mapper.toDtoList(List.of(statement))).thenReturn(List.of(statementDTO));

            // When
            List<DividendStatementDto> result = service.findByDateRange(userId, startDate, endDate);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(statementId);
            verify(statementRepository).findByUserIdAndPeriodBetween(userId, startDate, endDate);
        }
    }

    @Nested
    @DisplayName("Count By Status Tests")
    class CountByStatusTests {

        @Test
        @DisplayName("Should count statements by status")
        void shouldCountStatementsByStatus() {
            // Given
            when(statementRepository.countByUserIdAndStatus(userId, DividendStatementStatus.UPLOADED))
                    .thenReturn(3L);

            // When
            long count = service.countByStatus(userId, DividendStatementStatus.UPLOADED);

            // Then
            assertThat(count).isEqualTo(3L);
            verify(statementRepository).countByUserIdAndStatus(userId, DividendStatementStatus.UPLOADED);
        }
    }
}
