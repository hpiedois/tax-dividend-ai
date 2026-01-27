package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import java.io.File;
import com.taxdividend.bff.client.model.UploadFile200Response;

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
public class StorageApi {
    private ApiClient apiClient;

    public StorageApi() {
        this(new ApiClient());
    }

    @Autowired
    public StorageApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Upload file to storage
     * 
     * <p><b>200</b> - Upload successful
     * @param _file The _file parameter
     * @param bucket The bucket parameter
     * @return UploadFile200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec uploadFileRequestCreation(File _file, String bucket) throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (_file != null)
            formParams.add("file", new FileSystemResource(_file));
        if (bucket != null)
            formParams.add("bucket", bucket);

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<UploadFile200Response> localVarReturnType = new ParameterizedTypeReference<UploadFile200Response>() {};
        return apiClient.invokeAPI("/storage/upload", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Upload file to storage
     * 
     * <p><b>200</b> - Upload successful
     * @param _file The _file parameter
     * @param bucket The bucket parameter
     * @return UploadFile200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<UploadFile200Response> uploadFile(File _file, String bucket) throws WebClientResponseException {
        ParameterizedTypeReference<UploadFile200Response> localVarReturnType = new ParameterizedTypeReference<UploadFile200Response>() {};
        return uploadFileRequestCreation(_file, bucket).bodyToMono(localVarReturnType);
    }

    /**
     * Upload file to storage
     * 
     * <p><b>200</b> - Upload successful
     * @param _file The _file parameter
     * @param bucket The bucket parameter
     * @return ResponseEntity&lt;UploadFile200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<UploadFile200Response>> uploadFileWithHttpInfo(File _file, String bucket) throws WebClientResponseException {
        ParameterizedTypeReference<UploadFile200Response> localVarReturnType = new ParameterizedTypeReference<UploadFile200Response>() {};
        return uploadFileRequestCreation(_file, bucket).toEntity(localVarReturnType);
    }

    /**
     * Upload file to storage
     * 
     * <p><b>200</b> - Upload successful
     * @param _file The _file parameter
     * @param bucket The bucket parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec uploadFileWithResponseSpec(File _file, String bucket) throws WebClientResponseException {
        return uploadFileRequestCreation(_file, bucket);
    }
}
