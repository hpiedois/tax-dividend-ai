package com.taxdividend.bff.service;

import com.taxdividend.bff.client.api.DividendsApi;
import com.taxdividend.bff.client.model.BulkImportDividendItem;
import com.taxdividend.bff.client.model.BulkImportDividendsRequest;
import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.mapper.DividendMapper;
import com.taxdividend.bff.model.DividendCase;
import com.taxdividend.bff.model.DividendData;
import com.taxdividend.bff.model.DividendHistoryResponse;
import com.taxdividend.bff.model.DividendStats;
import com.taxdividend.bff.model.ParseStatementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DividendService {

    private final DividendsApi dividendsApi;
    private final com.taxdividend.bff.client.api.DividendStatementsApi dividendStatementsApi;
    private final DividendMapper dividendMapper;
    private final com.taxdividend.bff.agent.client.api.ParsingApi parsingApi;

    // ==================== Public API ====================

    public Mono<DividendStats> getDividendStats(UUID userId, Integer taxYear) {
        log.debug("Fetching dividend stats for user {}", userId);
        return dividendsApi.getDividendStats(userId, taxYear)
                .map(statsDTO -> {
                    DividendStats stats = new DividendStats();
                    stats.setTotalReclaimed(statsDTO.getTotalReclaimed());
                    stats.setPendingAmount(statsDTO.getPendingAmount());
                    stats.setCasesCount(statsDTO.getCasesCount());
                    return stats;
                });
    }

    public Mono<DividendHistoryResponse> getDividendHistory(UUID userId, Integer page, Integer pageSize) {
        return dividendsApi.listDividends(userId, page, pageSize, null, null, null)
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

    public Mono<ParseStatementResponse> parseDividendStatement(Part file) {
        log.info("Parsing dividend statement via Agent");

        return validateFile(file)
                .flatMap(this::createTempFile)
                .flatMap(tempFile -> processStatement(tempFile, (FilePart) file)
                        .doFinally(signal -> cleanupTempFile(tempFile))
                );
    }

    // ==================== Validation ====================

    private Mono<FilePart> validateFile(Part file) {
        if (!(file instanceof FilePart)) {
            return Mono.error(new IllegalArgumentException("Uploaded part is not a file"));
        }
        FilePart filePart = (FilePart) file;

        if (filePart.filename() == null || filePart.filename().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Filename is required"));
        }

        if (!filePart.filename().toLowerCase().endsWith(".pdf")) {
            return Mono.error(new IllegalArgumentException("Only PDF files are supported"));
        }

        return Mono.just(filePart);
    }

    // ==================== File Handling ====================

    private Mono<File> createTempFile(FilePart filePart) {
        return Mono.fromCallable(() ->
                File.createTempFile("upload_", "_" + filePart.filename())
        ).onErrorResume(e -> {
            log.error("Failed to create temp file", e);
            return Mono.error(new RuntimeException("Failed to create temp file", e));
        });
    }

    private void cleanupTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath());
            }
        }
    }

    // ==================== Main Processing Pipeline ====================

    private Mono<ParseStatementResponse> processStatement(File tempFile, FilePart filePart) {
        return filePart.transferTo(tempFile.toPath())
                .then(parseWithAgent(tempFile))
                .flatMap(agentResponse -> createStatementInBackend(tempFile, agentResponse))
                .flatMap(this::importDividendsToBackend)
                .map(this::buildResponse)
                .onErrorResume(this::handleError);
    }

    // ==================== Agent Parsing ====================

    private Mono<com.taxdividend.bff.agent.client.model.ParseResponse> parseWithAgent(File tempFile) {
        return parsingApi.parseDocument(tempFile, null)
                .doOnSuccess(response -> {
                    int count = response.getData() != null ? response.getData().size() : 0;
                    log.debug("Agent parsed {} dividends from {}", count, tempFile.getName());
                })
                .onErrorResume(e -> {
                    log.error("Agent parsing failed for file {}", tempFile.getName(), e);
                    return Mono.error(new RuntimeException("Failed to parse PDF with Agent", e));
                });
    }

    // ==================== Backend Statement Creation ====================

    private Mono<StatementWithDividends> createStatementInBackend(
            File tempFile,
            com.taxdividend.bff.agent.client.model.ParseResponse agentResponse) {

        return getUserId().flatMap(userId -> {
            // Extract metadata from agent response
            LocalDate periodStart = extractPeriodStart(agentResponse);
            LocalDate periodEnd = extractPeriodEnd(agentResponse);
            String broker = extractBroker(agentResponse);

            log.debug("Creating statement in backend: broker={}, period={} to {}", 
                    broker, periodStart, periodEnd);

            return dividendStatementsApi.uploadDividendStatement(
                    userId, broker, periodStart, periodEnd, tempFile
            ).map(statement -> new StatementWithDividends(statement, agentResponse, userId));
        }).onErrorResume(e -> {
            log.error("Failed to create statement in backend", e);
            return Mono.error(new RuntimeException("Failed to store dividend statement", e));
        });
    }

    // ==================== Backend Dividend Import ====================

    private Mono<ImportResult> importDividendsToBackend(StatementWithDividends data) {
        if (data.agentResponse.getData() == null || data.agentResponse.getData().isEmpty()) {
            log.warn("No dividends to import for statement {}", data.statement.getId());
            return Mono.just(new ImportResult(data, null));
        }

        BulkImportDividendsRequest bulkRequest = buildBulkImportRequest(data);

        log.debug("Importing {} dividends for statement {}", 
                bulkRequest.getDividends().size(), data.statement.getId());

        return dividendsApi.bulkImportDividends(data.userId, bulkRequest)
                .map(importResponse -> new ImportResult(data, importResponse))
                .onErrorResume(e -> {
                    log.error("Failed to import dividends for statement {}", data.statement.getId(), e);
                    return Mono.error(new RuntimeException("Failed to import dividends", e));
                });
    }

    // ==================== Response Building ====================

    private ParseStatementResponse buildResponse(ImportResult result) {
        ParseStatementResponse response = new ParseStatementResponse();

        if (result.agentResponse().getData() != null && !result.agentResponse().getData().isEmpty()) {
            List<DividendData> dividends = result.agentResponse().getData().stream()
                    .map(this::mapToDividendData)
                    .collect(Collectors.toList());
            response.setDividends(dividends);
        }

        log.info("Statement processing completed: {} dividends returned", 
                response.getDividends() != null ? response.getDividends().size() : 0);

        return response;
    }

    // ==================== Error Handling ====================

    private Mono<ParseStatementResponse> handleError(Throwable e) {
        // Don't wrap already meaningful RuntimeExceptions
        if (e instanceof RuntimeException && e.getMessage() != null && 
            (e.getMessage().contains("Failed to parse PDF") ||
             e.getMessage().contains("Failed to store dividend statement") ||
             e.getMessage().contains("Failed to import dividends"))) {
            return Mono.error(e);
        }
        
        log.error("Unexpected error during statement processing", e);
        return Mono.error(new RuntimeException("Failed to process dividend statement", e));
    }

    // ==================== Helper Methods ====================

    private Mono<UUID> getUserId() {
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                .switchIfEmpty(Mono.error(new SecurityException("User not authenticated")));
    }

    private LocalDate extractPeriodStart(com.taxdividend.bff.agent.client.model.ParseResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            return null;
        }
        return response.getData().stream()
                .map(d -> d.getPaymentDate())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private LocalDate extractPeriodEnd(com.taxdividend.bff.agent.client.model.ParseResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            return null;
        }
        return response.getData().stream()
                .map(d -> d.getPaymentDate())
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private String extractBroker(com.taxdividend.bff.agent.client.model.ParseResponse response) {
        // TODO: Agent should provide broker in future version
        // For now, use default value
        return "Unknown";
    }

    // ==================== Mapping ====================

    private BulkImportDividendsRequest buildBulkImportRequest(StatementWithDividends data) {
        BulkImportDividendsRequest request = new BulkImportDividendsRequest();
        request.setStatementId(data.statement.getId());

        List<BulkImportDividendItem> items = data.agentResponse.getData().stream()
                .map(this::mapToBulkImportItem)
                .collect(Collectors.toList());

        request.setDividends(items);
        return request;
    }

    private BulkImportDividendItem mapToBulkImportItem(com.taxdividend.bff.agent.client.model.DividendData d) {
        BulkImportDividendItem item = new BulkImportDividendItem();
        item.setSecurityName(d.getSecurityName());
        item.setIsin(d.getIsin());
        item.setGrossAmount(d.getGrossAmount());
        item.setCurrency(d.getCurrency());
        item.setPaymentDate(d.getPaymentDate());
        item.setWithholdingTax(d.getWithholdingTax());
        
        // TODO: Update Agent spec to include withholdingRate
        // For now, calculate it from withholdingTax / grossAmount
        if (d.getWithholdingTax() != null && d.getGrossAmount() != null 
                && d.getGrossAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = d.getWithholdingTax()
                    .divide(d.getGrossAmount(), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            item.setWithholdingRate(rate);
        }
        
        item.setSourceCountry(d.getCountry());
        return item;
    }

    private DividendData mapToDividendData(com.taxdividend.bff.agent.client.model.DividendData d) {
        DividendData dd = new DividendData();
        dd.setSecurityName(d.getSecurityName());
        dd.setIsin(d.getIsin());
        dd.setGrossAmount(d.getGrossAmount());
        dd.setCurrency(d.getCurrency());
        dd.setPaymentDate(d.getPaymentDate());
        dd.setWithholdingTax(d.getWithholdingTax());
        dd.setSourceCountry(d.getCountry());
        
        // ReclaimableAmount calculated by backend and returned after import
        // Could be retrieved from backend response if needed
        
        return dd;
    }

    // ==================== Internal Records ====================

    /**
     * Internal record to carry statement with agent response through the pipeline
     */
    private record StatementWithDividends(
            com.taxdividend.bff.client.model.DividendStatement statement,
            com.taxdividend.bff.agent.client.model.ParseResponse agentResponse,
            UUID userId
    ) {}

    /**
     * Internal record to carry import result through the pipeline
     */
    private record ImportResult(
            StatementWithDividends data,
            com.taxdividend.bff.client.model.BulkImportDividendsResponse importResponse
    ) {
        public com.taxdividend.bff.agent.client.model.ParseResponse agentResponse() {
            return data.agentResponse;
        }
    }
}
