# StorageApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**uploadFile**](StorageApi.md#uploadFile) | **POST** /storage/upload | Upload file to storage |



## uploadFile

> UploadFile200Response uploadFile(_file, bucket)

Upload file to storage

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.StorageApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        StorageApi apiInstance = new StorageApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String bucket = "bucket_example"; // String | 
        try {
            UploadFile200Response result = apiInstance.uploadFile(_file, bucket);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling StorageApi#uploadFile");
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
| **_file** | **File**|  | [optional] |
| **bucket** | **String**|  | [optional] |

### Return type

[**UploadFile200Response**](UploadFile200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Upload successful |  -  |

