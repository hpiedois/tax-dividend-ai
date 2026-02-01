package com.taxdividend.bff.service;

import com.taxdividend.bff.client.api.FormsApi;
import com.taxdividend.bff.client.model.FormGenerationRequest;
import com.taxdividend.bff.mapper.FormMapper;
import com.taxdividend.bff.model.GenerateTaxFormsRequest;
import com.taxdividend.bff.model.GenerateTaxFormsResponse;
import com.taxdividend.bff.model.GeneratedForm;
import com.taxdividend.bff.model.FormDownloadUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormService {

    private final FormsApi formsApi;
    private final FormMapper formMapper;

    public Mono<GenerateTaxFormsResponse> generateForms(UUID userId, GenerateTaxFormsRequest request) {
        log.info("Generating forms for user {}", userId);

        FormGenerationRequest backendRequest = new FormGenerationRequest();
        if (request.getFormType() != null) {
            backendRequest.setFormType(FormGenerationRequest.FormTypeEnum.fromValue(request.getFormType().getValue()));
        }
        backendRequest.setUserId(userId); // Use authenticated User ID
        backendRequest.setTaxYear(request.getTaxYear());
        if (request.getDividendIds() != null) {
            backendRequest.setDividendIds(request.getDividendIds().stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList()));
        }
        backendRequest.setIncludeAttestation(request.getIncludeAttestation());
        backendRequest.setIncludeDividends(request.getIncludeDividends());

        return formsApi.generateForms(userId, backendRequest)
                .map(formMapper::toGenerateResponse);
    }

    public reactor.core.publisher.Flux<GeneratedForm> listForms(UUID userId, Integer taxYear, String formType) {
        return formsApi.listForms(userId, taxYear, formType)
                .map(formMapper::toGeneratedForm);
    }

    public Mono<GeneratedForm> getForm(UUID id, UUID userId) {
        return formsApi.getForm(id, userId)
                .map(formMapper::toGeneratedForm);
    }

    public Mono<ResponseEntity<Resource>> downloadForm(UUID id, UUID userId) {
        return formsApi.downloadForm(id, userId)
                .map(file -> {
                    Resource resource = new org.springframework.core.io.FileSystemResource(file);
                    return ResponseEntity.ok()
                            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + file.getName() + "\"")
                            .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf") // Assumed
                                                                                                          // default
                            .body(resource);
                });
    }

    public Mono<FormDownloadUrlResponse> getDownloadUrl(UUID id, UUID userId, Integer expiresIn) {
        return formsApi.getFormDownloadUrl(id, userId, expiresIn)
                .map(formMapper::toDownloadResponse);
    }

    public Mono<Void> deleteForm(UUID id, UUID userId) {
        return formsApi.deleteForm(id, userId);
    }
}
