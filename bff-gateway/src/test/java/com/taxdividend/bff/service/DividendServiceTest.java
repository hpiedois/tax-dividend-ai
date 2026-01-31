package com.taxdividend.bff.service;

import com.taxdividend.bff.client.api.DividendsApi;
import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.client.model.ListDividends200Response;
import com.taxdividend.bff.mapper.DividendMapper;
import com.taxdividend.bff.model.DividendStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DividendServiceTest {

    @Mock
    private DividendsApi dividendsApi;

    @Mock
    private DividendMapper dividendMapper;

    @InjectMocks
    private DividendService dividendService;

    @Mock
    private com.taxdividend.bff.client.MockAgentClient mockAgentClient;

    @Test
    void parseDividendPDF_ShouldCallMockAgent() {
        // Arrange
        org.springframework.http.codec.multipart.Part filePart = org.mockito.Mockito
                .mock(org.springframework.http.codec.multipart.Part.class);
        com.taxdividend.bff.model.ParsePDFResponse expectedResponse = new com.taxdividend.bff.model.ParsePDFResponse();
        when(mockAgentClient.parsePdf(filePart)).thenReturn(Mono.just(expectedResponse));

        // Act
        Mono<com.taxdividend.bff.model.ParsePDFResponse> result = dividendService.parseDividendPDF(filePart);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getDividendStats_ShouldAggregateValues() {
        // Arrange
        Dividend dividend = new Dividend();
        dividend.setReclaimableAmount(new BigDecimal("100.00"));

        ListDividends200Response response = new ListDividends200Response();
        response.setContent(List.of(dividend));

        when(dividendsApi.listDividends(any(), any(), any())).thenReturn(Mono.just(response));

        // Act
        Mono<DividendStats> result = dividendService.getDividendStats();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(stats -> stats.getTotalReclaimed().compareTo(BigDecimal.ZERO) == 0 && // Logic sets
                                                                                                         // totalReclaimed
                                                                                                         // to 0
                                                                                                         // currently
                        stats.getPendingAmount().compareTo(new BigDecimal("100.00")) == 0 &&
                        stats.getCasesCount() == 1)
                .verifyComplete();
    }

    @Test
    void getDividendStats_ShouldHandleEmptyList() {
        // Arrange
        ListDividends200Response response = new ListDividends200Response();
        response.setContent(Collections.emptyList());

        when(dividendsApi.listDividends(any(), any(), any())).thenReturn(Mono.just(response));

        // Act
        Mono<DividendStats> result = dividendService.getDividendStats();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(stats -> stats.getCasesCount() == 0 &&
                        stats.getPendingAmount().equals(BigDecimal.ZERO))
                .verifyComplete();
    }

    @Test
    void getDividendHistory_ShouldMapResponse() {
        // Arrange
        Dividend dividend = new Dividend();
        dividend.setId(UUID.randomUUID());

        ListDividends200Response response = new ListDividends200Response();
        response.setContent(List.of(dividend));

        when(dividendsApi.listDividends(any(), any(), any())).thenReturn(Mono.just(response));

        // Mock mapper behavior
        com.taxdividend.bff.model.DividendCase mappedCase = new com.taxdividend.bff.model.DividendCase();
        mappedCase.setId(dividend.getId().toString());
        when(dividendMapper.toDividendCase(dividend)).thenReturn(mappedCase);

        // Act
        Mono<com.taxdividend.bff.model.DividendHistoryResponse> result = dividendService.getDividendHistory(0, 10);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(hist -> hist.getData().size() == 1 &&
                        hist.getData().get(0).getId().equals(dividend.getId().toString()))
                .verifyComplete();
    }
}
