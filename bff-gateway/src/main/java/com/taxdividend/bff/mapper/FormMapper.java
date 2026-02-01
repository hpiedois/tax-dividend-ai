package com.taxdividend.bff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FormMapper {

        FormMapper INSTANCE = Mappers.getMapper(FormMapper.class);

        // Assuming identical fields for now as they come from same spec source usually
        com.taxdividend.bff.model.GeneratedForm toGeneratedForm(com.taxdividend.bff.client.model.GeneratedForm source);

        // Backend has downloadUrl (String), Frontend expects formUrl (String)
        @Mapping(target = "formUrl", source = "downloadUrl")
        com.taxdividend.bff.model.GenerateTaxFormsResponse toGenerateResponse(
                        com.taxdividend.bff.client.model.GenerateFormResultDTO source);

        com.taxdividend.bff.model.FormDownloadUrlResponse toDownloadResponse(
                        com.taxdividend.bff.client.model.FormDownloadUrlResponse source);
}
