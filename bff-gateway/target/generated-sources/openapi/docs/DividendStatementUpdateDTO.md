

# DividendStatementUpdateDTO


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**status** | **DividendStatementStatus** |  |  |
|**sentMethod** | **String** | For SENT status - submission method |  [optional] |
|**sentNotes** | **String** | For SENT status - additional notes |  [optional] |
|**paidAmount** | **BigDecimal** | For PAID status - amount received |  [optional] |
|**paidAt** | **OffsetDateTime** | For PAID status - payment date (defaults to now if not provided) |  [optional] |
|**parsedBy** | **String** | For AI Agent updates - who triggered the parsing |  [optional] |



