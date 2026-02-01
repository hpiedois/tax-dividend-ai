package com.taxdividend.bff.controller;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

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

        private final com.taxdividend.bff.service.FormService formService;

        @Override
        public Mono<ResponseEntity<GenerateTaxFormsResponse>> generateTaxForms(
                        Mono<GenerateTaxFormsRequest> generateTaxFormsRequest,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> generateTaxFormsRequest
                                                .flatMap(request -> formService.generateForms(userId, request)
                                                                .map(ResponseEntity::ok)));
        }

        @Override
        public Mono<ResponseEntity<reactor.core.publisher.Flux<com.taxdividend.bff.model.GeneratedForm>>> listForms(
                        Integer taxYear,
                        String formType,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .map(userId -> ResponseEntity.ok(formService.listForms(userId, taxYear, formType)));
        }

        @Override
        public Mono<ResponseEntity<com.taxdividend.bff.model.GeneratedForm>> getForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> formService.getForm(id, userId))
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Override
        public Mono<ResponseEntity<org.springframework.core.io.Resource>> downloadForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> formService.downloadForm(id, userId));
        }

        @Override
        public Mono<ResponseEntity<com.taxdividend.bff.model.FormDownloadUrlResponse>> getFormDownloadUrl(
                        UUID id,
                        Integer expiresIn,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> formService.getDownloadUrl(id, userId, expiresIn))
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> formService.deleteForm(id, userId))
                                .then(Mono.just(ResponseEntity.noContent().build()));
        }
}
