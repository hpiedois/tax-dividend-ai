package com.taxdividend.bff.service;

import com.taxdividend.bff.client.api.FormsApi;
import com.taxdividend.bff.client.model.FormGenerationRequest;
import com.taxdividend.bff.mapper.FormMapper;
import com.taxdividend.bff.model.GenerateTaxFormsRequestDto;
import com.taxdividend.bff.model.GenerateTaxFormsResponseDto;
import com.taxdividend.bff.model.GeneratedFormDto;
import com.taxdividend.bff.model.FormDownloadUrlResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormService {

    private final FormsApi formsApi;
    private final FormMapper formMapper;

    public Mono<GenerateTaxFormsResponseDto> generateForms(GenerateTaxFormsRequestDto request) {
        log.info("Generating forms");

        FormGenerationRequest backendRequest = new FormGenerationRequest();
        if (request.getFormType() != null) {
            backendRequest.setFormType(FormGenerationRequest.FormTypeEnum.fromValue(request.getFormType().getValue()));
        }
        // backendRequest.setUserId(userId); // Handled by X-User-Context
        backendRequest.setTaxYear(request.getTaxYear());
        if (request.getDividendIds() != null) {
            backendRequest.setDividendIds(request.getDividendIds().stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList()));
        }
        backendRequest.setIncludeAttestation(request.getIncludeAttestation());
        backendRequest.setIncludeDividends(request.getIncludeDividends());

        return formsApi.generateForms(backendRequest)
                .map(formMapper::toGenerateResponse);
    }

    public reactor.core.publisher.Flux<GeneratedFormDto> listForms(Integer taxYear, String formType) {
        return formsApi.listForms(taxYear, formType)
                .map(formMapper::toGeneratedForm);
    }

    public Mono<GeneratedFormDto> getForm(UUID id) {
        return formsApi.getForm(id)
                .map(formMapper::toGeneratedForm);
    }

    public Mono<ResponseEntity<Resource>> downloadForm(UUID id) {
        return formsApi.downloadForm(id)
                .map(file -> {
                    Resource resource = new org.springframework.core.io.FileSystemResource(file);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + file.getName() + "\"")
                            .header(HttpHeaders.CONTENT_TYPE, "application/pdf") // Assumed default
                            .body(resource);
                });
    }

    public Mono<FormDownloadUrlResponseDto> getDownloadUrl(UUID id, Integer expiresIn) {
        return formsApi.getFormDownloadUrl(id, expiresIn)
                .map(formMapper::toDownloadResponse);
    }

    public Mono<Void> deleteForm(UUID id) {
        return formsApi.deleteForm(id);
    }
}
