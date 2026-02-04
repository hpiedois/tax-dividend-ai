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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of DividendStatementService.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DividendStatementService {

        private final DividendStatementRepository statementRepository;
        private final UserRepository userRepository;
        private final StorageService storageService;
        private final DividendStatementMapper mapper;

        @Transactional
        public DividendStatementDto uploadStatement(
                        MultipartFile file,
                        UUID userId,
                        String broker,
                        LocalDate periodStart,
                        LocalDate periodEnd) {

                log.info("Uploading statement for user {}, broker {}, period {}-{}",
                                userId, broker, periodStart, periodEnd);

                // Validate user exists
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

                // Validate file
                if (file.isEmpty()) {
                        throw new IllegalArgumentException("File is empty");
                }

                // Validate dates
                if (periodEnd.isBefore(periodStart)) {
                        throw new IllegalArgumentException("Period end must be after period start");
                }

                // Upload file to storage
                FileUploadResultDTO uploadResult = storageService.uploadFile(file, "statements");
                if (!uploadResult.getSuccess()) {
                        throw new StorageException(
                                        "Failed to upload statement file: " + uploadResult.getErrorMessage());
                }

                // Create statement entity
                DividendStatement statement = DividendStatement.builder()
                                .user(user)
                                .sourceFileName(file.getOriginalFilename())
                                .sourceFileS3Key(uploadResult.getS3Key())
                                .broker(broker)
                                .periodStart(periodStart)
                                .periodEnd(periodEnd)
                                .status(DividendStatementStatus.UPLOADED)
                                .dividendCount(0)
                                .totalGrossAmount(BigDecimal.ZERO)
                                .totalReclaimable(BigDecimal.ZERO)
                                .build();

                statement = statementRepository.save(statement);

                log.info("Statement uploaded successfully: {}", statement.getId());

                return mapper.toDto(statement);
        }

        @Transactional(readOnly = true)
        public Optional<DividendStatementDto> getStatement(UUID id, UUID userId) {
                return statementRepository.findByIdAndUserId(id, userId)
                                .map(mapper::toDto);
        }

        @Transactional(readOnly = true)
        public Page<DividendStatementDto> listStatements(UUID userId, DividendStatementStatus status,
                        Pageable pageable) {
                Page<DividendStatement> statements;

                if (status != null) {
                        statements = statementRepository.findByUserIdAndStatus(userId, status, pageable);
                } else {
                        statements = statementRepository.findByUserId(userId, pageable);
                }

                return mapper.toDtoPage(statements);
        }

        @Transactional
        public DividendStatementDto updateStatus(UUID id, UUID userId, DividendStatementUpdateDto updateDTO) {
                log.info("Updating statement {} status to {} for user {}", id, updateDTO.getStatus(), userId);

                // Find and validate ownership
                DividendStatement statement = statementRepository.findByIdAndUserId(id, userId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Statement not found or does not belong to user: " + id));

                // Convert API DTO status to Internal Enum
                // Assuming API Enum names match Internal Enum names
                DividendStatementStatus newStatus = DividendStatementStatus.valueOf(updateDTO.getStatus().name());

                // Validate transition
                statement.validateTransition(newStatus);

                // Update status and corresponding fields
                statement.updateStatus(newStatus);

                // Set additional fields based on status
                switch (newStatus) {
                        case PARSING:
                                if (updateDTO.getParsedBy() != null) {
                                        statement.setParsedBy(updateDTO.getParsedBy());
                                }
                                break;

                        case SENT:
                                if (updateDTO.getSentMethod() != null) {
                                        statement.setSentMethod(updateDTO.getSentMethod());
                                }
                                if (updateDTO.getSentNotes() != null) {
                                        statement.setSentNotes(updateDTO.getSentNotes());
                                }
                                break;

                        case PAID:
                                if (updateDTO.getPaidAmount() != null) {
                                        statement.setPaidAmount(updateDTO.getPaidAmount());
                                }
                                if (updateDTO.getPaidAt() != null) {
                                        statement.setPaidAt(updateDTO.getPaidAt().toLocalDateTime());
                                }
                                break;

                        default:
                                // PARSED, VALIDATED - no additional fields
                                break;
                }

                statement = statementRepository.save(statement);

                log.info("Statement {} status updated to {}", id, newStatus);

                return mapper.toDto(statement);
        }

        @Transactional
        public void updateAfterParsing(UUID id, int dividendCount, BigDecimal totalGross, BigDecimal totalReclaimable) {
                log.info("Updating statement {} after parsing: {} dividends, total gross: {}, total reclaimable: {}",
                                id, dividendCount, totalGross, totalReclaimable);

                DividendStatement statement = statementRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Statement not found: " + id));

                statement.setDividendCount(dividendCount);
                statement.setTotalGrossAmount(totalGross);
                statement.setTotalReclaimable(totalReclaimable);

                statementRepository.save(statement);
        }

        @Transactional
        public void deleteStatement(UUID id, UUID userId) {
                log.info("Deleting statement {} for user {}", id, userId);

                DividendStatement statement = statementRepository.findByIdAndUserId(id, userId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Statement not found or does not belong to user: " + id));

                // Delete file from storage
                try {
                        storageService.deleteFile(statement.getSourceFileS3Key());
                } catch (Exception e) {
                        log.warn("Failed to delete statement file from storage: {}", statement.getSourceFileS3Key(), e);
                        // Continue with entity deletion even if file deletion fails
                }

                // Delete entity
                statementRepository.delete(statement);

                log.info("Statement {} deleted successfully", id);
        }

        @Transactional(readOnly = true)
        public List<DividendStatementDto> findByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
                List<DividendStatement> statements = statementRepository.findByUserIdAndPeriodBetween(
                                userId, startDate, endDate);
                return mapper.toDtoList(statements);
        }

        @Transactional(readOnly = true)
        public long countByStatus(UUID userId, DividendStatementStatus status) {
                return statementRepository.countByUserIdAndStatus(userId, status);
        }
}
