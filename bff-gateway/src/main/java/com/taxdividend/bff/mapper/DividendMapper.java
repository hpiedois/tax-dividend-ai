package com.taxdividend.bff.mapper;

import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.model.DividendCaseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DividendMapper {

    DividendMapper INSTANCE = Mappers.getMapper(DividendMapper.class);

    @Mapping(target = "date", source = "paymentDate")
    @Mapping(target = "security", source = "securityName")
    @Mapping(target = "reclaimedAmount", source = "reclaimableAmount")
    @Mapping(target = "status", expression = "java(source.getStatus() != null ? com.taxdividend.bff.model.DividendCaseDto.StatusEnum.fromValue(source.getStatus().name()) : com.taxdividend.bff.model.DividendCaseDto.StatusEnum.OPEN)")
    DividendCaseDto toDividendCase(Dividend source);
}
