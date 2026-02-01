package com.taxdividend.backend.mapper;

import com.taxdividend.backend.dto.DividendStatementDTO;
import com.taxdividend.backend.model.DividendStatement;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for DividendStatement entity to DTO.
 */
@Component
public class DividendStatementMapper {

    /**
     * Convert entity to DTO.
     */
    public DividendStatementDTO toDto(DividendStatement entity) {
        if (entity == null) {
            return null;
        }

        return DividendStatementDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .sourceFileName(entity.getSourceFileName())
                .sourceFileS3Key(entity.getSourceFileS3Key())
                .broker(entity.getBroker())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .status(entity.getStatus())
                .parsedAt(entity.getParsedAt())
                .validatedAt(entity.getValidatedAt())
                .sentAt(entity.getSentAt())
                .sentMethod(entity.getSentMethod())
                .sentNotes(entity.getSentNotes())
                .paidAt(entity.getPaidAt())
                .paidAmount(entity.getPaidAmount())
                .parsedBy(entity.getParsedBy())
                .dividendCount(entity.getDividendCount())
                .totalGrossAmount(entity.getTotalGrossAmount())
                .totalReclaimable(entity.getTotalReclaimable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of entities to list of DTOs.
     */
    public List<DividendStatementDTO> toDtoList(List<DividendStatement> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert Page of entities to Page of DTOs.
     */
    public Page<DividendStatementDTO> toDtoPage(Page<DividendStatement> entityPage) {
        return entityPage.map(this::toDto);
    }

    /**
     * Convert internal DTO to API DTO.
     */
    public com.taxdividend.backend.api.dto.DividendStatement toApiDto(DividendStatementDTO internal) {
        if (internal == null) {
            return null;
        }

        com.taxdividend.backend.api.dto.DividendStatement api = new com.taxdividend.backend.api.dto.DividendStatement();
        api.setId(internal.getId());
        api.setUserId(internal.getUserId());
        api.setSourceFileName(internal.getSourceFileName());
        api.setSourceFileS3Key(internal.getSourceFileS3Key());
        api.setBroker(internal.getBroker());
        api.setPeriodStart(internal.getPeriodStart());
        api.setPeriodEnd(internal.getPeriodEnd());

        // Convert internal status enum to API status enum
        if (internal.getStatus() != null) {
            api.setStatus(com.taxdividend.backend.api.dto.DividendStatementStatus.fromValue(
                    internal.getStatus().name()));
        }

        // Convert nullable LocalDateTime to JsonNullable<OffsetDateTime>
        api.setParsedAt(toJsonNullableOffsetDateTime(internal.getParsedAt()));
        api.setValidatedAt(toJsonNullableOffsetDateTime(internal.getValidatedAt()));
        api.setSentAt(toJsonNullableOffsetDateTime(internal.getSentAt()));
        api.setSentMethod(JsonNullable.of(internal.getSentMethod()));
        api.setSentNotes(JsonNullable.of(internal.getSentNotes()));
        api.setPaidAt(toJsonNullableOffsetDateTime(internal.getPaidAt()));
        api.setPaidAmount(JsonNullable.of(internal.getPaidAmount()));
        api.setParsedBy(JsonNullable.of(internal.getParsedBy()));
        api.setDividendCount(internal.getDividendCount());
        api.setTotalGrossAmount(internal.getTotalGrossAmount());
        api.setTotalReclaimable(internal.getTotalReclaimable());

        // Convert LocalDateTime to OffsetDateTime for non-nullable fields
        api.setCreatedAt(toOffsetDateTime(internal.getCreatedAt()));
        api.setUpdatedAt(toOffsetDateTime(internal.getUpdatedAt()));

        return api;
    }

    /**
     * Convert API update DTO to internal update DTO.
     */
    public com.taxdividend.backend.dto.DividendStatementUpdateDTO toInternalUpdateDto(
            com.taxdividend.backend.api.dto.DividendStatementUpdateDTO api) {

        if (api == null) {
            return null;
        }

        return com.taxdividend.backend.dto.DividendStatementUpdateDTO.builder()
                .status(com.taxdividend.backend.model.DividendStatementStatus.valueOf(
                        api.getStatus().name()))
                .sentMethod(api.getSentMethod())
                .sentNotes(api.getSentNotes())
                .paidAmount(api.getPaidAmount())
                .paidAt(api.getPaidAt() != null ? api.getPaidAt().toLocalDateTime() : null)
                .parsedBy(api.getParsedBy())
                .build();
    }

    /**
     * Helper: Convert LocalDateTime to OffsetDateTime (UTC).
     */
    private OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    /**
     * Helper: Convert LocalDateTime to JsonNullable<OffsetDateTime>.
     */
    private JsonNullable<OffsetDateTime> toJsonNullableOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? JsonNullable.of(localDateTime.atOffset(ZoneOffset.UTC)) : JsonNullable.undefined();
    }

    /**
     * Helper: Extract value from JsonNullable.
     */
    private <T> T fromJsonNullable(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent() ? nullable.get() : null;
    }

    /**
     * Helper: Convert JsonNullable<OffsetDateTime> to LocalDateTime.
     */
    private LocalDateTime fromJsonNullableOffsetDateTime(JsonNullable<OffsetDateTime> nullable) {
        if (nullable == null || !nullable.isPresent()) {
            return null;
        }
        OffsetDateTime offsetDateTime = nullable.get();
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
}
