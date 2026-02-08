package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.DividendsApi;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.DividendService;
import com.taxdividend.backend.service.TaxCalculationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.taxdividend.backend.api.dto.BulkImportDividendsResponseDto;
import com.taxdividend.backend.api.dto.PaginatedDividendListDto;
import com.taxdividend.backend.api.dto.TaxCalculationBatchResultDto;
import com.taxdividend.backend.api.dto.DividendDto;
import com.taxdividend.backend.api.dto.DividendStatsDto;
import com.taxdividend.backend.api.dto.TaxCalculationResultDto;
import com.taxdividend.backend.security.UserContextHolder;

/**
 * REST Controller for dividend-related operations.
 *
 * Endpoints:
 * - GET /internal/dividends - List user's dividends (with optional filters:
 * startDate, endDate, status)
 * - GET /internal/dividends/{id} - Get dividend details
 * - POST /internal/dividends/{id}/calculate - Calculate tax for single dividend
 * - POST /internal/dividends/calculate-batch - Calculate tax for multiple
 * dividends
 * - POST /internal/dividends/{userId}/calculate-all - Calculate all user's
 * dividends
 * - DELETE /internal/dividends/{id} - Delete dividend
 *
 * Note: PDF parsing is now handled by an AI agent (not in this controller)
 */
@Slf4j
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Tag(name = "Dividends", description = "Dividend tax calculation and management API")
public class DividendController implements DividendsApi {

    private final TaxCalculationService taxCalculationService;
    private final DividendService dividendService;
    private final AuditService auditService;
    // Removed direct repository and mapper dependencies where service handles it

    /**
     * Get all dividends for the authenticated user with optional filters.
     */
    @Override
    public ResponseEntity<PaginatedDividendListDto> listDividends(
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate,
            String status) {

        UUID userId = UserContextHolder.get().userId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));

        // Delegate to service with filters
        return ResponseEntity.ok(dividendService.listDividends(userId, pageable, startDate, endDate, status));
    }

    /**
     * Get dividend by ID.
     */
    @Override
    public ResponseEntity<DividendDto> getDividend(UUID id) {
        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();
        return dividendService.getDividend(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Calculate tax for a single dividend.
     */
    @Override
    public ResponseEntity<TaxCalculationResultDto> calculateTax(
            UUID id,
            String residenceCountry) {

        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();

        return dividendService.getDividend(id, userId)
                .map(dividend -> {
                    String country = residenceCountry != null ? residenceCountry : "CH";
                    TaxCalculationResultDto result = taxCalculationService.calculateAndUpdate(id, country);
                    return ResponseEntity.ok(result);
                })
                .orElseThrow(() -> new java.util.NoSuchElementException("Dividend not found or access denied"));
    }

    /**
     * Calculate tax for multiple dividends.
     */
    @Override
    public ResponseEntity<TaxCalculationBatchResultDto> calculateBatch(List<UUID> dividendIds) {
        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();

        TaxCalculationBatchResultDto result = taxCalculationService.calculateBatchByIds(dividendIds, userId);

        if (result.getSuccessCount() == 0) {
            return ResponseEntity.badRequest().build();
        }

        // Audit log
        auditService.logTaxCalculation(userId, result.getSuccessCount(),
                result.getTotalReclaimableAmount().toString());

        log.info("Batch calculation completed: {} dividends, {} total reclaimable",
                result.getSuccessCount(), result.getTotalReclaimableAmount());

        return ResponseEntity.ok(result);
    }

    /**
     * Calculate tax for all user's dividends (OpenAPI implementation).
     */
    @Override
    public ResponseEntity<TaxCalculationBatchResultDto> calculateAllUserDividends(UUID userIdPath) {
        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();

        // Verify user can only calculate their own dividends
        if (!userIdPath.equals(userId)) {
            log.warn("User {} attempted to calculate dividends for user {}", userId, userIdPath);
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only calculate dividends for yourself");
        }

        TaxCalculationBatchResultDto result = taxCalculationService.calculateAndUpdateForUser(userId);

        // Audit log
        auditService.logTaxCalculation(userId, result.getSuccessCount(),
                result.getTotalReclaimableAmount().toString());

        log.info("Calculated all dividends for user {}: {} total reclaimable",
                userId, result.getTotalReclaimableAmount());

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<DividendStatsDto> getDividendStats(Integer taxYear) {
        // Get userId from UserContext (set by InternalApiKeyFilter)
        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();
        return ResponseEntity.ok(dividendService.getStats(userId, taxYear));
    }

    /**
     * Delete a dividend.
     */
    @Override
    public ResponseEntity<Void> deleteDividend(UUID id) {
        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();
        dividendService.deleteDividend(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bulk import dividends from parsed statement.
     * Called by AI Agent after parsing a broker statement.
     */
    @Override
    public ResponseEntity<BulkImportDividendsResponseDto> bulkImportDividends(
            com.taxdividend.backend.api.dto.BulkImportDividendsRequestDto request) {

        UUID userId = com.taxdividend.backend.security.UserContextHolder.get().userId();
        log.info("Bulk import request from user {} for statement {}", userId, request.getStatementId());

        com.taxdividend.backend.api.dto.BulkImportDividendsResponseDto response = dividendService
                .bulkImportDividends(userId, request);

        auditService.logAction(userId, "BULK_IMPORT_DIVIDENDS");

        return ResponseEntity.ok(response);
    }
}
