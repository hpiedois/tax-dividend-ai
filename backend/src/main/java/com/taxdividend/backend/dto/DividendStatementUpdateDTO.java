package com.taxdividend.backend.dto;

import com.taxdividend.backend.model.DividendStatementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for updating dividend statement status.
 * Used by AI Agent (PARSING, PARSED, VALIDATED) and users (SENT, PAID).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendStatementUpdateDTO {

    /**
     * New status.
     * Required.
     */
    private DividendStatementStatus status;

    /**
     * For SENT status: submission method.
     * Optional.
     */
    private String sentMethod; // EMAIL, POSTAL, ONLINE

    /**
     * For SENT status: additional notes.
     * Optional.
     */
    private String sentNotes;

    /**
     * For PAID status: amount received.
     * Optional.
     */
    private BigDecimal paidAmount;

    /**
     * For PAID status: payment date.
     * Optional (defaults to now if not provided).
     */
    private LocalDateTime paidAt;

    /**
     * For AI Agent updates: who triggered the parsing.
     * Optional.
     */
    private String parsedBy;
}
