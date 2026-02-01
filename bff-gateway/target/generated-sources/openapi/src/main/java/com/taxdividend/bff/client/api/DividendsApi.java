package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import com.taxdividend.bff.client.model.BulkImportDividendsRequest;
import com.taxdividend.bff.client.model.BulkImportDividendsResponse;
import com.taxdividend.bff.client.model.Dividend;
import com.taxdividend.bff.client.model.DividendStatsDTO;
import com.taxdividend.bff.client.model.ListDividends200Response;
import java.time.LocalDate;
import com.taxdividend.bff.client.model.TaxCalculationBatchResultDTO;
import com.taxdividend.bff.client.model.TaxCalculationResultDTO;
import java.util.UUID;
import com.taxdividend.bff.client.model.UpdateDividendStatusRequest;

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
public class DividendsApi {
    private ApiClient apiClient;

    public DividendsApi() {
        this(new ApiClient());
    }

    public DividendsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Bulk import dividends from parsed statement
     * Import multiple dividends at once from a parsed broker statement. Called by AI Agent after parsing a statement. Automatically calculates tax and updates statement metadata. 
     * <p><b>200</b> - Dividends imported successfully
     * <p><b>400</b> - Invalid request or statement not found
     * <p><b>403</b> - User does not own the statement
     * @param xUserId The xUserId parameter
     * @param bulkImportDividendsRequest The bulkImportDividendsRequest parameter
     * @return BulkImportDividendsResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec bulkImportDividendsRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull BulkImportDividendsRequest bulkImportDividendsRequest) throws WebClientResponseException {
        Object postBody = bulkImportDividendsRequest;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling bulkImportDividends", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'bulkImportDividendsRequest' is set
        if (bulkImportDividendsRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'bulkImportDividendsRequest' when calling bulkImportDividends", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<BulkImportDividendsResponse> localVarReturnType = new ParameterizedTypeReference<BulkImportDividendsResponse>() {};
        return apiClient.invokeAPI("/dividends/bulk", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Bulk import dividends from parsed statement
     * Import multiple dividends at once from a parsed broker statement. Called by AI Agent after parsing a statement. Automatically calculates tax and updates statement metadata. 
     * <p><b>200</b> - Dividends imported successfully
     * <p><b>400</b> - Invalid request or statement not found
     * <p><b>403</b> - User does not own the statement
     * @param xUserId The xUserId parameter
     * @param bulkImportDividendsRequest The bulkImportDividendsRequest parameter
     * @return BulkImportDividendsResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<BulkImportDividendsResponse> bulkImportDividends(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull BulkImportDividendsRequest bulkImportDividendsRequest) throws WebClientResponseException {
        ParameterizedTypeReference<BulkImportDividendsResponse> localVarReturnType = new ParameterizedTypeReference<BulkImportDividendsResponse>() {};
        return bulkImportDividendsRequestCreation(xUserId, bulkImportDividendsRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Bulk import dividends from parsed statement
     * Import multiple dividends at once from a parsed broker statement. Called by AI Agent after parsing a statement. Automatically calculates tax and updates statement metadata. 
     * <p><b>200</b> - Dividends imported successfully
     * <p><b>400</b> - Invalid request or statement not found
     * <p><b>403</b> - User does not own the statement
     * @param xUserId The xUserId parameter
     * @param bulkImportDividendsRequest The bulkImportDividendsRequest parameter
     * @return ResponseEntity&lt;BulkImportDividendsResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<BulkImportDividendsResponse>> bulkImportDividendsWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull BulkImportDividendsRequest bulkImportDividendsRequest) throws WebClientResponseException {
        ParameterizedTypeReference<BulkImportDividendsResponse> localVarReturnType = new ParameterizedTypeReference<BulkImportDividendsResponse>() {};
        return bulkImportDividendsRequestCreation(xUserId, bulkImportDividendsRequest).toEntity(localVarReturnType);
    }

    /**
     * Bulk import dividends from parsed statement
     * Import multiple dividends at once from a parsed broker statement. Called by AI Agent after parsing a statement. Automatically calculates tax and updates statement metadata. 
     * <p><b>200</b> - Dividends imported successfully
     * <p><b>400</b> - Invalid request or statement not found
     * <p><b>403</b> - User does not own the statement
     * @param xUserId The xUserId parameter
     * @param bulkImportDividendsRequest The bulkImportDividendsRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec bulkImportDividendsWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull BulkImportDividendsRequest bulkImportDividendsRequest) throws WebClientResponseException {
        return bulkImportDividendsRequestCreation(xUserId, bulkImportDividendsRequest);
    }

    /**
     * Recalculate tax for all user dividends
     * Triggers tax recalculation for all dividends of a user. Useful after tax rule updates.
     * <p><b>200</b> - Tax recalculated for all dividends
     * <p><b>403</b> - Forbidden - user can only calculate their own dividends
     * <p><b>404</b> - User not found
     * @param userId User ID whose dividends should be recalculated
     * @param xUserId The xUserId parameter
     * @return TaxCalculationBatchResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec calculateAllUserDividendsRequestCreation(@jakarta.annotation.Nonnull UUID userId, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new WebClientResponseException("Missing the required parameter 'userId' when calling calculateAllUserDividends", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling calculateAllUserDividends", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("userId", userId);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TaxCalculationBatchResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationBatchResultDTO>() {};
        return apiClient.invokeAPI("/dividends/{userId}/calculate-all", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Recalculate tax for all user dividends
     * Triggers tax recalculation for all dividends of a user. Useful after tax rule updates.
     * <p><b>200</b> - Tax recalculated for all dividends
     * <p><b>403</b> - Forbidden - user can only calculate their own dividends
     * <p><b>404</b> - User not found
     * @param userId User ID whose dividends should be recalculated
     * @param xUserId The xUserId parameter
     * @return TaxCalculationBatchResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<TaxCalculationBatchResultDTO> calculateAllUserDividends(@jakarta.annotation.Nonnull UUID userId, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<TaxCalculationBatchResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationBatchResultDTO>() {};
        return calculateAllUserDividendsRequestCreation(userId, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Recalculate tax for all user dividends
     * Triggers tax recalculation for all dividends of a user. Useful after tax rule updates.
     * <p><b>200</b> - Tax recalculated for all dividends
     * <p><b>403</b> - Forbidden - user can only calculate their own dividends
     * <p><b>404</b> - User not found
     * @param userId User ID whose dividends should be recalculated
     * @param xUserId The xUserId parameter
     * @return ResponseEntity&lt;TaxCalculationBatchResultDTO&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<TaxCalculationBatchResultDTO>> calculateAllUserDividendsWithHttpInfo(@jakarta.annotation.Nonnull UUID userId, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<TaxCalculationBatchResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationBatchResultDTO>() {};
        return calculateAllUserDividendsRequestCreation(userId, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Recalculate tax for all user dividends
     * Triggers tax recalculation for all dividends of a user. Useful after tax rule updates.
     * <p><b>200</b> - Tax recalculated for all dividends
     * <p><b>403</b> - Forbidden - user can only calculate their own dividends
     * <p><b>404</b> - User not found
     * @param userId User ID whose dividends should be recalculated
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec calculateAllUserDividendsWithResponseSpec(@jakarta.annotation.Nonnull UUID userId, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return calculateAllUserDividendsRequestCreation(userId, xUserId);
    }

    /**
     * Calculate tax for multiple dividends
     * 
     * <p><b>200</b> - Batch calculation result
     * @param xUserId The xUserId parameter
     * @param UUID The UUID parameter
     * @return TaxCalculationBatchResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec calculateBatchRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull List<UUID> UUID) throws WebClientResponseException {
        Object postBody = UUID;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling calculateBatch", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'UUID' is set
        if (UUID == null) {
            throw new WebClientResponseException("Missing the required parameter 'UUID' when calling calculateBatch", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TaxCalculationBatchResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationBatchResultDTO>() {};
        return apiClient.invokeAPI("/dividends/calculate-batch", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Calculate tax for multiple dividends
     * 
     * <p><b>200</b> - Batch calculation result
     * @param xUserId The xUserId parameter
     * @param UUID The UUID parameter
     * @return TaxCalculationBatchResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<TaxCalculationBatchResultDTO> calculateBatch(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull List<UUID> UUID) throws WebClientResponseException {
        ParameterizedTypeReference<TaxCalculationBatchResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationBatchResultDTO>() {};
        return calculateBatchRequestCreation(xUserId, UUID).bodyToMono(localVarReturnType);
    }

    /**
     * Calculate tax for multiple dividends
     * 
     * <p><b>200</b> - Batch calculation result
     * @param xUserId The xUserId parameter
     * @param UUID The UUID parameter
     * @return ResponseEntity&lt;TaxCalculationBatchResultDTO&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<TaxCalculationBatchResultDTO>> calculateBatchWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull List<UUID> UUID) throws WebClientResponseException {
        ParameterizedTypeReference<TaxCalculationBatchResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationBatchResultDTO>() {};
        return calculateBatchRequestCreation(xUserId, UUID).toEntity(localVarReturnType);
    }

    /**
     * Calculate tax for multiple dividends
     * 
     * <p><b>200</b> - Batch calculation result
     * @param xUserId The xUserId parameter
     * @param UUID The UUID parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec calculateBatchWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull List<UUID> UUID) throws WebClientResponseException {
        return calculateBatchRequestCreation(xUserId, UUID);
    }

    /**
     * Calculate tax for dividend
     * 
     * <p><b>200</b> - Calculation result
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param residenceCountry The residenceCountry parameter
     * @return TaxCalculationResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec calculateTaxRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String residenceCountry) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling calculateTax", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling calculateTax", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "residenceCountry", residenceCountry));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<TaxCalculationResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationResultDTO>() {};
        return apiClient.invokeAPI("/dividends/{id}/calculate", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Calculate tax for dividend
     * 
     * <p><b>200</b> - Calculation result
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param residenceCountry The residenceCountry parameter
     * @return TaxCalculationResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<TaxCalculationResultDTO> calculateTax(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String residenceCountry) throws WebClientResponseException {
        ParameterizedTypeReference<TaxCalculationResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationResultDTO>() {};
        return calculateTaxRequestCreation(id, xUserId, residenceCountry).bodyToMono(localVarReturnType);
    }

    /**
     * Calculate tax for dividend
     * 
     * <p><b>200</b> - Calculation result
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param residenceCountry The residenceCountry parameter
     * @return ResponseEntity&lt;TaxCalculationResultDTO&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<TaxCalculationResultDTO>> calculateTaxWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String residenceCountry) throws WebClientResponseException {
        ParameterizedTypeReference<TaxCalculationResultDTO> localVarReturnType = new ParameterizedTypeReference<TaxCalculationResultDTO>() {};
        return calculateTaxRequestCreation(id, xUserId, residenceCountry).toEntity(localVarReturnType);
    }

    /**
     * Calculate tax for dividend
     * 
     * <p><b>200</b> - Calculation result
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param residenceCountry The residenceCountry parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec calculateTaxWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String residenceCountry) throws WebClientResponseException {
        return calculateTaxRequestCreation(id, xUserId, residenceCountry);
    }

    /**
     * Delete dividend
     * 
     * <p><b>204</b> - Deleted
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteDividendRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling deleteDividend", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling deleteDividend", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/dividends/{id}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete dividend
     * 
     * <p><b>204</b> - Deleted
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deleteDividend(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteDividendRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Delete dividend
     * 
     * <p><b>204</b> - Deleted
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> deleteDividendWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteDividendRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Delete dividend
     * 
     * <p><b>204</b> - Deleted
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deleteDividendWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return deleteDividendRequestCreation(id, xUserId);
    }

    /**
     * Get dividend by ID
     * 
     * <p><b>200</b> - Dividend details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return Dividend
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getDividendRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getDividend", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling getDividend", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Dividend> localVarReturnType = new ParameterizedTypeReference<Dividend>() {};
        return apiClient.invokeAPI("/dividends/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get dividend by ID
     * 
     * <p><b>200</b> - Dividend details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return Dividend
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Dividend> getDividend(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Dividend> localVarReturnType = new ParameterizedTypeReference<Dividend>() {};
        return getDividendRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Get dividend by ID
     * 
     * <p><b>200</b> - Dividend details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseEntity&lt;Dividend&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Dividend>> getDividendWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Dividend> localVarReturnType = new ParameterizedTypeReference<Dividend>() {};
        return getDividendRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Get dividend by ID
     * 
     * <p><b>200</b> - Dividend details
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getDividendWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return getDividendRequestCreation(id, xUserId);
    }

    /**
     * Get dividend statistics
     * 
     * <p><b>200</b> - Statistics
     * @param xUserId The xUserId parameter
     * @param taxYear Optional tax year filter. If not provided, returns all-time stats.
     * @return DividendStatsDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getDividendStatsRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling getDividendStats", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "taxYear", taxYear));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<DividendStatsDTO> localVarReturnType = new ParameterizedTypeReference<DividendStatsDTO>() {};
        return apiClient.invokeAPI("/dividends/stats", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get dividend statistics
     * 
     * <p><b>200</b> - Statistics
     * @param xUserId The xUserId parameter
     * @param taxYear Optional tax year filter. If not provided, returns all-time stats.
     * @return DividendStatsDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<DividendStatsDTO> getDividendStats(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatsDTO> localVarReturnType = new ParameterizedTypeReference<DividendStatsDTO>() {};
        return getDividendStatsRequestCreation(xUserId, taxYear).bodyToMono(localVarReturnType);
    }

    /**
     * Get dividend statistics
     * 
     * <p><b>200</b> - Statistics
     * @param xUserId The xUserId parameter
     * @param taxYear Optional tax year filter. If not provided, returns all-time stats.
     * @return ResponseEntity&lt;DividendStatsDTO&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<DividendStatsDTO>> getDividendStatsWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatsDTO> localVarReturnType = new ParameterizedTypeReference<DividendStatsDTO>() {};
        return getDividendStatsRequestCreation(xUserId, taxYear).toEntity(localVarReturnType);
    }

    /**
     * Get dividend statistics
     * 
     * <p><b>200</b> - Statistics
     * @param xUserId The xUserId parameter
     * @param taxYear Optional tax year filter. If not provided, returns all-time stats.
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getDividendStatsWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear) throws WebClientResponseException {
        return getDividendStatsRequestCreation(xUserId, taxYear);
    }

    /**
     * List user&#39;s dividends with optional filters
     * 
     * <p><b>200</b> - List of dividends
     * @param xUserId The xUserId parameter
     * @param page The page parameter
     * @param size The size parameter
     * @param startDate Filter dividends from this date (inclusive)
     * @param endDate Filter dividends until this date (inclusive)
     * @param status Filter by submission status
     * @return ListDividends200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listDividendsRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable LocalDate startDate, @jakarta.annotation.Nullable LocalDate endDate, @jakarta.annotation.Nullable String status) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling listDividends", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "startDate", startDate));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "endDate", endDate));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "status", status));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ListDividends200Response> localVarReturnType = new ParameterizedTypeReference<ListDividends200Response>() {};
        return apiClient.invokeAPI("/dividends", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List user&#39;s dividends with optional filters
     * 
     * <p><b>200</b> - List of dividends
     * @param xUserId The xUserId parameter
     * @param page The page parameter
     * @param size The size parameter
     * @param startDate Filter dividends from this date (inclusive)
     * @param endDate Filter dividends until this date (inclusive)
     * @param status Filter by submission status
     * @return ListDividends200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ListDividends200Response> listDividends(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable LocalDate startDate, @jakarta.annotation.Nullable LocalDate endDate, @jakarta.annotation.Nullable String status) throws WebClientResponseException {
        ParameterizedTypeReference<ListDividends200Response> localVarReturnType = new ParameterizedTypeReference<ListDividends200Response>() {};
        return listDividendsRequestCreation(xUserId, page, size, startDate, endDate, status).bodyToMono(localVarReturnType);
    }

    /**
     * List user&#39;s dividends with optional filters
     * 
     * <p><b>200</b> - List of dividends
     * @param xUserId The xUserId parameter
     * @param page The page parameter
     * @param size The size parameter
     * @param startDate Filter dividends from this date (inclusive)
     * @param endDate Filter dividends until this date (inclusive)
     * @param status Filter by submission status
     * @return ResponseEntity&lt;ListDividends200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ListDividends200Response>> listDividendsWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable LocalDate startDate, @jakarta.annotation.Nullable LocalDate endDate, @jakarta.annotation.Nullable String status) throws WebClientResponseException {
        ParameterizedTypeReference<ListDividends200Response> localVarReturnType = new ParameterizedTypeReference<ListDividends200Response>() {};
        return listDividendsRequestCreation(xUserId, page, size, startDate, endDate, status).toEntity(localVarReturnType);
    }

    /**
     * List user&#39;s dividends with optional filters
     * 
     * <p><b>200</b> - List of dividends
     * @param xUserId The xUserId parameter
     * @param page The page parameter
     * @param size The size parameter
     * @param startDate Filter dividends from this date (inclusive)
     * @param endDate Filter dividends until this date (inclusive)
     * @param status Filter by submission status
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listDividendsWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size, @jakarta.annotation.Nullable LocalDate startDate, @jakarta.annotation.Nullable LocalDate endDate, @jakarta.annotation.Nullable String status) throws WebClientResponseException {
        return listDividendsRequestCreation(xUserId, page, size, startDate, endDate, status);
    }

    /**
     * Update status for multiple dividends
     * 
     * <p><b>204</b> - Status updated
     * @param xUserId The xUserId parameter
     * @param updateDividendStatusRequest The updateDividendStatusRequest parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateDividendStatusRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull UpdateDividendStatusRequest updateDividendStatusRequest) throws WebClientResponseException {
        Object postBody = updateDividendStatusRequest;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling updateDividendStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'updateDividendStatusRequest' is set
        if (updateDividendStatusRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'updateDividendStatusRequest' when calling updateDividendStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/dividends/status", HttpMethod.PATCH, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update status for multiple dividends
     * 
     * <p><b>204</b> - Status updated
     * @param xUserId The xUserId parameter
     * @param updateDividendStatusRequest The updateDividendStatusRequest parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> updateDividendStatus(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull UpdateDividendStatusRequest updateDividendStatusRequest) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return updateDividendStatusRequestCreation(xUserId, updateDividendStatusRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Update status for multiple dividends
     * 
     * <p><b>204</b> - Status updated
     * @param xUserId The xUserId parameter
     * @param updateDividendStatusRequest The updateDividendStatusRequest parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> updateDividendStatusWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull UpdateDividendStatusRequest updateDividendStatusRequest) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return updateDividendStatusRequestCreation(xUserId, updateDividendStatusRequest).toEntity(localVarReturnType);
    }

    /**
     * Update status for multiple dividends
     * 
     * <p><b>204</b> - Status updated
     * @param xUserId The xUserId parameter
     * @param updateDividendStatusRequest The updateDividendStatusRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec updateDividendStatusWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull UpdateDividendStatusRequest updateDividendStatusRequest) throws WebClientResponseException {
        return updateDividendStatusRequestCreation(xUserId, updateDividendStatusRequest);
    }
}
