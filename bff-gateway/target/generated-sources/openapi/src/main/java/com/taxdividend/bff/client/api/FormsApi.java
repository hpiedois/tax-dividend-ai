package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import java.io.File;
import com.taxdividend.bff.client.model.FormDownloadUrlResponse;
import com.taxdividend.bff.client.model.FormGenerationRequest;
import com.taxdividend.bff.client.model.GenerateFormResultDTO;
import com.taxdividend.bff.client.model.GeneratedForm;
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
public class FormsApi {
    private ApiClient apiClient;

    public FormsApi() {
        this(new ApiClient());
    }

    public FormsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Delete form
     * 
     * <p><b>204</b> - Deleted
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteFormRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling deleteForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling deleteForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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
        return apiClient.invokeAPI("/forms/{id}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete form
     * 
     * <p><b>204</b> - Deleted
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deleteForm(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteFormRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Delete form
     * 
     * <p><b>204</b> - Deleted
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> deleteFormWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteFormRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Delete form
     * 
     * <p><b>204</b> - Deleted
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deleteFormWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return deleteFormRequestCreation(id, xUserId);
    }

    /**
     * Download form
     * 
     * <p><b>200</b> - Form file
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return File
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec downloadFormRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling downloadForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling downloadForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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
            "application/pdf", "application/zip"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<File> localVarReturnType = new ParameterizedTypeReference<File>() {};
        return apiClient.invokeAPI("/forms/{id}/download", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Download form
     * 
     * <p><b>200</b> - Form file
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return File
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<File> downloadForm(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<File> localVarReturnType = new ParameterizedTypeReference<File>() {};
        return downloadFormRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Download form
     * 
     * <p><b>200</b> - Form file
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseEntity&lt;File&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<File>> downloadFormWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<File> localVarReturnType = new ParameterizedTypeReference<File>() {};
        return downloadFormRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Download form
     * 
     * <p><b>200</b> - Form file
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec downloadFormWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return downloadFormRequestCreation(id, xUserId);
    }

    /**
     * Generate tax forms
     * 
     * <p><b>200</b> - Form generated
     * @param xUserId The xUserId parameter
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return GenerateFormResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec generateFormsRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        Object postBody = formGenerationRequest;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling generateForms", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'formGenerationRequest' is set
        if (formGenerationRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'formGenerationRequest' when calling generateForms", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<GenerateFormResultDTO> localVarReturnType = new ParameterizedTypeReference<GenerateFormResultDTO>() {};
        return apiClient.invokeAPI("/forms/generate", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Generate tax forms
     * 
     * <p><b>200</b> - Form generated
     * @param xUserId The xUserId parameter
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return GenerateFormResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<GenerateFormResultDTO> generateForms(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        ParameterizedTypeReference<GenerateFormResultDTO> localVarReturnType = new ParameterizedTypeReference<GenerateFormResultDTO>() {};
        return generateFormsRequestCreation(xUserId, formGenerationRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Generate tax forms
     * 
     * <p><b>200</b> - Form generated
     * @param xUserId The xUserId parameter
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return ResponseEntity&lt;GenerateFormResultDTO&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<GenerateFormResultDTO>> generateFormsWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        ParameterizedTypeReference<GenerateFormResultDTO> localVarReturnType = new ParameterizedTypeReference<GenerateFormResultDTO>() {};
        return generateFormsRequestCreation(xUserId, formGenerationRequest).toEntity(localVarReturnType);
    }

    /**
     * Generate tax forms
     * 
     * <p><b>200</b> - Form generated
     * @param xUserId The xUserId parameter
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec generateFormsWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        return generateFormsRequestCreation(xUserId, formGenerationRequest);
    }

    /**
     * Get form metadata
     * 
     * <p><b>200</b> - Form metadata
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return GeneratedForm
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getFormRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling getForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return apiClient.invokeAPI("/forms/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get form metadata
     * 
     * <p><b>200</b> - Form metadata
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return GeneratedForm
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<GeneratedForm> getForm(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return getFormRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Get form metadata
     * 
     * <p><b>200</b> - Form metadata
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseEntity&lt;GeneratedForm&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<GeneratedForm>> getFormWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return getFormRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Get form metadata
     * 
     * <p><b>200</b> - Form metadata
     * <p><b>404</b> - Not found
     * @param id The id parameter
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getFormWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return getFormRequestCreation(id, xUserId);
    }

    /**
     * Get pre-signed download URL for form
     * Returns a temporary pre-signed URL to download the form from storage (MinIO/S3). URL expires after specified duration.
     * <p><b>200</b> - Pre-signed download URL
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * @param id Form ID
     * @param xUserId The xUserId parameter
     * @param expiresIn URL expiration time in seconds
     * @return FormDownloadUrlResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getFormDownloadUrlRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer expiresIn) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getFormDownloadUrl", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling getFormDownloadUrl", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expiresIn", expiresIn));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<FormDownloadUrlResponse> localVarReturnType = new ParameterizedTypeReference<FormDownloadUrlResponse>() {};
        return apiClient.invokeAPI("/forms/{id}/download-url", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get pre-signed download URL for form
     * Returns a temporary pre-signed URL to download the form from storage (MinIO/S3). URL expires after specified duration.
     * <p><b>200</b> - Pre-signed download URL
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * @param id Form ID
     * @param xUserId The xUserId parameter
     * @param expiresIn URL expiration time in seconds
     * @return FormDownloadUrlResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<FormDownloadUrlResponse> getFormDownloadUrl(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer expiresIn) throws WebClientResponseException {
        ParameterizedTypeReference<FormDownloadUrlResponse> localVarReturnType = new ParameterizedTypeReference<FormDownloadUrlResponse>() {};
        return getFormDownloadUrlRequestCreation(id, xUserId, expiresIn).bodyToMono(localVarReturnType);
    }

    /**
     * Get pre-signed download URL for form
     * Returns a temporary pre-signed URL to download the form from storage (MinIO/S3). URL expires after specified duration.
     * <p><b>200</b> - Pre-signed download URL
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * @param id Form ID
     * @param xUserId The xUserId parameter
     * @param expiresIn URL expiration time in seconds
     * @return ResponseEntity&lt;FormDownloadUrlResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<FormDownloadUrlResponse>> getFormDownloadUrlWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer expiresIn) throws WebClientResponseException {
        ParameterizedTypeReference<FormDownloadUrlResponse> localVarReturnType = new ParameterizedTypeReference<FormDownloadUrlResponse>() {};
        return getFormDownloadUrlRequestCreation(id, xUserId, expiresIn).toEntity(localVarReturnType);
    }

    /**
     * Get pre-signed download URL for form
     * Returns a temporary pre-signed URL to download the form from storage (MinIO/S3). URL expires after specified duration.
     * <p><b>200</b> - Pre-signed download URL
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * @param id Form ID
     * @param xUserId The xUserId parameter
     * @param expiresIn URL expiration time in seconds
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getFormDownloadUrlWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer expiresIn) throws WebClientResponseException {
        return getFormDownloadUrlRequestCreation(id, xUserId, expiresIn);
    }

    /**
     * List user&#39;s forms
     * 
     * <p><b>200</b> - List of forms
     * @param xUserId The xUserId parameter
     * @param taxYear The taxYear parameter
     * @param formType The formType parameter
     * @return List&lt;GeneratedForm&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec listFormsRequestCreation(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear, @jakarta.annotation.Nullable String formType) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling listForms", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "taxYear", taxYear));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "formType", formType));

        if (xUserId != null)
        headerParams.add("X-User-Id", apiClient.parameterToString(xUserId));
        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return apiClient.invokeAPI("/forms", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * List user&#39;s forms
     * 
     * <p><b>200</b> - List of forms
     * @param xUserId The xUserId parameter
     * @param taxYear The taxYear parameter
     * @param formType The formType parameter
     * @return List&lt;GeneratedForm&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<GeneratedForm> listForms(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear, @jakarta.annotation.Nullable String formType) throws WebClientResponseException {
        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return listFormsRequestCreation(xUserId, taxYear, formType).bodyToFlux(localVarReturnType);
    }

    /**
     * List user&#39;s forms
     * 
     * <p><b>200</b> - List of forms
     * @param xUserId The xUserId parameter
     * @param taxYear The taxYear parameter
     * @param formType The formType parameter
     * @return ResponseEntity&lt;List&lt;GeneratedForm&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<GeneratedForm>>> listFormsWithHttpInfo(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear, @jakarta.annotation.Nullable String formType) throws WebClientResponseException {
        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return listFormsRequestCreation(xUserId, taxYear, formType).toEntityList(localVarReturnType);
    }

    /**
     * List user&#39;s forms
     * 
     * <p><b>200</b> - List of forms
     * @param xUserId The xUserId parameter
     * @param taxYear The taxYear parameter
     * @param formType The formType parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec listFormsWithResponseSpec(@jakarta.annotation.Nonnull UUID xUserId, @jakarta.annotation.Nullable Integer taxYear, @jakarta.annotation.Nullable String formType) throws WebClientResponseException {
        return listFormsRequestCreation(xUserId, taxYear, formType);
    }

    /**
     * Regenerate expired form
     * Regenerates a form that has expired (after 30 days). The new form will have updated data and a new expiration date.
     * <p><b>200</b> - Form regenerated successfully
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * <p><b>400</b> - Bad request - form is not expired yet
     * @param id Form ID to regenerate
     * @param xUserId The xUserId parameter
     * @return GeneratedForm
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec regenerateFormRequestCreation(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling regenerateForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xUserId' is set
        if (xUserId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xUserId' when calling regenerateForm", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
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

        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return apiClient.invokeAPI("/forms/{id}/regenerate", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Regenerate expired form
     * Regenerates a form that has expired (after 30 days). The new form will have updated data and a new expiration date.
     * <p><b>200</b> - Form regenerated successfully
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * <p><b>400</b> - Bad request - form is not expired yet
     * @param id Form ID to regenerate
     * @param xUserId The xUserId parameter
     * @return GeneratedForm
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<GeneratedForm> regenerateForm(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return regenerateFormRequestCreation(id, xUserId).bodyToMono(localVarReturnType);
    }

    /**
     * Regenerate expired form
     * Regenerates a form that has expired (after 30 days). The new form will have updated data and a new expiration date.
     * <p><b>200</b> - Form regenerated successfully
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * <p><b>400</b> - Bad request - form is not expired yet
     * @param id Form ID to regenerate
     * @param xUserId The xUserId parameter
     * @return ResponseEntity&lt;GeneratedForm&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<GeneratedForm>> regenerateFormWithHttpInfo(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        ParameterizedTypeReference<GeneratedForm> localVarReturnType = new ParameterizedTypeReference<GeneratedForm>() {};
        return regenerateFormRequestCreation(id, xUserId).toEntity(localVarReturnType);
    }

    /**
     * Regenerate expired form
     * Regenerates a form that has expired (after 30 days). The new form will have updated data and a new expiration date.
     * <p><b>200</b> - Form regenerated successfully
     * <p><b>404</b> - Form not found
     * <p><b>403</b> - Forbidden - user does not own this form
     * <p><b>400</b> - Bad request - form is not expired yet
     * @param id Form ID to regenerate
     * @param xUserId The xUserId parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec regenerateFormWithResponseSpec(@jakarta.annotation.Nonnull UUID id, @jakarta.annotation.Nonnull UUID xUserId) throws WebClientResponseException {
        return regenerateFormRequestCreation(id, xUserId);
    }
}
