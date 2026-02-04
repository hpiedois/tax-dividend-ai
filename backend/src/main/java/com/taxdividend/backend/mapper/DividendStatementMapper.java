package com.taxdividend.backend.mapper;

import com.taxdividend.backend.api.dto.DividendStatementDto;
import com.taxdividend.backend.model.DividendStatement;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DividendStatementMapper {

    DividendStatementDto toDto(DividendStatement entity);

    List<DividendStatementDto> toDtoList(List<DividendStatement> entities);

    default Page<DividendStatementDto> toDtoPage(Page<DividendStatement> entityPage) {
        return entityPage.map(this::toDto);
    }

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }

    default <T> JsonNullable<T> map(T value) {
        return JsonNullable.of(value);
    }
}
