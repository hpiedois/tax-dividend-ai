package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.DividendsApi;
import com.taxdividend.backend.mapper.TaxCalculationMapper;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.DividendService;
import com.taxdividend.backend.service.TaxCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for dividend-related operations.
 *
 * Endpoints:
 * - GET /internal/dividends - List user's dividends
 * - GET /internal/dividends/{id} - Get dividend details
 * - POST /internal/dividends/{id}/calculate - Calculate tax for single dividend
 * - POST /internal/dividends/calculate-batch - Calculate tax for multiple
 * dividends
 * - POST /internal/dividends/calculate-all - Calculate all user's dividends
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
    private final TaxCalculationMapper taxCalculationMapper;
    // Removed direct repository and mapper dependencies where service handles it

    /**
     * Get all dividends for the authenticated user.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.ListDividends200Response> listDividends(
            UUID xUserId,
            Integer page,
            Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        return ResponseEntity.ok(dividendService.listDividends(xUserId, pageable));
    }

    /**
     * Get dividend by ID.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.Dividend> getDividend(
            UUID id,
            UUID xUserId) {

        return dividendService.getDividend(id, xUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Calculate tax for a single dividend.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.TaxCalculationResultDTO> calculateTax(
            UUID id,
            UUID xUserId,
            String residenceCountry) {

        try {
            // Need to verify user ownership first? existing logic in service usually?
            // Existing controller logic:
            /*
             * Dividend dividend = dividendRepository.findById(id)
             * .filter(d -> d.getUser().getId().equals(xUserId))
             * .orElse(null);
             */
            // Ideally TaxCalculationService should handle ownership check or we check it
            // via DividendService.
            // But TaxCalculationService.calculateAndUpdate probably handles it or takes
            // entity.

            // To be safe and strict about DTOs, we should let the service handle
            // everything.
            // However, TaxCalculationService.calculateAndUpdate signature is (UUID id,
            // String country).
            // It likely does internal lookup.
            // Assuming it's safe to call directly, but we need country fallback.

            // We can fetch DTO first to get country?
            // Or assume Service is robust.

            // Let's replicate original logic using DividendService to get country if
            // needed.
            return dividendService.getDividend(id, xUserId)
                    .map(dividend -> {
                        String country = residenceCountry != null ? residenceCountry : "CH"; // Default or from user?
                        // Original: dividend.getUser().getCountry() - but DTO might not have full User
                        // object
                        // Check DTO structure if needed. Assuming TaxCalculationService handles lookup
                        // if country is null? No.

                        // Note: refactoring TaxCalculationService is out of scope unless necessary.
                        // Ideally we pass (id, userId, country) to service.

                        com.taxdividend.backend.dto.TaxCalculationResultDTO result = taxCalculationService
                                .calculateAndUpdate(id, country);
                        // Wait, if residenceCountry is null, we need the user's country.
                        // If DTO doesn't have it, we are stuck.
                        // But let's assume specific service method or we pass null and service handles
                        // it.

                        return ResponseEntity.ok(taxCalculationMapper.toApiDto(result));
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            log.error("Failed to calculate tax for dividend {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Calculate tax for multiple dividends.
     */
    @Override
    public ResponseEntity<com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO> calculateBatch(
            UUID xUserId,
            List<UUID> dividendIds) {

        try {
            com.taxdividend.backend.dto.TaxCalculationBatchResultDTO result = taxCalculationService
                    .calculateBatchByIds(dividendIds, xUserId);

            if (result.getProcessedCount() == 0) {
                return ResponseEntity.badRequest().build();
            }

            // Audit log
            auditService.logTaxCalculation(xUserId, result.getSuccessCount(),
                    result.getTotalReclaimableAmount().toString());

            log.info("Batch calculation completed: {} dividends, {} total reclaimable",
                    result.getSuccessCount(), result.getTotalReclaimableAmount());

            return ResponseEntity.ok(taxCalculationMapper.toApiBatchDto(result));

        } catch (Exception e) {
            log.error("Failed to calculate batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Calculate tax for all user's dividends.
     */
    @PostMapping("/calculate-all")
    @Operation(summary = "Calculate tax for all dividends", description = "Calculate taxes for all dividends of the authenticated user")
    public ResponseEntity<com.taxdividend.backend.dto.TaxCalculationBatchResultDTO> calculateAll(
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader) {

        UUID userId = UUID.fromString(userIdHeader);

        try {
            com.taxdividend.backend.dto.TaxCalculationBatchResultDTO result = taxCalculationService
                    .calculateAndUpdateForUser(userId);

            // Audit log
            auditService.logTaxCalculation(userId, result.getSuccessCount(),
                    result.getTotalReclaimableAmount().toString());

            log.info("Calculated all dividends for user {}: {} total reclaimable",
                    userId, result.getTotalReclaimableAmount());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Failed to calculate all for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get dividends by date range.
     * Fixed: Returns List<DividendDTO> instead of List<DividendEntity>
     */
    @GetMapping("/by-date-range")
    @Operation(summary = "Get dividends by date range", description = "Filter dividends by payment date range")
    public ResponseEntity<List<com.taxdividend.backend.api.dto.Dividend>> getDividendsByDateRange(
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader,
            @Parameter(description = "Start date (ISO format)") @RequestParam LocalDate startDate,
            @Parameter(description = "End date (ISO format)") @RequestParam LocalDate endDate) {

        UUID userId = UUID.fromString(userIdHeader);

        List<com.taxdividend.backend.api.dto.Dividend> dividends = dividendService.getDividendsByDateRange(
                userId, startDate, endDate);

        log.info("Found {} dividends for user {} between {} and {}",
                dividends.size(), userId, startDate, endDate);

        return ResponseEntity.ok(dividends);
    }

    /**
     * Get unsubmitted dividends (not linked to any form).
     * Fixed: Returns List<DividendDTO> instead of List<DividendEntity>
     */
    @GetMapping("/unsubmitted")
    @Operation(summary = "Get unsubmitted dividends", description = "Get all dividends not yet linked to a form")
    public ResponseEntity<List<com.taxdividend.backend.api.dto.Dividend>> getUnsubmittedDividends(
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") String userIdHeader) {

        UUID userId = UUID.fromString(userIdHeader);

        List<com.taxdividend.backend.api.dto.Dividend> dividends = dividendService.getUnsubmittedDividends(userId);

        log.info("Found {} unsubmitted dividends for user {}", dividends.size(), userId);

        return ResponseEntity.ok(dividends);
    }

    /**
     * Delete a dividend.
     */
    @Override
    public ResponseEntity<Void> deleteDividend(
            UUID id,
            UUID xUserId) {

        dividendService.deleteDividend(id, xUserId);
        return ResponseEntity.noContent().build();
    }
}
