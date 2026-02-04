# DividendsApi

All URIs are relative to *http://localhost:8080/api*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getDividendHistory**](#getdividendhistory) | **GET** /dividends/history | Get dividend history|
|[**getDividendStats**](#getdividendstats) | **GET** /dividends/stats | Get dividend statistics|
|[**parseDividendStatement**](#parsedividendstatement) | **POST** /dividends/parse-statement | Parse a dividend statement (PDF/Image)|
|[**updateDividendStatus**](#updatedividendstatus) | **PATCH** /dividends/status | Update status for multiple dividends|

# **getDividendHistory**
> DividendHistoryResponse getDividendHistory()


### Example

```typescript
import {
    DividendsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new DividendsApi(configuration);

let page: number; // (optional) (default to 0)
let pageSize: number; // (optional) (default to 10)

const { status, data } = await apiInstance.getDividendHistory(
    page,
    pageSize
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **page** | [**number**] |  | (optional) defaults to 0|
| **pageSize** | [**number**] |  | (optional) defaults to 10|


### Return type

**DividendHistoryResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | History retrieved |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getDividendStats**
> DividendStats getDividendStats()


### Example

```typescript
import {
    DividendsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new DividendsApi(configuration);

let taxYear: number; //Optional tax year filter (optional) (default to undefined)

const { status, data } = await apiInstance.getDividendStats(
    taxYear
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **taxYear** | [**number**] | Optional tax year filter | (optional) defaults to undefined|


### Return type

**DividendStats**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Stats retrieved |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **parseDividendStatement**
> DividendStatement parseDividendStatement()


### Example

```typescript
import {
    DividendsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new DividendsApi(configuration);

let file: File; // (optional) (default to undefined)

const { status, data } = await apiInstance.parseDividendStatement(
    file
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **file** | [**File**] |  | (optional) defaults to undefined|


### Return type

**DividendStatement**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successful parsing |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateDividendStatus**
> updateDividendStatus(updateDividendStatusRequest)


### Example

```typescript
import {
    DividendsApi,
    Configuration,
    UpdateDividendStatusRequest
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new DividendsApi(configuration);

let updateDividendStatusRequest: UpdateDividendStatusRequest; //

const { status, data } = await apiInstance.updateDividendStatus(
    updateDividendStatusRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateDividendStatusRequest** | **UpdateDividendStatusRequest**|  | |


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
|**204** | Status updated |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

