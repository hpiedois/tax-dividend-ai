package com.taxdividend.bff.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.taxdividend.bff.api.DividendsApi;
import com.taxdividend.bff.model.DividendCase;
import com.taxdividend.bff.model.DividendData;
import com.taxdividend.bff.model.DividendHistoryResponse;
import com.taxdividend.bff.model.DividendStats;
import com.taxdividend.bff.model.ParsePDFResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.codec.multipart.Part;

@RestController
public class DividendController implements DividendsApi {

    @Override
    public Mono<ResponseEntity<DividendStats>> getDividendStats(ServerWebExchange exchange) {
        DividendStats stats = new DividendStats();
        stats.setTotalReclaimed(BigDecimal.valueOf(231.00));
        stats.setPendingAmount(BigDecimal.valueOf(548.70));
        stats.setCasesCount(4);

        return Mono.just(ResponseEntity.ok(stats));
    }

    @Override
    public Mono<ResponseEntity<DividendHistoryResponse>> getDividendHistory(Integer page, Integer pageSize,
            ServerWebExchange exchange) {
        DividendCase case1 = new DividendCase();
        case1.setId("1");
        case1.setDate(LocalDate.of(2026, 5, 20)); // Match mock-db.ts
        case1.setSecurity("SANOFI");
        case1.setGrossAmount(BigDecimal.valueOf(1500));
        case1.setReclaimedAmount(BigDecimal.valueOf(177.00));
        case1.setStatus(DividendCase.StatusEnum.PENDING);

        DividendCase case2 = new DividendCase();
        case2.setId("2");
        case2.setDate(LocalDate.of(2025, 6, 12));
        case2.setSecurity("L'OREAL");
        case2.setGrossAmount(BigDecimal.valueOf(2100));
        case2.setReclaimedAmount(BigDecimal.valueOf(247.80));
        case2.setStatus(DividendCase.StatusEnum.SUBMITTED);

        DividendHistoryResponse response = new DividendHistoryResponse();
        response.setData(List.of(case1, case2));
        response.setTotal(6);
        response.setPage(page != null ? page : 0);
        response.setPageSize(pageSize != null ? pageSize : 10);

        return Mono.just(ResponseEntity.ok(response));
    }

    @Override
    public Mono<ResponseEntity<ParsePDFResponse>> parseDividendPDF(Flux<Part> file, ServerWebExchange exchange) {
        // Mock parsing logic
        DividendData data = new DividendData();
        data.setSecurityName("AIR LIQUIDE SA");
        data.setIsin("FR0000120073");
        data.setGrossAmount(BigDecimal.valueOf(1250.00));
        data.setCurrency("EUR");
        data.setPaymentDate(LocalDate.of(2024, 5, 15));
        data.setWithholdingTax(BigDecimal.valueOf(150.00)); // 12.8% usually but mock
        data.setReclaimableAmount(BigDecimal.valueOf(147.50));
        data.setFrenchRate(BigDecimal.valueOf(12.8));

        ParsePDFResponse response = new ParsePDFResponse();
        response.setData(data);

        return Mono.just(ResponseEntity.ok(response));
    }
}
