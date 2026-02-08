package com.taxdividend.bff.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.taxdividend.bff.api.DividendsApi;
import com.taxdividend.bff.model.DividendHistoryResponseDto;
import com.taxdividend.bff.model.DividendStatsDto;
import com.taxdividend.bff.model.DividendStatementDto;
import com.taxdividend.bff.service.DividendService;

import reactor.core.publisher.Mono;

import org.springframework.http.codec.multipart.Part;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DividendController implements DividendsApi {

    private final DividendService dividendService;

    @Override
    public Mono<ResponseEntity<DividendStatsDto>> getDividendStats(Integer taxYear, ServerWebExchange exchange) {
        return dividendService.getDividendStats(taxYear)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(new DividendStatsDto()));
    }

    @Override
    public Mono<ResponseEntity<DividendHistoryResponseDto>> getDividendHistory(Integer page, Integer pageSize,
            ServerWebExchange exchange) {
        return dividendService.getDividendHistory(page, pageSize)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<DividendStatementDto>> parseDividendStatement(Part file, ServerWebExchange exchange) {
        return dividendService.parseDividendStatement(file)
                .map(ResponseEntity::ok);
    }

}
