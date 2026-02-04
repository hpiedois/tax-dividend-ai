package com.taxdividend.backend.mapper;

import com.taxdividend.backend.api.dto.TaxRuleDto;
import com.taxdividend.backend.api.dto.TreatyRateResponseDto;
import com.taxdividend.backend.model.TaxRule;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Mapper for converting between TaxRule entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface TaxRuleMapper {

    TaxRuleDto toApiDto(TaxRule entity);

    List<TaxRuleDto> toApiDtoList(List<TaxRule> entities);

    @Mapping(target = "standardRate", source = "standardWithholdingRate")
    TreatyRateResponseDto toTreatyRateResponse(TaxRule entity);

    default OffsetDateTime map(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
