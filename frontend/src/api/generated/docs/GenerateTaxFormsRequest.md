# GenerateTaxFormsRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**dividendIds** | **Array&lt;string&gt;** |  | [optional] [default to undefined]
**taxYear** | **number** |  | [optional] [default to undefined]
**formType** | **string** |  | [optional] [default to undefined]
**includeAttestation** | **boolean** |  | [optional] [default to false]
**includeDividends** | **boolean** |  | [optional] [default to false]

## Example

```typescript
import { GenerateTaxFormsRequest } from 'tax-dividend-api';

const instance: GenerateTaxFormsRequest = {
    dividendIds,
    taxYear,
    formType,
    includeAttestation,
    includeDividends,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
