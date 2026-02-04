package com.taxdividend.backend.service.strategy;

import com.taxdividend.backend.api.dto.FormGenerationRequestDto;

public interface TaxFormStrategy {

    /**
     * Determines if this strategy supports the given country code.
     * 
     * @param country Country code (ISO 2 chars, e.g. "FR")
     * @return true if supported
     */
    boolean supports(String country);

    /**
     * Generates all required tax forms for this country strategy.
     * 
     * @param request The form generation request
     * @return A byte array containing the combined PDF or serialized data
     */
    byte[] generate(FormGenerationRequestDto request);

    /**
     * Prefix for the generated file name (e.g., "france-forms")
     * 
     * @return filename prefix
     */
    String getFileNamePrefix();
}
