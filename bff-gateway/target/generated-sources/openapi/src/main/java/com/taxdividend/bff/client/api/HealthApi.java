package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import com.taxdividend.bff.client.model.HealthCheckResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-01-31T12:02:59.936362+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class HealthApi {
    private ApiClient apiClient;

    public HealthApi() {
        this(new ApiClient());
    }

    public HealthApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Deep health check
     * 
     * <p><b>200</b> - Health status
     * @return HealthCheckResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deepHealthCheckRequestCreation() throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<HealthCheckResponse> localVarReturnType = new ParameterizedTypeReference<HealthCheckResponse>() {};
        return apiClient.invokeAPI("/health/deep", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Deep health check
     * 
     * <p><b>200</b> - Health status
     * @return HealthCheckResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<HealthCheckResponse> deepHealthCheck() throws WebClientResponseException {
        ParameterizedTypeReference<HealthCheckResponse> localVarReturnType = new ParameterizedTypeReference<HealthCheckResponse>() {};
        return deepHealthCheckRequestCreation().bodyToMono(localVarReturnType);
    }

    /**
     * Deep health check
     * 
     * <p><b>200</b> - Health status
     * @return ResponseEntity&lt;HealthCheckResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<HealthCheckResponse>> deepHealthCheckWithHttpInfo() throws WebClientResponseException {
        ParameterizedTypeReference<HealthCheckResponse> localVarReturnType = new ParameterizedTypeReference<HealthCheckResponse>() {};
        return deepHealthCheckRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Deep health check
     * 
     * <p><b>200</b> - Health status
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deepHealthCheckWithResponseSpec() throws WebClientResponseException {
        return deepHealthCheckRequestCreation();
    }

    /**
     * Liveness probe
     * 
     * <p><b>200</b> - Alive
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec livenessRequestCreation() throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/health/live", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Liveness probe
     * 
     * <p><b>200</b> - Alive
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> liveness() throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return livenessRequestCreation().bodyToMono(localVarReturnType);
    }

    /**
     * Liveness probe
     * 
     * <p><b>200</b> - Alive
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> livenessWithHttpInfo() throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return livenessRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Liveness probe
     * 
     * <p><b>200</b> - Alive
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec livenessWithResponseSpec() throws WebClientResponseException {
        return livenessRequestCreation();
    }

    /**
     * Readiness probe
     * 
     * <p><b>200</b> - Ready
     * <p><b>503</b> - Not ready
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec readinessRequestCreation() throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/health/ready", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Readiness probe
     * 
     * <p><b>200</b> - Ready
     * <p><b>503</b> - Not ready
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> readiness() throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return readinessRequestCreation().bodyToMono(localVarReturnType);
    }

    /**
     * Readiness probe
     * 
     * <p><b>200</b> - Ready
     * <p><b>503</b> - Not ready
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> readinessWithHttpInfo() throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return readinessRequestCreation().toEntity(localVarReturnType);
    }

    /**
     * Readiness probe
     * 
     * <p><b>200</b> - Ready
     * <p><b>503</b> - Not ready
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec readinessWithResponseSpec() throws WebClientResponseException {
        return readinessRequestCreation();
    }
}
