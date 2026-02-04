package com.taxdividend.bff.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.taxdividend.bff.api.FormsApi;
import com.taxdividend.bff.model.GenerateTaxFormsRequestDto;
import com.taxdividend.bff.model.GenerateTaxFormsResponseDto;
import com.taxdividend.bff.model.GeneratedFormDto;
import com.taxdividend.bff.service.FormService;
import com.taxdividend.bff.model.FormDownloadUrlResponseDto;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FormController implements FormsApi {

        private final FormService formService;

        @Override
        public Mono<ResponseEntity<GenerateTaxFormsResponseDto>> generateTaxForms(
                        Mono<GenerateTaxFormsRequestDto> generateTaxFormsRequest,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> generateTaxFormsRequest
                                                .flatMap(request -> formService.generateForms(userId, request)
                                                                .map(ResponseEntity::ok)));
        }

        @Override
        public Mono<ResponseEntity<Flux<GeneratedFormDto>>> listForms(
                        Integer taxYear,
                        String formType,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .map(userId -> ResponseEntity.ok(formService.listForms(userId, taxYear, formType)));
        }

        @Override
        public Mono<ResponseEntity<GeneratedFormDto>> getForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> formService.getForm(id, userId))
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Override
        public Mono<ResponseEntity<Resource>> downloadForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return ReactiveSecurityContextHolder.getContext()
                                .map(ctx -> UUID.fromString(ctx.getAuthentication().getName()))
                                .flatMap(userId -> formService.downloadForm(id, userId));
        }

        @Override
        public Mono<ResponseEntity<FormDownloadUrlResponseDto>> getFormDownloadUrl(
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
