package com.taxdividend.backend.dto;

import com.taxdividend.backend.model.DividendStatementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for dividend statement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendStatementDTO {

    private UUID id;
    private UUID userId;

    // Fichier source
    private String sourceFileName;
    private String sourceFileS3Key;
    private String broker;

    // Période
    private LocalDate periodStart;
    private LocalDate periodEnd;

    // Status
    private DividendStatementStatus status;

    // Tracking dates
    private LocalDateTime parsedAt;
    private LocalDateTime validatedAt;
    private LocalDateTime sentAt;
    private String sentMethod;
    private String sentNotes;
    private LocalDateTime paidAt;
    private BigDecimal paidAmount;

    // Métadonnées
    private String parsedBy;
    private Integer dividendCount;
    private BigDecimal totalGrossAmount;
    private BigDecimal totalReclaimable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
