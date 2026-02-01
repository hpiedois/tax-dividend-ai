package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.BulkImportDividendsRequest;
import com.taxdividend.backend.api.dto.BulkImportDividendsResponse;
import com.taxdividend.backend.api.dto.Dividend;
import com.taxdividend.backend.api.dto.ListDividends200Response;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DividendService {
    ListDividends200Response listDividends(UUID userId, Pageable pageable, LocalDate startDate, LocalDate endDate,
            String status);

    Optional<Dividend> getDividend(UUID id, UUID userId);

    // Legacy methods - can be removed if not used elsewhere
    List<Dividend> getDividendsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    List<Dividend> getUnsubmittedDividends(UUID userId);

    void deleteDividend(UUID id, UUID userId);

    com.taxdividend.backend.api.dto.DividendStatsDTO getStats(UUID userId, Integer taxYear);

    /**
     * Bulk import dividends from a parsed broker statement.
     * Called by AI Agent after parsing.
     * Automatically calculates tax and updates statement metadata.
     *
     * @param userId User ID for ownership validation
     * @param request Bulk import request with statement ID and dividends
     * @return Response with import results
     */
    BulkImportDividendsResponse bulkImportDividends(UUID userId, BulkImportDividendsRequest request);
}
