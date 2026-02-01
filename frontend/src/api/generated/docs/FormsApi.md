# FormsApi

All URIs are relative to *http://localhost:8080/api*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**deleteForm**](#deleteform) | **DELETE** /forms/{id} | Delete form|
|[**downloadForm**](#downloadform) | **GET** /forms/{id}/download | Download form|
|[**generateTaxForms**](#generatetaxforms) | **POST** /forms/generate | Generate tax forms|
|[**getForm**](#getform) | **GET** /forms/{id} | Get form metadata|
|[**getFormDownloadUrl**](#getformdownloadurl) | **GET** /forms/{id}/download-url | Get pre-signed download URL for form|
|[**listForms**](#listforms) | **GET** /forms | List user\&#39;s forms|

# **deleteForm**
> deleteForm()


### Example

```typescript
import {
    FormsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new FormsApi(configuration);

let id: string; // (default to undefined)

const { status, data } = await apiInstance.deleteForm(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**string**] |  | defaults to undefined|


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
|**204** | Deleted |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **downloadForm**
> File downloadForm()


### Example

```typescript
import {
    FormsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new FormsApi(configuration);

let id: string; // (default to undefined)

const { status, data } = await apiInstance.downloadForm(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**string**] |  | defaults to undefined|


### Return type

**File**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/pdf, application/zip


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Form file |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **generateTaxForms**
> GenerateTaxFormsResponse generateTaxForms()


### Example

```typescript
import {
    FormsApi,
    Configuration,
    GenerateTaxFormsRequest
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new FormsApi(configuration);

let generateTaxFormsRequest: GenerateTaxFormsRequest; // (optional)

const { status, data } = await apiInstance.generateTaxForms(
    generateTaxFormsRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **generateTaxFormsRequest** | **GenerateTaxFormsRequest**|  | |


### Return type

**GenerateTaxFormsResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Forms generated |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getForm**
> GeneratedForm getForm()


### Example

```typescript
import {
    FormsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new FormsApi(configuration);

let id: string; // (default to undefined)

const { status, data } = await apiInstance.getForm(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**string**] |  | defaults to undefined|


### Return type

**GeneratedForm**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Form metadata |  -  |
|**404** | Not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getFormDownloadUrl**
> FormDownloadUrlResponse getFormDownloadUrl()


### Example

```typescript
import {
    FormsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new FormsApi(configuration);

let id: string; // (default to undefined)
let expiresIn: number; // (optional) (default to 3600)

const { status, data } = await apiInstance.getFormDownloadUrl(
    id,
    expiresIn
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**string**] |  | defaults to undefined|
| **expiresIn** | [**number**] |  | (optional) defaults to 3600|


### Return type

**FormDownloadUrlResponse**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Pre-signed download URL |  -  |
|**404** | Form not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **listForms**
> Array<GeneratedForm> listForms()


### Example

```typescript
import {
    FormsApi,
    Configuration
} from 'tax-dividend-api';

const configuration = new Configuration();
const apiInstance = new FormsApi(configuration);

let taxYear: number; // (optional) (default to undefined)
let formType: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.listForms(
    taxYear,
    formType
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **taxYear** | [**number**] |  | (optional) defaults to undefined|
| **formType** | [**string**] |  | (optional) defaults to undefined|


### Return type

**Array<GeneratedForm>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | List of forms |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

