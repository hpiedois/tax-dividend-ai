package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import java.io.File;
import com.taxdividend.bff.client.model.FormGenerationRequest;
import com.taxdividend.bff.client.model.ParseMakePdf200Response;
import com.taxdividend.bff.client.model.PdfGenerationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-01-26T22:26:13.434473+01:00[Europe/Zurich]")
public class PdfApi {
    private ApiClient apiClient;

    public PdfApi() {
        this(new ApiClient());
    }

    @Autowired
    public PdfApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return PdfGenerationResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec generatePdfRequestCreation(FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        Object postBody = formGenerationRequest;
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
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<PdfGenerationResponse> localVarReturnType = new ParameterizedTypeReference<PdfGenerationResponse>() {};
        return apiClient.invokeAPI("/pdf/generate", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return PdfGenerationResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<PdfGenerationResponse> generatePdf(FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        ParameterizedTypeReference<PdfGenerationResponse> localVarReturnType = new ParameterizedTypeReference<PdfGenerationResponse>() {};
        return generatePdfRequestCreation(formGenerationRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return ResponseEntity&lt;PdfGenerationResponse&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<PdfGenerationResponse>> generatePdfWithHttpInfo(FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        ParameterizedTypeReference<PdfGenerationResponse> localVarReturnType = new ParameterizedTypeReference<PdfGenerationResponse>() {};
        return generatePdfRequestCreation(formGenerationRequest).toEntity(localVarReturnType);
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec generatePdfWithResponseSpec(FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        return generatePdfRequestCreation(formGenerationRequest);
    }
    /**
     * Parse Dividend PDF
     * 
     * <p><b>200</b> - Parsing result
     * @param _file The _file parameter
     * @return ParseMakePdf200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec parseMakePdfRequestCreation(File _file) throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

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

        ParameterizedTypeReference<ParseMakePdf200Response> localVarReturnType = new ParameterizedTypeReference<ParseMakePdf200Response>() {};
        return apiClient.invokeAPI("/pdf/parse", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Parse Dividend PDF
     * 
     * <p><b>200</b> - Parsing result
     * @param _file The _file parameter
     * @return ParseMakePdf200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ParseMakePdf200Response> parseMakePdf(File _file) throws WebClientResponseException {
        ParameterizedTypeReference<ParseMakePdf200Response> localVarReturnType = new ParameterizedTypeReference<ParseMakePdf200Response>() {};
        return parseMakePdfRequestCreation(_file).bodyToMono(localVarReturnType);
    }

    /**
     * Parse Dividend PDF
     * 
     * <p><b>200</b> - Parsing result
     * @param _file The _file parameter
     * @return ResponseEntity&lt;ParseMakePdf200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ParseMakePdf200Response>> parseMakePdfWithHttpInfo(File _file) throws WebClientResponseException {
        ParameterizedTypeReference<ParseMakePdf200Response> localVarReturnType = new ParameterizedTypeReference<ParseMakePdf200Response>() {};
        return parseMakePdfRequestCreation(_file).toEntity(localVarReturnType);
    }

    /**
     * Parse Dividend PDF
     * 
     * <p><b>200</b> - Parsing result
     * @param _file The _file parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec parseMakePdfWithResponseSpec(File _file) throws WebClientResponseException {
        return parseMakePdfRequestCreation(_file);
    }
}
