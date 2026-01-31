package com.taxdividend.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dividend entity representing dividend data extracted from bank statements or manually entered.
 * Corresponds to the 'dividends' table in the database.
 */
@Entity
@Table(name = "dividends", indexes = {
    @Index(name = "idx_dividends_form_id", columnList = "form_id"),
    @Index(name = "idx_dividends_user_id", columnList = "user_id"),
    @Index(name = "idx_dividends_isin", columnList = "isin"),
    @Index(name = "idx_dividends_payment_date", columnList = "paymentDate"),
    @Index(name = "idx_dividends_source_country", columnList = "sourceCountry")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dividend {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private GeneratedForm form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "security_name", nullable = false, length = 255)
    private String securityName;

    /**
     * International Securities Identification Number (12 chars)
     */
    @Column(nullable = false, length = 12)
    private String isin;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "withholding_tax", nullable = false, precision = 12, scale = 2)
    private BigDecimal withholdingTax;

    /**
     * Actual withholding rate applied (%)
     */
    @Column(name = "withholding_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal withholdingRate;

    /**
     * Amount that can be reclaimed (withheld - treaty)
     */
    @Column(name = "reclaimable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal reclaimableAmount;

    /**
     * Tax treaty reduced rate (%)
     */
    @Column(name = "treaty_rate", precision = 5, scale = 2)
    private BigDecimal treatyRate;

    /**
     * Country where dividend was paid (ISO 2-letter code)
     */
    @Column(name = "source_country", nullable = false, length = 2)
    private String sourceCountry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
