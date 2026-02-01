package com.taxdividend.backend.service.pdf;

import com.taxdividend.backend.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps User data to Form 5000 (Attestation de résidence fiscale) PDF fields.
 *
 * Form 5000 is the Swiss tax residence certificate required for claiming
 * tax treaty benefits in France.
 *
 * Note: Field names are based on the official French tax form.
 * If actual field names differ, they can be adjusted here.
 */
@Slf4j
@Component
public class Form5000FieldMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Create field mappings for Form 5000.
     *
     * @param user User information
     * @param taxYear Tax year
     * @return Map of PDF field names to values
     */
    public Map<String, String> mapToFormFields(User user, Integer taxYear) {
        log.debug("Mapping user {} to Form 5000 fields for year {}", user.getId(), taxYear);

        Map<String, String> fields = new HashMap<>();

        // Tax year
        fields.put("annee", taxYear != null ? taxYear.toString() : "");
        fields.put("taxYear", taxYear != null ? taxYear.toString() : "");

        // Personal information
        fields.put("nom", user.getFullName() != null ? user.getFullName() : "");
        fields.put("fullName", user.getFullName() != null ? user.getFullName() : "");
        fields.put("nomPrenom", user.getFullName() != null ? user.getFullName() : "");

        // Address
        fields.put("adresse", user.getAddress() != null ? user.getAddress() : "");
        fields.put("address", user.getAddress() != null ? user.getAddress() : "");
        fields.put("rue", user.getAddress() != null ? user.getAddress() : "");

        // Canton
        fields.put("canton", user.getCanton() != null ? user.getCanton() : "");
        fields.put("lieu", user.getCanton() != null ? user.getCanton() : "");

        // Country
        fields.put("pays", "Suisse");
        fields.put("country", "Switzerland");

        // Tax ID (NIF - Numéro d'Identification Fiscale)
        fields.put("nif", user.getTaxId() != null ? user.getTaxId() : "");
        fields.put("taxId", user.getTaxId() != null ? user.getTaxId() : "");
        fields.put("numeroFiscal", user.getTaxId() != null ? user.getTaxId() : "");

        // Email
        fields.put("email", user.getEmail() != null ? user.getEmail() : "");
        fields.put("courriel", user.getEmail() != null ? user.getEmail() : "");

        // Date
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        fields.put("date", currentDate);
        fields.put("dateSignature", currentDate);
        fields.put("dateAttestation", currentDate);

        // Postal code (extract from address if possible, otherwise empty)
        fields.put("codePostal", extractPostalCode(user.getAddress()));
        fields.put("postalCode", extractPostalCode(user.getAddress()));

        log.debug("Mapped {} fields for Form 5000", fields.size());
        return fields;
    }

    /**
     * Try to extract postal code from address string.
     * Swiss postal codes are 4 digits.
     *
     * @param address Full address string
     * @return Postal code if found, empty string otherwise
     */
    private String extractPostalCode(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }

        // Look for 4-digit postal code in address
        String[] parts = address.split("[\\s,]+");
        for (String part : parts) {
            if (part.matches("\\d{4}")) {
                return part;
            }
        }

        return "";
    }
}
