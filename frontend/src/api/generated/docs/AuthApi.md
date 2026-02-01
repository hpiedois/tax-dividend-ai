# AuthApi

All URIs are relative to *http://localhost:8080/api*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**register**](#register) | **POST** /auth/register | Register a new user|
|[**verifyEmail**](#verifyemail) | **POST** /auth/verify | Verify email|

# **register**
> register()


### Example

```typescript
import {
    AuthApi,
    Configuration,
    RegisterRequest
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new AuthApi(configuration);

let registerRequest: RegisterRequest; // (optional)

const { status, data } = await apiInstance.register(
    registerRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **registerRequest** | **RegisterRequest**|  | |


### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Registration initiated |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **verifyEmail**
> verifyEmail()


### Example

```typescript
import {
    AuthApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new AuthApi(configuration);

let token: string; // (default to undefined)

const { status, data } = await apiInstance.verifyEmail(
    token
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **token** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Verified |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

