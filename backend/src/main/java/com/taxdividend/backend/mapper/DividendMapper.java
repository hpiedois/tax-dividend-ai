package com.taxdividend.backend.mapper;

import com.taxdividend.backend.api.dto.ListDividends200Response;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper for converting between Dividend entity and Dividend DTO.
 */
@Component
public class DividendMapper {

    public com.taxdividend.backend.api.dto.Dividend toDto(com.taxdividend.backend.model.Dividend entity) {
        if (entity == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.Dividend dto = new com.taxdividend.backend.api.dto.Dividend();
        dto.setId(entity.getId());
        dto.setSecurityName(entity.getSecurityName());
        dto.setIsin(entity.getIsin());
        dto.setGrossAmount(entity.getGrossAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setWithholdingTax(entity.getWithholdingTax());
        dto.setReclaimableAmount(entity.getReclaimableAmount());

        return dto;
    }

    public List<com.taxdividend.backend.api.dto.Dividend> toDtoList(List<com.taxdividend.backend.model.Dividend> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public ListDividends200Response toPageResponse(Page<com.taxdividend.backend.model.Dividend> page) {
        if (page == null) {
            return null;
        }

        ListDividends200Response response = new ListDividends200Response();
        response.setContent(toDtoList(page.getContent()));

        return response;
    }

    public com.taxdividend.backend.model.Dividend toEntity(com.taxdividend.backend.api.dto.Dividend dto) {
        if (dto == null) {
            return null;
        }

        com.taxdividend.backend.model.Dividend entity = new com.taxdividend.backend.model.Dividend();
        entity.setId(dto.getId());
        entity.setSecurityName(dto.getSecurityName());
        entity.setIsin(dto.getIsin());
        entity.setGrossAmount(dto.getGrossAmount());
        entity.setCurrency(dto.getCurrency());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setWithholdingTax(dto.getWithholdingTax());
        entity.setReclaimableAmount(dto.getReclaimableAmount());

        return entity;
    }
}
