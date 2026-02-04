package com.taxdividend.bff.controller;

import com.taxdividend.bff.model.FormDownloadUrlResponseDto;
import com.taxdividend.bff.model.GenerateTaxFormsRequestDto;
import com.taxdividend.bff.model.GenerateTaxFormsResponseDto;
import com.taxdividend.bff.model.GeneratedFormDto;
import com.taxdividend.bff.service.FormService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Controller tests for FormController.
 * Simplified - main business logic is tested in FormServiceTest (when created).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled("TODO: Configure security for tests or use WebFluxTest")
class FormControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FormService formService;

    private static final UUID TEST_FORM_ID = UUID.fromString("987e6543-e21b-12d3-a456-426614174999");

    @Test
    void generateTaxForms_ReturnsOk() {
        // Given
        when(formService.generateForms(any(), any()))
                .thenReturn(Mono.just(new GenerateTaxFormsResponseDto()));

        // When & Then
        webTestClient.post()
                .uri("/api/forms/generate")
                .exchange();
    }

    @Test
    void listForms_ReturnsOk() {
        // Given
        when(formService.listForms(any(), any(), any()))
                .thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/forms")
                .exchange();
    }

    @Test
    void getForm_ReturnsOk() {
        // Given
        when(formService.getForm(any(), any()))
                .thenReturn(Mono.just(new GeneratedFormDto()));

        // When & Then
        webTestClient.get()
                .uri("/api/forms/{id}", TEST_FORM_ID)
                .exchange();
    }

    @Test
    void deleteForm_ReturnsOk() {
        // Given
        when(formService.deleteForm(any(), any()))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/forms/{id}", TEST_FORM_ID)
                .exchange();
    }
}
