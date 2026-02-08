package com.taxdividend.backend.controller;

import com.taxdividend.backend.api.DividendStatementsApi;
import com.taxdividend.backend.api.dto.DividendStatementDto;
import com.taxdividend.backend.api.dto.DividendStatementStatusDto;
import com.taxdividend.backend.api.dto.DividendStatementUpdateDto;
import com.taxdividend.backend.api.dto.PaginatedDividendStatementsDto;
import com.taxdividend.backend.model.DividendStatementStatus;
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
import com.taxdividend.backend.security.SecurityHelper;
import com.taxdividend.backend.security.UserContextHolder;

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

        @Override
        public ResponseEntity<DividendStatementDto> uploadDividendStatement(
                        String broker,
                        LocalDate periodStart,
                        LocalDate periodEnd,
                        MultipartFile file) {

                UUID userId = SecurityHelper.getCurrentUserId();
                log.info("Uploading statement for user {}, broker {}, period {}-{}",
                                userId, broker, periodStart, periodEnd);

                DividendStatementDto statement = statementService.uploadStatement(file, userId, broker, periodStart,
                                periodEnd);

                return ResponseEntity.status(HttpStatus.CREATED).body(statement);
        }

        @Override
        public ResponseEntity<DividendStatementDto> getDividendStatement(UUID id) {
                UUID userId = SecurityHelper.getCurrentUserId();
                log.debug("Getting statement {} for user {}", id, userId);

                return statementService.getStatement(id, userId)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @Override
        public ResponseEntity<PaginatedDividendStatementsDto> listDividendStatements(
                        DividendStatementStatusDto status,
                        Integer page,
                        Integer size) {

                UUID userId = SecurityHelper.getCurrentUserId();
                log.debug("Listing statements for user {} with status filter: {}", userId, status);

                // Convert API enum (DTO) to internal enum (Model)
                // If status is null, pass null. If not null, convert name.
                DividendStatementStatus internalStatus = status != null ? DividendStatementStatus.valueOf(status.name())
                                : null;

                Page<DividendStatementDto> statementsPage = statementService.listStatements(userId, internalStatus,
                                PageRequest.of(page, size));

                PaginatedDividendStatementsDto response = new PaginatedDividendStatementsDto()
                                .content(statementsPage.getContent())
                                .totalElements((int) statementsPage.getTotalElements())
                                .totalPages(statementsPage.getTotalPages())
                                .size(statementsPage.getSize())
                                .number(statementsPage.getNumber());

                return ResponseEntity.ok(response);
        }

        @Override
        public ResponseEntity<DividendStatementDto> updateDividendStatementStatus(
                        UUID id,
                        DividendStatementUpdateDto updateDTO) {

                UUID userId = SecurityHelper.getCurrentUserId();
                log.info("Updating statement {} status to {} for user {}",
                                id, updateDTO.getStatus(), userId);

                DividendStatementDto updated = statementService.updateStatus(id, userId, updateDTO);

                return ResponseEntity.ok(updated);
        }

        @Override
        public ResponseEntity<Void> deleteDividendStatement(UUID id) {
                UUID userId = SecurityHelper.getCurrentUserId();
                log.info("Deleting statement {} for user {}", id, userId);

                statementService.deleteStatement(id, userId);
                return ResponseEntity.noContent().build();
        }

        @Override
        public ResponseEntity<List<DividendStatementDto>> getDividendStatementsByDateRange(
                        LocalDate startDate,
                        LocalDate endDate) {

                UUID userId = SecurityHelper.getCurrentUserId();
                log.debug("Finding statements for user {} in date range {}-{}", userId, startDate, endDate);

                List<DividendStatementDto> statements = statementService.findByDateRange(userId, startDate, endDate);

                return ResponseEntity.ok(statements);
        }

        @Override
        public ResponseEntity<Long> countDividendStatementsByStatus(DividendStatementStatusDto status) {
                UUID userId = SecurityHelper.getCurrentUserId();
                log.debug("Counting statements with status {} for user {}", status, userId);

                // Convert API enum to internal enum
                DividendStatementStatus internalStatus = DividendStatementStatus.valueOf(status.name());

                long count = statementService.countByStatus(userId, internalStatus);
                return ResponseEntity.ok(count);
        }
}
