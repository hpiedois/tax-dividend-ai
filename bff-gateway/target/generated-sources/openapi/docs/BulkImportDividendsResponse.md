

# BulkImportDividendsResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**importedCount** | **Integer** | Number of dividends successfully imported |  [optional] |
|**failedCount** | **Integer** | Number of dividends that failed to import |  [optional] |
|**totalGrossAmount** | **BigDecimal** | Total gross amount of all imported dividends |  [optional] |
|**totalReclaimable** | **BigDecimal** | Total reclaimable amount across all dividends |  [optional] |
|**dividendIds** | **List&lt;UUID&gt;** | IDs of successfully imported dividends |  [optional] |
|**errors** | **List&lt;String&gt;** | Error messages for failed imports |  [optional] |



