package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.DividendStatementsApi;
import com.taxdividend.backend.api.dto.DividendStatement;
import com.taxdividend.backend.api.dto.DividendStatementStatus;
import com.taxdividend.backend.api.dto.DividendStatementUpdateDTO;
import com.taxdividend.backend.api.dto.ListDividendStatements200Response;
import com.taxdividend.backend.mapper.DividendStatementMapper;
import com.taxdividend.backend.service.DividendStatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing dividend statements.
 * Implements the generated OpenAPI interface.
 */
@RestController
@RequestMapping("/internal")
@Slf4j
@RequiredArgsConstructor
public class DividendStatementController implements DividendStatementsApi {

    private final DividendStatementService statementService;
    private final DividendStatementMapper mapper;

    @Override
    public ResponseEntity<DividendStatement> uploadDividendStatement(
            UUID xUserId,
            String broker,
            LocalDate periodStart,
            LocalDate periodEnd,
            MultipartFile file) {

        log.info("Uploading statement for user {}, broker {}, period {}-{}",
                xUserId, broker, periodStart, periodEnd);

        com.taxdividend.backend.dto.DividendStatementDTO statement =
                statementService.uploadStatement(file, xUserId, broker, periodStart, periodEnd);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toApiDto(statement));
    }

    @Override
    public ResponseEntity<DividendStatement> getDividendStatement(UUID id, UUID xUserId) {
        log.debug("Getting statement {} for user {}", id, xUserId);

        return statementService.getStatement(id, xUserId)
                .map(mapper::toApiDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ListDividendStatements200Response> listDividendStatements(
            UUID xUserId,
            DividendStatementStatus status,
            Integer page,
            Integer size) {

        log.debug("Listing statements for user {} with status filter: {}", xUserId, status);

        // Convert API enum to internal enum
        com.taxdividend.backend.model.DividendStatementStatus internalStatus =
                status != null ? com.taxdividend.backend.model.DividendStatementStatus.valueOf(status.name()) : null;

        Page<com.taxdividend.backend.dto.DividendStatementDTO> statementsPage =
                statementService.listStatements(xUserId, internalStatus, PageRequest.of(page, size));

        // Convert to API response
        List<DividendStatement> apiStatements = statementsPage.getContent().stream()
                .map(mapper::toApiDto)
                .toList();

        ListDividendStatements200Response response = new ListDividendStatements200Response()
                .content(apiStatements)
                .totalElements((int) statementsPage.getTotalElements())
                .totalPages(statementsPage.getTotalPages())
                .size(statementsPage.getSize())
                .number(statementsPage.getNumber());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DividendStatement> updateDividendStatementStatus(
            UUID id,
            UUID xUserId,
            DividendStatementUpdateDTO updateDTO) {

        log.info("Updating statement {} status to {} for user {}",
                id, updateDTO.getStatus(), xUserId);

        // Convert API DTO to internal DTO
        com.taxdividend.backend.dto.DividendStatementUpdateDTO internalUpdateDTO =
                mapper.toInternalUpdateDto(updateDTO);

        com.taxdividend.backend.dto.DividendStatementDTO updated =
                statementService.updateStatus(id, xUserId, internalUpdateDTO);

        return ResponseEntity.ok(mapper.toApiDto(updated));
    }

    @Override
    public ResponseEntity<Void> deleteDividendStatement(UUID id, UUID xUserId) {
        log.info("Deleting statement {} for user {}", id, xUserId);

        statementService.deleteStatement(id, xUserId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<DividendStatement>> getDividendStatementsByDateRange(
            UUID xUserId,
            LocalDate startDate,
            LocalDate endDate) {

        log.debug("Finding statements for user {} in date range {}-{}", xUserId, startDate, endDate);

        List<com.taxdividend.backend.dto.DividendStatementDTO> statements =
                statementService.findByDateRange(xUserId, startDate, endDate);

        List<DividendStatement> apiStatements = statements.stream()
                .map(mapper::toApiDto)
                .toList();

        return ResponseEntity.ok(apiStatements);
    }

    @Override
    public ResponseEntity<Long> countDividendStatementsByStatus(UUID xUserId, DividendStatementStatus status) {
        log.debug("Counting statements with status {} for user {}", status, xUserId);

        // Convert API enum to internal enum
        com.taxdividend.backend.model.DividendStatementStatus internalStatus =
                com.taxdividend.backend.model.DividendStatementStatus.valueOf(status.name());

        long count = statementService.countByStatus(xUserId, internalStatus);
        return ResponseEntity.ok(count);
    }
}
