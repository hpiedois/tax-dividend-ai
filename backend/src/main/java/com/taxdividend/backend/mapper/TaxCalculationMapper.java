package com.taxdividend.backend.mapper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for converting between TaxCalculation DTOs (internal vs API).
 */
@Component
public class TaxCalculationMapper {

    public com.taxdividend.backend.api.dto.TaxCalculationResultDTO toApiDto(
            com.taxdividend.backend.dto.TaxCalculationResultDTO internal) {
        if (internal == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.TaxCalculationResultDTO api =
                new com.taxdividend.backend.api.dto.TaxCalculationResultDTO();

        api.setDividendId(internal.getDividendId());
        api.setSuccess(internal.getTreatyApplied() != null ? internal.getTreatyApplied() : false);
        api.setReclaimableAmount(internal.getReclaimableAmount());
        api.setStandardRate(internal.getWithholdingRate());
        api.setTreatyRate(internal.getTreatyRate());
        api.setWithheldAmount(internal.getWithholdingTax());

        if (internal.getNotes() != null && !internal.getNotes().isEmpty()) {
            List<String> errors = new ArrayList<>();
            errors.add(internal.getNotes());
            api.setErrors(errors);
        }

        return api;
    }

    public com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO toApiBatchDto(
            com.taxdividend.backend.dto.TaxCalculationBatchResultDTO internal) {
        if (internal == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO api =
                new com.taxdividend.backend.api.dto.TaxCalculationBatchResultDTO();

        api.setSuccessCount(internal.getSuccessCount());
        api.setFailureCount(internal.getFailureCount());
        api.setTotalReclaimableAmount(internal.getTotalReclaimableAmount());

        if (internal.getResults() != null) {
            List<com.taxdividend.backend.api.dto.TaxCalculationResultDTO> apiResults =
                    internal.getResults().stream()
                            .map(this::toApiDto)
                            .toList();
            api.setResults(apiResults);
        }

        return api;
    }
}
