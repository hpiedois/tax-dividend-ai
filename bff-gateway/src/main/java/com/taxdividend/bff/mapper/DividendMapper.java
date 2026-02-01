package com.taxdividend.bff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

// Use fully qualified names to avoid confusion between Client and Server models
@Mapper(componentModel = "spring")
public interface DividendMapper {

    DividendMapper INSTANCE = Mappers.getMapper(DividendMapper.class);

    @Mapping(target = "date", source = "paymentDate")
    @Mapping(target = "security", source = "securityName")
    @Mapping(target = "reclaimedAmount", source = "reclaimableAmount")
    @Mapping(target = "status", expression = "java(source.getStatus() != null ? com.taxdividend.bff.model.DividendCase.StatusEnum.fromValue(source.getStatus().name()) : com.taxdividend.bff.model.DividendCase.StatusEnum.OPEN)")
    com.taxdividend.bff.model.DividendCase toDividendCase(com.taxdividend.bff.client.model.Dividend source);
}
