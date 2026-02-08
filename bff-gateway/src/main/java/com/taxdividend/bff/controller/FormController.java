package com.taxdividend.bff.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

                return generateTaxFormsRequest
                                .flatMap(request -> formService.generateForms(request)
                                                .map(ResponseEntity::ok));
        }

        @Override
        public Mono<ResponseEntity<Flux<GeneratedFormDto>>> listForms(
                        Integer taxYear,
                        String formType,
                        ServerWebExchange exchange) {

                return Mono.just(ResponseEntity.ok(formService.listForms(taxYear, formType)));
        }

        @Override
        public Mono<ResponseEntity<GeneratedFormDto>> getForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return formService.getForm(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Override
        public Mono<ResponseEntity<Resource>> downloadForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return formService.downloadForm(id);
        }

        @Override
        public Mono<ResponseEntity<FormDownloadUrlResponseDto>> getFormDownloadUrl(
                        UUID id,
                        Integer expiresIn,
                        ServerWebExchange exchange) {

                return formService.getDownloadUrl(id, expiresIn)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteForm(
                        UUID id,
                        ServerWebExchange exchange) {

                return formService.deleteForm(id)
                                .then(Mono.just(ResponseEntity.noContent().build()));
        }
}
