package com.taxdividend.bff.mapper;

import com.taxdividend.bff.client.model.FormDownloadUrlResponse;
import com.taxdividend.bff.client.model.GenerateFormResultDTO;
import com.taxdividend.bff.client.model.GeneratedForm;
import com.taxdividend.bff.model.GenerateTaxFormsResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T09:58:27+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Homebrew)"
)
@Component
public class FormMapperImpl implements FormMapper {

    @Override
    public com.taxdividend.bff.model.GeneratedForm toGeneratedForm(GeneratedForm source) {
        if ( source == null ) {
            return null;
        }

        com.taxdividend.bff.model.GeneratedForm generatedForm = new com.taxdividend.bff.model.GeneratedForm();

        generatedForm.setId( source.getId() );
        generatedForm.setFormType( source.getFormType() );
        generatedForm.setTaxYear( source.getTaxYear() );
        generatedForm.setFileName( source.getFileName() );
        generatedForm.setGeneratedAt( source.getGeneratedAt() );

        return generatedForm;
    }

    @Override
    public GenerateTaxFormsResponse toGenerateResponse(GenerateFormResultDTO source) {
        if ( source == null ) {
            return null;
        }

        GenerateTaxFormsResponse generateTaxFormsResponse = new GenerateTaxFormsResponse();

        generateTaxFormsResponse.setFormUrl( source.getDownloadUrl() );

        return generateTaxFormsResponse;
    }

    @Override
    public com.taxdividend.bff.model.FormDownloadUrlResponse toDownloadResponse(FormDownloadUrlResponse source) {
        if ( source == null ) {
            return null;
        }

        com.taxdividend.bff.model.FormDownloadUrlResponse formDownloadUrlResponse = new com.taxdividend.bff.model.FormDownloadUrlResponse();

        formDownloadUrlResponse.setUrl( source.getUrl() );
        formDownloadUrlResponse.setExpiresAt( source.getExpiresAt() );

        return formDownloadUrlResponse;
    }
}
