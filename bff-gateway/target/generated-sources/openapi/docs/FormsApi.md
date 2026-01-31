# FormsApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteForm**](FormsApi.md#deleteForm) | **DELETE** /forms/{id} | Delete form |
| [**downloadForm**](FormsApi.md#downloadForm) | **GET** /forms/{id}/download | Download form |
| [**generateForms**](FormsApi.md#generateForms) | **POST** /forms/generate | Generate tax forms |
| [**getForm**](FormsApi.md#getForm) | **GET** /forms/{id} | Get form metadata |
| [**listForms**](FormsApi.md#listForms) | **GET** /forms | List user&#39;s forms |



## deleteForm

> deleteForm(id, xUserId)

Delete form

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.FormsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        FormsApi apiInstance = new FormsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            apiInstance.deleteForm(id, xUserId);
        } catch (ApiException e) {
            System.err.println("Exception when calling FormsApi#deleteForm");
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
| **id** | **UUID**|  | |
| **xUserId** | **UUID**|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Deleted |  -  |


## downloadForm

> File downloadForm(id, xUserId)

Download form

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.FormsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        FormsApi apiInstance = new FormsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            File result = apiInstance.downloadForm(id, xUserId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FormsApi#downloadForm");
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
| **id** | **UUID**|  | |
| **xUserId** | **UUID**|  | |

### Return type

[**File**](File.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/pdf, application/zip


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Form file |  -  |


## generateForms

> GenerateFormResultDTO generateForms(xUserId, formGenerationRequest)

Generate tax forms

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.FormsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        FormsApi apiInstance = new FormsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        FormGenerationRequest formGenerationRequest = new FormGenerationRequest(); // FormGenerationRequest | 
        try {
            GenerateFormResultDTO result = apiInstance.generateForms(xUserId, formGenerationRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FormsApi#generateForms");
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
| **xUserId** | **UUID**|  | |
| **formGenerationRequest** | [**FormGenerationRequest**](FormGenerationRequest.md)|  | |

### Return type

[**GenerateFormResultDTO**](GenerateFormResultDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Form generated |  -  |


## getForm

> GeneratedForm getForm(id, xUserId)

Get form metadata

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.FormsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        FormsApi apiInstance = new FormsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            GeneratedForm result = apiInstance.getForm(id, xUserId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FormsApi#getForm");
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
| **id** | **UUID**|  | |
| **xUserId** | **UUID**|  | |

### Return type

[**GeneratedForm**](GeneratedForm.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Form metadata |  -  |
| **404** | Not found |  -  |


## listForms

> List&lt;GeneratedForm&gt; listForms(xUserId, taxYear, formType)

List user&#39;s forms

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.FormsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        FormsApi apiInstance = new FormsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        Integer taxYear = 56; // Integer | 
        String formType = "formType_example"; // String | 
        try {
            List<GeneratedForm> result = apiInstance.listForms(xUserId, taxYear, formType);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FormsApi#listForms");
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
| **xUserId** | **UUID**|  | |
| **taxYear** | **Integer**|  | [optional] |
| **formType** | **String**|  | [optional] |

### Return type

[**List&lt;GeneratedForm&gt;**](GeneratedForm.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of forms |  -  |

