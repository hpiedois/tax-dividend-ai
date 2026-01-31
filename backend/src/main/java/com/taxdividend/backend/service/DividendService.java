package com.taxdividend.backend.service;

import com.taxdividend.backend.api.dto.Dividend;
import com.taxdividend.backend.api.dto.ListDividends200Response;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DividendService {
    ListDividends200Response listDividends(UUID userId, Pageable pageable);

    Optional<Dividend> getDividend(UUID id, UUID userId);

    List<Dividend> getDividendsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    List<Dividend> getUnsubmittedDividends(UUID userId);

    void deleteDividend(UUID id, UUID userId);
}
