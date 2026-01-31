package com.taxdividend.bff.client;

import com.taxdividend.bff.model.DividendData;
import com.taxdividend.bff.model.ParsePDFResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class MockAgentClient {

    private final WebClient webClient;

    public MockAgentClient(@Qualifier("mockAgentWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ParsePDFResponse> parsePdf(Part file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file);

        return webClient.post()
                .uri("/parse")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(this::mapToResponse);
    }

    private ParsePDFResponse mapToResponse(Map<String, Object> data) {
        DividendData dividendData = new DividendData();
        dividendData.setSecurityName((String) data.getOrDefault("securityName", "Unknown"));
        dividendData.setIsin((String) data.getOrDefault("isin", ""));

        // Handle number types carefully (JSON numbers -> Double or Integer)
        Object grossAmount = data.get("grossAmount");
        if (grossAmount instanceof Number) {
            dividendData.setGrossAmount(BigDecimal.valueOf(((Number) grossAmount).doubleValue()));
        }

        Object reclaimableAmount = data.get("reclaimableAmount");
        if (reclaimableAmount instanceof Number) {
            dividendData.setReclaimableAmount(BigDecimal.valueOf(((Number) reclaimableAmount).doubleValue()));
        }

        dividendData.setCurrency("EUR"); // Default or extract

        ParsePDFResponse response = new ParsePDFResponse();
        response.setData(dividendData);
        return response;
    }
}
