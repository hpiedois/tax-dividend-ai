package com.taxdividend.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taxdividend.backend.api.dto.DividendStatementDto;
import com.taxdividend.backend.api.dto.DividendStatementStatusDto;
import com.taxdividend.backend.api.dto.DividendStatementUpdateDto;
import com.taxdividend.backend.api.dto.PaginatedDividendStatementsDto;
import com.taxdividend.backend.config.TestSecurityConfig; // Restored
import com.taxdividend.backend.service.DividendStatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for DividendStatementController using MockMvc.
 */
@WebMvcTest(controllers = DividendStatementController.class)
@Import(TestSecurityConfig.class)
@DisplayName("DividendStatement Controller Tests")
class DividendStatementControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private DividendStatementService statementService;

        // Mapper removed

        private ObjectMapper objectMapper;
        private UUID testUserId;
        private UUID statementId;
        private DividendStatementDto testStatementDTO;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                testUserId = UUID.randomUUID();
                statementId = UUID.randomUUID();

                testStatementDTO = new DividendStatementDto();
                testStatementDTO.setId(statementId);
                testStatementDTO.setUserId(testUserId);
                testStatementDTO.setSourceFileName("statement.pdf");
                testStatementDTO.setBroker("Interactive Brokers");
                testStatementDTO.setStatus(DividendStatementStatusDto.UPLOADED);
        }

        @Nested
        @DisplayName("Upload Statement Tests")
        class UploadStatementTests {

                @Test
                @DisplayName("Should upload dividend statement successfully")
                void shouldUploadStatementSuccessfully() throws Exception {
                        // Given
                        MockMultipartFile file = new MockMultipartFile(
                                        "file",
                                        "statement.pdf",
                                        "application/pdf",
                                        "PDF content".getBytes());

                        when(statementService.uploadStatement(any(), eq(testUserId), eq("Interactive Brokers"),
                                        any(LocalDate.class), any(LocalDate.class)))
                                        .thenReturn(testStatementDTO);

                        // When/Then
                        mockMvc.perform(multipart("/internal/dividend-statements")
                                        .file(file)
                                        .header("X-User-Id", testUserId.toString())
                                        .param("broker", "Interactive Brokers")
                                        .param("periodStart", "2024-01-01")
                                        .param("periodEnd", "2024-12-31"))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id").value(statementId.toString()))
                                        .andExpect(jsonPath("$.broker").value("Interactive Brokers"))
                                        .andExpect(jsonPath("$.status").value("UPLOADED"));

                        verify(statementService).uploadStatement(any(), eq(testUserId), eq("Interactive Brokers"),
                                        eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2024, 12, 31)));
                }

                // Note: File and parameter validation is handled at OpenAPI generation level
                // These tests would require specific validation annotations on the generated
                // API interface
        }

        @Nested
        @DisplayName("List Statements Tests")
        class ListStatementsTests {

                @Test
                @DisplayName("Should list all statements with pagination")
                void shouldListAllStatementsWithPagination() throws Exception {
                        // Given
                        Page<DividendStatementDto> page = new PageImpl<>(
                                        List.of(testStatementDTO),
                                        PageRequest.of(0, 20),
                                        1);
                        when(statementService.listStatements(eq(testUserId), isNull(), any(PageRequest.class)))
                                        .thenReturn(page);

                        PaginatedDividendStatementsDto response = new PaginatedDividendStatementsDto();
                        response.setContent(List.of(testStatementDTO));
                        response.setTotalElements(1);
                        response.setTotalPages(1);
                        response.setSize(20);
                        response.setNumber(0);

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("page", "0")
                                        .param("size", "20"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isArray())
                                        .andExpect(jsonPath("$.content[0].id").value(statementId.toString()))
                                        .andExpect(jsonPath("$.totalElements").value(1))
                                        .andExpect(jsonPath("$.totalPages").value(1));

                        verify(statementService).listStatements(eq(testUserId), isNull(), any(PageRequest.class));
                }

                @Test
                @DisplayName("Should filter statements by status")
                void shouldFilterStatementsByStatus() throws Exception {
                        // Given
                        Page<DividendStatementDto> page = new PageImpl<>(
                                        List.of(testStatementDTO),
                                        PageRequest.of(0, 20),
                                        1);
                        when(statementService.listStatements(
                                        eq(testUserId),
                                        eq(com.taxdividend.backend.model.DividendStatementStatus.UPLOADED),
                                        any(PageRequest.class)))
                                        .thenReturn(page);

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("status", "UPLOADED")
                                        .param("page", "0")
                                        .param("size", "20"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isArray())
                                        .andExpect(jsonPath("$.content[0].status").value("UPLOADED"));

                        verify(statementService).listStatements(
                                        eq(testUserId),
                                        eq(com.taxdividend.backend.model.DividendStatementStatus.UPLOADED),
                                        any(PageRequest.class));
                }

                @Test
                @DisplayName("Should return empty list when no statements found")
                void shouldReturnEmptyListWhenNoStatementsFound() throws Exception {
                        // Given
                        Page<DividendStatementDto> emptyPage = new PageImpl<>(
                                        Collections.emptyList(),
                                        PageRequest.of(0, 20),
                                        0);
                        when(statementService.listStatements(eq(testUserId), isNull(), any(PageRequest.class)))
                                        .thenReturn(emptyPage);

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("page", "0")
                                        .param("size", "20"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isEmpty())
                                        .andExpect(jsonPath("$.totalElements").value(0));
                }
        }

        @Nested
        @DisplayName("Get Statement Tests")
        class GetStatementTests {

                @Test
                @DisplayName("Should get statement by ID successfully")
                void shouldGetStatementByIdSuccessfully() throws Exception {
                        // Given
                        when(statementService.getStatement(statementId, testUserId))
                                        .thenReturn(Optional.of(testStatementDTO));

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements/{id}", statementId)
                                        .header("X-User-Id", testUserId.toString()))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(statementId.toString()))
                                        .andExpect(jsonPath("$.broker").value("Interactive Brokers"))
                                        .andExpect(jsonPath("$.status").value("UPLOADED"));

                        verify(statementService).getStatement(statementId, testUserId);
                }

                @Test
                @DisplayName("Should return 404 when statement not found")
                void shouldReturn404WhenStatementNotFound() throws Exception {
                        // Given
                        when(statementService.getStatement(statementId, testUserId))
                                        .thenReturn(Optional.empty());

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements/{id}", statementId)
                                        .header("X-User-Id", testUserId.toString()))
                                        .andExpect(status().isNotFound());

                        verify(statementService).getStatement(statementId, testUserId);
                }
        }

        @Nested
        @DisplayName("Update Status Tests")
        class UpdateStatusTests {

                @Test
                @DisplayName("Should update statement status successfully")
                void shouldUpdateStatementStatusSuccessfully() throws Exception {
                        // Given
                        DividendStatementUpdateDto updateRequest = new DividendStatementUpdateDto();
                        updateRequest.setStatus(DividendStatementStatusDto.PARSING);

                        DividendStatementDto updatedDTO = new DividendStatementDto();
                        updatedDTO.setId(statementId);
                        updatedDTO.setStatus(DividendStatementStatusDto.PARSING);

                        when(statementService.updateStatus(eq(statementId), eq(testUserId), any()))
                                        .thenReturn(updatedDTO);

                        // When/Then
                        mockMvc.perform(patch("/internal/dividend-statements/{id}", statementId)
                                        .header("X-User-Id", testUserId.toString())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value(statementId.toString()))
                                        .andExpect(jsonPath("$.status").value("PARSING"));

                        verify(statementService).updateStatus(eq(statementId), eq(testUserId), any());
                }

                // Note: Error handling for IllegalArgumentException would require a
                // @ControllerAdvice
                // The service throws IllegalArgumentException but controller doesn't catch it
                // yet
        }

        @Nested
        @DisplayName("Delete Statement Tests")
        class DeleteStatementTests {

                @Test
                @DisplayName("Should delete statement successfully")
                void shouldDeleteStatementSuccessfully() throws Exception {
                        // Given
                        doNothing().when(statementService).deleteStatement(statementId, testUserId);

                        // When/Then
                        mockMvc.perform(delete("/internal/dividend-statements/{id}", statementId)
                                        .header("X-User-Id", testUserId.toString()))
                                        .andExpect(status().isNoContent());

                        verify(statementService).deleteStatement(statementId, testUserId);
                }

                @Test
                @DisplayName("Should handle delete of non-existent statement gracefully")
                void shouldHandleDeleteOfNonExistentStatementGracefully() throws Exception {
                        // Given
                        doNothing().when(statementService).deleteStatement(statementId, testUserId);

                        // When/Then
                        mockMvc.perform(delete("/internal/dividend-statements/{id}", statementId)
                                        .header("X-User-Id", testUserId.toString()))
                                        .andExpect(status().isNoContent());
                }
        }

        @Nested
        @DisplayName("Find By Date Range Tests")
        class FindByDateRangeTests {

                @Test
                @DisplayName("Should find statements by date range")
                void shouldFindStatementsByDateRange() throws Exception {
                        // Given
                        LocalDate startDate = LocalDate.of(2024, 1, 1);
                        LocalDate endDate = LocalDate.of(2024, 12, 31);

                        when(statementService.findByDateRange(testUserId, startDate, endDate))
                                        .thenReturn(List.of(testStatementDTO));

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements/by-date-range")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("startDate", "2024-01-01")
                                        .param("endDate", "2024-12-31"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$[0].id").value(statementId.toString()));

                        verify(statementService).findByDateRange(testUserId, startDate, endDate);
                }

                @Test
                @DisplayName("Should return empty array when no statements in date range")
                void shouldReturnEmptyArrayWhenNoStatementsInDateRange() throws Exception {
                        // Given
                        LocalDate startDate = LocalDate.of(2024, 1, 1);
                        LocalDate endDate = LocalDate.of(2024, 12, 31);

                        when(statementService.findByDateRange(testUserId, startDate, endDate))
                                        .thenReturn(Collections.emptyList());

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements/by-date-range")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("startDate", "2024-01-01")
                                        .param("endDate", "2024-12-31"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isEmpty());
                }
        }

        @Nested
        @DisplayName("Count By Status Tests")
        class CountByStatusTests {

                @Test
                @DisplayName("Should count statements by status")
                void shouldCountStatementsByStatus() throws Exception {
                        // Given
                        when(statementService.countByStatus(
                                        testUserId,
                                        com.taxdividend.backend.model.DividendStatementStatus.UPLOADED))
                                        .thenReturn(5L);

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements/count-by-status")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("status", "UPLOADED"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().string("5"));

                        verify(statementService).countByStatus(
                                        testUserId,
                                        com.taxdividend.backend.model.DividendStatementStatus.UPLOADED);
                }

                @Test
                @DisplayName("Should return 0 when no statements with given status")
                void shouldReturn0WhenNoStatementsWithGivenStatus() throws Exception {
                        // Given
                        when(statementService.countByStatus(
                                        testUserId,
                                        com.taxdividend.backend.model.DividendStatementStatus.PAID))
                                        .thenReturn(0L);

                        // When/Then
                        mockMvc.perform(get("/internal/dividend-statements/count-by-status")
                                        .header("X-User-Id", testUserId.toString())
                                        .param("status", "PAID"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().string("0"));
                }
        }
}
