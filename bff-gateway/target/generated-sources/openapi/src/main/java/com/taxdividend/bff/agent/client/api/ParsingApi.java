package com.taxdividend.bff.agent.client.api;

import com.taxdividend.bff.agent.client.ApiClient;

import java.io.File;
import com.taxdividend.bff.agent.client.model.ParseResponse;

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

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-02-01T09:58:26.071387+01:00[Europe/Zurich]", comments = "Generator version: 7.17.0")
public class ParsingApi {
    private ApiClient apiClient;

    public ParsingApi() {
        this(new ApiClient());
    }

    public ParsingApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Parse a PDF or Image dividend statement
     * 
     * <p><b>200</b> - Successful extraction
     * <p><b>400</b> - Invalid input or unreadable file
     * <p><b>422</b> - Password required but not provided
     * <p><b>500</b> - Extraction failed
     * @param _file The dividend statement file (PDF, JPEG, PNG)
     * @param password Optional password for encrypted PDFs
     * @return ParseResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec parseDocumentRequestCreation(@jakarta.annotation.Nonnull File _file, @jakarta.annotation.Nullable String password) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter '_file' is set
        if (_file == null) {
            throw new WebClientResponseException("Missing the required parameter '_file' when calling parseDocument", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (_file != null)
            formParams.add("file", new FileSystemResource(_file));
        if (password != null)
            formParams.add("password", password);

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<ParseResponse> localVarReturnType = new ParameterizedTypeReference<ParseResponse>() {};
        return apiClient.invokeAPI("/parse", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Parse a PDF or Image dividend statement
     * 
     * <p><b>200</b> - Successful extraction
     * <p><b>400</b> - Invalid input or unreadable file
     * <p><b>422</b> - Password required but not provided
     * <p><b>500</b> - Extraction failed
     * @param _file The dividend statement file (PDF, JPEG, PNG)
     * @param password Optional password for encrypted PDFs
     * @return ParseResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ParseResponse> parseDocument(@jakarta.annotation.Nonnull File _file, @jakarta.annotation.Nullable String password) throws WebClientResponseException {
        ParameterizedTypeReference<ParseResponse> localVarReturnType = new ParameterizedTypeReference<ParseResponse>() {};
        return parseDocumentRequestCreation(_file, password).bodyToMono(localVarReturnType);
    }

    /**
     * Parse a PDF or Image dividend statement
     * 
     * <p><b>200</b> - Successful extraction
     * <p><b>400</b> - Invalid input or unreadable file
     * <p><b>422</b> - Password required but not provided
     * <p><b>500</b> - Extraction failed
     * @param _file The dividend statement file (PDF, JPEG, PNG)
     * @param password Optional password for encrypted PDFs
     * @return ResponseEntity&lt;ParseResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ParseResponse>> parseDocumentWithHttpInfo(@jakarta.annotation.Nonnull File _file, @jakarta.annotation.Nullable String password) throws WebClientResponseException {
        ParameterizedTypeReference<ParseResponse> localVarReturnType = new ParameterizedTypeReference<ParseResponse>() {};
        return parseDocumentRequestCreation(_file, password).toEntity(localVarReturnType);
    }

    /**
     * Parse a PDF or Image dividend statement
     * 
     * <p><b>200</b> - Successful extraction
     * <p><b>400</b> - Invalid input or unreadable file
     * <p><b>422</b> - Password required but not provided
     * <p><b>500</b> - Extraction failed
     * @param _file The dividend statement file (PDF, JPEG, PNG)
     * @param password Optional password for encrypted PDFs
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec parseDocumentWithResponseSpec(@jakarta.annotation.Nonnull File _file, @jakarta.annotation.Nullable String password) throws WebClientResponseException {
        return parseDocumentRequestCreation(_file, password);
    }
}
