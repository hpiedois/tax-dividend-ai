package com.taxdividend.bff.config;

import com.taxdividend.bff.agent.client.ApiClient;
import com.taxdividend.bff.agent.client.api.ParsingApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DividendParsingAgentClientConfig {

    @Value("${dividend-agent.url}")
    private String dividendAgentUrl;

    @Bean
    public ApiClient agentApiClient(WebClient.Builder builder) {
        WebClient webClient = builder.build();
        ApiClient apiClient = new ApiClient(
                webClient);
        apiClient.setBasePath(dividendAgentUrl);
        return apiClient;
    }

    @Bean
    public ParsingApi parsingApi(ApiClient agentApiClient) {
        return new ParsingApi(agentApiClient);
    }
}
