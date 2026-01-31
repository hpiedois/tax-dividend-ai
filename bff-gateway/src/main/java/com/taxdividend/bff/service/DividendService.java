package com.taxdividend.bff.service;

import com.taxdividend.bff.client.api.DividendsApi;
import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.mapper.DividendMapper;
import com.taxdividend.bff.model.DividendCase;
import com.taxdividend.bff.model.DividendData;
import com.taxdividend.bff.model.DividendHistoryResponse;
import com.taxdividend.bff.model.DividendStats;
import com.taxdividend.bff.model.ParsePDFResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import com.taxdividend.bff.client.MockAgentClient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DividendService {

    private final DividendsApi dividendsApi;
    private final DividendMapper dividendMapper;
    private final MockAgentClient mockAgentClient;

    public Mono<DividendStats> getDividendStats() {
        log.debug("Fetching dividend stats");
        // Limitation: Backend doesn't have a stats endpoint yet.
        // We fetch a larger list to aggregate (MVP approach).
        // In production, backend should have /dividends/stats.
        return dividendsApi.listDividends(UUID.randomUUID(), 0, 1000)
                .map(response -> {
                    List<Dividend> list = response.getContent();
                    if (list == null)
                        list = Collections.emptyList();

                    BigDecimal totalReclaimed = list.stream()
                            .map(d -> d.getReclaimableAmount() != null ? d.getReclaimableAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal pendingAmount = totalReclaimed;

                    DividendStats stats = new DividendStats();
                    stats.setTotalReclaimed(BigDecimal.ZERO);
                    stats.setPendingAmount(pendingAmount);
                    stats.setCasesCount(list.size());
                    return stats;
                });
    }

    public Mono<DividendHistoryResponse> getDividendHistory(Integer page, Integer pageSize) {
        return dividendsApi.listDividends(UUID.randomUUID(), page, pageSize)
                .map(response -> {
                    List<DividendCase> cases = response.getContent().stream()
                            .map(dividendMapper::toDividendCase)
                            .collect(Collectors.toList());

                    DividendHistoryResponse history = new DividendHistoryResponse();
                    history.setData(cases);
                    history.setPage(page);
                    history.setPageSize(pageSize);
                    history.setTotal(cases.size());
                    return history;
                });
    }

    public Mono<ParsePDFResponse> parseDividendPDF(Part file) {
        log.info("Parsing PDF via Mock Agent");
        return mockAgentClient.parsePdf(file);
    }
}
