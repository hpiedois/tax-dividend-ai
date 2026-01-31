package com.taxdividend.bff.controller;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.taxdividend.bff.client.api.FormsApi;
import com.taxdividend.bff.client.api.PdfApi;
import com.taxdividend.bff.client.model.FormGenerationRequest;
import com.taxdividend.bff.model.GenerateTaxFormsRequest;
import com.taxdividend.bff.model.GenerateTaxFormsResponse;

import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FormController implements com.taxdividend.bff.api.FormsApi {

    private final FormsApi formsApi;
    private final PdfApi pdfApi;

    @Override
    public Mono<ResponseEntity<GenerateTaxFormsResponse>> generateTaxForms(
            Mono<GenerateTaxFormsRequest> generateTaxFormsRequest,
            ServerWebExchange exchange) {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName()) // Extract 'sub' (userId)
                .flatMap(userId -> generateTaxFormsRequest.flatMap(request -> {
                    FormGenerationRequest backendRequest = new FormGenerationRequest();

                    // Map fields
                    if (request.getFormType() != null) {
                        backendRequest.setFormType(FormGenerationRequest.FormTypeEnum
                                .fromValue(request.getFormType().getValue()));
                    }

                    try {
                        backendRequest.setUserId(UUID.fromString(userId));
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new IllegalArgumentException("Invalid user ID format in token: " + userId));
                    }

                    backendRequest.setTaxYear(request.getTaxYear());
                    if (request.getDividendIds() != null) {
                        backendRequest.setDividendIds(request.getDividendIds().stream()
                                .map(UUID::fromString)
                                .collect(Collectors.toList()));
                    }
                    backendRequest.setIncludeAttestation(request.getIncludeAttestation());
                    backendRequest.setIncludeDividends(request.getIncludeDividends());

                    // Delegate to backend
                    return formsApi.generateForms(UUID.fromString(userId), backendRequest)
                            .map(backendResponse -> {
                                GenerateTaxFormsResponse response = new GenerateTaxFormsResponse();
                                response.setFormUrl(backendResponse.getDownloadUrl());
                                return ResponseEntity.ok(response);
                            });
                }));
    }
}
