# TaxRulesApi

All URIs are relative to *http://localhost:8081/internal*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**findApplicableRule**](TaxRulesApi.md#findApplicableRule) | **GET** /tax-rules/applicable | Find applicable tax rule |
| [**getAllTaxRules**](TaxRulesApi.md#getAllTaxRules) | **GET** /tax-rules | List all tax rules |
| [**getTaxRule**](TaxRulesApi.md#getTaxRule) | **GET** /tax-rules/{id} | Get tax rule by ID |
| [**getTreatyRate**](TaxRulesApi.md#getTreatyRate) | **GET** /tax-rules/treaty-rate | Get treaty rate |



## findApplicableRule

> TaxRule findApplicableRule(sourceCountry, residenceCountry, securityType, date)

Find applicable tax rule

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.TaxRulesApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        TaxRulesApi apiInstance = new TaxRulesApi(defaultClient);
        String sourceCountry = "sourceCountry_example"; // String | 
        String residenceCountry = "residenceCountry_example"; // String | 
        String securityType = "EQUITY"; // String | 
        LocalDate date = LocalDate.now(); // LocalDate | 
        try {
            TaxRule result = apiInstance.findApplicableRule(sourceCountry, residenceCountry, securityType, date);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TaxRulesApi#findApplicableRule");
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
| **sourceCountry** | **String**|  | |
| **residenceCountry** | **String**|  | |
| **securityType** | **String**|  | [optional] [default to EQUITY] |
| **date** | **LocalDate**|  | [optional] |

### Return type

[**TaxRule**](TaxRule.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Applicable rule |  -  |
| **404** | No rule found |  -  |


## getAllTaxRules

> List&lt;TaxRule&gt; getAllTaxRules()

List all tax rules

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.TaxRulesApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        TaxRulesApi apiInstance = new TaxRulesApi(defaultClient);
        try {
            List<TaxRule> result = apiInstance.getAllTaxRules();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TaxRulesApi#getAllTaxRules");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**List&lt;TaxRule&gt;**](TaxRule.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | List of tax rules |  -  |


## getTaxRule

> TaxRule getTaxRule(id)

Get tax rule by ID

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.TaxRulesApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        TaxRulesApi apiInstance = new TaxRulesApi(defaultClient);
        UUID id = UUID.randomUUID(); // UUID | 
        try {
            TaxRule result = apiInstance.getTaxRule(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TaxRulesApi#getTaxRule");
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

### Return type

[**TaxRule**](TaxRule.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tax rule details |  -  |
| **404** | Not found |  -  |


## getTreatyRate

> TreatyRateResponse getTreatyRate(sourceCountry, residenceCountry, securityType, date)

Get treaty rate

### Example

```java
// Import classes:
import com.taxdividend.bff.client.ApiClient;
import com.taxdividend.bff.client.ApiException;
import com.taxdividend.bff.client.Configuration;
import com.taxdividend.bff.client.models.*;
import com.taxdividend.bff.client.api.TaxRulesApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8081/internal");

        TaxRulesApi apiInstance = new TaxRulesApi(defaultClient);
        String sourceCountry = "sourceCountry_example"; // String | 
        String residenceCountry = "residenceCountry_example"; // String | 
        String securityType = "EQUITY"; // String | 
        LocalDate date = LocalDate.now(); // LocalDate | 
        try {
            TreatyRateResponse result = apiInstance.getTreatyRate(sourceCountry, residenceCountry, securityType, date);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling TaxRulesApi#getTreatyRate");
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
| **sourceCountry** | **String**|  | |
| **residenceCountry** | **String**|  | |
| **securityType** | **String**|  | [optional] [default to EQUITY] |
| **date** | **LocalDate**|  | [optional] |

### Return type

[**TreatyRateResponse**](TreatyRateResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Treaty rate details |  -  |
| **404** | No treaty found |  -  |

