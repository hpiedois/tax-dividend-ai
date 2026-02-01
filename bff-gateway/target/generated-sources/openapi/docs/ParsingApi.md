# ParsingApi

All URIs are relative to *http://localhost:8083/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**parseDocument**](ParsingApi.md#parseDocument) | **POST** /parse | Parse a PDF or Image dividend statement |



## parseDocument

> ParseResponse parseDocument(_file, password)

Parse a PDF or Image dividend statement

### Example

```java
// Import classes:
import com.taxdividend.bff.agent.client.ApiClient;
import com.taxdividend.bff.agent.client.ApiException;
import com.taxdividend.bff.agent.client.Configuration;
import com.taxdividend.bff.agent.client.models.*;
import com.taxdividend.bff.agent.client.api.ParsingApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8083/api");

        ParsingApi apiInstance = new ParsingApi(defaultClient);
        File _file = new File("/path/to/file"); // File | The dividend statement file (PDF, JPEG, PNG)
        String password = "password_example"; // String | Optional password for encrypted PDFs
        try {
            ParseResponse result = apiInstance.parseDocument(_file, password);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ParsingApi#parseDocument");
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
| **_file** | **File**| The dividend statement file (PDF, JPEG, PNG) | |
| **password** | **String**| Optional password for encrypted PDFs | [optional] |

### Return type

[**ParseResponse**](ParseResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful extraction |  -  |
| **400** | Invalid input or unreadable file |  -  |
| **422** | Password required but not provided |  -  |
| **500** | Extraction failed |  -  |

