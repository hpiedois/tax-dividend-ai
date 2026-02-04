package com.taxdividend.backend.mapper;

import com.taxdividend.backend.api.dto.DividendDto;
import com.taxdividend.backend.api.dto.PaginatedDividendListDto;
import com.taxdividend.backend.model.Dividend;
import com.taxdividend.backend.model.DividendStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Mapper for converting between Dividend entity and Dividend DTO.
 */
@Mapper(componentModel = "spring")
public interface DividendMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToDto")
    DividendDto toDto(Dividend entity);

    List<DividendDto> toDtoList(List<Dividend> entities);

    default PaginatedDividendListDto toPageResponse(Page<Dividend> page) {
        if (page == null) {
            return null;
        }

        PaginatedDividendListDto response = new PaginatedDividendListDto();
        response.setContent(toDtoList(page.getContent()));
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setSize(page.getSize());
        response.setNumber(page.getNumber());

        return response;
    }

    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToEntity")
    Dividend toEntity(DividendDto dto);

    default OffsetDateTime map(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    @Named("mapStatusToDto")
    default DividendDto.StatusEnum mapStatusToDto(DividendStatus status) {
        if (status == null) {
            return DividendDto.StatusEnum.OPEN;
        }
        return DividendDto.StatusEnum.valueOf(status.name());
    }

    @Named("mapStatusToEntity")
    default DividendStatus mapStatusToEntity(DividendDto.StatusEnum status) {
        if (status == null) {
            return DividendStatus.OPEN;
        }
        return DividendStatus.valueOf(status.name());
    }
}
