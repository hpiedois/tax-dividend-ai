package com.taxdividend.backend.service.impl;

import com.taxdividend.backend.api.dto.Dividend;
import com.taxdividend.backend.api.dto.ListDividends200Response;
import com.taxdividend.backend.mapper.DividendMapper;
import com.taxdividend.backend.repository.DividendRepository;
import com.taxdividend.backend.service.AuditService;
import com.taxdividend.backend.service.DividendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DividendServiceImpl implements DividendService {

    private final DividendRepository dividendRepository;
    private final DividendMapper dividendMapper;
    private final AuditService auditService;

    @Override
    public ListDividends200Response listDividends(UUID userId, Pageable pageable) {
        List<com.taxdividend.backend.model.Dividend> dividends = dividendRepository.findByUserId(userId);
        // Note: Repository returns List, but we need Page for mapper.
        // Assuming we want to support actual pagination later, but for now logic was:
        // List<Dividend> dividends = dividendRepository.findByUserId(xUserId);
        // Page<Dividend> dividendPage = Page.empty(pageable);
        // wait, the original code had:
        // List<Dividend> dividends = dividendRepository.findByUserId(xUserId);
        // Page<Dividend> dividendPage = Page.empty(pageable);
        // return ResponseEntity.ok(dividendMapper.toPageResponse(dividendPage));
        // It seems the original code was BROKEN or placeholder regarding listing. It
        // fetched list but returned empty page?
        // Ah: Page<Dividend> dividendPage = Page.empty(pageable);
        // It seems it was returning empty page.
        // I will attempt to fix this to return actual data if possible, or replicate
        // behavior.
        // Let's replicate behavior but map the list if the mapper supports it, or just
        // use the logic from controller.

        // Actually, let's just return what the controller was doing but correctly.
        // The current implementation seemed to ignore the fetched dividends and return
        // empty page?
        // "List<Dividend> dividends = dividendRepository.findByUserId(xUserId);"
        // "Page<Dividend> dividendPage = Page.empty(pageable);"
        // "return ResponseEntity.ok(dividendMapper.toPageResponse(dividendPage));"

        // Use PageImpl to wrap the list (suboptimal for large data but matches current
        // repo method)
        // Or if repo supports Pageable... it likely extends JpaRepository which does.
        // But findByUserId returns List.

        Page<com.taxdividend.backend.model.Dividend> dividendPage = org.springframework.data.support.PageableExecutionUtils
                .getPage(
                        dividends, pageable, dividends::size);

        return dividendMapper.toPageResponse(dividendPage);
    }

    @Override
    public Optional<Dividend> getDividend(UUID id, UUID userId) {
        return dividendRepository.findById(id)
                .filter(dividend -> dividend.getUser().getId().equals(userId))
                .map(dividendMapper::toDto);
    }

    @Override
    public List<Dividend> getDividendsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return dividendRepository.findByUserIdAndPaymentDateBetween(userId, startDate, endDate)
                .stream()
                .map(dividendMapper::toDto)
                .toList();
    }

    @Override
    public List<Dividend> getUnsubmittedDividends(UUID userId) {
        return dividendRepository.findByUserIdAndFormIsNull(userId)
                .stream()
                .map(dividendMapper::toDto)
                .toList();
    }

    @Override
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
}
