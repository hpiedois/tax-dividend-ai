# DividendsApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**calculateBatch**](DividendsApi.md#calculateBatch) | **POST** /dividends/calculate-batch | Calculate tax for multiple dividends |
| [**calculateTax**](DividendsApi.md#calculateTax) | **POST** /dividends/{id}/calculate | Calculate tax for dividend |
| [**deleteDividend**](DividendsApi.md#deleteDividend) | **DELETE** /dividends/{id} | Delete dividend |
| [**getDividend**](DividendsApi.md#getDividend) | **GET** /dividends/{id} | Get dividend by ID |
| [**listDividends**](DividendsApi.md#listDividends) | **GET** /dividends | List user&#39;s dividends |



## calculateBatch

> TaxCalculationBatchResultDTO calculateBatch(xUserId, UUID)

Calculate tax for multiple dividends

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendsApi apiInstance = new DividendsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        List<UUID> UUID = Arrays.asList(); // List<UUID> | 
        try {
            TaxCalculationBatchResultDTO result = apiInstance.calculateBatch(xUserId, UUID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#calculateBatch");
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
| **UUID** | [**List&lt;UUID&gt;**](UUID.md)|  | |

### Return type

[**TaxCalculationBatchResultDTO**](TaxCalculationBatchResultDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Batch calculation result |  -  |


## calculateTax

> TaxCalculationResultDTO calculateTax(id, xUserId, residenceCountry)

Calculate tax for dividend

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendsApi apiInstance = new DividendsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        String residenceCountry = "residenceCountry_example"; // String | 
        try {
            TaxCalculationResultDTO result = apiInstance.calculateTax(id, xUserId, residenceCountry);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#calculateTax");
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
| **residenceCountry** | **String**|  | [optional] |

### Return type

[**TaxCalculationResultDTO**](TaxCalculationResultDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Calculation result |  -  |


## deleteDividend

> deleteDividend(id, xUserId)

Delete dividend

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendsApi apiInstance = new DividendsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            apiInstance.deleteDividend(id, xUserId);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#deleteDividend");
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
| **404** | Not found |  -  |


## getDividend

> Dividend getDividend(id, xUserId)

Get dividend by ID

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendsApi apiInstance = new DividendsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            Dividend result = apiInstance.getDividend(id, xUserId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#getDividend");
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

[**Dividend**](Dividend.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Dividend details |  -  |
| **404** | Not found |  -  |


## listDividends

> ListDividends200Response listDividends(xUserId, page, size)

List user&#39;s dividends

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendsApi apiInstance = new DividendsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        Integer page = 0; // Integer | 
        Integer size = 20; // Integer | 
        try {
            ListDividends200Response result = apiInstance.listDividends(xUserId, page, size);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#listDividends");
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
| **page** | **Integer**|  | [optional] [default to 0] |
| **size** | **Integer**|  | [optional] [default to 20] |

### Return type

[**ListDividends200Response**](ListDividends200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of dividends |  -  |

