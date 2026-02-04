package com.taxdividend.bff.mapper;

import com.taxdividend.bff.client.model.FormDownloadUrlResponse;
import com.taxdividend.bff.client.model.GenerateFormResult;
import com.taxdividend.bff.client.model.GeneratedForm;
import com.taxdividend.bff.model.GeneratedFormDto;
import com.taxdividend.bff.model.FormDownloadUrlResponseDto;
import com.taxdividend.bff.model.GenerateTaxFormsResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FormMapper {

        FormMapper INSTANCE = Mappers.getMapper(FormMapper.class);

        // Assuming identical fields for now as they come from same spec source usually
        GeneratedFormDto toGeneratedForm(GeneratedForm source);

        // Backend has downloadUrl (String), Frontend expects formUrl (String)
        @Mapping(target = "formUrl", source = "downloadUrl")
        GenerateTaxFormsResponseDto toGenerateResponse(GenerateFormResult source);

        FormDownloadUrlResponseDto toDownloadResponse(FormDownloadUrlResponse source);
}
