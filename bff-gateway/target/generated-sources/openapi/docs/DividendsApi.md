# DividendsApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**bulkImportDividends**](DividendsApi.md#bulkImportDividends) | **POST** /dividends/bulk | Bulk import dividends from parsed statement |
| [**calculateAllUserDividends**](DividendsApi.md#calculateAllUserDividends) | **POST** /dividends/{userId}/calculate-all | Recalculate tax for all user dividends |
| [**calculateBatch**](DividendsApi.md#calculateBatch) | **POST** /dividends/calculate-batch | Calculate tax for multiple dividends |
| [**calculateTax**](DividendsApi.md#calculateTax) | **POST** /dividends/{id}/calculate | Calculate tax for dividend |
| [**deleteDividend**](DividendsApi.md#deleteDividend) | **DELETE** /dividends/{id} | Delete dividend |
| [**getDividend**](DividendsApi.md#getDividend) | **GET** /dividends/{id} | Get dividend by ID |
| [**getDividendStats**](DividendsApi.md#getDividendStats) | **GET** /dividends/stats | Get dividend statistics |
| [**listDividends**](DividendsApi.md#listDividends) | **GET** /dividends | List user&#39;s dividends with optional filters |
| [**updateDividendStatus**](DividendsApi.md#updateDividendStatus) | **PATCH** /dividends/status | Update status for multiple dividends |



## bulkImportDividends

> BulkImportDividendsResponse bulkImportDividends(xUserId, bulkImportDividendsRequest)

Bulk import dividends from parsed statement

Import multiple dividends at once from a parsed broker statement. Called by AI Agent after parsing a statement. Automatically calculates tax and updates statement metadata. 

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
        BulkImportDividendsRequest bulkImportDividendsRequest = new BulkImportDividendsRequest(); // BulkImportDividendsRequest | 
        try {
            BulkImportDividendsResponse result = apiInstance.bulkImportDividends(xUserId, bulkImportDividendsRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#bulkImportDividends");
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
| **bulkImportDividendsRequest** | [**BulkImportDividendsRequest**](BulkImportDividendsRequest.md)|  | |

### Return type

[**BulkImportDividendsResponse**](BulkImportDividendsResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Dividends imported successfully |  -  |
| **400** | Invalid request or statement not found |  -  |
| **403** | User does not own the statement |  -  |


## calculateAllUserDividends

> TaxCalculationBatchResultDTO calculateAllUserDividends(userId, xUserId)

Recalculate tax for all user dividends

Triggers tax recalculation for all dividends of a user. Useful after tax rule updates.

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
        UUID userId = UUID.randomUUID(); // UUID | User ID whose dividends should be recalculated
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            TaxCalculationBatchResultDTO result = apiInstance.calculateAllUserDividends(userId, xUserId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#calculateAllUserDividends");
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
| **userId** | **UUID**| User ID whose dividends should be recalculated | |
| **xUserId** | **UUID**|  | |

### Return type

[**TaxCalculationBatchResultDTO**](TaxCalculationBatchResultDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tax recalculated for all dividends |  -  |
| **403** | Forbidden - user can only calculate their own dividends |  -  |
| **404** | User not found |  -  |


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


## getDividendStats

> DividendStatsDTO getDividendStats(xUserId, taxYear)

Get dividend statistics

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
        Integer taxYear = 56; // Integer | Optional tax year filter. If not provided, returns all-time stats.
        try {
            DividendStatsDTO result = apiInstance.getDividendStats(xUserId, taxYear);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#getDividendStats");
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
| **taxYear** | **Integer**| Optional tax year filter. If not provided, returns all-time stats. | [optional] |

### Return type

[**DividendStatsDTO**](DividendStatsDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Statistics |  -  |


## listDividends

> ListDividends200Response listDividends(xUserId, page, size, startDate, endDate, status)

List user&#39;s dividends with optional filters

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
        LocalDate startDate = LocalDate.now(); // LocalDate | Filter dividends from this date (inclusive)
        LocalDate endDate = LocalDate.now(); // LocalDate | Filter dividends until this date (inclusive)
        String status = "UNSUBMITTED"; // String | Filter by submission status
        try {
            ListDividends200Response result = apiInstance.listDividends(xUserId, page, size, startDate, endDate, status);
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
| **startDate** | **LocalDate**| Filter dividends from this date (inclusive) | [optional] |
| **endDate** | **LocalDate**| Filter dividends until this date (inclusive) | [optional] |
| **status** | **String**| Filter by submission status | [optional] [enum: UNSUBMITTED, SUBMITTED, APPROVED, PAID] |

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


## updateDividendStatus

> updateDividendStatus(xUserId, updateDividendStatusRequest)

Update status for multiple dividends

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
        UpdateDividendStatusRequest updateDividendStatusRequest = new UpdateDividendStatusRequest(); // UpdateDividendStatusRequest | 
        try {
            apiInstance.updateDividendStatus(xUserId, updateDividendStatusRequest);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendsApi#updateDividendStatus");
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
| **updateDividendStatusRequest** | [**UpdateDividendStatusRequest**](UpdateDividendStatusRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Status updated |  -  |

