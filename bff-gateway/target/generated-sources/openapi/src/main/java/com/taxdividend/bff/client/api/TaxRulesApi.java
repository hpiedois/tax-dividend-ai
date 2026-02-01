package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import java.time.LocalDate;
import com.taxdividend.bff.client.model.TaxRule;
import com.taxdividend.bff.client.model.TreatyRateResponse;
import java.util.UUID;

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

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-02-01T09:58:25.591675+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class TaxRulesApi {
    private ApiClient apiClient;

    public TaxRulesApi() {
        this(new ApiClient());
    }

    public TaxRulesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Find applicable tax rule
     * 
     * <p><b>200</b> - Applicable rule
     * <p><b>404</b> - No rule found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return TaxRule
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec findApplicableRuleRequestCreation(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'sourceCountry' is set
        if (sourceCountry == null) {
            throw new WebClientResponseException("Missing the required parameter 'sourceCountry' when calling findApplicableRule", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'residenceCountry' is set
        if (residenceCountry == null) {
            throw new WebClientResponseException("Missing the required parameter 'residenceCountry' when calling findApplicableRule", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sourceCountry", sourceCountry));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "residenceCountry", residenceCountry));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "securityType", securityType));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "date", date));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return apiClient.invokeAPI("/tax-rules/applicable", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Find applicable tax rule
     * 
     * <p><b>200</b> - Applicable rule
     * <p><b>404</b> - No rule found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return TaxRule
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<TaxRule> findApplicableRule(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return findApplicableRuleRequestCreation(sourceCountry, residenceCountry, securityType, date).bodyToMono(localVarReturnType);
    }

    /**
     * Find applicable tax rule
     * 
     * <p><b>200</b> - Applicable rule
     * <p><b>404</b> - No rule found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return ResponseEntity&lt;TaxRule&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<TaxRule>> findApplicableRuleWithHttpInfo(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return findApplicableRuleRequestCreation(sourceCountry, residenceCountry, securityType, date).toEntity(localVarReturnType);
    }

    /**
     * Find applicable tax rule
     * 
     * <p><b>200</b> - Applicable rule
     * <p><b>404</b> - No rule found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec findApplicableRuleWithResponseSpec(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        return findApplicableRuleRequestCreation(sourceCountry, residenceCountry, securityType, date);
    }

    /**
     * List tax rules with optional filters
     * Retrieve tax rules with optional filtering by countries, active status, and procedure availability
     * <p><b>200</b> - List of tax rules
     * @param sourceCountry Filter by source country (ISO 3166-1 alpha-2 code)
     * @param residenceCountry Filter by residence country (ISO 3166-1 alpha-2 code)
     * @param active Filter by active status (rules effective as of today)
     * @param reliefAtSource Filter by relief at source availability
     * @param refundProcedure Filter by refund procedure availability
     * @return List&lt;TaxRule&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getAllTaxRulesRequestCreation(@jakarta.annotation.Nullable String sourceCountry, @jakarta.annotation.Nullable String residenceCountry, @jakarta.annotation.Nullable Boolean active, @jakarta.annotation.Nullable Boolean reliefAtSource, @jakarta.annotation.Nullable Boolean refundProcedure) throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sourceCountry", sourceCountry));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "residenceCountry", residenceCountry));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "active", active));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "reliefAtSource", reliefAtSource));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "refundProcedure", refundProcedure));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return apiClient.invokeAPI("/tax-rules", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List tax rules with optional filters
     * Retrieve tax rules with optional filtering by countries, active status, and procedure availability
     * <p><b>200</b> - List of tax rules
     * @param sourceCountry Filter by source country (ISO 3166-1 alpha-2 code)
     * @param residenceCountry Filter by residence country (ISO 3166-1 alpha-2 code)
     * @param active Filter by active status (rules effective as of today)
     * @param reliefAtSource Filter by relief at source availability
     * @param refundProcedure Filter by refund procedure availability
     * @return List&lt;TaxRule&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<TaxRule> getAllTaxRules(@jakarta.annotation.Nullable String sourceCountry, @jakarta.annotation.Nullable String residenceCountry, @jakarta.annotation.Nullable Boolean active, @jakarta.annotation.Nullable Boolean reliefAtSource, @jakarta.annotation.Nullable Boolean refundProcedure) throws WebClientResponseException {
        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return getAllTaxRulesRequestCreation(sourceCountry, residenceCountry, active, reliefAtSource, refundProcedure).bodyToFlux(localVarReturnType);
    }

    /**
     * List tax rules with optional filters
     * Retrieve tax rules with optional filtering by countries, active status, and procedure availability
     * <p><b>200</b> - List of tax rules
     * @param sourceCountry Filter by source country (ISO 3166-1 alpha-2 code)
     * @param residenceCountry Filter by residence country (ISO 3166-1 alpha-2 code)
     * @param active Filter by active status (rules effective as of today)
     * @param reliefAtSource Filter by relief at source availability
     * @param refundProcedure Filter by refund procedure availability
     * @return ResponseEntity&lt;List&lt;TaxRule&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<TaxRule>>> getAllTaxRulesWithHttpInfo(@jakarta.annotation.Nullable String sourceCountry, @jakarta.annotation.Nullable String residenceCountry, @jakarta.annotation.Nullable Boolean active, @jakarta.annotation.Nullable Boolean reliefAtSource, @jakarta.annotation.Nullable Boolean refundProcedure) throws WebClientResponseException {
        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return getAllTaxRulesRequestCreation(sourceCountry, residenceCountry, active, reliefAtSource, refundProcedure).toEntityList(localVarReturnType);
    }

    /**
     * List tax rules with optional filters
     * Retrieve tax rules with optional filtering by countries, active status, and procedure availability
     * <p><b>200</b> - List of tax rules
     * @param sourceCountry Filter by source country (ISO 3166-1 alpha-2 code)
     * @param residenceCountry Filter by residence country (ISO 3166-1 alpha-2 code)
     * @param active Filter by active status (rules effective as of today)
     * @param reliefAtSource Filter by relief at source availability
     * @param refundProcedure Filter by refund procedure availability
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getAllTaxRulesWithResponseSpec(@jakarta.annotation.Nullable String sourceCountry, @jakarta.annotation.Nullable String residenceCountry, @jakarta.annotation.Nullable Boolean active, @jakarta.annotation.Nullable Boolean reliefAtSource, @jakarta.annotation.Nullable Boolean refundProcedure) throws WebClientResponseException {
        return getAllTaxRulesRequestCreation(sourceCountry, residenceCountry, active, reliefAtSource, refundProcedure);
    }

    /**
     * Get tax rule by ID
     * 
     * <p><b>200</b> - Tax rule details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @return TaxRule
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getTaxRuleRequestCreation(@jakarta.annotation.Nonnull UUID id) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getTaxRule", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

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

        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return apiClient.invokeAPI("/tax-rules/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get tax rule by ID
     * 
     * <p><b>200</b> - Tax rule details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @return TaxRule
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<TaxRule> getTaxRule(@jakarta.annotation.Nonnull UUID id) throws WebClientResponseException {
        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return getTaxRuleRequestCreation(id).bodyToMono(localVarReturnType);
    }

    /**
     * Get tax rule by ID
     * 
     * <p><b>200</b> - Tax rule details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @return ResponseEntity&lt;TaxRule&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<TaxRule>> getTaxRuleWithHttpInfo(@jakarta.annotation.Nonnull UUID id) throws WebClientResponseException {
        ParameterizedTypeReference<TaxRule> localVarReturnType = new ParameterizedTypeReference<TaxRule>() {};
        return getTaxRuleRequestCreation(id).toEntity(localVarReturnType);
    }

    /**
     * Get tax rule by ID
     * 
     * <p><b>200</b> - Tax rule details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getTaxRuleWithResponseSpec(@jakarta.annotation.Nonnull UUID id) throws WebClientResponseException {
        return getTaxRuleRequestCreation(id);
    }

    /**
     * Get treaty rate
     * 
     * <p><b>200</b> - Treaty rate details
     * <p><b>404</b> - No treaty found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return TreatyRateResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getTreatyRateRequestCreation(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'sourceCountry' is set
        if (sourceCountry == null) {
            throw new WebClientResponseException("Missing the required parameter 'sourceCountry' when calling getTreatyRate", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'residenceCountry' is set
        if (residenceCountry == null) {
            throw new WebClientResponseException("Missing the required parameter 'residenceCountry' when calling getTreatyRate", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sourceCountry", sourceCountry));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "residenceCountry", residenceCountry));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "securityType", securityType));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "date", date));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TreatyRateResponse> localVarReturnType = new ParameterizedTypeReference<TreatyRateResponse>() {};
        return apiClient.invokeAPI("/tax-rules/treaty-rate", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get treaty rate
     * 
     * <p><b>200</b> - Treaty rate details
     * <p><b>404</b> - No treaty found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return TreatyRateResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<TreatyRateResponse> getTreatyRate(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        ParameterizedTypeReference<TreatyRateResponse> localVarReturnType = new ParameterizedTypeReference<TreatyRateResponse>() {};
        return getTreatyRateRequestCreation(sourceCountry, residenceCountry, securityType, date).bodyToMono(localVarReturnType);
    }

    /**
     * Get treaty rate
     * 
     * <p><b>200</b> - Treaty rate details
     * <p><b>404</b> - No treaty found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return ResponseEntity&lt;TreatyRateResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<TreatyRateResponse>> getTreatyRateWithHttpInfo(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        ParameterizedTypeReference<TreatyRateResponse> localVarReturnType = new ParameterizedTypeReference<TreatyRateResponse>() {};
        return getTreatyRateRequestCreation(sourceCountry, residenceCountry, securityType, date).toEntity(localVarReturnType);
    }

    /**
     * Get treaty rate
     * 
     * <p><b>200</b> - Treaty rate details
     * <p><b>404</b> - No treaty found
     * @param sourceCountry The sourceCountry parameter
     * @param residenceCountry The residenceCountry parameter
     * @param securityType The securityType parameter
     * @param date The date parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getTreatyRateWithResponseSpec(@jakarta.annotation.Nonnull String sourceCountry, @jakarta.annotation.Nonnull String residenceCountry, @jakarta.annotation.Nullable String securityType, @jakarta.annotation.Nullable LocalDate date) throws WebClientResponseException {
        return getTreatyRateRequestCreation(sourceCountry, residenceCountry, securityType, date);
    }
}
