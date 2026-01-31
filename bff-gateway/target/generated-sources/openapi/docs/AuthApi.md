# AuthApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**registerUser**](AuthApi.md#registerUser) | **POST** /auth/register | Register a new user |
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
| **registerUserRequest** | [**RegisterUserRequest**](RegisterUserRequest.md)|  | |

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


## verifyEmail

> VerifyEmailResponseDTO verifyEmail(token)

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
            VerifyEmailResponseDTO result = apiInstance.verifyEmail(token);
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

[**VerifyEmailResponseDTO**](VerifyEmailResponseDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Email verified successfully |  -  |
| **400** | Invalid token |  -  |

