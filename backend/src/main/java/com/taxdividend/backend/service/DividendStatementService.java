package com.taxdividend.backend.service;

import com.taxdividend.backend.dto.DividendStatementDTO;
import com.taxdividend.backend.dto.DividendStatementUpdateDTO;
import com.taxdividend.backend.model.DividendStatementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing dividend statements (broker statements).
 */
public interface DividendStatementService {

    /**
     * Upload a new broker statement file.
     * Creates a new DividendStatement with status UPLOADED.
     *
     * @param file The broker statement file (PDF/CSV)
     * @param userId User ID
     * @param broker Broker name (InteractiveBrokers, Swissquote, etc.)
     * @param periodStart Start of period covered by statement
     * @param periodEnd End of period covered by statement
     * @return Created statement DTO
     */
    DividendStatementDTO uploadStatement(
            MultipartFile file,
            UUID userId,
            String broker,
            LocalDate periodStart,
            LocalDate periodEnd
    );

    /**
     * Get statement by ID.
     * Validates that the statement belongs to the user.
     *
     * @param id Statement ID
     * @param userId User ID (for ownership validation)
     * @return Statement DTO if found and owned by user
     */
    Optional<DividendStatementDTO> getStatement(UUID id, UUID userId);

    /**
     * List user's statements with pagination.
     *
     * @param userId User ID
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of statements
     */
    Page<DividendStatementDTO> listStatements(UUID userId, DividendStatementStatus status, Pageable pageable);

    /**
     * Update statement status.
     * Validates status transition and sets corresponding timestamps.
     *
     * @param id Statement ID
     * @param userId User ID (for ownership validation)
     * @param updateDTO Update data (status, sent/paid info)
     * @return Updated statement DTO
     * @throws IllegalStateException if status transition is invalid
     * @throws IllegalArgumentException if statement not found or not owned by user
     */
    DividendStatementDTO updateStatus(UUID id, UUID userId, DividendStatementUpdateDTO updateDTO);

    /**
     * Update statement metadata after parsing.
     * Called by AI Agent after parsing dividends.
     *
     * @param id Statement ID
     * @param dividendCount Number of dividends extracted
     * @param totalGross Total gross amount
     * @param totalReclaimable Total reclaimable amount
     */
    void updateAfterParsing(UUID id, int dividendCount, java.math.BigDecimal totalGross, java.math.BigDecimal totalReclaimable);

    /**
     * Delete a statement.
     * Also deletes associated file from storage.
     *
     * @param id Statement ID
     * @param userId User ID (for ownership validation)
     */
    void deleteStatement(UUID id, UUID userId);

    /**
     * Find statements by date range.
     *
     * @param userId User ID
     * @param startDate Period start date
     * @param endDate Period end date
     * @return List of statements covering the date range
     */
    List<DividendStatementDTO> findByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Count statements by status for a user.
     *
     * @param userId User ID
     * @param status Status
     * @return Count
     */
    long countByStatus(UUID userId, DividendStatementStatus status);
}
