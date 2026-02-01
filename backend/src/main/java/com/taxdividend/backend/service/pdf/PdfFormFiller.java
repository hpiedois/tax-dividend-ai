package com.taxdividend.backend.service.pdf;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Helper class for filling PDF forms using official French tax form templates.
 *
 * Uses Apache PDFBox to load templates and fill form fields.
 */
@Slf4j
@Component
public class PdfFormFiller {

    /**
     * Fill a PDF form template with provided field values.
     *
     * @param templatePath Path to template in classpath (e.g., "templates/forms/form_5000_template.pdf")
     * @param fieldValues Map of field names to values
     * @param flatten Whether to flatten the PDF (make fields non-editable)
     * @return Filled PDF as byte array
     * @throws IOException if template cannot be loaded or filled
     */
    public byte[] fillPdfForm(String templatePath, Map<String, String> fieldValues, boolean flatten)
            throws IOException {

        log.debug("Loading PDF template from: {}", templatePath);

        try (InputStream templateStream = new ClassPathResource(templatePath).getInputStream();
                PDDocument document = Loader.loadPDF(templateStream.readAllBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            if (acroForm == null) {
                log.warn("Template {} has no fillable form fields - using as-is", templatePath);
                document.save(baos);
                return baos.toByteArray();
            }

            log.debug("Template has {} form fields", acroForm.getFields().size());

            // Fill each field
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                try {
                    PDField field = acroForm.getField(fieldName);
                    if (field != null) {
                        field.setValue(fieldValue);
                        log.debug("Set field '{}' to '{}'", fieldName, fieldValue);
                    } else {
                        log.warn("Field '{}' not found in template", fieldName);
                    }
                } catch (IOException e) {
                    log.error("Failed to set field '{}': {}", fieldName, e.getMessage());
                }
            }

            // Flatten if requested (makes fields non-editable)
            if (flatten) {
                acroForm.flatten();
                log.debug("PDF form flattened");
            }

            document.save(baos);
            log.info("PDF form filled successfully with {} fields", fieldValues.size());

            return baos.toByteArray();
        }
    }

    /**
     * List all form fields in a PDF template (useful for debugging).
     *
     * @param templatePath Path to template in classpath
     * @return Map of field names to field types
     * @throws IOException if template cannot be loaded
     */
    public Map<String, String> listFormFields(String templatePath) throws IOException {
        try (InputStream templateStream = new ClassPathResource(templatePath).getInputStream();
                PDDocument document = Loader.loadPDF(templateStream.readAllBytes())) {

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            if (acroForm == null) {
                log.warn("Template {} has no form fields", templatePath);
                return Map.of();
            }

            Map<String, String> fields = new java.util.HashMap<>();
            for (PDField field : acroForm.getFields()) {
                String fieldType = field.getFieldType();
                fields.put(field.getFullyQualifiedName(), fieldType);
            }

            return fields;
        }
    }
}
