package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.BulkImportDividendItemDto;
import com.taxdividend.backend.api.dto.BulkImportDividendsRequestDto;
import com.taxdividend.backend.api.dto.BulkImportDividendsResponseDto;
import com.taxdividend.backend.api.dto.DividendDto;
import com.taxdividend.backend.api.dto.DividendStatsDto;
import com.taxdividend.backend.api.dto.PaginatedDividendListDto;
import com.taxdividend.backend.api.dto.TaxCalculationResultDto;
import com.taxdividend.backend.mapper.DividendMapper;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.DividendStatement;
import com.taxdividend.backend.model.DividendStatus;
import com.taxdividend.backend.model.User;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.repository.DividendStatementRepository;
import com.taxdividend.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DividendService {

    private final DividendRepository dividendRepository;
    private final DividendStatementRepository statementRepository;
    private final UserRepository userRepository;
    private final DividendMapper dividendMapper;
    private final AuditService auditService;
    private final TaxCalculationService taxCalculationService;
    private final DividendStatementService statementService;

    public PaginatedDividendListDto listDividends(UUID userId, Pageable pageable,
            LocalDate startDate,
            LocalDate endDate, String status) {
        List<Dividend> dividends;

        // Apply filters based on provided parameters
        if (startDate != null && endDate != null) {
            // Date range filter
            dividends = dividendRepository.findByUserIdAndPaymentDateBetween(userId, startDate, endDate);
        } else if ("UNSUBMITTED".equalsIgnoreCase(status)) {
            // Unsubmitted filter (dividends not linked to a form)
            dividends = dividendRepository.findByUserIdAndFormIsNull(userId);
        } else {
            // No filters, get all dividends for user
            dividends = dividendRepository.findByUserId(userId);
        }

        // Note: Status filtering for SUBMITTED, APPROVED, PAID would require additional
        // logic
        // based on form_submissions table, which is not yet implemented

        Page<Dividend> dividendPage = PageableExecutionUtils.getPage(dividends, pageable, dividends::size);

        PaginatedDividendListDto result = dividendMapper.toPageResponse(dividendPage);

        return result;
    }

    public Optional<DividendDto> getDividend(UUID id, UUID userId) {
        return dividendRepository.findById(id)
                .filter(dividend -> dividend.getUser().getId().equals(userId))
                .map(dividendMapper::toDto);
    }

    public List<DividendDto> getDividendsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return dividendRepository.findByUserIdAndPaymentDateBetween(userId, startDate, endDate)
                .stream()
                .map(dividendMapper::toDto)
                .toList();
    }

    public List<DividendDto> getUnsubmittedDividends(UUID userId) {
        return dividendRepository.findByUserIdAndFormIsNull(userId)
                .stream()
                .map(dividendMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteDividend(UUID id, UUID userId) {
        dividendRepository.findById(id)
                .filter(dividend -> dividend.getUser().getId().equals(userId))
                .ifPresent(dividend -> {
                    dividendRepository.delete(dividend);
                    log.info("Deleted dividend {} for user {}", id, userId);
                    auditService.logAction(userId, "DIVIDEND_DELETED", "DIVIDEND", id, null, null, null);
                });
    }

    public DividendStatsDto getStats(UUID userId, Integer taxYear) {
        BigDecimal totalReclaimed;
        BigDecimal pendingAmount;
        long casesCount;

        if (taxYear == null) {
            // Global stats
            totalReclaimed = dividendRepository.sumReclaimableByStatus(userId, DividendStatus.PAID);
            pendingAmount = dividendRepository.sumReclaimableByStatuses(userId,
                    Arrays.asList(DividendStatus.OPEN, DividendStatus.SENT));
            casesCount = dividendRepository.countByUserId(userId);
        } else {
            // Stats filtered by year
            totalReclaimed = dividendRepository.sumReclaimableByStatusAndYear(
                    userId,
                    Arrays.asList(DividendStatus.PAID),
                    taxYear);
            pendingAmount = dividendRepository.sumReclaimableByStatusAndYear(
                    userId,
                    Arrays.asList(DividendStatus.OPEN, DividendStatus.SENT),
                    taxYear);
            casesCount = dividendRepository.countByUserIdAndYear(userId, taxYear);
        }

        DividendStatsDto stats = new DividendStatsDto();
        stats.setTotalReclaimed(totalReclaimed != null ? totalReclaimed : BigDecimal.ZERO);
        stats.setPendingAmount(pendingAmount != null ? pendingAmount : BigDecimal.ZERO);
        stats.setCasesCount((int) casesCount);

        return stats;
    }

    @Transactional
    public BulkImportDividendsResponseDto bulkImportDividends(UUID userId, BulkImportDividendsRequestDto request) {
        log.info("Bulk importing {} dividends for user {} from statement {}",
                request.getDividends().size(), userId, request.getStatementId());

        // Validate statement exists and belongs to user
        DividendStatement statement = statementRepository
                .findByIdAndUserId(request.getStatementId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Statement not found or does not belong to user: " + request.getStatementId()));

        // Update broker if provided by agent (addressing TODO)
        // Agent detects broker from file content, which might be more accurate or
        // specific than user input
        if (request.getBroker() != null && !request.getBroker().isBlank()) {
            statement.setBroker(request.getBroker());
            statementRepository.save(statement);
            log.info("Updated statement {} broker to {}", statement.getId(), request.getBroker());
        }

        // Get user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<UUID> importedIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalReclaimable = BigDecimal.ZERO;
        int successCount = 0;

        // Process each dividend
        for (BulkImportDividendItemDto item : request.getDividends()) {
            try {
                // Create dividend entity
                Dividend dividend = Dividend.builder()
                        .user(user)
                        .statement(statement)
                        .securityName(item.getSecurityName())
                        .isin(item.getIsin())
                        .paymentDate(item.getPaymentDate())
                        .grossAmount(item.getGrossAmount())
                        .currency(item.getCurrency())
                        .withholdingTax(item.getWithholdingTax())
                        .withholdingRate(item.getWithholdingRate())
                        .sourceCountry(item.getSourceCountry())
                        .status(DividendStatus.OPEN)
                        .build();

                // Calculate tax to get reclaimable amount and treaty rate
                TaxCalculationResultDto taxResult = taxCalculationService.calculateForDividend(dividend,
                        user.getCountry());

                // Set calculated values
                dividend.setReclaimableAmount(taxResult.getReclaimableAmount());
                dividend.setTreatyRate(taxResult.getTreatyRate());

                // Save dividend
                dividend = dividendRepository.save(dividend);
                importedIds.add(dividend.getId());

                totalGross = totalGross.add(item.getGrossAmount());
                totalReclaimable = totalReclaimable.add(taxResult.getReclaimableAmount());
                successCount++;

                log.debug("Imported dividend {} - {} ({})", dividend.getId(),
                        item.getSecurityName(), item.getIsin());

            } catch (Exception e) {
                log.error("Failed to import dividend: {} - {}", item.getIsin(), e.getMessage());
                errors.add(String.format("%s (%s): %s", item.getSecurityName(), item.getIsin(), e.getMessage()));
            }
        }

        // Update statement metadata
        statementService.updateAfterParsing(
                request.getStatementId(),
                successCount,
                totalGross,
                totalReclaimable);

        log.info("Bulk import completed: {} success, {} failures, total gross: {}, total reclaimable: {}",
                successCount, errors.size(), totalGross, totalReclaimable);

        // Build response
        BulkImportDividendsResponseDto response = new BulkImportDividendsResponseDto();
        response.setImportedCount(successCount);
        response.setFailedCount(errors.size());
        response.setTotalGrossAmount(totalGross);
        response.setTotalReclaimable(totalReclaimable);
        response.setDividendIds(importedIds);
        response.setErrors(errors);

        return response;
    }
}
