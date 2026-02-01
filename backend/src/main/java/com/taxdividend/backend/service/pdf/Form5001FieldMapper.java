package com.taxdividend.backend.service.pdf;

import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps User and Dividend data to Form 5001 (Liquidation de dividendes) PDF fields.
 *
 * Form 5001 is the dividend reclaim form listing all dividends subject to reclaim
 * with their gross amounts, withheld taxes, and reclaimable amounts.
 *
 * Note: Official Form 5001 typically has space for 10-12 dividend lines.
 * If more dividends exist, multiple forms or attachments may be needed.
 */
@Slf4j
@Component
public class Form5001FieldMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_DIVIDEND_LINES = 10;

    /**
     * Create field mappings for Form 5001.
     *
     * @param user User information
     * @param dividends List of dividends to include
     * @param taxYear Tax year
     * @return Map of PDF field names to values
     */
    public Map<String, String> mapToFormFields(User user, List<Dividend> dividends, Integer taxYear) {
        log.debug("Mapping user {} and {} dividends to Form 5001 fields for year {}",
                user.getId(), dividends.size(), taxYear);

        Map<String, String> fields = new HashMap<>();

        // Header - Personal information
        fields.put("annee", taxYear != null ? taxYear.toString() : "");
        fields.put("taxYear", taxYear != null ? taxYear.toString() : "");
        fields.put("nom", user.getFullName() != null ? user.getFullName() : "");
        fields.put("nomPrenom", user.getFullName() != null ? user.getFullName() : "");
        fields.put("adresse", user.getAddress() != null ? user.getAddress() : "");
        fields.put("nif", user.getTaxId() != null ? user.getTaxId() : "");
        fields.put("email", user.getEmail() != null ? user.getEmail() : "");

        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        fields.put("date", currentDate);
        fields.put("dateSignature", currentDate);

        // Dividend lines (limit to MAX_DIVIDEND_LINES)
        int lineCount = Math.min(dividends.size(), MAX_DIVIDEND_LINES);
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalWithheld = BigDecimal.ZERO;
        BigDecimal totalReclaimable = BigDecimal.ZERO;

        for (int i = 0; i < lineCount; i++) {
            Dividend dividend = dividends.get(i);
            int lineNum = i + 1;

            // Security name and ISIN
            fields.put("ligne" + lineNum + "_nom", dividend.getSecurityName() != null ? dividend.getSecurityName() : "");
            fields.put("ligne" + lineNum + "_isin", dividend.getIsin() != null ? dividend.getIsin() : "");
            fields.put("nomTitre" + lineNum, dividend.getSecurityName() != null ? dividend.getSecurityName() : "");
            fields.put("isin" + lineNum, dividend.getIsin() != null ? dividend.getIsin() : "");

            // Payment date
            String paymentDate = dividend.getPaymentDate() != null ?
                    dividend.getPaymentDate().format(DATE_FORMATTER) : "";
            fields.put("ligne" + lineNum + "_date", paymentDate);
            fields.put("dateVersement" + lineNum, paymentDate);

            // Amounts
            String grossAmount = formatAmount(dividend.getGrossAmount());
            String withheldTax = formatAmount(dividend.getWithholdingTax());
            String reclaimable = formatAmount(dividend.getReclaimableAmount());

            fields.put("ligne" + lineNum + "_brut", grossAmount);
            fields.put("ligne" + lineNum + "_retenue", withheldTax);
            fields.put("ligne" + lineNum + "_recuperable", reclaimable);

            fields.put("montantBrut" + lineNum, grossAmount);
            fields.put("taxeRetenue" + lineNum, withheldTax);
            fields.put("montantRecuperable" + lineNum, reclaimable);

            // Currency
            fields.put("ligne" + lineNum + "_devise", dividend.getCurrency() != null ? dividend.getCurrency() : "EUR");
            fields.put("devise" + lineNum, dividend.getCurrency() != null ? dividend.getCurrency() : "EUR");

            // Source country
            fields.put("ligne" + lineNum + "_pays", dividend.getSourceCountry() != null ? dividend.getSourceCountry() : "FR");
            fields.put("paysSource" + lineNum, dividend.getSourceCountry() != null ? dividend.getSourceCountry() : "FR");

            // Accumulate totals
            totalGross = totalGross.add(dividend.getGrossAmount());
            totalWithheld = totalWithheld.add(dividend.getWithholdingTax());
            totalReclaimable = totalReclaimable.add(dividend.getReclaimableAmount());
        }

        // Total fields
        fields.put("totalBrut", formatAmount(totalGross));
        fields.put("totalRetenue", formatAmount(totalWithheld));
        fields.put("totalRecuperable", formatAmount(totalReclaimable));
        fields.put("montantTotalBrut", formatAmount(totalGross));
        fields.put("montantTotalRetenue", formatAmount(totalWithheld));
        fields.put("montantTotalRecuperable", formatAmount(totalReclaimable));

        // Number of dividends
        fields.put("nombreDividendes", String.valueOf(dividends.size()));
        fields.put("dividendCount", String.valueOf(dividends.size()));

        if (dividends.size() > MAX_DIVIDEND_LINES) {
            log.warn("Form 5001 can only display {} dividends, but {} were provided. " +
                    "Consider splitting into multiple forms.", MAX_DIVIDEND_LINES, dividends.size());
            fields.put("note", String.format("Attention: %d dividendes au total (formulaire limité à %d lignes)",
                    dividends.size(), MAX_DIVIDEND_LINES));
        }

        log.debug("Mapped {} fields for Form 5001 with {} dividend lines", fields.size(), lineCount);
        return fields;
    }

    /**
     * Format amount for PDF form (2 decimal places).
     */
    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%.2f", amount);
    }
}
