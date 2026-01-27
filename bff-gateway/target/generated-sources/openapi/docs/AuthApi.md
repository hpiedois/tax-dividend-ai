# AuthApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**registerUser**](AuthApi.md#registerUser) | **POST** /auth/register | Register a new user |
| [**validateToken**](AuthApi.md#validateToken) | **POST** /auth/validate-token | Validate a JWT token |
| [**verifyEmail**](AuthApi.md#verifyEmail) | **POST** /auth/verify | Verify user email |



## registerUser

> RegisterUser200Response registerUser(registerUserRequest)

Register a new user

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.AuthApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        AuthApi apiInstance = new AuthApi(defaultClient);
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(); // RegisterUserRequest | 
        try {
            RegisterUser200Response result = apiInstance.registerUser(registerUserRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AuthApi#registerUser");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **registerUserRequest** | [**RegisterUserRequest**](RegisterUserRequest.md)|  | [optional] |

### Return type

[**RegisterUser200Response**](RegisterUser200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Registration pending verification |  -  |


## validateToken

> ValidateToken200Response validateToken(validateTokenRequest)

Validate a JWT token

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.AuthApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        AuthApi apiInstance = new AuthApi(defaultClient);
        ValidateTokenRequest validateTokenRequest = new ValidateTokenRequest(); // ValidateTokenRequest | 
        try {
            ValidateToken200Response result = apiInstance.validateToken(validateTokenRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AuthApi#validateToken");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **validateTokenRequest** | [**ValidateTokenRequest**](ValidateTokenRequest.md)|  | [optional] |

### Return type

[**ValidateToken200Response**](ValidateToken200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Token is valid |  -  |


## verifyEmail

> VerifyEmail200Response verifyEmail(token)

Verify user email

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.AuthApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        AuthApi apiInstance = new AuthApi(defaultClient);
        String token = "token_example"; // String | 
        try {
            VerifyEmail200Response result = apiInstance.verifyEmail(token);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AuthApi#verifyEmail");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **token** | **String**|  | |

### Return type

[**VerifyEmail200Response**](VerifyEmail200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Verification successful |  -  |

