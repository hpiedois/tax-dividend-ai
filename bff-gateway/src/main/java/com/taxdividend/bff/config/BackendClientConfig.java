package com.taxdividend.bff.config;

import com.taxdividend.bff.client.api.PdfApi;
import com.taxdividend.bff.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BackendClientConfig {

    @Value("${backend.url}")
    private String backendUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public ApiClient apiClient(WebClient webClient) {
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(backendUrl);
        return apiClient;
    }

    @Bean
    public PdfApi pdfApi(ApiClient apiClient) {
        return new PdfApi(apiClient);
    }

    @Bean
    public com.taxdividend.bff.client.api.AuthApi authApi(ApiClient apiClient) {
        return new com.taxdividend.bff.client.api.AuthApi(apiClient);
    }
}
