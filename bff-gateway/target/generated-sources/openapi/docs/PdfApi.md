# PdfApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**generatePdf**](PdfApi.md#generatePdf) | **POST** /pdf/generate | Generate Tax Forms PDF |



## generatePdf

> GenerateFormResultDTO generatePdf(formGenerationRequest)

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
            GenerateFormResultDTO result = apiInstance.generatePdf(formGenerationRequest);
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
| **200** | PDF Generated |  -  |
| **400** | Bad request |  -  |
| **500** | Generation failed |  -  |

