package com.taxdividend.backend.mapper;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Mapper for converting between Form entities/DTOs.
 */
@Component
public class FormMapper {

    public com.taxdividend.backend.api.dto.GeneratedForm toApiDto(
            com.taxdividend.backend.model.GeneratedForm entity) {
        if (entity == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.GeneratedForm api =
                new com.taxdividend.backend.api.dto.GeneratedForm();

        api.setId(entity.getId());
        api.setFormType(entity.getFormType());
        api.setTaxYear(entity.getTaxYear());
        api.setFileName(entity.getFileName());
        api.setS3Key(entity.getS3Key());
        api.setFileSize(entity.getFileSize());

        // Convert LocalDateTime to OffsetDateTime
        if (entity.getCreatedAt() != null) {
            api.setGeneratedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        }
        if (entity.getExpiresAt() != null) {
            api.setExpiresAt(entity.getExpiresAt().atOffset(ZoneOffset.UTC));
        }

        return api;
    }

    public List<com.taxdividend.backend.api.dto.GeneratedForm> toApiDtoList(
            List<com.taxdividend.backend.model.GeneratedForm> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toApiDto)
                .toList();
    }

    public com.taxdividend.backend.api.dto.GenerateFormResultDTO toApiResultDto(
            com.taxdividend.backend.dto.GenerateFormResultDTO internal) {
        if (internal == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.GenerateFormResultDTO api =
                new com.taxdividend.backend.api.dto.GenerateFormResultDTO();

        api.setSuccess(internal.getSuccess());
        api.setFormId(internal.getFormId());
        api.setFormType(internal.getFormType());
        api.setDownloadUrl(internal.getDownloadUrl());
        api.setFileName(internal.getFileName());
        api.setDividendCount(internal.getDividendCount());
        api.setErrors(internal.getErrors());

        return api;
    }
}
