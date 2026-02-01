package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import com.taxdividend.bff.client.model.DividendStatement;
import com.taxdividend.bff.client.model.DividendStatementStatus;
import com.taxdividend.bff.client.model.DividendStatementUpdateDTO;
import java.io.File;
import com.taxdividend.bff.client.model.ListDividendStatements200Response;
import java.time.LocalDate;
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
public class DividendStatementsApi {
    private ApiClient apiClient;

    public DividendStatementsApi() {
        this(new ApiClient());
    }

    public DividendStatementsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Count statements by status
     * Get count of statements for a specific status
     * <p><b>200</b> - Count of statements
     * @param xUserId The xUserId parameter
     * @param status The status parameter
     * @return Long
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec countDividendStatementsByStatusRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementStatus status) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling countDividendStatementsByStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'status' is set
        if (status == null) {
            throw new WebClientResponseException("Missing the required parameter 'status' when calling countDividendStatementsByStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

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

        ParameterizedTypeReference<Long> localVarReturnType = new ParameterizedTypeReference<Long>() {};
        return apiClient.invokeAPI("/dividend-statements/count-by-status", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Count statements by status
     * Get count of statements for a specific status
     * <p><b>200</b> - Count of statements
     * @param xUserId The xUserId parameter
     * @param status The status parameter
     * @return Long
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Long> countDividendStatementsByStatus(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementStatus status) throws WebClientResponseException {
        ParameterizedTypeReference<Long> localVarReturnType = new ParameterizedTypeReference<Long>() {};
        return countDividendStatementsByStatusRequestCreation(xUserId, status).bodyToMono(localVarReturnType);
    }

    /**
     * Count statements by status
     * Get count of statements for a specific status
     * <p><b>200</b> - Count of statements
     * @param xUserId The xUserId parameter
     * @param status The status parameter
     * @return ResponseEntity&lt;Long&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Long>> countDividendStatementsByStatusWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementStatus status) throws WebClientResponseException {
        ParameterizedTypeReference<Long> localVarReturnType = new ParameterizedTypeReference<Long>() {};
        return countDividendStatementsByStatusRequestCreation(xUserId, status).toEntity(localVarReturnType);
    }

    /**
     * Count statements by status
     * Get count of statements for a specific status
     * <p><b>200</b> - Count of statements
     * @param xUserId The xUserId parameter
     * @param status The status parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec countDividendStatementsByStatusWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementStatus status) throws WebClientResponseException {
        return countDividendStatementsByStatusRequestCreation(xUserId, status);
    }

    /**
     * Delete dividend statement
     * Delete statement and associated file from storage
     * <p><b>204</b> - Deleted successfully
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteDividendStatementRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling deleteDividendStatement", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling deleteDividendStatement", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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
        return apiClient.invokeAPI("/dividend-statements/{id}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete dividend statement
     * Delete statement and associated file from storage
     * <p><b>204</b> - Deleted successfully
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deleteDividendStatement(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteDividendStatementRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Delete dividend statement
     * Delete statement and associated file from storage
     * <p><b>204</b> - Deleted successfully
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> deleteDividendStatementWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteDividendStatementRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Delete dividend statement
     * Delete statement and associated file from storage
     * <p><b>204</b> - Deleted successfully
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deleteDividendStatementWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return deleteDividendStatementRequestCreation(id, xUserId);
    }

    /**
     * Get dividend statement by ID
     * Get a specific dividend statement. Validates ownership.
     * <p><b>200</b> - Dividend statement details
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return DividendStatement
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getDividendStatementRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getDividendStatement", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling getDividendStatement", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return apiClient.invokeAPI("/dividend-statements/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get dividend statement by ID
     * Get a specific dividend statement. Validates ownership.
     * <p><b>200</b> - Dividend statement details
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return DividendStatement
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<DividendStatement> getDividendStatement(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return getDividendStatementRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Get dividend statement by ID
     * Get a specific dividend statement. Validates ownership.
     * <p><b>200</b> - Dividend statement details
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseEntity&lt;DividendStatement&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<DividendStatement>> getDividendStatementWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return getDividendStatementRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Get dividend statement by ID
     * Get a specific dividend statement. Validates ownership.
     * <p><b>200</b> - Dividend statement details
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getDividendStatementWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return getDividendStatementRequestCreation(id, xUserId);
    }

    /**
     * Find statements by date range
     * Find statements whose period overlaps with the specified date range
     * <p><b>200</b> - List of statements in date range
     * @param xUserId The xUserId parameter
     * @param startDate The startDate parameter
     * @param endDate The endDate parameter
     * @return List&lt;DividendStatement&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getDividendStatementsByDateRangeRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull LocalDate startDate, @jakarta.annotation.Nonnull LocalDate endDate) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling getDividendStatementsByDateRange", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'startDate' is set
        if (startDate == null) {
            throw new WebClientResponseException("Missing the required parameter 'startDate' when calling getDividendStatementsByDateRange", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'endDate' is set
        if (endDate == null) {
            throw new WebClientResponseException("Missing the required parameter 'endDate' when calling getDividendStatementsByDateRange", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "startDate", startDate));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "endDate", endDate));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return apiClient.invokeAPI("/dividend-statements/by-date-range", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Find statements by date range
     * Find statements whose period overlaps with the specified date range
     * <p><b>200</b> - List of statements in date range
     * @param xUserId The xUserId parameter
     * @param startDate The startDate parameter
     * @param endDate The endDate parameter
     * @return List&lt;DividendStatement&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<DividendStatement> getDividendStatementsByDateRange(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull LocalDate startDate, @jakarta.annotation.Nonnull LocalDate endDate) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return getDividendStatementsByDateRangeRequestCreation(xUserId, startDate, endDate).bodyToFlux(localVarReturnType);
    }

    /**
     * Find statements by date range
     * Find statements whose period overlaps with the specified date range
     * <p><b>200</b> - List of statements in date range
     * @param xUserId The xUserId parameter
     * @param startDate The startDate parameter
     * @param endDate The endDate parameter
     * @return ResponseEntity&lt;List&lt;DividendStatement&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<DividendStatement>>> getDividendStatementsByDateRangeWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull LocalDate startDate, @jakarta.annotation.Nonnull LocalDate endDate) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return getDividendStatementsByDateRangeRequestCreation(xUserId, startDate, endDate).toEntityList(localVarReturnType);
    }

    /**
     * Find statements by date range
     * Find statements whose period overlaps with the specified date range
     * <p><b>200</b> - List of statements in date range
     * @param xUserId The xUserId parameter
     * @param startDate The startDate parameter
     * @param endDate The endDate parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getDividendStatementsByDateRangeWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull LocalDate startDate, @jakarta.annotation.Nonnull LocalDate endDate) throws WebClientResponseException {
        return getDividendStatementsByDateRangeRequestCreation(xUserId, startDate, endDate);
    }

    /**
     * List user&#39;s dividend statements
     * List dividend statements with optional status filter and pagination
     * <p><b>200</b> - Page of dividend statements
     * @param xUserId The xUserId parameter
     * @param status Filter by status
     * @param page The page parameter
     * @param size The size parameter
     * @return ListDividendStatements200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listDividendStatementsRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable DividendStatementStatus status, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling listDividendStatements", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "status", status));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ListDividendStatements200Response> localVarReturnType = new ParameterizedTypeReference<ListDividendStatements200Response>() {};
        return apiClient.invokeAPI("/dividend-statements", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List user&#39;s dividend statements
     * List dividend statements with optional status filter and pagination
     * <p><b>200</b> - Page of dividend statements
     * @param xUserId The xUserId parameter
     * @param status Filter by status
     * @param page The page parameter
     * @param size The size parameter
     * @return ListDividendStatements200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ListDividendStatements200Response> listDividendStatements(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable DividendStatementStatus status, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size) throws WebClientResponseException {
        ParameterizedTypeReference<ListDividendStatements200Response> localVarReturnType = new ParameterizedTypeReference<ListDividendStatements200Response>() {};
        return listDividendStatementsRequestCreation(xUserId, status, page, size).bodyToMono(localVarReturnType);
    }

    /**
     * List user&#39;s dividend statements
     * List dividend statements with optional status filter and pagination
     * <p><b>200</b> - Page of dividend statements
     * @param xUserId The xUserId parameter
     * @param status Filter by status
     * @param page The page parameter
     * @param size The size parameter
     * @return ResponseEntity&lt;ListDividendStatements200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ListDividendStatements200Response>> listDividendStatementsWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable DividendStatementStatus status, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size) throws WebClientResponseException {
        ParameterizedTypeReference<ListDividendStatements200Response> localVarReturnType = new ParameterizedTypeReference<ListDividendStatements200Response>() {};
        return listDividendStatementsRequestCreation(xUserId, status, page, size).toEntity(localVarReturnType);
    }

    /**
     * List user&#39;s dividend statements
     * List dividend statements with optional status filter and pagination
     * <p><b>200</b> - Page of dividend statements
     * @param xUserId The xUserId parameter
     * @param status Filter by status
     * @param page The page parameter
     * @param size The size parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listDividendStatementsWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable DividendStatementStatus status, @jakarta.annotation.Nullable Integer page, @jakarta.annotation.Nullable Integer size) throws WebClientResponseException {
        return listDividendStatementsRequestCreation(xUserId, status, page, size);
    }

    /**
     * Update dividend statement status
     * Update statement status. Validates status transitions: - UPLOADED → PARSING (AI Agent starts parsing) - PARSING → PARSED (AI Agent completes parsing) - PARSED → VALIDATED (User downloads forms) - VALIDATED → SENT (User submits forms offline) - SENT → PAID (User receives payment) 
     * <p><b>200</b> - Status updated successfully
     * <p><b>400</b> - Invalid status transition
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param dividendStatementUpdateDTO The dividendStatementUpdateDTO parameter
     * @return DividendStatement
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateDividendStatementStatusRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementUpdateDTO dividendStatementUpdateDTO) throws WebClientResponseException {
        Object postBody = dividendStatementUpdateDTO;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling updateDividendStatementStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling updateDividendStatementStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'dividendStatementUpdateDTO' is set
        if (dividendStatementUpdateDTO == null) {
            throw new WebClientResponseException("Missing the required parameter 'dividendStatementUpdateDTO' when calling updateDividendStatementStatus", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return apiClient.invokeAPI("/dividend-statements/{id}", HttpMethod.PATCH, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update dividend statement status
     * Update statement status. Validates status transitions: - UPLOADED → PARSING (AI Agent starts parsing) - PARSING → PARSED (AI Agent completes parsing) - PARSED → VALIDATED (User downloads forms) - VALIDATED → SENT (User submits forms offline) - SENT → PAID (User receives payment) 
     * <p><b>200</b> - Status updated successfully
     * <p><b>400</b> - Invalid status transition
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param dividendStatementUpdateDTO The dividendStatementUpdateDTO parameter
     * @return DividendStatement
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<DividendStatement> updateDividendStatementStatus(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementUpdateDTO dividendStatementUpdateDTO) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return updateDividendStatementStatusRequestCreation(id, xUserId, dividendStatementUpdateDTO).bodyToMono(localVarReturnType);
    }

    /**
     * Update dividend statement status
     * Update statement status. Validates status transitions: - UPLOADED → PARSING (AI Agent starts parsing) - PARSING → PARSED (AI Agent completes parsing) - PARSED → VALIDATED (User downloads forms) - VALIDATED → SENT (User submits forms offline) - SENT → PAID (User receives payment) 
     * <p><b>200</b> - Status updated successfully
     * <p><b>400</b> - Invalid status transition
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param dividendStatementUpdateDTO The dividendStatementUpdateDTO parameter
     * @return ResponseEntity&lt;DividendStatement&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<DividendStatement>> updateDividendStatementStatusWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementUpdateDTO dividendStatementUpdateDTO) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return updateDividendStatementStatusRequestCreation(id, xUserId, dividendStatementUpdateDTO).toEntity(localVarReturnType);
    }

    /**
     * Update dividend statement status
     * Update statement status. Validates status transitions: - UPLOADED → PARSING (AI Agent starts parsing) - PARSING → PARSED (AI Agent completes parsing) - PARSED → VALIDATED (User downloads forms) - VALIDATED → SENT (User submits forms offline) - SENT → PAID (User receives payment) 
     * <p><b>200</b> - Status updated successfully
     * <p><b>400</b> - Invalid status transition
     * <p><b>404</b> - Not found or not owned by user
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @param dividendStatementUpdateDTO The dividendStatementUpdateDTO parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec updateDividendStatementStatusWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull DividendStatementUpdateDTO dividendStatementUpdateDTO) throws WebClientResponseException {
        return updateDividendStatementStatusRequestCreation(id, xUserId, dividendStatementUpdateDTO);
    }

    /**
     * Upload broker statement
     * Upload a broker statement PDF/CSV file. Creates a new DividendStatement with status UPLOADED.
     * <p><b>201</b> - Statement uploaded successfully
     * <p><b>400</b> - Bad request - invalid file or parameters
     * @param xUserId The xUserId parameter
     * @param broker Broker name (InteractiveBrokers, Swissquote, etc.)
     * @param periodStart Start of period covered by statement
     * @param periodEnd End of period covered by statement
     * @param _file Broker statement file (PDF or CSV)
     * @return DividendStatement
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec uploadDividendStatementRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String broker, @jakarta.annotation.Nullable LocalDate periodStart, @jakarta.annotation.Nullable LocalDate periodEnd, @jakarta.annotation.Nullable File _file) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling uploadDividendStatement", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "broker", broker));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "periodStart", periodStart));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "periodEnd", periodEnd));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        if (_file != null)
            formParams.add("file", new FileSystemResource(_file));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return apiClient.invokeAPI("/dividend-statements", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Upload broker statement
     * Upload a broker statement PDF/CSV file. Creates a new DividendStatement with status UPLOADED.
     * <p><b>201</b> - Statement uploaded successfully
     * <p><b>400</b> - Bad request - invalid file or parameters
     * @param xUserId The xUserId parameter
     * @param broker Broker name (InteractiveBrokers, Swissquote, etc.)
     * @param periodStart Start of period covered by statement
     * @param periodEnd End of period covered by statement
     * @param _file Broker statement file (PDF or CSV)
     * @return DividendStatement
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<DividendStatement> uploadDividendStatement(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String broker, @jakarta.annotation.Nullable LocalDate periodStart, @jakarta.annotation.Nullable LocalDate periodEnd, @jakarta.annotation.Nullable File _file) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return uploadDividendStatementRequestCreation(xUserId, broker, periodStart, periodEnd, _file).bodyToMono(localVarReturnType);
    }

    /**
     * Upload broker statement
     * Upload a broker statement PDF/CSV file. Creates a new DividendStatement with status UPLOADED.
     * <p><b>201</b> - Statement uploaded successfully
     * <p><b>400</b> - Bad request - invalid file or parameters
     * @param xUserId The xUserId parameter
     * @param broker Broker name (InteractiveBrokers, Swissquote, etc.)
     * @param periodStart Start of period covered by statement
     * @param periodEnd End of period covered by statement
     * @param _file Broker statement file (PDF or CSV)
     * @return ResponseEntity&lt;DividendStatement&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<DividendStatement>> uploadDividendStatementWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String broker, @jakarta.annotation.Nullable LocalDate periodStart, @jakarta.annotation.Nullable LocalDate periodEnd, @jakarta.annotation.Nullable File _file) throws WebClientResponseException {
        ParameterizedTypeReference<DividendStatement> localVarReturnType = new ParameterizedTypeReference<DividendStatement>() {};
        return uploadDividendStatementRequestCreation(xUserId, broker, periodStart, periodEnd, _file).toEntity(localVarReturnType);
    }

    /**
     * Upload broker statement
     * Upload a broker statement PDF/CSV file. Creates a new DividendStatement with status UPLOADED.
     * <p><b>201</b> - Statement uploaded successfully
     * <p><b>400</b> - Bad request - invalid file or parameters
     * @param xUserId The xUserId parameter
     * @param broker Broker name (InteractiveBrokers, Swissquote, etc.)
     * @param periodStart Start of period covered by statement
     * @param periodEnd End of period covered by statement
     * @param _file Broker statement file (PDF or CSV)
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec uploadDividendStatementWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable String broker, @jakarta.annotation.Nullable LocalDate periodStart, @jakarta.annotation.Nullable LocalDate periodEnd, @jakarta.annotation.Nullable File _file) throws WebClientResponseException {
        return uploadDividendStatementRequestCreation(xUserId, broker, periodStart, periodEnd, _file);
    }
}
