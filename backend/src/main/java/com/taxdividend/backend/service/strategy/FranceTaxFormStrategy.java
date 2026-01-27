package com.taxdividend.backend.service.strategy;

import com.taxdividend.backend.model.FormGenerationRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class FranceTaxFormStrategy implements TaxFormStrategy {

    @Override
    public boolean supports(String country) {
        return "FR".equalsIgnoreCase(country);
    }

    @Override
    public byte[] generate(FormGenerationRequest request) {
        try (PDDocument document = new PDDocument()) {

            // Generate Form 5000 (Mock for now)
            PDPage page5000 = new PDPage();
            document.addPage(page5000);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page5000)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("FORMULAIRE 5000 - ATTESTATION DE RÉSIDENCE");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("Taxpayer: " + request.getTaxpayerName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Tax Year: " + request.getTaxYear()); // request.getTaxYear() might be int, fix
                                                                             // implicit string conv if needed
                contentStream.endText();
            }

            // Generate Form 5001 (Mock for now)
            PDPage page5001 = new PDPage();
            document.addPage(page5001);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page5001)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("FORMULAIRE 5001 - LIQUIDATION");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText(
                        "Dividends Count: " + (request.getDividends() != null ? request.getDividends().size() : 0));
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating France tax forms", e);
        }
    }

    @Override
    public String getFileNamePrefix() {
        return "france-forms";
    }
}
