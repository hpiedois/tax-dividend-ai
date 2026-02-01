package com.taxdividend.bff.service;

import com.taxdividend.bff.agent.client.api.ParsingApi;
import com.taxdividend.bff.agent.client.model.DividendData;
import com.taxdividend.bff.agent.client.model.ParseResponse;
import com.taxdividend.bff.client.api.DividendStatementsApi;
import com.taxdividend.bff.client.api.DividendsApi;
import com.taxdividend.bff.client.model.*;
import com.taxdividend.bff.mapper.DividendMapper;
import com.taxdividend.bff.model.DividendCase;
import com.taxdividend.bff.model.DividendHistoryResponse;
import com.taxdividend.bff.model.DividendStats;
import com.taxdividend.bff.model.ParseStatementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DividendService Tests")
class DividendServiceTest {

    @Mock
    private DividendsApi dividendsApi;

    @Mock
    private DividendStatementsApi dividendStatementsApi;

    @Mock
    private DividendMapper dividendMapper;

    @Mock
    private ParsingApi parsingApi;

    @InjectMocks
    private DividendService dividendService;

    private UUID testUserId;
    private UUID testStatementId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testStatementId = UUID.randomUUID();
    }

    // ==================== getDividendStats Tests ====================

    @Test
    @DisplayName("Should get dividend stats successfully")
    void getDividendStats_Success() {
        // Arrange
        Integer taxYear = 2024;
        DividendStatsDTO statsDTO = new DividendStatsDTO();
        statsDTO.setTotalReclaimed(new BigDecimal("150.00"));
        statsDTO.setPendingAmount(new BigDecimal("50.00"));
        statsDTO.setCasesCount(5);

        when(dividendsApi.getDividendStats(testUserId, taxYear))
                .thenReturn(Mono.just(statsDTO));

        // Act & Assert
        StepVerifier.create(dividendService.getDividendStats(testUserId, taxYear))
                .assertNext(stats -> {
                    assertThat(stats.getTotalReclaimed()).isEqualTo(new BigDecimal("150.00"));
                    assertThat(stats.getPendingAmount()).isEqualTo(new BigDecimal("50.00"));
                    assertThat(stats.getCasesCount()).isEqualTo(5);
                })
                .verifyComplete();

        verify(dividendsApi).getDividendStats(testUserId, taxYear);
    }

    @Test
    @DisplayName("Should handle error when fetching dividend stats")
    void getDividendStats_Error() {
        // Arrange
        Integer taxYear = 2024;
        when(dividendsApi.getDividendStats(testUserId, taxYear))
                .thenReturn(Mono.error(new RuntimeException("Backend error")));

        // Act & Assert
        StepVerifier.create(dividendService.getDividendStats(testUserId, taxYear))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Backend error"))
                .verify();
    }

    // ==================== getDividendHistory Tests ====================

    @Test
    @DisplayName("Should get dividend history successfully")
    void getDividendHistory_Success() {
        // Arrange
        Dividend dividend1 = new Dividend();
        dividend1.setId(UUID.randomUUID());
        dividend1.setSecurityName("Test Security 1");

        Dividend dividend2 = new Dividend();
        dividend2.setId(UUID.randomUUID());
        dividend2.setSecurityName("Test Security 2");

        ListDividends200Response response = new ListDividends200Response();
        response.setContent(List.of(dividend1, dividend2));

        DividendCase case1 = new DividendCase();
        case1.setId(dividend1.getId().toString());

        DividendCase case2 = new DividendCase();
        case2.setId(dividend2.getId().toString());

        when(dividendsApi.listDividends(eq(testUserId), eq(0), eq(10), any(), any(), any()))
                .thenReturn(Mono.just(response));
        when(dividendMapper.toDividendCase(dividend1)).thenReturn(case1);
        when(dividendMapper.toDividendCase(dividend2)).thenReturn(case2);

        // Act & Assert
        StepVerifier.create(dividendService.getDividendHistory(testUserId, 0, 10))
                .assertNext(history -> {
                    assertThat(history.getData()).hasSize(2);
                    assertThat(history.getPage()).isEqualTo(0);
                    assertThat(history.getPageSize()).isEqualTo(10);
                    assertThat(history.getTotal()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty history when no dividends found")
    void getDividendHistory_EmptyResult() {
        // Arrange
        ListDividends200Response response = new ListDividends200Response();
        response.setContent(List.of());

        when(dividendsApi.listDividends(eq(testUserId), eq(0), eq(10), any(), any(), any()))
                .thenReturn(Mono.just(response));

        // Act & Assert
        StepVerifier.create(dividendService.getDividendHistory(testUserId, 0, 10))
                .assertNext(history -> {
                    assertThat(history.getData()).isEmpty();
                    assertThat(history.getTotal()).isEqualTo(0);
                })
                .verifyComplete();
    }

    // ==================== parseDividendStatement Tests ====================

    @Test
    @DisplayName("Should reject non-FilePart uploads")
    void parseDividendStatement_NotFilePart() {
        // Arrange
        Part nonFilePart = mock(Part.class);

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(nonFilePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Uploaded part is not a file"))
                .verify();
    }

    @Test
    @DisplayName("Should reject files without filename")
    void parseDividendStatement_NoFilename() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn(null);

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Filename is required"))
                .verify();
    }

    @Test
    @DisplayName("Should reject files with empty filename")
    void parseDividendStatement_EmptyFilename() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("");

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Filename is required"))
                .verify();
    }

    @Test
    @DisplayName("Should reject non-PDF files")
    void parseDividendStatement_NotPdf() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("document.docx");

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Only PDF files are supported"))
                .verify();
    }

    @Test
    @DisplayName("Should parse dividend statement successfully")
    void parseDividendStatement_Success() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());

        // Mock Agent response
        DividendData agentDividend = new DividendData();
        agentDividend.setSecurityName("Test Security");
        agentDividend.setIsin("FR0000120271");
        agentDividend.setGrossAmount(new BigDecimal("100.00"));
        agentDividend.setCurrency("EUR");
        agentDividend.setPaymentDate(LocalDate.of(2024, 6, 15));
        agentDividend.setCountry("FR");
        agentDividend.setWithholdingTax(new BigDecimal("30.00"));

        ParseResponse agentResponse = new ParseResponse();
        agentResponse.setData(List.of(agentDividend));

        when(parsingApi.parseDocument(any(), any())).thenReturn(Mono.just(agentResponse));

        // Mock Backend statement creation
        DividendStatement statement = new DividendStatement();
        statement.setId(testStatementId);
        statement.setUserId(testUserId);

        when(dividendStatementsApi.uploadDividendStatement(
                eq(testUserId), anyString(), any(), any(), any()))
                .thenReturn(Mono.just(statement));

        // Mock Backend bulk import
        BulkImportDividendsResponse importResponse = new BulkImportDividendsResponse();
        when(dividendsApi.bulkImportDividends(eq(testUserId), any()))
                .thenReturn(Mono.just(importResponse));

        // Mock Security Context
        SecurityContext securityContext = new SecurityContextImpl(
                new TestingAuthenticationToken(testUserId.toString(), null)
        );

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart)
                        .contextWrite(ctx -> ctx.put(SecurityContext.class, Mono.just(securityContext))))
                .assertNext(response -> {
                    assertThat(response.getDividends()).isNotNull();
                    assertThat(response.getDividends()).hasSize(1);
                    assertThat(response.getDividends().get(0).getSecurityName())
                            .isEqualTo("Test Security");
                })
                .verifyComplete();

        verify(parsingApi).parseDocument(any(), any());
        verify(dividendStatementsApi).uploadDividendStatement(
                eq(testUserId), anyString(), any(), any(), any());
        verify(dividendsApi).bulkImportDividends(eq(testUserId), any());
    }

    @Test
    @DisplayName("Should handle agent parsing error")
    void parseDividendStatement_AgentError() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());
        when(parsingApi.parseDocument(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Agent timeout")));

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Failed to parse PDF"))
                .verify();
    }

    @Test
    @DisplayName("Should handle backend statement creation error")
    void parseDividendStatement_BackendStatementError() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());

        ParseResponse agentResponse = new ParseResponse();
        DividendData dividend = new DividendData();
        dividend.setPaymentDate(LocalDate.of(2024, 6, 15));
        agentResponse.setData(List.of(dividend));

        when(parsingApi.parseDocument(any(), any())).thenReturn(Mono.just(agentResponse));
        when(dividendStatementsApi.uploadDividendStatement(any(), any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Backend error")));

        SecurityContext securityContext = new SecurityContextImpl(
                new TestingAuthenticationToken(testUserId.toString(), null)
        );

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart)
                        .contextWrite(ctx -> ctx.put(SecurityContext.class, Mono.just(securityContext))))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Failed to store dividend statement"))
                .verify();
    }

    @Test
    @DisplayName("Should handle backend import error")
    void parseDividendStatement_BackendImportError() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());

        DividendData agentDividend = new DividendData();
        agentDividend.setPaymentDate(LocalDate.of(2024, 6, 15));
        agentDividend.setGrossAmount(new BigDecimal("100.00"));
        agentDividend.setWithholdingTax(new BigDecimal("30.00"));

        ParseResponse agentResponse = new ParseResponse();
        agentResponse.setData(List.of(agentDividend));

        when(parsingApi.parseDocument(any(), any())).thenReturn(Mono.just(agentResponse));

        DividendStatement statement = new DividendStatement();
        statement.setId(testStatementId);
        when(dividendStatementsApi.uploadDividendStatement(any(), any(), any(), any(), any()))
                .thenReturn(Mono.just(statement));

        when(dividendsApi.bulkImportDividends(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Import failed")));

        SecurityContext securityContext = new SecurityContextImpl(
                new TestingAuthenticationToken(testUserId.toString(), null)
        );

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart)
                        .contextWrite(ctx -> ctx.put(SecurityContext.class, Mono.just(securityContext))))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Failed to import dividends"))
                .verify();
    }

    @Test
    @DisplayName("Should handle empty agent response")
    void parseDividendStatement_EmptyAgentResponse() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());

        ParseResponse agentResponse = new ParseResponse();
        agentResponse.setData(List.of()); // Empty list

        when(parsingApi.parseDocument(any(), any())).thenReturn(Mono.just(agentResponse));

        DividendStatement statement = new DividendStatement();
        statement.setId(testStatementId);
        when(dividendStatementsApi.uploadDividendStatement(any(), any(), any(), any(), any()))
                .thenReturn(Mono.just(statement));

        SecurityContext securityContext = new SecurityContextImpl(
                new TestingAuthenticationToken(testUserId.toString(), null)
        );

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart)
                        .contextWrite(ctx -> ctx.put(SecurityContext.class, Mono.just(securityContext))))
                .assertNext(response -> {
                    // Should still succeed but with empty dividends
                    assertThat(response.getDividends()).isEmpty();
                })
                .verifyComplete();

        // Should NOT call bulkImportDividends for empty list
        verify(dividendsApi, never()).bulkImportDividends(any(), any());
    }

    @Test
    @DisplayName("Should extract period start and end from dividends")
    void parseDividendStatement_ExtractDates() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());

        // Multiple dividends with different dates
        DividendData dividend1 = new DividendData();
        dividend1.setPaymentDate(LocalDate.of(2024, 3, 15));
        dividend1.setGrossAmount(new BigDecimal("100.00"));
        dividend1.setWithholdingTax(new BigDecimal("30.00"));

        DividendData dividend2 = new DividendData();
        dividend2.setPaymentDate(LocalDate.of(2024, 6, 20));
        dividend2.setGrossAmount(new BigDecimal("100.00"));
        dividend2.setWithholdingTax(new BigDecimal("30.00"));

        DividendData dividend3 = new DividendData();
        dividend3.setPaymentDate(LocalDate.of(2024, 9, 10));
        dividend3.setGrossAmount(new BigDecimal("100.00"));
        dividend3.setWithholdingTax(new BigDecimal("30.00"));

        ParseResponse agentResponse = new ParseResponse();
        agentResponse.setData(List.of(dividend1, dividend2, dividend3));

        when(parsingApi.parseDocument(any(), any())).thenReturn(Mono.just(agentResponse));

        DividendStatement statement = new DividendStatement();
        statement.setId(testStatementId);
        when(dividendStatementsApi.uploadDividendStatement(
                eq(testUserId), anyString(),
                eq(LocalDate.of(2024, 3, 15)), // periodStart (min date)
                eq(LocalDate.of(2024, 9, 10)), // periodEnd (max date)
                any()))
                .thenReturn(Mono.just(statement));

        BulkImportDividendsResponse importResponse = new BulkImportDividendsResponse();
        when(dividendsApi.bulkImportDividends(any(), any()))
                .thenReturn(Mono.just(importResponse));

        SecurityContext securityContext = new SecurityContextImpl(
                new TestingAuthenticationToken(testUserId.toString(), null)
        );

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart)
                        .contextWrite(ctx -> ctx.put(SecurityContext.class, Mono.just(securityContext))))
                .assertNext(response -> {
                    assertThat(response.getDividends()).hasSize(3);
                })
                .verifyComplete();

        // Verify dates were extracted correctly
        verify(dividendStatementsApi).uploadDividendStatement(
                eq(testUserId),
                anyString(),
                eq(LocalDate.of(2024, 3, 15)),
                eq(LocalDate.of(2024, 9, 10)),
                any()
        );
    }

    @Test
    @DisplayName("Should calculate withholdingRate when grossAmount is positive")
    void parseDividendStatement_CalculateWithholdingRate() {
        // Arrange
        FilePart filePart = mock(FilePart.class);
        when(filePart.filename()).thenReturn("statement.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());

        DividendData agentDividend = new DividendData();
        agentDividend.setSecurityName("Test Security");
        agentDividend.setGrossAmount(new BigDecimal("100.00"));
        agentDividend.setWithholdingTax(new BigDecimal("30.00"));
        agentDividend.setPaymentDate(LocalDate.of(2024, 6, 15));

        ParseResponse agentResponse = new ParseResponse();
        agentResponse.setData(List.of(agentDividend));

        when(parsingApi.parseDocument(any(), any())).thenReturn(Mono.just(agentResponse));

        DividendStatement statement = new DividendStatement();
        statement.setId(testStatementId);
        when(dividendStatementsApi.uploadDividendStatement(any(), any(), any(), any(), any()))
                .thenReturn(Mono.just(statement));

        when(dividendsApi.bulkImportDividends(eq(testUserId), argThat(request -> {
            // Verify withholdingRate is calculated correctly (30%)
            BulkImportDividendItem item = request.getDividends().get(0);
            return item.getWithholdingRate() != null &&
                    item.getWithholdingRate().compareTo(new BigDecimal("30.0000")) == 0;
        }))).thenReturn(Mono.just(new BulkImportDividendsResponse()));

        SecurityContext securityContext = new SecurityContextImpl(
                new TestingAuthenticationToken(testUserId.toString(), null)
        );

        // Act & Assert
        StepVerifier.create(dividendService.parseDividendStatement(filePart)
                        .contextWrite(ctx -> ctx.put(SecurityContext.class, Mono.just(securityContext))))
                .assertNext(response -> {
                    assertThat(response.getDividends()).isNotEmpty();
                })
                .verifyComplete();
    }
}
