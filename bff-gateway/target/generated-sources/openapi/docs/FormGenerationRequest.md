

# FormGenerationRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**userId** | **UUID** |  |  |
|**taxYear** | **Integer** |  |  |
|**formType** | [**FormTypeEnum**](#FormTypeEnum) |  |  |
|**dividendIds** | **List&lt;UUID&gt;** |  |  [optional] |
|**includeAttestation** | **Boolean** |  |  [optional] |
|**includeDividends** | **Boolean** |  |  [optional] |
|**canton** | **String** |  |  [optional] |
|**address** | **String** |  |  [optional] |
|**taxId** | **String** |  |  [optional] |



## Enum: FormTypeEnum

| Name | Value |
|---- | -----|
| _5000 | &quot;5000&quot; |
| _5001 | &quot;5001&quot; |
| BUNDLE | &quot;BUNDLE&quot; |



