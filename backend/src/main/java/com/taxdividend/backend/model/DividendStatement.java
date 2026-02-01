package com.taxdividend.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a broker dividend statement.
 * Tracks the lifecycle from upload to payment.
 */
@Entity
@Table(name = "dividend_statements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendStatement {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Fichier source
    @Column(name = "source_file_name", nullable = false)
    private String sourceFileName;

    @Column(name = "source_file_s3_key", nullable = false, length = 500)
    private String sourceFileS3Key;

    @Column(name = "broker", length = 100)
    private String broker;

    // Période couverte
    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    // Status workflow
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DividendStatementStatus status = DividendStatementStatus.UPLOADED;

    // Tracking dates
    @Column(name = "parsed_at")
    private LocalDateTime parsedAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "sent_method", length = 50)
    private String sentMethod;

    @Column(name = "sent_notes", columnDefinition = "TEXT")
    private String sentNotes;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    // Métadonnées
    @Column(name = "parsed_by", length = 50)
    private String parsedBy;

    @Column(name = "dividend_count")
    @Builder.Default
    private Integer dividendCount = 0;

    @Column(name = "total_gross_amount", precision = 12, scale = 2)
    private BigDecimal totalGrossAmount;

    @Column(name = "total_reclaimable", precision = 12, scale = 2)
    private BigDecimal totalReclaimable;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relation bidirectionnelle avec dividends
    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Dividend> dividends = new ArrayList<>();

    /**
     * Update the status and set the corresponding timestamp.
     */
    public void updateStatus(DividendStatementStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        switch (newStatus) {
            case PARSED:
                this.parsedAt = LocalDateTime.now();
                break;
            case VALIDATED:
                this.validatedAt = LocalDateTime.now();
                break;
            case SENT:
                this.sentAt = LocalDateTime.now();
                break;
            case PAID:
                this.paidAt = LocalDateTime.now();
                break;
            default:
                // UPLOADED, PARSING - no specific timestamp
                break;
        }
    }

    /**
     * Validate status transition.
     * Throws IllegalStateException if transition is invalid.
     */
    public void validateTransition(DividendStatementStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition: %s → %s", status, newStatus));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
