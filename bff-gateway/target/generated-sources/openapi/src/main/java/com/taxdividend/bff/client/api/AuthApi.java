package com.taxdividend.bff.client.api;

import com.taxdividend.bff.client.ApiClient;

import com.taxdividend.bff.client.model.RegisterUser200Response;
import com.taxdividend.bff.client.model.RegisterUserRequest;
import com.taxdividend.bff.client.model.ValidateToken200Response;
import com.taxdividend.bff.client.model.ValidateTokenRequest;
import com.taxdividend.bff.client.model.VerifyEmail200Response;

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
public class AuthApi {
    private ApiClient apiClient;

    public AuthApi() {
        this(new ApiClient());
    }

    @Autowired
    public AuthApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Register a new user
     * 
     * <p><b>200</b> - Registration pending verification
     * @param registerUserRequest The registerUserRequest parameter
     * @return RegisterUser200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec registerUserRequestCreation(RegisterUserRequest registerUserRequest) throws WebClientResponseException {
        Object postBody = registerUserRequest;
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

        ParameterizedTypeReference<RegisterUser200Response> localVarReturnType = new ParameterizedTypeReference<RegisterUser200Response>() {};
        return apiClient.invokeAPI("/auth/register", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Register a new user
     * 
     * <p><b>200</b> - Registration pending verification
     * @param registerUserRequest The registerUserRequest parameter
     * @return RegisterUser200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<RegisterUser200Response> registerUser(RegisterUserRequest registerUserRequest) throws WebClientResponseException {
        ParameterizedTypeReference<RegisterUser200Response> localVarReturnType = new ParameterizedTypeReference<RegisterUser200Response>() {};
        return registerUserRequestCreation(registerUserRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Register a new user
     * 
     * <p><b>200</b> - Registration pending verification
     * @param registerUserRequest The registerUserRequest parameter
     * @return ResponseEntity&lt;RegisterUser200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<RegisterUser200Response>> registerUserWithHttpInfo(RegisterUserRequest registerUserRequest) throws WebClientResponseException {
        ParameterizedTypeReference<RegisterUser200Response> localVarReturnType = new ParameterizedTypeReference<RegisterUser200Response>() {};
        return registerUserRequestCreation(registerUserRequest).toEntity(localVarReturnType);
    }

    /**
     * Register a new user
     * 
     * <p><b>200</b> - Registration pending verification
     * @param registerUserRequest The registerUserRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec registerUserWithResponseSpec(RegisterUserRequest registerUserRequest) throws WebClientResponseException {
        return registerUserRequestCreation(registerUserRequest);
    }
    /**
     * Validate a JWT token
     * 
     * <p><b>200</b> - Token is valid
     * @param validateTokenRequest The validateTokenRequest parameter
     * @return ValidateToken200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec validateTokenRequestCreation(ValidateTokenRequest validateTokenRequest) throws WebClientResponseException {
        Object postBody = validateTokenRequest;
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

        ParameterizedTypeReference<ValidateToken200Response> localVarReturnType = new ParameterizedTypeReference<ValidateToken200Response>() {};
        return apiClient.invokeAPI("/auth/validate-token", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Validate a JWT token
     * 
     * <p><b>200</b> - Token is valid
     * @param validateTokenRequest The validateTokenRequest parameter
     * @return ValidateToken200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ValidateToken200Response> validateToken(ValidateTokenRequest validateTokenRequest) throws WebClientResponseException {
        ParameterizedTypeReference<ValidateToken200Response> localVarReturnType = new ParameterizedTypeReference<ValidateToken200Response>() {};
        return validateTokenRequestCreation(validateTokenRequest).bodyToMono(localVarReturnType);
    }

    /**
     * Validate a JWT token
     * 
     * <p><b>200</b> - Token is valid
     * @param validateTokenRequest The validateTokenRequest parameter
     * @return ResponseEntity&lt;ValidateToken200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<ValidateToken200Response>> validateTokenWithHttpInfo(ValidateTokenRequest validateTokenRequest) throws WebClientResponseException {
        ParameterizedTypeReference<ValidateToken200Response> localVarReturnType = new ParameterizedTypeReference<ValidateToken200Response>() {};
        return validateTokenRequestCreation(validateTokenRequest).toEntity(localVarReturnType);
    }

    /**
     * Validate a JWT token
     * 
     * <p><b>200</b> - Token is valid
     * @param validateTokenRequest The validateTokenRequest parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec validateTokenWithResponseSpec(ValidateTokenRequest validateTokenRequest) throws WebClientResponseException {
        return validateTokenRequestCreation(validateTokenRequest);
    }
    /**
     * Verify user email
     * 
     * <p><b>200</b> - Verification successful
     * @param token The token parameter
     * @return VerifyEmail200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec verifyEmailRequestCreation(String token) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'token' is set
        if (token == null) {
            throw new WebClientResponseException("Missing the required parameter 'token' when calling verifyEmail", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "token", token));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<VerifyEmail200Response> localVarReturnType = new ParameterizedTypeReference<VerifyEmail200Response>() {};
        return apiClient.invokeAPI("/auth/verify", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Verify user email
     * 
     * <p><b>200</b> - Verification successful
     * @param token The token parameter
     * @return VerifyEmail200Response
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<VerifyEmail200Response> verifyEmail(String token) throws WebClientResponseException {
        ParameterizedTypeReference<VerifyEmail200Response> localVarReturnType = new ParameterizedTypeReference<VerifyEmail200Response>() {};
        return verifyEmailRequestCreation(token).bodyToMono(localVarReturnType);
    }

    /**
     * Verify user email
     * 
     * <p><b>200</b> - Verification successful
     * @param token The token parameter
     * @return ResponseEntity&lt;VerifyEmail200Response&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<VerifyEmail200Response>> verifyEmailWithHttpInfo(String token) throws WebClientResponseException {
        ParameterizedTypeReference<VerifyEmail200Response> localVarReturnType = new ParameterizedTypeReference<VerifyEmail200Response>() {};
        return verifyEmailRequestCreation(token).toEntity(localVarReturnType);
    }

    /**
     * Verify user email
     * 
     * <p><b>200</b> - Verification successful
     * @param token The token parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec verifyEmailWithResponseSpec(String token) throws WebClientResponseException {
        return verifyEmailRequestCreation(token);
    }
}
