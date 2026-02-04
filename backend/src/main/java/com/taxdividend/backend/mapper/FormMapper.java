package com.taxdividend.backend.mapper;

import com.taxdividend.backend.api.dto.GeneratedFormDto;
import com.taxdividend.backend.model.GeneratedForm;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FormMapper {

    FormMapper INSTANCE = Mappers.getMapper(FormMapper.class);

    GeneratedFormDto toApiDto(GeneratedForm source);

    List<GeneratedFormDto> toGeneratedFormDto(List<GeneratedForm> source);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
