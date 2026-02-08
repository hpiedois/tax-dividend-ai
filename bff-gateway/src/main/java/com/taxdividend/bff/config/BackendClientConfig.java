package com.taxdividend.bff.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxdividend.bff.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.taxdividend.bff.client.api.AuthApi;
import com.taxdividend.bff.client.api.DividendsApi;
import com.taxdividend.bff.client.api.FormsApi;
import com.taxdividend.bff.client.api.TaxRulesApi;
import com.taxdividend.bff.client.api.DividendStatementsApi;
import com.taxdividend.bff.client.filter.UserContextHeaderFilter;

@Configuration
public class BackendClientConfig {

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${app.security.internal-api-key}")
    private String internalApiKey;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder, ObjectMapper objectMapper) {
        return builder
                .defaultHeader("X-Internal-Api-Key", internalApiKey)
                .filter(new UserContextHeaderFilter(objectMapper))
                .build();
    }

    @Bean
    public ApiClient apiClient(WebClient webClient) {
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(backendUrl);
        return apiClient;
    }

    @Bean
    public AuthApi authApi(ApiClient apiClient) {
        return new AuthApi(apiClient);
    }

    @Bean
    public FormsApi formsApi(ApiClient apiClient) {
        return new FormsApi(apiClient);
    }

    @Bean
    public DividendsApi dividendsApi(ApiClient apiClient) {
        return new DividendsApi(apiClient);
    }

    @Bean
    public TaxRulesApi taxRulesApi(ApiClient apiClient) {
        return new TaxRulesApi(apiClient);
    }

    @Bean
    public DividendStatementsApi dividendStatementsApi(ApiClient apiClient) {
        return new DividendStatementsApi(apiClient);
    }
}
