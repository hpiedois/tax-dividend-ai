package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import com.taxdividend.bff.client.model.FormGenerationRequest;
import com.taxdividend.bff.client.model.GenerateFormResultDTO;

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
public class PdfApi {
    private ApiClient apiClient;

    public PdfApi() {
        this(new ApiClient());
    }

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
     * <p><b>400</b> - Bad request
     * <p><b>500</b> - Generation failed
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return GenerateFormResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec generatePdfRequestCreation(@jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        Object postBody = formGenerationRequest;
        // verify the required parameter 'formGenerationRequest' is set
        if (formGenerationRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'formGenerationRequest' when calling generatePdf", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
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

        ParameterizedTypeReference<GenerateFormResultDTO> localVarReturnType = new ParameterizedTypeReference<GenerateFormResultDTO>() {};
        return apiClient.invokeAPI("/pdf/generate", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * <p><b>400</b> - Bad request
     * <p><b>500</b> - Generation failed
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return GenerateFormResultDTO
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<GenerateFormResultDTO> generatePdf(@jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        ParameterizedTypeReference<GenerateFormResultDTO> localVarReturnType = new ParameterizedTypeReference<GenerateFormResultDTO>() {};
        return generatePdfRequestCreation(formGenerationRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * <p><b>400</b> - Bad request
     * <p><b>500</b> - Generation failed
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return ResponseEntity&lt;GenerateFormResultDTO&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<GenerateFormResultDTO>> generatePdfWithHttpInfo(@jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        ParameterizedTypeReference<GenerateFormResultDTO> localVarReturnType = new ParameterizedTypeReference<GenerateFormResultDTO>() {};
        return generatePdfRequestCreation(formGenerationRequest).toEntity(localVarReturnType);
    }

    /**
     * Generate Tax Forms PDF
     * 
     * <p><b>200</b> - PDF Generated
     * <p><b>400</b> - Bad request
     * <p><b>500</b> - Generation failed
     * @param formGenerationRequest The formGenerationRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec generatePdfWithResponseSpec(@jakarta.annotation.Nonnull FormGenerationRequest formGenerationRequest) throws WebClientResponseException {
        return generatePdfRequestCreation(formGenerationRequest);
    }
}
