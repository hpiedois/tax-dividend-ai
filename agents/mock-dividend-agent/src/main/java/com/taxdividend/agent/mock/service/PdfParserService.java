package com.taxdividend.agent.mock.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PdfParserService {

    public Map<String, Object> parseDividendPdf(MultipartFile file) throws IOException {
        log.info("Parsing PDF file: {}", file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream();
                PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) { // PDFBox 3.x uses Loader

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug("Extracted text length: {}", text.length());

            // Simple mock extraction logic (to be improved)
            Map<String, Object> result = new HashMap<>();
            result.put("rawTextFragment", text.substring(0, Math.min(text.length(), 100)));
            result.put("securityName", "Mock Security from PDF");
            result.put("isin", "FR0000000000");
            result.put("grossAmount", BigDecimal.valueOf(100.00));
            result.put("broker", "Mock Broker (IBKR)");

            // Here you would implement real regex parsing

            return result;
        }
    }
}
