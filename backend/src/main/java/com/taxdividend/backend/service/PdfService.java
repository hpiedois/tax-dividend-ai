package com.taxdividend.backend.service;

import com.taxdividend.backend.model.FormGenerationRequest;
import com.taxdividend.backend.service.strategy.TaxFormStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PdfService {

    private final List<TaxFormStrategy> strategies;

    public PdfService(List<TaxFormStrategy> strategies) {
        this.strategies = strategies;
    }

    public byte[] generateForms(FormGenerationRequest request) {
        String country = request.getCountry();
        if (country == null) {
            // Default or throw? For now default to FR if null, or assume validated
            country = "FR";
        }

        String finalCountry = country;
        TaxFormStrategy strategy = strategies.stream()
                .filter(s -> s.supports(finalCountry))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No tax form strategy found for country: " + finalCountry));

        return strategy.generate(request);
    }
}
