# PdfApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**generatePdf**](PdfApi.md#generatePdf) | **POST** /pdf/generate | Generate Tax Forms PDF |
| [**parseMakePdf**](PdfApi.md#parseMakePdf) | **POST** /pdf/parse | Parse Dividend PDF |



## generatePdf

> PdfGenerationResponse generatePdf(formGenerationRequest)

Generate Tax Forms PDF

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.PdfApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        PdfApi apiInstance = new PdfApi(defaultClient);
        FormGenerationRequest formGenerationRequest = new FormGenerationRequest(); // FormGenerationRequest | 
        try {
            PdfGenerationResponse result = apiInstance.generatePdf(formGenerationRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PdfApi#generatePdf");
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
| **formGenerationRequest** | [**FormGenerationRequest**](FormGenerationRequest.md)|  | [optional] |

### Return type

[**PdfGenerationResponse**](PdfGenerationResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | PDF Generated |  -  |


## parseMakePdf

> ParseMakePdf200Response parseMakePdf(_file)

Parse Dividend PDF

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.PdfApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        PdfApi apiInstance = new PdfApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        try {
            ParseMakePdf200Response result = apiInstance.parseMakePdf(_file);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PdfApi#parseMakePdf");
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

### Return type

[**ParseMakePdf200Response**](ParseMakePdf200Response.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Parsing result |  -  |

