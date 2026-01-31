package com.taxdividend.backend.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between TaxRule entities and DTOs.
 */
@Component
public class TaxRuleMapper {

    public com.taxdividend.backend.api.dto.TaxRule toApiDto(
            com.taxdividend.backend.model.TaxRule entity) {
        if (entity == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.TaxRule api =
                new com.taxdividend.backend.api.dto.TaxRule();

        api.setId(entity.getId());
        api.setSourceCountry(entity.getSourceCountry());
        api.setResidenceCountry(entity.getResidenceCountry());
        api.setSecurityType(entity.getSecurityType());
        api.setStandardWithholdingRate(entity.getStandardWithholdingRate());
        api.setTreatyRate(entity.getTreatyRate());
        api.setReliefAtSourceAvailable(entity.getReliefAtSourceAvailable());
        api.setRefundProcedureAvailable(entity.getRefundProcedureAvailable());
        api.setEffectiveFrom(entity.getEffectiveFrom());
        api.setEffectiveTo(entity.getEffectiveTo());
        api.setNotes(entity.getNotes());

        return api;
    }

    public List<com.taxdividend.backend.api.dto.TaxRule> toApiDtoList(
            List<com.taxdividend.backend.model.TaxRule> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toApiDto)
                .toList();
    }

    public com.taxdividend.backend.api.dto.TreatyRateResponse toTreatyRateResponse(
            com.taxdividend.backend.model.TaxRule entity) {
        if (entity == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.TreatyRateResponse response =
                new com.taxdividend.backend.api.dto.TreatyRateResponse();

        response.setStandardRate(entity.getStandardWithholdingRate());
        response.setTreatyRate(entity.getTreatyRate());
        response.setReliefAtSourceAvailable(entity.getReliefAtSourceAvailable());
        response.setRefundProcedureAvailable(entity.getRefundProcedureAvailable());
        response.setNotes(entity.getNotes());

        return response;
    }
}
