package com.taxdividend.bff.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.taxdividend.bff.api.DividendsApi;
import com.taxdividend.bff.model.DividendHistoryResponse;
import com.taxdividend.bff.model.DividendStats;
import com.taxdividend.bff.model.ParseStatementResponse;
import com.taxdividend.bff.service.DividendService;

import reactor.core.publisher.Mono;

import org.springframework.http.codec.multipart.Part;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DividendController implements DividendsApi {

    private final DividendService dividendService;

    @Override
    public Mono<ResponseEntity<DividendStats>> getDividendStats(Integer taxYear, ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                .flatMap(userId -> dividendService.getDividendStats(userId, taxYear))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(new DividendStats()));
    }

    @Override
    public Mono<ResponseEntity<DividendHistoryResponse>> getDividendHistory(Integer page, Integer pageSize,
            ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                .flatMap(userId -> dividendService.getDividendHistory(userId, page, pageSize))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ParseStatementResponse>> parseDividendStatement(Part file, ServerWebExchange exchange) {
        return dividendService.parseDividendStatement(file)
                .map(ResponseEntity::ok);
    }
}
