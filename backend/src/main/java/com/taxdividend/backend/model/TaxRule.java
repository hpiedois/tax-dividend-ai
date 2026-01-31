package com.taxdividend.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tax Rule entity representing double taxation treaty rules.
 * Corresponds to the 'tax_rules' table in the database.
 */
@Entity
@Table(name = "tax_rules",
    indexes = {
        @Index(name = "idx_tax_rules_source_country", columnList = "sourceCountry"),
        @Index(name = "idx_tax_rules_residence_country", columnList = "residenceCountry"),
        @Index(name = "idx_tax_rules_effective_from", columnList = "effectiveFrom")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_tax_rules_unique",
            columnNames = {"source_country", "residence_country", "security_type", "effective_from"}
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Country where dividend is paid (ISO 2-letter code)
     */
    @Column(name = "source_country", nullable = false, length = 2)
    private String sourceCountry;

    /**
     * Country of tax residence (ISO 2-letter code)
     */
    @Column(name = "residence_country", nullable = false, length = 2)
    private String residenceCountry;

    /**
     * Type of security: EQUITY, BOND, REIT, etc.
     */
    @Column(name = "security_type", length = 50)
    @Builder.Default
    private String securityType = "EQUITY";

    /**
     * Standard withholding rate without treaty (%)
     */
    @Column(name = "standard_withholding_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal standardWithholdingRate;

    /**
     * Reduced rate under tax treaty (%)
     */
    @Column(name = "treaty_rate", precision = 5, scale = 2)
    private BigDecimal treatyRate;

    /**
     * Whether relief at source is available
     */
    @Column(name = "relief_at_source_available")
    @Builder.Default
    private Boolean reliefAtSourceAvailable = false;

    /**
     * Whether refund procedure is available
     */
    @Column(name = "refund_procedure_available")
    @Builder.Default
    private Boolean refundProcedureAvailable = true;

    /**
     * Date from which this rule is effective
     */
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /**
     * Date until which this rule is effective (null = indefinite)
     */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
