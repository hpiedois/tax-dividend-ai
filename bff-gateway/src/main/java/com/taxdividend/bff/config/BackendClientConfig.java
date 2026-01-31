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
import com.taxdividend.bff.client.api.PdfApi;
import com.taxdividend.bff.client.api.TaxRulesApi;
import com.taxdividend.bff.client.filter.UserContextHeaderFilter;

@Configuration
public class BackendClientConfig {

    @Value("${backend.url}")
    private String backendUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder, ObjectMapper objectMapper) {
        return builder
                .filter(new UserContextHeaderFilter(objectMapper))
                .build();
    }

    @Value("${mock-agent.url}")
    private String mockAgentUrl;

    @Bean("mockAgentWebClient")
    public WebClient mockAgentWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(mockAgentUrl)
                .build();
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
}
