# DividendStatementsApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**countDividendStatementsByStatus**](DividendStatementsApi.md#countDividendStatementsByStatus) | **GET** /dividend-statements/count-by-status | Count statements by status |
| [**deleteDividendStatement**](DividendStatementsApi.md#deleteDividendStatement) | **DELETE** /dividend-statements/{id} | Delete dividend statement |
| [**getDividendStatement**](DividendStatementsApi.md#getDividendStatement) | **GET** /dividend-statements/{id} | Get dividend statement by ID |
| [**getDividendStatementsByDateRange**](DividendStatementsApi.md#getDividendStatementsByDateRange) | **GET** /dividend-statements/by-date-range | Find statements by date range |
| [**listDividendStatements**](DividendStatementsApi.md#listDividendStatements) | **GET** /dividend-statements | List user&#39;s dividend statements |
| [**updateDividendStatementStatus**](DividendStatementsApi.md#updateDividendStatementStatus) | **PATCH** /dividend-statements/{id} | Update dividend statement status |
| [**uploadDividendStatement**](DividendStatementsApi.md#uploadDividendStatement) | **POST** /dividend-statements | Upload broker statement |



## countDividendStatementsByStatus

> Long countDividendStatementsByStatus(xUserId, status)

Count statements by status

Get count of statements for a specific status

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        DividendStatementStatus status = DividendStatementStatus.fromValue("UPLOADED"); // DividendStatementStatus | 
        try {
            Long result = apiInstance.countDividendStatementsByStatus(xUserId, status);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#countDividendStatementsByStatus");
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
| **status** | [**DividendStatementStatus**](.md)|  | [enum: UPLOADED, PARSING, PARSED, VALIDATED, SENT, PAID] |

### Return type

**Long**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Count of statements |  -  |


## deleteDividendStatement

> deleteDividendStatement(id, xUserId)

Delete dividend statement

Delete statement and associated file from storage

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            apiInstance.deleteDividendStatement(id, xUserId);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#deleteDividendStatement");
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
| **204** | Deleted successfully |  -  |
| **404** | Not found or not owned by user |  -  |


## getDividendStatement

> DividendStatement getDividendStatement(id, xUserId)

Get dividend statement by ID

Get a specific dividend statement. Validates ownership.

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        try {
            DividendStatement result = apiInstance.getDividendStatement(id, xUserId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#getDividendStatement");
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

[**DividendStatement**](DividendStatement.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Dividend statement details |  -  |
| **404** | Not found or not owned by user |  -  |


## getDividendStatementsByDateRange

> List&lt;DividendStatement&gt; getDividendStatementsByDateRange(xUserId, startDate, endDate)

Find statements by date range

Find statements whose period overlaps with the specified date range

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        LocalDate startDate = LocalDate.now(); // LocalDate | 
        LocalDate endDate = LocalDate.now(); // LocalDate | 
        try {
            List<DividendStatement> result = apiInstance.getDividendStatementsByDateRange(xUserId, startDate, endDate);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#getDividendStatementsByDateRange");
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
| **startDate** | **LocalDate**|  | |
| **endDate** | **LocalDate**|  | |

### Return type

[**List&lt;DividendStatement&gt;**](DividendStatement.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of statements in date range |  -  |


## listDividendStatements

> ListDividendStatements200Response listDividendStatements(xUserId, status, page, size)

List user&#39;s dividend statements

List dividend statements with optional status filter and pagination

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        DividendStatementStatus status = DividendStatementStatus.fromValue("UPLOADED"); // DividendStatementStatus | Filter by status
        Integer page = 0; // Integer | 
        Integer size = 20; // Integer | 
        try {
            ListDividendStatements200Response result = apiInstance.listDividendStatements(xUserId, status, page, size);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#listDividendStatements");
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
| **status** | [**DividendStatementStatus**](.md)| Filter by status | [optional] [enum: UPLOADED, PARSING, PARSED, VALIDATED, SENT, PAID] |
| **page** | **Integer**|  | [optional] [default to 0] |
| **size** | **Integer**|  | [optional] [default to 20] |

### Return type

[**ListDividendStatements200Response**](ListDividendStatements200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Page of dividend statements |  -  |


## updateDividendStatementStatus

> DividendStatement updateDividendStatementStatus(id, xUserId, dividendStatementUpdateDTO)

Update dividend statement status

Update statement status. Validates status transitions: - UPLOADED → PARSING (AI Agent starts parsing) - PARSING → PARSED (AI Agent completes parsing) - PARSED → VALIDATED (User downloads forms) - VALIDATED → SENT (User submits forms offline) - SENT → PAID (User receives payment) 

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        UUID xUserId = UUID.randomUUID(); // UUID | 
        DividendStatementUpdateDTO dividendStatementUpdateDTO = new DividendStatementUpdateDTO(); // DividendStatementUpdateDTO | 
        try {
            DividendStatement result = apiInstance.updateDividendStatementStatus(id, xUserId, dividendStatementUpdateDTO);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#updateDividendStatementStatus");
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
| **dividendStatementUpdateDTO** | [**DividendStatementUpdateDTO**](DividendStatementUpdateDTO.md)|  | |

### Return type

[**DividendStatement**](DividendStatement.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Status updated successfully |  -  |
| **400** | Invalid status transition |  -  |
| **404** | Not found or not owned by user |  -  |


## uploadDividendStatement

> DividendStatement uploadDividendStatement(xUserId, broker, periodStart, periodEnd, _file)

Upload broker statement

Upload a broker statement PDF/CSV file. Creates a new DividendStatement with status UPLOADED.

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.DividendStatementsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        DividendStatementsApi apiInstance = new DividendStatementsApi(defaultClient);
        UUID xUserId = UUID.randomUUID(); // UUID | 
        String broker = "broker_example"; // String | Broker name (InteractiveBrokers, Swissquote, etc.)
        LocalDate periodStart = LocalDate.now(); // LocalDate | Start of period covered by statement
        LocalDate periodEnd = LocalDate.now(); // LocalDate | End of period covered by statement
        File _file = new File("/path/to/file"); // File | Broker statement file (PDF or CSV)
        try {
            DividendStatement result = apiInstance.uploadDividendStatement(xUserId, broker, periodStart, periodEnd, _file);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DividendStatementsApi#uploadDividendStatement");
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
| **broker** | **String**| Broker name (InteractiveBrokers, Swissquote, etc.) | [optional] |
| **periodStart** | **LocalDate**| Start of period covered by statement | [optional] |
| **periodEnd** | **LocalDate**| End of period covered by statement | [optional] |
| **_file** | **File**| Broker statement file (PDF or CSV) | [optional] |

### Return type

[**DividendStatement**](DividendStatement.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Statement uploaded successfully |  -  |
| **400** | Bad request - invalid file or parameters |  -  |

